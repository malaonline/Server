import logging

# django modules
from django.core.files.base import ContentFile
from django.utils.decorators import method_decorator
from django.contrib.auth.decorators import user_passes_test, login_required
from django.contrib.auth import login, authenticate, _get_backends, logout
from django.http import HttpResponse, JsonResponse, HttpResponseRedirect
from django.core.urlresolvers import reverse
from django.views.generic import View
from django.shortcuts import render, get_object_or_404
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth.models import User
from django.conf import settings
import json

# local modules
from app import models

logger = logging.getLogger('app')

# Create your views here.

# 目前老师端的公共登录url,这里不能用reverse,不然会发生循环引用
LOGIN_URL = "/teacher/login"


class TeacherLogin(View):
    """
    老师用户注册页面 TW-1-1
    """
    def get(self, request):
        context = {}
        return render(request, 'teacher/register.html', context)


class VerifySmsCode(View):
    """
    检查短信验证码是否正确
    """
    def post(self, request):
        phone = request.POST.get("phone", None)
        code = request.POST.get("code", None)
        Profile = models.Profile
        CheckCode = models.Checkcode
        Teacher = models.Teacher
        new_user = True
        try:
            profile = Profile.objects.get(phone=phone)
            user = profile.user
            for backend, backend_path in _get_backends(return_tuples=True):
                user.backend = backend_path
                break
            teacher = Teacher.objects.get(user=user)
            new_user = False
        except Profile.DoesNotExist:
            # new user
            user = Teacher.new_teacher()
            teacher = user.teacher
            profile = teacher.user.profile
            profile.phone = phone
            profile.save()
        if CheckCode.verify_sms(phone, code) is True:
            # 验证通过
            percent = information_complete_percent(user)
            login(request, user)

            if percent < 1:
                # 如果老师信息没有完成,就填写老师信息
                return JsonResponse({
                    "result": True,
                    "url": reverse("teacher:complete-information")
                })
            else:
                if teacher.status != Teacher.INTERVIEW_OK:
                    return JsonResponse({
                        "result": True,
                        "url": reverse("teacher:register-progress")
                    })
                else:
                    return JsonResponse({
                        "result": True,
                        "url": reverse("teacher:first-page")
                    })
        else:
            # 验证失败
            return JsonResponse({
                "result": False
            })


def information_complete_percent(user: User):
    """
    计算用户信息完成度
    :param user:
    :return:
    """
    total = 4
    unfinished = 0
    Teacher = models.Teacher
    Profile = models.Profile
    teacher = Teacher.objects.get(user=user)
    profile = Profile.objects.get(user=user)
    if teacher.name == "":
        unfinished += 1
    if profile.gender == "u":
        unfinished += 1
    if teacher.region == None:
        unfinished += 1
    if len(teacher.abilities.all()) == 0:
        unfinished += 1
    return (total - unfinished) / total


