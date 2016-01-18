import logging

# django modules
from django.core.files.base import ContentFile
from django.utils.decorators import method_decorator
from django.contrib.auth.decorators import user_passes_test
from django.http import HttpResponse, JsonResponse
from django.views.generic import View
from django.shortcuts import render, get_object_or_404

# local modules
from app import models

logger = logging.getLogger('app')

# Create your views here.


def register(request):
    """
    老师用户注册页面 TW-1-1
    :param request:
    :return:
    """
    context = {}
    return render(request, 'teacher/register.html', context)


def complete_information(request):
    """
    完善老师的个人信息 TW-2-1
    :param request:
    :return:
    """
    context = {}
    return render(request, 'teacher/complete_information.html', context)


def register_progress(request):
    """
    显示注册进度
    :param request:
    :return:
    """
    context = {}
    return render(request, "teacher/register_progress.html", context)


def first_page(request):
    """
    TW-4-1,通过面试的老师见到的第一个页面
    :param request:
    :return:
    """
    context = {}
    return render(request, "teacher/first_page.html", context)


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
        context['teacherName'] = teacher.name
        return context, teacher

class CertificateView(BaseTeacherView):
    """
    certifications overview
    """
    def get(self, request):
        context, teacher = self.getContextTeacher(request)
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
        context['idHeldUrl'] = certIdHeld.img and certIdHeld.img.url or ''
        context['idFrontUrl'] = certIdFront.img and certIdFront.img.url or ''
        return context

    def post(self, request):
        context, teacher = self.getContextTeacher(request)
        certIdHeld, created = models.Certificate.objects.get_or_create(teacher=teacher, type=models.Certificate.ID_HELD,
                                                              defaults={'name':"",'verified':False})
        certIdFront, created = models.Certificate.objects.get_or_create(teacher=teacher, type=models.Certificate.ID_FRONT,
                                                              defaults={'name':"",'verified':False})
        if certIdHeld.verified:
            context['error_msg'] = '已通过认证的不能更改'
            return render(request, self.template_path, self.buildContextData(context, certIdHeld, certIdFront))

        id_num = request.POST.get('id_num')
        if not id_num:
            context['error_msg'] = '身份证号不能为空'
            return render(request, self.template_path, self.buildContextData(context, certIdHeld, certIdFront))
        certIdHeld.name = id_num

        if request.FILES and len(request.FILES):
            idHeldImgFile = request.FILES.get('idHeldImg')
            if idHeldImgFile:
                held_img_content = ContentFile(request.FILES['idHeldImg'].read())
                # idHeldImg = Image.open(idHeldImgFile)
                certIdHeld.img.save("idHeld", held_img_content)
            idFrontImgFile = request.FILES.get('idFrontImg')
            if idFrontImgFile:
                front_img_content = ContentFile(request.FILES['idFrontImg'].read())
                # idHeldImg = Image.open(idHeldImgFile)
                certIdFront.img.save("idFrontImg", front_img_content)

        certIdHeld.save()
        certIdFront.save()

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
        context['certImgUrl'] = cert.img and cert.img.url or ''
        context['hint_content'] = self.hint_content
        return context

    def post(self, request):
        context, teacher = self.getContextTeacher(request)
        cert, created = models.Certificate.objects.get_or_create(teacher=teacher, type=self.cert_type,
                                                              defaults={'name':"",'verified':False})
        if cert.verified:
            context['error_msg'] = '已通过认证的不能更改'
            return render(request, self.template_path, self.buildContextData(context, cert))

        name = request.POST.get('name')
        if not name:
            context['error_msg'] = '证书名称不能为空'
            return render(request, self.template_path, self.buildContextData(context, cert))
        cert.name = name

        if request.FILES and len(request.FILES):
            idHeldImgFile = request.FILES.get('certImg')
            if idHeldImgFile:
                held_img_content = ContentFile(request.FILES['certImg'].read())
                cert.img.save("certImg"+str(self.cert_type)+str(cert.id), held_img_content)

        cert.save()

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