# 完善老师的个人信息 TW-2-1
class CompleteInformation(View):
    def get(self, request):
        user = request.user
        teacher = models.Teacher.objects.get(user=user)
        profile = models.Profile.objects.get(user=user)

        name = teacher.name
        gender_dict = {"f": "女", "m": "男", "u": ""}
        gender = gender_dict.get(profile.gender, "")
        if teacher.region:
            region = teacher.region.name or ""
        else:
            region = ""
        ability_set_all = teacher.abilities.all()
        phone = profile.mask_phone()
        if len(ability_set_all) > 0:
            subclass = ability_set_all[0].subject.name
        else:
            subclass = ""
        # 初始化年级
        grade = [[False for i in range(6)],
                 [False for i in range(3)],
                 [False for i in range(3)]]
        grade_name = models.Grade.get_all_grades()
        grade_slot = {}
        for x, one_level in enumerate(grade_name):
            for y, one_grade in enumerate(one_level):
                grade_slot[one_grade] = (x, y)

        grade_list = [item.grade.name for item in list(teacher.abilities.all())]
        for one_grade in grade_list:
            x, y = grade_slot.get(one_grade, (0, 0))
            grade[x][y] = True

        context = {
            "name": name,
            "gender": gender,
            "region": region,
            "subclass": subclass,
            "grade": json.dumps(grade),
            "phone_name": phone
        }
        return render(request, 'teacher/complete_information.html', context)

    def post(self, request):
        user = request.user
        teacher = models.Teacher.objects.get(user=user)
        profile = models.Profile.objects.get(user=user)

        name = request.POST.get("name", "")
        gender = request.POST.get("gender", "")
        region = request.POST.get("region")
        subject = request.POST.get("subclass")
        grade = request.POST.get("grade")

        grade_list = json.loads(grade)

        teacher.name = name
        gender_dict = {"男": "m", "女": "f"}
        profile.gender = gender_dict.get(gender, "u")
        teacher.region = models.Region.objects.get(name=region)

        the_subject = models.Subject.objects.get(name=subject)
        grade_name_list = models.Grade.get_all_grades()
        page_grade_list = [["小学一年级", "小学二年级", "小学三年级", "小学四年级", "小学五年级", "小学六年级"],
                           ["初一", "初二", "初三"],
                           ["高一", "高二", "高三"]]
        grade_dict = {}
        for page_level, database_level in list(zip(page_grade_list, grade_name_list)):
            for page_grade, database_grade in list(zip(page_level, database_level)):
                grade_dict[page_grade] = database_grade

        # clear ability_set
        teacher.abilities.clear()

        for one_grade in grade_list:
            the_grade = models.Grade.objects.get(name=grade_dict.get(one_grade, one_grade))
            ability, _ = models.Ability.objects.get_or_create(grade=the_grade, subject=the_subject)
            teacher.abilities.add(ability)
            ability.save()

        teacher.save()
        profile.save()

        return JsonResponse({"url": reverse("teacher:register-progress")})


class RegisterProgress(View):
    """
    显示注册进度
    """
    def get(self, request):
        context = {}
        try:
            teacher = models.Teacher.objects.get(user=request.user)
        except models.Teacher.DoesNotExist:
            return HttpResponseRedirect(reverse("teacher:register"))

        if settings.FIX_TEACHER_STATUS:
            teacher.status = teacher.INTERVIEW_OK
        context["progress"] = teacher.get_progress()
        context["text_list"] = teacher.build_progress_info()
        context["user_name"] = "{name} 老师".format(name=teacher.name)
        return render(request, "teacher/register_progress.html", context)


# 设置老师页面的通用上下文
def set_teacher_page_general_context(teacher, context):
    context["user_name"] = "{name} 老师".format(name=teacher.name)


class FirstPage(View):
    """
    通过面试的老师见到的第一个页面
    """
    def get(self, request):
        teacher = models.Teacher.objects.get(user=request.user)
        context = {}
        set_teacher_page_general_context(teacher, context)
        return render(request, "teacher/first_page.html", context)


class MySchoolTimetable(View):
    """
    TW-5-1, 查看课表上的内容
    """
    def get(self, request):
        user = request.user
        teacher = models.Teacher.objects.get(user=user)
        context = {}
        set_teacher_page_general_context(teacher, context)
        return render(request, "teacher/my_school_timetable.html", context)


class MyStudents(View):
    """
    TW-5-2, 我的学生
    """
    def get(self, request):
        user = request.user
        teacher = models.Teacher.objects.get(user=user)
        context = {}
        set_teacher_page_general_context(teacher, context)
        return render(request, "teacher/my_students.html", context)


class TeacherLogout(View):
    """
    登出
    """
    def get(self, request):
        logout(request)
        return HttpResponseRedirect(redirect_to=reverse("teacher:register"))


# 判断是否是已登录老师
def is_teacher_logined(u):
    if not u:
        return False
    if not u.is_authenticated():
        return False
    try:
        models.Teacher.objects.get(user=u)
        return True
    except models.Teacher.DoesNotExist as ex:
        logger.error("Can not find Teacher related with user {0}".format(u))
    except Exception as err:
        logger.error(err)
    return False


class BaseTeacherView(View):
    """
    Base View for Teacher web client, require teacher being logined
    """
    @method_decorator(user_passes_test(is_teacher_logined, login_url='teacher:register'))
    def dispatch(self, request, *args, **kwargs):
        return super(BaseTeacherView, self).dispatch(request, *args, **kwargs)

    def getContextTeacher(self, request):
        context = {}
        teacher = get_object_or_404(models.Teacher, user=request.user)
        context['teacher'] = teacher
        context['teacherName'] = teacher.name
        return context, teacher

class CertificateView(BaseTeacherView):
    """
    certifications overview
    """
    def get(self, request):
        context, teacher = self.getContextTeacher(request)
        context['isEnglishTeacher'] = teacher.is_english_teacher()
        certifications = models.Certificate.objects.filter(teacher=teacher)
        tmp_other_cert = None
        for cert in certifications:
            if cert.type == models.Certificate.ID_HELD:
                context['cert_id'] = cert
            elif cert.type == models.Certificate.ID_FRONT:
                continue
            elif cert.type == models.Certificate.ACADEMIC:
                context['cert_academic'] = cert
            elif cert.type == models.Certificate.TEACHING:
                context['cert_teaching'] = cert
            elif cert.type == models.Certificate.ENGLISH:
                context['cert_english'] = cert
            else:
                if not tmp_other_cert or not tmp_other_cert.verified and cert.verified:
                    tmp_other_cert = cert
        context['cert_other'] = tmp_other_cert
        return render(request, 'teacher/certificate/overview.html', context)

class CertificateIDView(BaseTeacherView):
    """
    page of certificate id
    """
    template_path = 'teacher/certificate/certificate_id.html'
    def get(self, request):
        context, teacher = self.getContextTeacher(request)
        certIdHeld, created = models.Certificate.objects.get_or_create(teacher=teacher, type=models.Certificate.ID_HELD,
                                                              defaults={'name':"",'verified':False})
        certIdFront, created = models.Certificate.objects.get_or_create(teacher=teacher, type=models.Certificate.ID_FRONT,
                                                              defaults={'name':"",'verified':False})
        context = self.buildContextData(context, certIdHeld, certIdFront)
        return render(request, self.template_path, context)

    def buildContextData(self, context, certIdHeld, certIdFront):
        context['id_num'] = certIdHeld.name
        context['idHeldUrl'] = certIdHeld.imgUrl()
        context['idFrontUrl'] = certIdFront.imgUrl()
        return context

    def post(self, request):
        context, teacher = self.getContextTeacher(request)
        certIdHeld, created = models.Certificate.objects.get_or_create(teacher=teacher, type=models.Certificate.ID_HELD,
                                                              defaults={'name':"",'verified':False})
        certIdFront, created = models.Certificate.objects.get_or_create(teacher=teacher, type=models.Certificate.ID_FRONT,
                                                              defaults={'name':"",'verified':False})
        isJsonReq = request.POST.get('format') == 'json'
        if certIdHeld.verified:
            error_msg = '已通过认证的不能更改'
            if isJsonReq:
                return JsonResponse({'ok': False, 'msg': error_msg, 'code': -1})
            context['error_msg'] = error_msg
            return render(request, self.template_path, self.buildContextData(context, certIdHeld, certIdFront))

        id_num = request.POST.get('id_num')
        if not id_num:
            error_msg = '身份证号不能为空'
            if isJsonReq:
                return JsonResponse({'ok': False, 'msg': error_msg, 'code': -1})
            context['error_msg'] = error_msg
            return render(request, self.template_path, self.buildContextData(context, certIdHeld, certIdFront))
        certIdHeld.name = id_num

        if request.FILES:
            idHeldImgFile = request.FILES.get('idHeldImg')
            if idHeldImgFile:
                held_img_content = ContentFile(idHeldImgFile.read())
                certIdHeld.img.save("idHeld"+str(teacher.id), held_img_content)
            idFrontImgFile = request.FILES.get('idFrontImg')
            if idFrontImgFile:
                front_img_content = ContentFile(idFrontImgFile.read())
                certIdFront.img.save("idFrontImg"+str(teacher.id), front_img_content)

        certIdHeld.save()
        certIdFront.save()

        if isJsonReq:
            return JsonResponse({'ok': True, 'msg': '', 'code': 0, 'idHeldUrl': certIdHeld.imgUrl(), 'idFrontUrl': certIdFront.imgUrl()})
        context = self.buildContextData(context, certIdHeld, certIdFront)
        return render(request, self.template_path, context)


class CertificateForOnePicView(BaseTeacherView):
    """
    page of certificate for only one pic is needed
    """
    template_path = 'teacher/certificate/certificate_simple.html'
    # cert_types = ['academic', 'teaching', 'english']
    cert_type = 0
    cert_title = '证书标题'
    cert_name = '证书名字'
    hint_content = "提示内容"

    def get(self, request):
        context, teacher = self.getContextTeacher(request)
        cert, created = models.Certificate.objects.get_or_create(teacher=teacher, type=self.cert_type,
                                                              defaults={'name':"",'verified':False})
        context = self.buildContextData(context, cert)
        return render(request, self.template_path, context)

    def buildContextData(self, context, cert):
        context['cert_title'] = self.cert_title
        context['cert_name'] = self.cert_name
        context['name_val'] = cert.name
        context['certImgUrl'] = cert.imgUrl()
        context['hint_content'] = self.hint_content
        return context

    def post(self, request):
        context, teacher = self.getContextTeacher(request)
        cert, created = models.Certificate.objects.get_or_create(teacher=teacher, type=self.cert_type,
                                                              defaults={'name':"",'verified':False})
        isJsonReq = request.POST.get('format') == 'json'
        if cert.verified:
            error_msg = '已通过认证的不能更改'
            if isJsonReq:
                return JsonResponse({'ok': False, 'msg': error_msg, 'code': -1})
            context['error_msg'] = error_msg
            return render(request, self.template_path, self.buildContextData(context, cert))

        name = request.POST.get('name')
        if not name:
            error_msg = '证书名称不能为空'
            if isJsonReq:
                return JsonResponse({'ok': False, 'msg': error_msg, 'code': 1})
            context['error_msg'] = error_msg
            return render(request, self.template_path, self.buildContextData(context, cert))
        cert.name = name

        if request.FILES:
            certImgFile = request.FILES.get('certImg')
            if certImgFile:
                cert_img_content = ContentFile(certImgFile.read())
                cert.img.save("certImg"+str(self.cert_type)+str(teacher.id), cert_img_content)

        cert.save()

        if isJsonReq:
            return JsonResponse({'ok': True, 'msg': '', 'code': 0, 'certImgUrl': cert.imgUrl()})
        context = self.buildContextData(context, cert)
        return render(request, self.template_path, context)

class CertificateAcademicView(CertificateForOnePicView):
    cert_type = models.Certificate.ACADEMIC
    cert_title = '学历认证'
    cert_name = '毕业院校'
    hint_content = "请上传最新的毕业证或学位证书照片"

class CertificateTeachingView(CertificateForOnePicView):
    cert_type = models.Certificate.TEACHING
    cert_title = '教师资质认证'
    cert_name = '证书名称'
    hint_content = "请上传有效期内的教师资格证书或同等资格证明"

class CertificateEnglishView(CertificateForOnePicView):
    cert_type = models.Certificate.ENGLISH
    cert_title = '英语水平认证'
    cert_name = '证书名称'
    hint_content = "请上传你最具代表性的英语水平证书"

class CertificateOthersView(BaseTeacherView):
    """
    page of others certifications
    """
    template_path = 'teacher/certificate/certificate_others.html'

    def get(self, request):
        context, teacher = self.getContextTeacher(request)
        context = self.buildContextData(context, teacher)
        return render(request, self.template_path, context)

    def buildContextData(self, context, teacher):
        otherCerts = models.Certificate.objects.filter(teacher=teacher, type=models.Certificate.OTHER)
        context['otherCerts'] = otherCerts
        return context

    def post(self, request):
        if request.POST.get('action') == 'delete':
            return self.doDeleteCert(request)

        context, teacher = self.getContextTeacher(request)
        isJsonReq = request.POST.get('format') == 'json'
        cert = None
        id = request.POST.get('id')
        if id:
            cert = models.Certificate.objects.get(id=id, teacher=teacher)
        else:
            cert = models.Certificate(teacher=teacher, type=models.Certificate.OTHER, verified=False)
        name = request.POST.get('name')
        if not name:
            error_msg = '证书名称不能为空'
            if isJsonReq:
                return JsonResponse({'ok': False, 'msg': error_msg, 'code': 1})
            context['error_msg'] = error_msg
            return render(request, self.template_path, self.buildContextData(context, teacher))
        cert.name = name

        if request.FILES:
            certImgFile = request.FILES.get('certImg')
            if certImgFile:
                cert_img_content = ContentFile(certImgFile.read())
                cert.img.save("certImg"+str(cert.type)+str(teacher.id)+'_'+str(cert_img_content.size), cert_img_content)

        cert.save()

        if isJsonReq:
            return JsonResponse({'ok': True, 'msg': '', 'code': 0})
        context = self.buildContextData(context, teacher)
        return render(request, self.template_path, context)

    """
    return message format: {'ok': False, 'msg': msg, 'code': 1}
    """
    def doDeleteCert(self, request):
        context, teacher = self.getContextTeacher(request)
        certId = request.POST.get('certId')
        if not certId:
            return JsonResponse({'ok': False, 'msg': '参数错误', 'code': 1})
        try:
            cert = models.Certificate.objects.get(id=certId, teacher=teacher)
            if cert.type and cert.type != models.Certificate.OTHER:
                return JsonResponse({'ok': False, 'msg': '不支持删除该类型的证书', 'code': 4})
            cert.delete()
            return JsonResponse({'ok': True, 'msg': '', 'code': 0})
        except models.Certificate.DoesNotExist as e:
            logger.warning(e)
            return JsonResponse({'ok': False, 'msg': '没有找到相应的记录', 'code': 2})
        except Exception as err:
            logger.error(err)
            return JsonResponse({'ok': False, 'msg': '请求失败,请稍后重试,或联系管理员!', 'code': -1})


class HighscoreView(BaseTeacherView):
    """
    提分榜
    """
    template_path = 'teacher/highscore/highscore.html'

    def get(self, request):
        context, teacher = self.getContextTeacher(request)
        highscore = None
        if teacher:
            highscores = models.Highscore.objects.filter(teacher=teacher)
        context = self.buildContextData(context, teacher)
        context["highscores"] = highscores
        return render(request, self.template_path, context)

    def buildContextData(self, context, teacher):
        return context

    def post(self, request):
        if request.POST.get('action') == 'delete':
            return self.doDelHighscore(request)

        if request.POST.get('action') == 'add':
            return self.addNewHighscore(request)

        context, teacher = self.getContextTeacher(request)
        highscore = None

        return render(request, self.template_path, context)

    """
    增加一行
    """
    def addNewHighscore(self, request):
        context, teacher = self.getContextTeacher(request)
        name = request.POST.get('name')
        increased_scores = request.POST.get('increased_scores')
        school_name = request.POST.get('school_name')
        admitted_to = request.POST.get('admitted_to')
        highscore = models.Highscore(teacher=teacher, name = name, increased_scores = increased_scores, school_name = school_name, admitted_to = admitted_to)
        highscore.save()
        return JsonResponse({'ok': True, 'msg': '', 'code': 0})

    """
    return message format: {'ok': False, 'msg': msg, 'code': 1}
    """
    def doDelHighscore(self, request):
        context, teacher = self.getContextTeacher(request)
        delIds = request.POST.get('ids')
        if not delIds:
            return JsonResponse({'ok': False, 'msg': '参数错误', 'code': 1})
        delIds = delIds.split(",");
        delIds = list(map(int, filter(lambda x:x, delIds)))
        try:
            delObjs = models.Highscore.objects.filter(id__in = delIds)
            allIsSelf = True
            for p in delObjs:
                if p.teacher.id != teacher.id:
                    allIsSelf = False
            if not allIsSelf:
                return JsonResponse({'ok': False, 'msg': '只能删除自己的记录', 'code': -1})

            ret = models.Highscore.objects.filter(id__in = delIds).delete()

            return JsonResponse({'ok': True, 'msg': '', 'code': 0})
        except Exception as err:
            logger.error(err)
            return JsonResponse({'ok': False, 'msg': '请求失败,请稍后重试,或联系管理员!', 'code': -1})

class BasicDocument(BaseTeacherView):
    """
    基本资料
    """
    template_path = 'teacher/doc/basic.html'

    def get(self, request):
        context, teacher = self.getContextTeacher(request)
        highscore = None
        if teacher:
            highscores = models.Highscore.objects.filter(teacher=teacher)
        context = self.buildContextData(context, teacher)
        context["highscores"] = highscores
        return render(request, self.template_path, context)

    def buildContextData(self, context, teacher):
        context["teacher"] = teacher
        return context


class AchievementView(BaseTeacherView):
    """
    特殊成果
    """
    template_path = 'teacher/achievement/achievement.html'
    edit_template_path = 'teacher/achievement/achievement_edit.html'

    def get(self, request, action=None, id=None):
        context, teacher = self.getContextTeacher(request)
        if action == 'add':
            context['achieve_title'] = '新建'
            return render(request, self.edit_template_path, context)
        if action == 'edit' and id:
            achievement = models.Achievement.objects.get(id=id, teacher=teacher)
            context['achieve_title'] = '修改'
            context['achieve'] = achievement
            return render(request, self.edit_template_path, context)
        # 返回列表页
        achievements = models.Achievement.objects.filter(teacher=teacher)
        context["achievements"] = achievements
        return render(request, self.template_path, context)

    def post(self, request, action=None, id=None):
        if action == 'add':
            return self.doSave(request, None)
        if action == 'edit':
            return self.doSave(request, id)
        if action == 'delete':
            return self.doDelete(request, id)
        return HttpResponse('', status=403)

    def doDelete(self, request, achieveId):
        context, teacher = self.getContextTeacher(request)
        if not achieveId:
            return JsonResponse({'ok': False, 'msg': '参数错误', 'code': 1})
        try:
            achievement = models.Achievement.objects.get(id=achieveId, teacher=teacher)
            achievement.delete()
            return JsonResponse({'ok': True, 'msg': '', 'code': 0})
        except models.Achievement.DoesNotExist as e:
            logger.warning(e)
            return JsonResponse({'ok': False, 'msg': '没有找到相应的记录', 'code': 2})
        except Exception as err:
            logger.error(err)
            return JsonResponse({'ok': False, 'msg': '请求失败,请稍后重试,或联系管理员!', 'code': -1})

    def doSave(self, request, id):
        title = request.POST.get('title')
        if not title:
            error_msg = '名称不能为空'
            return JsonResponse({'ok': False, 'msg': error_msg, 'code': 1})
        if len(title) > 10:
            error_msg = '名称不能超过10个字'
            return JsonResponse({'ok': False, 'msg': error_msg, 'code': 2})

        context, teacher = self.getContextTeacher(request)
        achievement = None
        if id:
            achievement = models.Achievement.objects.get(id=id, teacher=teacher)
        else:
            achievement = models.Achievement(teacher=teacher)

        achieveImgFile = None
        if request.FILES:
            achieveImgFile = request.FILES.get('achieveImg')
        if not achievement.img and not achieveImgFile:
                error_msg = '请选择图片'
                return JsonResponse({'ok': False, 'msg': error_msg, 'code': 4})

        achievement.title = title
        if achieveImgFile:
            img_content = ContentFile(achieveImgFile.read())
            achievement.img.save("achievement"+str(teacher.id)+'_'+str(img_content.size), img_content)

        achievement.save()
        return JsonResponse({'ok': True, 'msg': '', 'code': 0})
