import logging
import datetime
import json
from collections import OrderedDict
import xlwt

# django modules
from django.conf import settings
from django.core.files.base import ContentFile
from django.shortcuts import render, redirect, get_object_or_404
from django.http import HttpResponse, JsonResponse
from django.views.decorators.http import require_POST
from django.views.generic import View, TemplateView, ListView
from django.utils.decorators import method_decorator
from django.contrib import auth
from django.db import IntegrityError, transaction
from django.db.models import Q
from django.utils import timezone
from rest_framework.renderers import JSONRenderer

# local modules
from app import models
from app.utils import smsUtil
from app.utils.algorithm import check_id_number
from app.utils.types import parseInt, parse_date, parse_date_next
from app.utils.db import paginate, Pager
from app.utils import excel
from .decorators import mala_staff_required, is_manager
from app.exception import TimeSlotConflict, OrderStatusIncorrect, RefundError


logger = logging.getLogger('app')

# Create your views here.

@mala_staff_required
def index(request):
    return render(request, 'staff/index.html')

def login(request, context={}):
    if is_manager(request.user):
        return redirect('staff:index')
    return render(request, 'staff/login.html', context)

def logout(request):
    auth.logout(request)
    return redirect('staff:login')

@require_POST
def login_auth(request):
    username = request.POST.get('username')
    password = request.POST.get('password')
    goto_page = request.POST.get('next')
    logger.debug('try to login, username: '+username+', password: '+password+', goto_page: '+str(goto_page))
    # TODO: 错误信息包含‘错误码’，错误描述可能会变
    if not username or not password:
        return login(request, {'errors': '请输入用户名和密码'})
    #登录前需要先验证
    newUser=auth.authenticate(username=username,password=password)
    if newUser is not None:
        if not is_manager(newUser):
            return login(request, {'errors': '你不是管理员呀'})
        auth.login(request, newUser)
        if goto_page:
            return redirect(goto_page)
        else:
            return redirect('staff:index')
    return login(request, {'errors': '用户名或密码错误'})



class BaseStaffView(TemplateView):
    """
    Base view for staff management page views.
    """

    @method_decorator(mala_staff_required)
    def dispatch(self, request, *args, **kwargs):
        return super(BaseStaffView, self).dispatch(request, *args, **kwargs)


class BaseStaffActionView(View):
    """
    Base view for staff management action views.
    """

    defaultErrMeg = "操作失败,请稍后重试或联系管理员"

    # @method_decorator(csrf_exempt) # 不加csrf,不允许跨域访问
    @method_decorator(mala_staff_required)
    def dispatch(self, request, *args, **kwargs):
        return super(BaseStaffActionView, self).dispatch(request, *args, **kwargs)


def _try_send_sms(phone, tpl_id=0, params=None, times=1):
    """
    尝试发送短信
    :return: True or False
    """
    if not phone:
        return False
    if not tpl_id:
        return True
    if settings.FAKE_SMS_SERVER:
        return True
    ok = False
    while (not ok and times > 0):
        try:
            smsUtil.tpl_send_sms(phone, tpl_id, params)
            ok = True
        except Exception as ex:
            logger.error(ex)
        times -= 1
    return ok


#因为使用了listView而无法直接继承BaseStaffView
@method_decorator(mala_staff_required, name='dispatch')
class CouponsListView(ListView):
    model = models.Coupon
    template_name = 'staff/coupon/coupons_list.html'
    context_object_name = 'coupons_list'
    paginate_by = 10

    def get_context_data(self, **kwargs):
        context = super(CouponsListView, self).get_context_data(**kwargs)
        context['statusList'] = [
            {'text':"状态",'value':""},
            {'text':"已使用",'value':"used"},
            {'text':"未使用",'value':"unused"},
            {'text':"已过期",'value':"expired"},
                                ]
        context['typesList'] = [
            {'text':"类型",'value':""},
            {'text':"注册",'value':"reg"},
            {'text':"抽奖",'value':"lotto"},
                                ]
        context['name'] = self.request.GET.get('name', '')
        context['phone'] = self.request.GET.get('phone', '')
        context['dateFrom'] = self.request.GET.get('dateFrom', '')
        context['dateTo'] = self.request.GET.get('dateTo', '')
        context['type'] = self.request.GET.get('type', '')
        context['req_status'] = self.request.GET.get('status', '')
        page_obj = context.get('page_obj')
        if page_obj:
            paginator = page_obj.paginator
            context['pager'] = Pager(page_obj.number, paginator.num_pages, paginator.count, self.paginate_by)
        return context

    def get_queryset(self):
        coupons_list = self.model.objects.all()
        #TODO:用字典循环取值
        # for keyword in ['name','phone','dateFrom','dateTo','type','status']:
        #     cmd = "%s = self.request.GET.get('%s',None)" % (keyword,keyword)
        #     exec(cmd)
        #     print(name)
        name = self.request.GET.get('name',None)
        phone = self.request.GET.get('phone',None)
        dateFrom = self.request.GET.get('dateFrom',None)
        dateTo = self.request.GET.get('dateTo',None)
        type = self.request.GET.get('type',None)
        status = self.request.GET.get('status',None)

        if name:
            coupons_list = coupons_list.filter(parent__student_name__icontains=name)
        if phone:
            coupons_list = coupons_list.filter(parent__user__profile__phone__contains=phone)
        # if dateFrom:
        #     now = timezone.now()
        #     if not dateTo:
        #         dateTo = now
        #     print(dateFrom,dateTo)
        #     coupons_list = coupons_list.filter(created_at__range=(dateFrom, dateTo))
        if dateFrom:
            try:
                date_from = parse_date(dateFrom)
                coupons_list = coupons_list.filter(created_at__gte = date_from)
            except:
                pass
        if dateTo:
            try:
                date_to = parse_date_next(dateTo)
                coupons_list = coupons_list.filter(created_at__lt = date_to)
            except:
                pass
        if type == 'reg':
            pass
        if type == 'reg':
            pass
        if status == 'used':
            coupons_list = coupons_list.filter(used = True)
        if status == 'unused':
            now = timezone.now()
            coupons_list = coupons_list.filter(used = False).exclude(expired_at__lt = now)
        if status == 'expired':
            now = timezone.now()
            coupons_list = coupons_list.filter(used = False).filter(expired_at__lt = now)
        return coupons_list


class TeacherView(BaseStaffView):
    template_name = 'staff/teacher/teachers.html'

    def get_context_data(self, **kwargs):
        # 把查询参数数据放到kwargs['query_data'], 以便template回显
        kwargs['query_data'] = self.request.GET.dict()
        #
        name = self.request.GET.get('name')
        phone = self.request.GET.get('phone')
        status = self.request.GET.get('status')
        reg_date_from = self.request.GET.get('reg_date_from')
        reg_date_to = self.request.GET.get('reg_date_to')
        region = self.request.GET.get('region')
        page = self.request.GET.get('page')
        query_set = models.Teacher.objects.filter()
        if name:
            query_set = query_set.filter(name__icontains = name)
        if phone:
            query_set = query_set.filter(user__profile__phone__contains = phone)
        if status and status.isdigit():
            query_set = query_set.filter(status = status)
        if reg_date_from:
            try:
                date_from = parse_date(reg_date_from)
                query_set = query_set.filter(user__date_joined__gte = date_from)
            except:
                pass
        if reg_date_to:
            try:
                date_to = parse_date_next(reg_date_to)
                query_set = query_set.filter(user__date_joined__lt = date_to)
            except:
                pass
        if region and region.isdigit():
            query_set = query_set.filter(region_id = region)
        query_set = query_set.order_by('-user__date_joined')
        # paginate
        query_set, pager = paginate(query_set, page)
        kwargs['teachers'] = query_set
        kwargs['pager'] = pager
        # 一些固定数据
        kwargs['status_choices'] = models.Teacher.STATUS_CHOICES
        kwargs['region_list'] = models.Region.objects.filter(Q(opened=True)|Q(name='其他'))
        return super(TeacherView, self).get_context_data(**kwargs)


class TeacherUnpublishedView(BaseStaffView):
    """
    待上架老师列表view
    """
    template_name = 'staff/teacher/teachers_unpublished.html'
    list_type = 'unpublished'

    def get_context_data(self, **kwargs):
        kwargs['list_type'] = self.list_type
        # 把查询参数数据放到kwargs['query_data'], 以便template回显
        kwargs['query_data'] = self.request.GET.dict()
        #
        name = self.request.GET.get('name') and self.request.GET.get('name').strip() or ''
        phone = self.request.GET.get('phone') and self.request.GET.get('phone').strip() or ''
        # province = self.request.GET.get('province')
        # city = self.request.GET.get('city')
        # district = self.request.GET.get('district')
        # region = district and district or city and city or province
        region = self.request.GET.get('region')
        grade = self.request.GET.get('grade') and self.request.GET.get('grade').strip() or ''
        subject = self.request.GET.get('subject') and self.request.GET.get('subject').strip() or ''
        level = self.request.GET.get('level') and self.request.GET.get('level').strip() or ''
        page = self.request.GET.get('page') and self.request.GET.get('page').strip() or ''
        for_published = self.list_type == 'published'
        query_set = models.Teacher.objects.filter(status=models.Teacher.INTERVIEW_OK, published=for_published)
        if name:
            query_set = query_set.filter(name__icontains = name)
        if phone:
            query_set = query_set.filter(user__profile__phone__contains = phone)
        if region:
            query_set = query_set.filter(region_id=region)
        if grade:
            query_set = query_set.filter(Q(abilities__grade_id=grade) | Q(abilities__grade__superset_id=grade))
        if subject:
            query_set = query_set.filter(abilities__subject_id=subject)
        if level:
            query_set = query_set.filter(level_id=level)
        query_set = query_set.order_by('id')
        # paginate
        query_set, pager = paginate(query_set, page, 15)
        kwargs['teachers'] = query_set
        kwargs['pager'] = pager
        # 一些固定数据
        # kwargs['provinces'] = models.Region.objects.filter(superset_id__isnull=True)
        kwargs['region_list'] = models.Region.objects.filter(Q(opened=True)|Q(name='其他'))
        kwargs['grades'] = models.Grade.objects.filter(superset_id__isnull=True)
        kwargs['subjects'] = models.Subject.objects.all
        kwargs['levels'] = models.Level.objects.all
        return super(TeacherUnpublishedView, self).get_context_data(**kwargs)


class TeacherPublishedView(TeacherUnpublishedView):
    """
    已上架老师列表view
    """
    list_type = 'published'
    def get_context_data(self, **kwargs):
        return super(TeacherPublishedView, self).get_context_data(**kwargs)


class TeacherUnpublishedEditView(BaseStaffView):
    """
    待上架老师编辑页面view
    """
    template_name = 'staff/teacher/teachers_unpublished_edit.html'

    def get_context_data(self, **kwargs):
        teacherId = kwargs['tid']
        teacher = get_object_or_404(models.Teacher, id=teacherId)
        kwargs['teacher'] = teacher
        # 老师科目年级
        curSubject = teacher.subject()
        if curSubject:
            kwargs['grade_ids_range'] = models.Ability.objects.filter(subject=curSubject).values_list('grade_id', flat=True)
        kwargs['teacher_grade_ids'] = [grade.id for grade in teacher.grades()]
        # 证书数据
        certification_all = models.Certificate.objects.filter(teacher=teacher)
        cert_others = []
        for cert in certification_all:
            if cert.type == models.Certificate.ID_HELD:
                kwargs['cert_id_held'] = cert
            elif cert.type == models.Certificate.ID_FRONT:
                kwargs['cert_id_front'] = cert
            elif cert.type == models.Certificate.ACADEMIC:
                kwargs['cert_academic'] = cert
            elif cert.type == models.Certificate.TEACHING:
                kwargs['cert_teaching'] = cert
            elif cert.type == models.Certificate.ENGLISH:
                kwargs['cert_english'] = cert
            else:
                cert_others.append(cert)
        kwargs['cert_others'] = cert_others
        # 地区数据
        region_dict = teacher.region and teacher.region.make_dict() or None
        kwargs['region_dict'] = region_dict
        kwargs['provinces'] = models.Region.objects.filter(superset_id__isnull=True)
        if region_dict and region_dict.get('city'):
            kwargs['cities'] = models.Region.objects.filter(superset_id=region_dict.get('city').superset_id)
        if region_dict and region_dict.get('district'):
            kwargs['districts'] = models.Region.objects.filter(superset_id=region_dict.get('district').superset_id)
        # 一些固定数据
        kwargs['gender_choices'] = models.Profile.GENDER_CHOICES
        kwargs['subjects'] = models.Subject.objects.all
        kwargs['levels'] = models.Level.objects.all
        grades_all = models.Grade.objects.all().order_by('-superset_id')
        _heap = {}
        grades_tree = []
        for grade in grades_all:
            if not grade.superset_id:
                _temp = {'id':grade.id, 'name':grade.name, 'children':[]}
                _heap[grade.id] = _temp
                grades_tree.append(_temp)
            else:
                _temp = _heap[grade.superset_id]
                _temp['children'].append({'id':grade.id, 'name':grade.name})
        kwargs['grades_tree'] = grades_tree
        kwargs['tags_all'] = models.Tag.objects.all
        return super(TeacherUnpublishedEditView, self).get_context_data(**kwargs)

    def post(self, request, tid):
        teacher = get_object_or_404(models.Teacher, id=tid)
        try:
            # 获取参数, 并检验
            newSubjectId = parseInt(request.POST.get('subject'), False)
            if not newSubjectId:
                return JsonResponse({'ok': False, 'msg': '请选择科目', 'code': 1})
            newGradeIds = request.POST.getlist('grade')
            if not newGradeIds:
                return JsonResponse({'ok': False, 'msg': '请选择年级', 'code': -1})
            newTagIds  = request.POST.getlist('tag')
            if not newTagIds or len(newTagIds)>3:
                return JsonResponse({'ok': False, 'msg': '风格标记 (最少选一个，最多选3个)', 'code': -1})
            certIdHeldOk = request.POST.get('certIdHeldOk')
            id_num = request.POST.get('id_num')
            if certIdHeldOk and certIdHeldOk=='True':
                if not check_id_number(id_num):
                    return JsonResponse({'ok': False, 'msg': '身份认证失败, 身份证号不合法', 'code': -1})

            certIdHeld, created = models.Certificate.objects.get_or_create(teacher=teacher, type=models.Certificate.ID_HELD,
                                                                  defaults={'name':"",'verified':False})
            profile = teacher.user.profile
            # 基本信息
            teacher.name = request.POST.get('name')
            certIdHeld.name = id_num
            profile.phone = request.POST.get('phone')
            profile.gender = request.POST.get('gender')
            province = request.POST.get('province')
            city = request.POST.get('city')
            district = request.POST.get('district')
            region = district and district or city and city or province
            region = parseInt(region, None)
            if not region:
                teacher.region = None
            else:
                teacher.region_id = region
            teacher.teaching_age = parseInt(request.POST.get('teaching_age'), 0)
            # 更改成带有日志的模式
            new_level = models.Level.objects.get(pk=parseInt(request.POST.get('level'), 1))
            teacher.set_level(new_level)

            # teacher.level_id = parseInt(request.POST.get('level'), 1)

            teacher.experience = parseInt(request.POST.get('experience'), 0)
            teacher.profession = parseInt(request.POST.get('profession'), 0)
            teacher.interaction = parseInt(request.POST.get('interaction'), 0)
            certIdHeld.save()
            # 科目年级 & 风格标签
            teacher.abilities.clear()
            ability_set = models.Ability.objects.filter(subject_id=newSubjectId, grade_id__in=newGradeIds)
            for ability in ability_set:
                teacher.abilities.add(ability)
            teacher.tags.clear()
            tag_set = models.Tag.objects.filter(id__in=newTagIds)
            for tag in tag_set:
                teacher.tags.add(tag)
            teacher.save()
            # 头像 & 照片
            avatarImg = None
            if request.FILES:
                avatarImg = request.FILES.get('avatarImg')
            if avatarImg:
                _img_content = ContentFile(avatarImg.read())
                profile.avatar.save("avatar"+str(teacher.id)+'_'+str(_img_content.size), _img_content)
            else:
                if request.POST.get('toDeleteAvatar'):
                    profile.avatar.delete()
            profile.save()
            stayPhotoIds = request.POST.getlist('photoId')
            stayPhotoIds = [i for i in stayPhotoIds if i]
            newPhotoImgs = request.FILES.getlist('photoImg')
            models.Photo.objects.filter(teacher_id=teacher.id).exclude(id__in=stayPhotoIds).delete()
            for photoImg in newPhotoImgs:
                photo = models.Photo(teacher=teacher, public=True)
                _img_content = ContentFile(photoImg.read())
                photo.img.save("photo"+str(teacher.id)+'_'+str(_img_content.size), _img_content)
                photo.save()
            # 提分榜
            allHsIds = request.POST.getlist('highscoreId')
            stayHsIds = [s for s in allHsIds if s and (not s.startswith('new'))]
            newHsIds = [s for s in allHsIds if s.startswith('new')]
            models.Highscore.objects.filter(teacher_id=teacher.id).exclude(id__in=stayHsIds).delete()
            for hsId in newHsIds:
                name = request.POST.get(hsId+'name')
                scores = request.POST.get(hsId+'scores')
                school_from = request.POST.get(hsId+'from')
                school_to = request.POST.get(hsId+'to')
                highscore = models.Highscore(teacher=teacher, name=name, increased_scores=scores,
                                             school_name=school_from, admitted_to=school_to)
                highscore.save()
            ### 认证
            # 身份认证
            oldCertIdVerify = certIdHeld.verified
            if certIdHeldOk and certIdHeldOk=='True':
                wasVerified = certIdHeld.verified
                certIdHeld.verified = True
                if not wasVerified:
                    certIdHeld.show_hint = True
            elif certIdHeldOk and certIdHeldOk=='Fail':
                wasFail = certIdHeld.audited and not certIdHeld.verified
                certIdHeld.audited = True
                certIdHeld.verified = False
                if not wasFail:
                    certIdHeld.show_hint = True
            else:
                certIdHeld.verified = False
            certIdHeldImg = None
            if request.FILES:
                certIdHeldImg = request.FILES.get('certIdHeldImg')
            if certIdHeldImg:
                _img_content = ContentFile(certIdHeldImg.read())
                certIdHeld.img.save("idHeld"+str(teacher.id)+'_'+str(_img_content.size), _img_content)
            else:
                if request.POST.get('toDeleteCertIdHeld'):
                    certIdHeld.img.delete()
            if not certIdHeld.img:
                certIdHeld.verified = False
            certIdFront, created = models.Certificate.objects.get_or_create(teacher=teacher, type=models.Certificate.ID_FRONT,
                                                              defaults={'name':"",'verified':False})
            certIdFrontImg = None
            if request.FILES:
                certIdFrontImg = request.FILES.get('certIdFrontImg')
            if certIdFrontImg:
                _img_content = ContentFile(certIdFrontImg.read())
                certIdFront.img.save("IdFront"+str(teacher.id)+'_'+str(_img_content.size), _img_content)
            else:
                if request.POST.get('toDeleteCertIdFront'):
                    certIdFront.img.delete()
            if not certIdFront.img:
                certIdHeld.verified = False
            certIdFront.save()
            certIdHeld.save()
            if oldCertIdVerify != certIdHeld.verified:
                # send notice (sms) to teacher, when verified value is changed
                self._send_cert_sms_notify('身份认证', certIdHeld.verified, profile.phone)
            # 学历, 教师资格证,英语水平
            self.postSaveCert(request, teacher, models.Certificate.ACADEMIC, 'Academic')
            self.postSaveCert(request, teacher, models.Certificate.TEACHING, 'Teaching')
            self.postSaveCert(request, teacher, models.Certificate.ENGLISH, 'English')
            # 其他认证
            allCertOtherIds = request.POST.getlist('certOtherId')
            stayCertOtherIds = [s for s in allCertOtherIds if s and (not s.startswith('new'))]
            newCertOtherIds = [s for s in allCertOtherIds if s.startswith('new')]
            models.Certificate.objects.filter(teacher=teacher, type=models.Certificate.OTHER)\
                .exclude(id__in=stayCertOtherIds).delete()
            for certId in stayCertOtherIds:
                name = request.POST.get(certId+'certName')
                certOk = request.POST.get(certId+'certOk')
                certImg = None
                if request.FILES:
                    certImg = request.FILES.get(certId+'certImg')
                cert = models.Certificate.objects.get(id=certId)
                oldCertVerify = cert.verified
                cert.name = name
                if certOk and certOk=='True':
                    cert.verified = True
                elif certOk and certOk=='Fail':
                    cert.audited = True
                    cert.verified = False
                else:
                    cert.verified = False
                if certImg:
                    _img_content = ContentFile(certImg.read())
                    cert.img.save("certOther"+str(teacher.id)+'_'+str(_img_content.size), _img_content)
                cert.save()
                if oldCertVerify != cert.verified:
                    # send notice (sms) to teacher, when verified value is changed
                    self._send_cert_sms_notify('其他证书"'+cert.name+'"', cert.verified, profile.phone)
            for certId in newCertOtherIds:
                name = request.POST.get(certId+'certName')
                certOk = request.POST.get(certId+'certOk')
                certImg = None
                if request.FILES:
                    certImg = request.FILES.get(certId+'certImg')
                if not certImg:
                    continue
                newCert = models.Certificate(teacher=teacher,name=name,type=models.Certificate.OTHER,verified=False)
                if certOk and certOk=='True':
                    newCert.verified = True
                elif certOk and certOk=='Fail':
                    newCert.audited = True
                    newCert.verified = False
                _img_content = ContentFile(certImg.read())
                newCert.img.save("certOther"+str(teacher.id)+'_'+str(_img_content.size), _img_content)
                newCert.save()
                if newCert.verified:
                    # send notice (sms) to teacher, when verified value is changed
                    self._send_cert_sms_notify('其他证书"'+newCert.name+'"', newCert.verified, profile.phone)
            # 介绍语音, 介绍视频
            introAudio = None
            if request.FILES:
                introAudio =  request.FILES.get('introAudio')
            if introAudio:
                _tmp_content = ContentFile(introAudio.read())
                teacher.audio.save('introAudio'+str(teacher.id)+'_'+str(_tmp_content.size), _tmp_content)
            else:
                if request.POST.get('toDeleteAudio'):
                    teacher.audio.delete()
            introVideo = None
            if request.FILES:
                introVideo =  request.FILES.get('introVideo')
            if introVideo:
                _tmp_content = ContentFile(introVideo.read())
                teacher.video.save('introVideo'+str(teacher.id)+'_'+str(_tmp_content.size), _tmp_content)
            else:
                if request.POST.get('toDeleteVideo'):
                    teacher.video.delete()
            teacher.save()
            # 教学成果
            allAchieveIds = request.POST.getlist('achieveId')
            stayAchieveIds = [s for s in allAchieveIds if s and (not s.startswith('new'))]
            newAchieveIds = [s for s in allAchieveIds if s.startswith('new')]
            models.Achievement.objects.filter(teacher=teacher).exclude(id__in=stayAchieveIds).delete()
            for achId in stayAchieveIds:
                title = request.POST.get(achId+'achieveName')
                achieveImg = None
                if request.FILES:
                    achieveImg = request.FILES.get(achId+'achieveImg')
                achievement = models.Achievement.objects.get(id=achId)
                achievement.title = title
                if achieveImg:
                    _img_content = ContentFile(achieveImg.read())
                    achievement.img.save("achievement"+str(teacher.id)+'_'+str(_img_content.size), _img_content)
                achievement.save()
            for achId in newAchieveIds:
                title = request.POST.get(achId+'achieveName')
                achieveImg = None
                if request.FILES:
                    achieveImg = request.FILES.get(achId+'achieveImg')
                if not title or not achieveImg:
                    continue
                newAch = models.Achievement(teacher=teacher,title=title)
                _img_content = ContentFile(achieveImg.read())
                newAch.img.save("achievement"+str(teacher.id)+'_'+str(_img_content.size), _img_content)
                newAch.save()
        except Exception as ex:
            logger.error(ex)
            return JsonResponse({'ok': False, 'msg': BaseStaffActionView.defaultErrMeg, 'code': -1})
        return JsonResponse({'ok': True, 'msg': '', 'code': 0})

    def postSaveCert(self, request, teacher, type_code, type_str, cert=None):
        if not cert:
            cert, created = models.Certificate.objects.get_or_create(teacher=teacher, type=type_code,
                                                                     defaults={'name':"",'verified':False})
        oldCertVerify = cert.verified
        certOk = request.POST.get('cert'+type_str+'Ok')
        if certOk and certOk=='True':
            wasVerified = cert.verified
            cert.verified = True
            if not wasVerified:
                cert.show_hint = True
        elif certOk and certOk=='Fail':
            wasFail = cert.audited and not cert.verified
            cert.audited = True
            cert.verified = False
            if not wasFail:
                cert.show_hint = True
        else:
            cert.verified = False
        certImg = None
        if request.FILES:
            certImg = request.FILES.get('cert'+type_str+'Img')
        if certImg:
            _img_content = ContentFile(certImg.read())
            cert.img.save(type_str+str(teacher.id)+'_'+str(_img_content.size), _img_content)
        else:
            if request.POST.get('toDeleteCert'+type_str):
                cert.img.delete()
        if not cert.img:
            cert.verified = False
        cert.save()
        cert_name = cert.get_type_display()
        if oldCertVerify != cert.verified:
            # send notice (sms) to teacher, when verified value is changed
            self._send_cert_sms_notify(cert_name, cert.verified, teacher.user.profile.phone)

    def _send_cert_sms_notify(self, cert_name, new_status, phone):
        # TODO:
        pass

class TeacherBankcardView(BaseStaffView):
    template_name = 'staff/teacher/teacher_bankcard_list.html'

    def get_context_data(self, **kwargs):
        # 把查询参数数据放到kwargs['query_data'], 以便template回显
        query_data = {}
        query_data['name'] = self.request.GET.get('name', '')
        query_data['phone'] = self.request.GET.get('phone', '')
        kwargs['query_data'] = query_data
        #
        name = self.request.GET.get('name')
        phone = self.request.GET.get('phone')
        page = self.request.GET.get('page')
        query_set = models.BankCard.objects.select_related('account__user__teacher', 'account__user__profile').filter(account__user__teacher__isnull=False)
        if name:
            query_set = query_set.filter(account__user__teacher__name__contains = name)
        if phone:
            query_set = query_set.filter(account__user__profile__phone__contains = phone)
        query_set = query_set.order_by('account__user__teacher__name')
        # paginate
        query_set, pager = paginate(query_set, page)
        kwargs['bankcards'] = query_set
        kwargs['pager'] = pager
        return super(TeacherBankcardView, self).get_context_data(**kwargs)


class TeacherIncomeView(BaseStaffView):
    template_name = 'staff/teacher/teacher_income_list.html'

    def get_context_data(self, **kwargs):
        # 把查询参数数据放到kwargs['query_data'], 以便template回显
        query_data = {}
        query_data['name'] = self.request.GET.get('name', '')
        query_data['phone'] = self.request.GET.get('phone', '')
        kwargs['query_data'] = query_data
        #
        name = self.request.GET.get('name')
        phone = self.request.GET.get('phone')
        page = self.request.GET.get('page')
        query_set = models.Account.objects.select_related('user__teacher', 'user__profile').filter(user__teacher__isnull=False)
        if name:
            query_set = query_set.filter(user__teacher__name__contains = name)
        if phone:
            query_set = query_set.filter(user__profile__phone__contains = phone)
        query_set = query_set.order_by('user__teacher__name')
        # paginate
        query_set, pager = paginate(query_set, page)
        kwargs['accounts'] = query_set
        kwargs['pager'] = pager
        return super(TeacherIncomeView, self).get_context_data(**kwargs)

    def get(self, request, *args, **kwargs):
        export = request.GET.get('export')
        if export == 'true':
            name = self.request.GET.get('name')
            phone = self.request.GET.get('phone')
            query_set = models.Account.objects.select_related('user__teacher', 'user__profile').filter(user__teacher__isnull=False)
            if name:
                query_set = query_set.filter(user__teacher__name__contains = name)
            if phone:
                query_set = query_set.filter(user__profile__phone__contains = phone)
            query_set = query_set.order_by('user__teacher__name')
            headers = ('老师姓名', '手机号', '授课年级', '科目', '所在地区', '账户余额', '可提现金额', '累计收入',)
            columns = ('user.teacher.name',
                       'user.profile.phone',
                       'user.teacher.grades',
                       'user.teacher.subject',
                       'user.teacher.region.full_name',
                       lambda x: (x.calculated_balance/100),
                       lambda x: (x.calculated_balance/100),
                       lambda x: (x.accumulated_income/100),
                       )
            return excel.excel_response(query_set, columns, headers, '老师收入列表.xls')
        return super(TeacherIncomeView, self).get(request, *args, **kwargs)


class TeacherIncomeDetailView(BaseStaffView):
    """
    某个老师收入明细
    """
    template_name = 'staff/teacher/teacher_income_detail.html'

    def get_context_data(self, **kwargs):
        teacherId = kwargs['tid']
        teacher = get_object_or_404(models.Teacher, id=teacherId)
        kwargs['teacher'] = teacher
        query_data = {}
        query_data['date_from'] = self.request.GET.get('date_from', '')
        query_data['date_to'] = self.request.GET.get('date_to', '')
        query_data['order_id'] = self.request.GET.get('order_id', '')
        kwargs['query_data'] = query_data
        #
        date_from = self.request.GET.get('date_from', '')
        date_to = self.request.GET.get('date_to', '')
        order_id = self.request.GET.get('order_id', '')
        page = self.request.GET.get('page')
        account = teacher.safe_get_account()
        query_set = models.AccountHistory.objects.select_related('timeslot__order').filter(account=account, amount__gt=0)
        if date_from:
            try:
                date_from = parse_date(date_from)
                query_set = query_set.filter(submit_time__gte = date_from)
            except:
                pass
        if date_to:
            try:
                date_to = parse_date_next(date_to)
                query_set = query_set.filter(submit_time__lt = date_to)
            except:
                pass
        if order_id:
            query_set = query_set.filter(timeslot__order__order_id__icontains=order_id)
        query_set = query_set.order_by('-submit_time')
        # paginate
        query_set, pager = paginate(query_set, page)
        histories = self.arrange_by_day(query_set, account, order_id)
        kwargs['histories'] = histories
        kwargs['pager'] = pager
        return super(TeacherIncomeDetailView, self).get_context_data(**kwargs)

    def arrange_by_day(self, query_set, account, order_id):
        if len(query_set) == 0:
            return []
        min_time = None
        max_time = None
        for hist in query_set:
            the_time = hist.submit_time
            if min_time is None or min_time > the_time:
                min_time = the_time
            if max_time is None or max_time < the_time:
                max_time = the_time
        # query_set数据是分页已经被分页过的, 根据query_set的时间范围, 再次从数据库查询记录, 防止某天记录跨页情况
        date_from = min_time.replace(hour=0, minute=0, second=0, microsecond=0)
        date_to = max_time.replace(hour=0, minute=0, second=0, microsecond=0) + datetime.timedelta(days=1)
        new_query_set = models.AccountHistory.objects.select_related('timeslot__order')\
            .filter(account=account, amount__gt=0, submit_time__gte = date_from, submit_time__lt = date_to)
        if order_id:
            new_query_set = new_query_set.filter(timeslot__order__order_id__icontains=order_id)
        new_query_set = new_query_set.order_by('-submit_time')
        day_income_dict = {}
        for hist in new_query_set:
            the_day = hist.submit_time.replace(hour=0, minute=0, second=0, microsecond=0)
            day_income = day_income_dict.get(the_day)
            if day_income is None:
                day_income = hist.amount
            else:
                day_income += hist.amount
            day_income_dict[the_day] = day_income
        # 重新组织原来的query_set数据
        day_group = {}
        for hist in query_set:
            the_day = hist.submit_time.replace(hour=0, minute=0, second=0, microsecond=0)
            day_obj = day_group.get(the_day)
            if day_obj is None:
                day_obj = {}
                day_obj['count'] = 1
                day_obj['income'] = day_income_dict.get(the_day)
                day_obj['records'] = [hist]
                day_group[the_day] = day_obj
            else:
                day_obj['count'] += 1
                day_obj['records'].append(hist)
        histories = []
        for day, obj in day_group.items():
            obj['day'] = day
            histories.append(obj)
        histories.sort(key=lambda x: x['day'], reverse=True)
        return histories

    def _excel_of_histories(self, histories, filename):
        headers = ('日期', '订单号', '上课年级', '科目', '教师级别', '上课时间', '课时单价', '消耗课时', '佣金比例', '收入金额', '当日收入',)
        columns = (
            lambda x: x.timeslot and x.timeslot.order.order_id or (x.comment or '非课时收入'),
            'timeslot.order.grade',
            'timeslot.order.subject',
            'timeslot.order.level',
            lambda x: x.timeslot and ('%s-%s' % (x.timeslot.start.strftime('%H:%M'), x.timeslot.end.strftime('%H:%M'),)) or '',
            lambda x: x.timeslot and (x.timeslot.order.price / 100) or '',
            lambda x: x.timeslot and x.timeslot.duration_hours() or '',
            lambda x: x.timeslot and ('%s%%' % (x.timeslot.order.commission_percentage),) or '',
            lambda x: x.amount and (x.amount / 100) or '',
        )
        workbook = xlwt.Workbook()
        sheet_name = 'Export {0}'.format(datetime.date.today().strftime('%Y-%m-%d'))
        sheet = workbook.add_sheet(sheet_name)
        for y, th in enumerate(headers):
            sheet.write(0, y, th, excel.HEADER_STYLE)
        x = 1
        for history in histories:
            y = 0
            day_val = history['day'].date()
            sheet.write_merge(x, x + history['count'] - 1, y, y, day_val, excel.get_style_by_value(day_val))
            x_sub = x
            records = history['records']
            for record in records:
                y_sub = y+1
                for column in columns:
                    value = callable(column) and column(record) or excel.get_column_cell(record, column)
                    sheet.write(x_sub, y_sub, value, excel.get_style_by_value(value))
                    y_sub += 1
                x_sub += 1
            income = history['income'] / 100
            y += len(columns) + 1
            sheet.write_merge(x, x + history['count'] - 1, y, y, income, excel.get_style_by_value(income))
            x += len(records)
        return excel.wb_excel_response(workbook, filename)

    def get(self, request, *args, **kwargs):
        export = request.GET.get('export')
        if export == 'true':
            teacherId = kwargs['tid']
            teacher = get_object_or_404(models.Teacher, id=teacherId)
            #
            date_from = self.request.GET.get('date_from', '')
            date_to = self.request.GET.get('date_to', '')
            order_id = self.request.GET.get('order_id', '')
            account = teacher.safe_get_account()
            query_set = models.AccountHistory.objects.select_related('timeslot__order').filter(account=account, amount__gt=0)
            if date_from:
                try:
                    date_from = parse_date(date_from)
                    query_set = query_set.filter(submit_time__gte = date_from)
                except:
                    pass
            if date_to:
                try:
                    date_to = parse_date_next(date_to)
                    query_set = query_set.filter(submit_time__lt = date_to)
                except:
                    pass
            if order_id:
                query_set = query_set.filter(timeslot__order__order_id__icontains=order_id)
            query_set = query_set.order_by('-submit_time')
            histories = self.arrange_by_day(query_set, account, order_id)
            return self._excel_of_histories(histories, teacher.name+'老师收入明细列表.xls')
        return super(TeacherIncomeDetailView, self).get(request, *args, **kwargs)


class TeacherWithdrawalView(BaseStaffView):
    template_name = 'staff/teacher/teacher_withdrawal_list.html'

    def _get_query_set(self):
        date_from = self.request.GET.get('date_from')
        date_to = self.request.GET.get('date_to')
        status = self.request.GET.get('status')
        name = self.request.GET.get('name')
        phone = self.request.GET.get('phone')
        query_set = models.AccountHistory.objects.select_related('account__user__teacher', 'account__user__profile')\
            .filter(account__user__teacher__isnull=False, withdrawal__isnull=False)
        if date_from:
            try:
                date_from = parse_date(date_from)
                query_set = query_set.filter(submit_time__gte = date_from)
            except:
                pass
        if date_to:
            try:
                date_to = parse_date_next(date_to)
                query_set = query_set.filter(submit_time__lt = date_to)
            except:
                pass
        if status:
            query_set = query_set.filter(withdrawal__status = status)
        if name:
            query_set = query_set.filter(account__user__teacher__name__contains = name)
        if phone:
            query_set = query_set.filter(account__user__profile__phone__contains = phone)
        return query_set.order_by('-submit_time')

    def get_context_data(self, **kwargs):
        # 把查询参数数据放到kwargs['query_data'], 以便template回显
        query_data = {}
        query_data['name'] = self.request.GET.get('name', '')
        query_data['phone'] = self.request.GET.get('phone', '')
        query_data['date_from'] = self.request.GET.get('date_from')
        query_data['date_to'] = self.request.GET.get('date_to')
        query_data['status'] = self.request.GET.get('status')
        kwargs['query_data'] = query_data
        #
        query_set = self._get_query_set()
        # paginate
        page = self.request.GET.get('page')
        query_set, pager = paginate(query_set, page)
        kwargs['withdrawals'] = query_set
        kwargs['pager'] = pager
        # 一些固定数据
        kwargs['status_choices'] = models.Withdrawal.STATUS_CHOICES
        return super(TeacherWithdrawalView, self).get_context_data(**kwargs)

    def get(self, request, *args, **kwargs):
        export = request.GET.get('export')
        if export == 'true':
            query_set = self._get_query_set()
            headers = ('提现申请时间', '姓名', '手机号', '提现金额', '所在银行', '银行卡号', '开户行', '状态',)
            columns = (lambda x: timezone.make_naive(x.submit_time),
                       'account.user.teacher.name',
                       'account.user.profile.phone',
                       lambda x: (x.abs_amount/100),
                       'withdrawal.bankcard.bank_name',
                       'withdrawal.bankcard.card_number',
                       'withdrawal.bankcard.opening_bank',
                       'withdrawal.get_status_display',
                       )
            return excel.excel_response(query_set, columns, headers, '老师提现审核列表.xls')
        return super(TeacherWithdrawalView, self).get(request, *args, **kwargs)

    def post(self, request):
        action = self.request.POST.get('action')
        wid = self.request.POST.get('wid')
        if not wid:
            return JsonResponse({'ok': False, 'msg': '参数错误', 'code': 1})
        if action == 'approve':
            return self.approve_withdraw(request, wid)
        return HttpResponse("Not supported request.", status=403)

    def approve_withdraw(self, request, ahid):
        ok = False
        try:
            with transaction.atomic():
                ah = models.AccountHistory.objects.get(id=ahid)
                if not ah.withdrawal.is_pending():
                    return JsonResponse({'ok': False, 'msg': '已经被审核过了', 'code': -1})
                ah.audit_withdrawal(True, request.user)
                ok = True
        except IntegrityError as err:
            logger.error(err)
            return JsonResponse({'ok': False, 'msg': '操作失败, 请稍后重试或联系管理员', 'code': -1})
        if ok:
            # 短信通知老师
            teacher = ah.account.user.teacher
            _try_send_sms(teacher.user.profile.phone, smsUtil.TPL_WITHDRAW_APPROVE, {'username':teacher.name}, 2)
        return JsonResponse({'ok': True, 'msg': 'OK', 'code': 0})


class TeacherActionView(BaseStaffActionView):

    NO_TEACHER_FORMAT = "没有查到老师, ID={id}"

    def get(self, request):
        action = self.request.GET.get('action')
        if action == 'list-highscore':
            return self.getTeacherHighscore(request)
        if action == 'list-achievement':
            return self.getTeacherAchievement(request)
        if action == 'get-weekly-schedule':
            return self.getTeacherWeeklySchedule(request)
        if action == 'get-course-schedule':
            return self.getTeacherCourseSchedule(request)
        if action == 'get-subject-grades-range':
            return self.getGradesRangeOfSubject(request)
        return HttpResponse("", status=404)

    def post(self, request):
        action = self.request.POST.get('action')
        logger.debug("try to modify teacher, action = " + action)
        if action == 'donot-choose':
            return self.updateTeacherStatus(request, models.Teacher.NOT_CHOSEN)
        if action == 'invite-interview':
            return self.updateTeacherStatus(request, models.Teacher.TO_INTERVIEW)
        if action == 'set-interview-ok':
            return self.updateTeacherStatus(request, models.Teacher.INTERVIEW_OK)
        if action == 'set-interview-fail':
            return self.updateTeacherStatus(request, models.Teacher.INTERVIEW_FAIL)
        if action == 'publish-teacher':
            return self.publishTeacher(request);
        return HttpResponse("Not supported request.", status=403)

    def publishTeacher(self, request):
        tid = request.POST.get('tid')
        flag = request.POST.get('flag')
        if not tid or not flag in ['true', 'false']:
            return JsonResponse({'ok': False, 'msg': '参数错误', 'code': 1})
        try:
            teacher = models.Teacher.objects.get(id=tid)
            teacher.published = (flag == 'true')

            # 判断是否可以上架 BE-71
            errmsg = ""
            if teacher.published:
                certification_all = models.Certificate.objects.filter(teacher=teacher)
                for cert in certification_all:
                    if cert.type == models.Certificate.ID_HELD:
                        if cert.name is None or cert.name == '':
                            errmsg += '身份证不能为空，'
                        if not cert.verified:
                            errmsg += '身份证照认证未通过，'
                    elif cert.type == models.Certificate.ACADEMIC:
                        if not cert.verified:
                            errmsg += '学历证认证未通过，'
                    elif cert.type == models.Certificate.TEACHING:  #教师资格
                        pass
                    elif cert.type == models.Certificate.ENGLISH:   #英语水平认证
                        pass

                if teacher.name is None or teacher.name == '':
                    errmsg += '老师姓名不能为空，'

                profile = models.Profile.objects.get(user=teacher.user)
                if profile.phone is None or profile.phone == '':
                    errmsg += '手机号未填写，'
                if profile.gender not in [models.Profile.MALE, models.Profile.FEMALE]:
                    errmsg += '性别没有，'
                if not profile.avatar or profile.avatar.url is None or profile.avatar.url == '':
                    errmsg += '头像没有，'
                if not teacher.region or not teacher.region.id:
                    errmsg += '所在地区不能为空，'
                if not teacher.level or not teacher.level.id:
                    errmsg += '教师级别不能为空，'
                if teacher.subject() is None:
                    errmsg += '教授科目不能为空，'
                if teacher.grades() is None or len(teacher.grades()) == 0:
                    errmsg += '年级至少选择一个，'
                if teacher.tags.all() is None or len(teacher.tags.all()) == 0:
                    errmsg += '风格标记至少选择一个，'

                if len(errmsg) > 0:
                    return JsonResponse({'ok': False, 'msg': errmsg, 'code': -1})

            teacher.save()
            # send notice (sms) to teacher
            phone = teacher.user.profile.phone
            if phone:
                sms_tpl_id = 0
                sms_data = None
                if teacher.published:
                    sms_tpl_id = smsUtil.TPL_PUBLISH_TEACHER
                    sms_data = {'username': teacher.name}
                else:
                    pass
                sms_ok = _try_send_sms(phone, sms_tpl_id, sms_data, 3)
                if not sms_ok:
                    ret_msg = '修改【'+teacher.name+'】老师状态成功, 但是短信通知失败, 请自行通知。'
                    return JsonResponse({'ok': True, 'msg': ret_msg, 'code': 3})
            return JsonResponse({'ok': True, 'msg': 'OK', 'code': 0})
        except models.Teacher.DoesNotExist as e:
            msg = self.NO_TEACHER_FORMAT.format(id=tid)
            logger.error(msg)
            return JsonResponse({'ok': False, 'msg': msg, 'code': 1})
        # except Exception as err:
        #     logger.error(err)
        #     return JsonResponse({'ok': False, 'msg': self.defaultErrMeg, 'code': -1})

    def getTeacherHighscore(self, request):
        """
        获取某个老师的提分榜列表
        :param request:
        :return:
        """
        tid = request.GET.get('tid')
        if not tid:
            return HttpResponse("")
        query_set = models.Highscore.objects.filter(teacher_id=tid)
        highscores = []
        for hs in query_set:
            highscores.append({'name': hs.name, 'scores': hs.increased_scores, 'from': hs.school_name, 'to': hs.admitted_to})
        return JsonResponse({'list': highscores})

    def getTeacherAchievement(self, request):
        """
        获取某个老师的特殊成果
        :param request:
        :return:
        """
        tid = request.GET.get('tid')
        if not tid:
            return HttpResponse("")
        query_set = models.Achievement.objects.filter(teacher_id=tid)
        achievements = []
        for ac in query_set:
            achievements.append({'title': ac.title, 'img': ac.img_url()})
        return JsonResponse({'list': achievements})

    def getTeacherWeeklySchedule(self, request):
        """
        获取某个老师的周时间表
        :param request:
        :return:
        """
        tid = request.GET.get('tid')
        if not tid:
            return HttpResponse("")
        teacher = get_object_or_404(models.Teacher, id=tid)
        weekly_time_slots = []
        for wts in teacher.weekly_time_slots.all():
            weekly_time_slots.append({'weekday': wts.weekday, 'start': wts.start, 'end': wts.end})
        return JsonResponse({'list': weekly_time_slots, 'dailyTimeSlots': models.WeeklyTimeSlot.DAILY_TIME_SLOTS})

    def getTeacherCourseSchedule(self, request):
        """
        查询老师某一周的课程安排
        :param request: 老师ID, 周偏移量
        :return: 课程记录
        """
        tid = request.GET.get('tid')
        weekOffset = parseInt(request.GET.get('weekOffset'), 0)
        if not tid:
            return HttpResponse("")
        teacher = get_object_or_404(models.Teacher, id=tid)
        # 每周时间计划
        weekly_time_slots = []
        for wts in teacher.weekly_time_slots.all():
            weekly_time_slots.append({'weekday': wts.weekday, 'start': wts.start, 'end': wts.end})
        # 计算该周日期
        now = timezone.now()
        from_day = now + datetime.timedelta(days=(-now.weekday()+weekOffset*7))  # 该周一
        to_day = now + datetime.timedelta(days=(7-now.weekday()+weekOffset*7))  # 下周一
        dates = []
        for i in range(7):
            _d = from_day + datetime.timedelta(days=i)
            dates.append(str(_d.month)+'.'+str(_d.day))
        # 查询课程安排
        from_time = from_day.replace(hour=0, minute=0, second=0, microsecond=0)
        to_time = to_day.replace(hour=0, minute=0, second=0, microsecond=0)
        timeSlots = models.TimeSlot.objects.select_related("order__parent")\
            .filter(order__teacher_id=teacher.id, start__gte=from_time, end__lt=to_time)
        courses = []
        TIME_FMT = '%H:%M:00'
        order_heap = {}
        # 组织课程信息, TODO: 调课退课退费记录
        for timeSlot in timeSlots:
            ts_dict = {}
            ts_dict['weekday'] = timeSlot.start.isoweekday()
            ts_dict['start'] = timeSlot.start.strftime(TIME_FMT)
            ts_dict['end'] = timeSlot.end.strftime(TIME_FMT)
            cur_order = order_heap.get(timeSlot.order_id)
            if not cur_order:
                cur_order = {}
                cur_order['subject'] = timeSlot.order.grade.name+timeSlot.order.subject.name
                cur_order['phone'] = timeSlot.order.parent.user.profile.phone
                cur_order['student'] = timeSlot.order.parent.student_name
                cur_order['school'] = timeSlot.order.school.name
                order_heap[timeSlot.order_id] = cur_order
            ts_dict.update(cur_order)
            courses.append(ts_dict)
        return JsonResponse({'list': weekly_time_slots, 'dailyTimeSlots': models.WeeklyTimeSlot.DAILY_TIME_SLOTS,
                             'dates': dates, 'courses': courses})

    def getGradesRangeOfSubject(self, request):
        """
        获取subject所属的的年级范围
        :param request:
        :return:
        """
        sid = request.GET.get('sid')  # subject id
        if not sid:
            return HttpResponse("")
        grade_ids = list(models.Ability.objects.filter(subject_id=sid).values_list('grade_id', flat=True))
        return JsonResponse({'list': grade_ids})

    def updateTeacherStatus(self, request, new_status):
        """
        新注册老师修改老师状态
        :param request:
        :param new_status:
        :return:
        """
        teacherId = request.POST.get('teacherId')
        try:
            teacher = models.Teacher.objects.get(id=teacherId)
            # 用带日志的方法来包裹裸的调用
            teacher.set_status(request.user, new_status)
            # teacher.status = new_status
            teacher.save()
            # send notice (sms) to teacher
            phone = teacher.user.profile.phone
            if phone:
                sms_tpl_id = 0
                sms_data = None
                if new_status == models.Teacher.NOT_CHOSEN:
                    pass
                elif new_status == models.Teacher.TO_INTERVIEW:
                    pass # TODO: 短信模板没有做好
                elif new_status == models.Teacher.INTERVIEW_OK:
                    sms_tpl_id = smsUtil.TPL_INTERVIEW_OK
                    sms_data = {'username': teacher.name}
                elif new_status == models.Teacher.INTERVIEW_FAIL:
                    sms_tpl_id = smsUtil.TPL_INTERVIEW_FAIL
                    sms_data = {'username': teacher.name}
                else:
                    pass
                sms_ok = _try_send_sms(phone, sms_tpl_id, sms_data, 3)
                if not sms_ok:
                    ret_msg = '修改【'+teacher.name+'】老师状态成功, 但是短信通知失败, 请自行通知。'
                    return JsonResponse({'ok': True, 'msg': ret_msg, 'code': 3})
            return JsonResponse({'ok': True, 'msg': 'OK', 'code': 0})
        except models.Teacher.DoesNotExist as e:
            msg = self.NO_TEACHER_FORMAT.format(id=teacherId)
            logger.error(msg)
            return JsonResponse({'ok': False, 'msg': msg, 'code': 1})
        except Exception as err:
            logger.error(err)
            return JsonResponse({'ok': False, 'msg': self.defaultErrMeg, 'code': -1})


class StudentView(BaseStaffView):
    template_name = 'staff/student/students.html'

    def get_context_data(self, **kwargs):
        kwargs['parents'] = models.Parent.objects.all
        kwargs['centers'] = models.School.objects.filter(center=True)
        kwargs['grades'] = models.Grade.objects.all
        kwargs['subjects'] = models.Subject.objects.all
        return super(StudentView, self).get_context_data(**kwargs)


class StudentScheduleManageView(BaseStaffView):
    template_name = 'staff/student/schedule_manage.html'

    def get_context_data(self, **kwargs):
        # kwargs['parents'] = models.Parent.objects.all 
        #  kwargs['centers'] = models.School.objects.filter(center=True)
        #  kwargs['grades'] = models.Grade.objects.all 
        #  kwargs['subjects'] = models.Subject.objects.all
        # 把查询参数数据放到kwargs['query_data'], 以便template回显
        kwargs['query_data'] = self.request.GET.dict()
        parent_phone = self.request.GET.get('phone',None)
        week = self.request.GET.get('week',0)
        week = int(week)
        kwargs['query_data']['week'] = week

        # deleted 代表已经释放和调课的, suspended 代表停课的, 这些都不显示
        query_set = models.TimeSlot.objects.filter(deleted=False, suspended=False)
        # 家长手机号, 精确匹配
        if parent_phone:
            query_set = query_set.filter(order__parent__user__profile__phone=parent_phone)

        kwargs['all_count'] = query_set.count()

        # 起始查询时间: 根据当前天 和 上下几周 确定
        start_search_time = timezone.now().replace(hour=0, minute=0, second=0, microsecond=0)
        start_search_time += datetime.timedelta(days=week*7)

        # 结束时间
        end_search_time = start_search_time + datetime.timedelta(days=7)

        # 一周内 weekday 和 具体日期
        weekdays = []
        for i in range(7):
            weekdays_dict = {}
            date = start_search_time + datetime.timedelta(days=i)
            weekdays_dict['weekday'] = date.weekday() + 1
            weekdays_dict['date'] = date
            weekdays.append(weekdays_dict)

        # 只获取一周内数据
        query_set = query_set.filter(start__gte=start_search_time).filter(end__lte=end_search_time).order_by('start')

        # 从今天起到后7天的 weekday 描述
        kwargs['weekdays'] = weekdays
        kwargs['today'] = timezone.now().replace(hour=0, minute=0, second=0, microsecond=0)
        # 固定的 weekly time slots
        kwargs['weekly_time_slots'] = models.WeeklyTimeSlot.DAILY_TIME_SLOTS
        # 查询结果数据集
        kwargs['timeslots'] = query_set
        kwargs['evaluations'] = models.Evaluation.objects.filter(
            start__gte=start_search_time,
            end__lte=end_search_time,
            status=models.Evaluation.SCHEDULED,
            order__parent__user__profile__phone=parent_phone)
        return super(StudentScheduleManageView, self).get_context_data(**kwargs)


class StudentScheduleChangelogView(BaseStaffView):
    template_name = 'staff/student/schedule_changelog.html'

    """
    已经调课停课课时(Timeslot)列表
    """
    def get_context_data(self, **kwargs):
        # 把查询参数数据放到kwargs['query_data'], 以便template回显
        kwargs['query_data'] = self.request.GET.dict()
        name = self.request.GET.get('name',None)
        phone = self.request.GET.get('phone',None)
        type = self.request.GET.get('type',None)
        searchDateOri = self.request.GET.get('searchDateOri',None)
        searchDateNew = self.request.GET.get('searchDateNew',None)
        page = self.request.GET.get('page')

        query_set = models.TimeSlotChangeLog.objects.all()
        # 家长姓名 or 学生姓名 or 老师姓名, 模糊匹配
        if name:
            query_set = query_set.filter(
                Q(old_timeslot__order__parent__user__username__icontains=name) |
                Q(old_timeslot__order__parent__student_name__icontains=name) |
                Q(old_timeslot__order__teacher__name__icontains=name)
            )
        # 家长手机 or 老师手机, 模糊匹配
        if phone:
            query_set = query_set.filter(
                Q(old_timeslot__order__parent__user__profile__phone__contains=phone) |
                Q(old_timeslot__order__teacher__user__profile__phone__contains=phone)
            )
        # 类型匹配
        if type:
            query_set = query_set.filter(record_type=type)

        # 原上课时间
        if searchDateOri:
            stTime = datetime.datetime.strptime(searchDateOri, '%Y-%m-%d')
            query_set = query_set.filter(
                Q(old_timeslot__start__date=stTime.date())
            )

        # 现上课时间
        if searchDateNew:
            stTime = datetime.datetime.strptime(searchDateNew, '%Y-%m-%d')
            query_set = query_set.filter(
                Q(new_timeslot__start__date=stTime.date())
            )
        # 可用筛选条件数据集
        kwargs['types'] = models.TimeSlotChangeLog.TYPE_CHOICES
        # paginate
        query_set, pager = paginate(query_set, page, 5)
        # 查询结果数据集
        kwargs['changelogs'] = query_set
        kwargs['pager'] = pager
        return super(StudentScheduleChangelogView, self).get_context_data(**kwargs)

    def get(self, request):
        context = self.get_context_data()
        return render(request, self.template_name, context)


class StudentScheduleActionView(BaseStaffActionView):
    def post(self, request):
        action = self.request.POST.get('action')
        if action == 'suspend-course':
            return self.suspend_course(request)
        if action == 'view-available':
            return self.view_available(request)
        if action == 'transfer-course':
            return self.transfer_course(request)
        return HttpResponse("Not supported action.", status=404)

    def suspend_course(self, request):
        tid = request.POST.get('tid')
        timeslot = models.TimeSlot.objects.get(id=tid)
        # 具体停课操作在里面
        if not timeslot.reschedule_for_suspend(self.request.user):
            return JsonResponse({'ok': False, 'msg': '停课失败, 请稍后重试或联系管理员', 'code': 'suspend_transaction'})
        return JsonResponse({'ok': True})

    def view_available(self, request):
        tid = request.POST.get('tid')
        timeslot = models.TimeSlot.objects.get(id=tid)
        teacher = timeslot.order.teacher
        school = timeslot.order.school
        sa_dict = teacher.shortterm_available_dict(school)

        data = [OrderedDict([
            ('weekday', one[0]),
            ('start', one[1]),
            ('end', one[2]),
            ('available', sa_dict[(one[0], one[1], one[2])])
        ]) for one in sa_dict]

        now_date = timezone.now().astimezone().strftime('%Y-%m-%d')
        now_time = timezone.now().astimezone().strftime('%H:%M:%S')

        return JsonResponse({'ok': True, 'sa_dict': data, 'now_date': now_date, 'now_time': now_time})

    def transfer_course(self, request):
        tid = request.POST.get('tid')
        new_date_str = request.POST.get('new_date')
        new_start_str = request.POST.get('new_start')
        new_end_str = request.POST.get('new_end')
        new_start_datetime = timezone.make_aware(
            datetime.datetime.strptime(new_date_str + ' ' + new_start_str, '%Y-%m-%d %H:%M:%S')
        )
        new_end_datetime = timezone.make_aware(
            datetime.datetime.strptime(new_date_str + ' ' + new_end_str, '%Y-%m-%d %H:%M:%S')
        )
        timeslot = models.TimeSlot.objects.get(id=tid)
        ret_code = timeslot.reschedule_for_transfer(new_start_datetime, new_end_datetime, self.request.user)

        if ret_code == -1:
            return JsonResponse({'ok': False, 'msg': '调课失败, 请稍后重试或联系管理员', 'code': 'transfer_transaction'})
        if ret_code == -2:
            return JsonResponse({'ok': False, 'msg': '调课失败, 调整后的课程时间冲突, 请稍后重试或联系管理员', 'code': 'transfer_conflict'})
        return JsonResponse({'ok': True})


class SchoolsView(BaseStaffView):
    template_name = 'staff/school/schools.html'

    def get_context_data(self, **kwargs):
        context = super(SchoolsView, self).get_context_data(**kwargs)
        schoolId = self.request.GET.get('schoolId')
        center = self.request.GET.get('center')

        query_set = models.School.objects.filter().order_by('-opened', '-id')
        if schoolId:
            query_set = query_set.filter(id = schoolId)

        if center == 1:
            query_set = query_set.filter(center = True)
        elif center == 2:
            query_set = query_set.filter(center = False)

        context['schools'] = query_set
        context['schoolId'] = schoolId
        context['center'] = center
        context['allSchools'] = models.School.objects.filter()
        return context


class SchoolView(BaseStaffView):
    template_name = 'staff/school/edit.html'

    def get_context_data(self, **kwargs):
        context = super(SchoolView, self).get_context_data(**kwargs)
        schoolId = self.request.GET.get('schoolId', None)
        memberservices = models.Memberservice.objects.all().values('id','name')
        if not schoolId:
            schoolId = self.request.POST.get('schoolId', None)

        if schoolId == 'None':
            schoolId = None

        context['region_list'] = models.Region.objects.filter(opened=True)
        context['schoolId'] = schoolId
        context['memberservices'] = memberservices
        return context

    def get(self, request):
        context = self.get_context_data()
        schoolId = context['schoolId']
        school = None
        if schoolId:
            school = models.School.objects.get(id=schoolId)

        context['school'] = school
        return render(request, self.template_name, context)

    def post(self, request):
        context = self.get_context_data()
        schoolId = context['schoolId']
        school = None
        if not schoolId is None:
            school = models.School.objects.get(id=schoolId)
        else:
            school = models.School()


        service_list = self.request.POST.getlist('services')


        school.phone = self.request.POST.get('phone', None)
        school.name = self.request.POST.get('schoolName', None)
        school.center = True if self.request.POST.get('center', '0') == '1' else False
        school.opened = True if self.request.POST.get('opened', '0') == '1' else False
        class_seat = self.request.POST.get('class_seat', None)
        if class_seat == '':
            class_seat = 0
        school.class_seat = class_seat
        study_seat = self.request.POST.get('study_seat', None)
        if study_seat == '':
            study_seat = 0
        school.study_seat = study_seat
        school.longitude = self.request.POST.get('longitude', None)
        school.latitude = self.request.POST.get('latitude', None)
        school.address = self.request.POST.get('address', None)
        school.desc_title = self.request.POST.get('desc_title', None)
        school.desc_content = self.request.POST.get('desc_content', None)
        regionId = self.request.POST.get('regionId', None)
        school.region = models.Region.objects.get(id=regionId)
        school.save()
        school.member_services = service_list

        context['school'] = school

        staySchoolImgIds = request.POST.getlist('schoolImgId')
        staySchoolImgIds = [i for i in staySchoolImgIds if i]
        models.SchoolPhoto.objects.filter(school_id=schoolId).exclude(id__in=staySchoolImgIds).delete()
        newSchoolImgs = request.FILES.getlist('schoolImg')
        for schoolImg in newSchoolImgs:
            photo = models.SchoolPhoto(school=school)
            _img_content = ContentFile(schoolImg.read())
            photo.img.save("photo"+str(school.id)+'_'+str(_img_content.size), _img_content)
            photo.save()

        return JsonResponse({'ok': True, 'msg': 'OK', 'code': 0})


class OrderReviewView(BaseStaffView):
    template_name = 'staff/order/review.html'

    def get_context_data(self, **kwargs):

        # 把查询参数数据放到kwargs['query_data'], 以便template回显
        kwargs['query_data'] = self.request.GET.dict()
        name = self.request.GET.get('name')
        phone = self.request.GET.get('phone')
        order_id = self.request.GET.get('order_id')
        status = self.request.GET.get('status')
        grade = self.request.GET.get('grade')
        subject = self.request.GET.get('subject')
        school = self.request.GET.get('school')
        order_date_from = self.request.GET.get('order_date_from')
        order_date_to = self.request.GET.get('order_date_to')
        page = self.request.GET.get('page')

        query_set = models.Order.objects.filter()
        # 家长姓名 or 学生姓名 or 老师姓名, 模糊匹配
        if name:
            query_set = query_set.filter(
                Q(parent__user__username__icontains=name) |
                Q(parent__student_name__icontains=name) |
                Q(teacher__name__icontains=name)
            )
        # 家长手机 or 老师手机, 模糊匹配
        if phone:
            query_set = query_set.filter(
                Q(parent__user__profile__phone__contains=phone) |
                Q(teacher__user__profile__phone__contains=phone)
            )
        # 后台系统订单号, 模糊匹配
        if order_id:
            query_set = query_set.filter(order_id__icontains=order_id)
        # 订单状态
        if status:
            # 此处 status 是前端传过来的值, 需要进一步判断具体状态
            if status == models.Order.PAID:
                # 已支付: 主状态 PAID, 最后审核状态 null
                query_set = query_set.filter(status=models.Order.PAID)
                query_set = query_set.filter(refund_status__isnull=True)
            elif status == models.Order.REFUND_PENDING or status == models.Order.REFUND_REJECTED:
                # 退费审核中/退费被驳回: 主状态 PAID, 最后审核状态 对应的审核状态
                query_set = query_set.filter(status=models.Order.PAID)
                query_set = query_set.filter(refund_status=status)
            else:
                """
                其他状态, 直接判断 order 状态: 未支付(PENDING)
                                           已取消(CANCELED)
                                           退费成功(REFUND)
                                           已结束(???)(最后一节课已经完成)
                """
                # todo: 缺少一个已结束的状态, 后续完善
                query_set = query_set.filter(status=status)

        # 年级
        if grade:
            query_set = query_set.filter(grade=grade)
        # 科目
        if subject:
            query_set = query_set.filter(subject=subject)
        # 授课中心
        if school:
            query_set = query_set.filter(school=school)
        # 下单日期区间
        if order_date_from:
            try:
                date_from = parse_date(order_date_from)
                query_set = query_set.filter(created_at__gte=date_from)
            except:
                pass
        if order_date_to:
            try:
                date_to = parse_date_next(order_date_to)
                query_set = query_set.filter(created_at__lt=date_to)
            except:
                pass

        # 可用筛选条件数据集
        # 订单状态 + 退费审核状态
        all_status = models.Order.STATUS_CHOICES + models.Order.REFUND_STATUS_CHOICES
        # 去除 退费审核通过 和 审核被驳回 的状态, 前端不需要显示
        remove_status = [models.Order.REFUND_APPROVED, models.Order.REFUND_REJECTED]
        kwargs['status'] = []
        for key, text in all_status:
            if key in remove_status:
                continue
            else:
                kwargs['status'].append((key, text))
        kwargs['schools'] = models.School.objects.filter()
        kwargs['grades'] = models.Grade.objects.all()
        kwargs['subjects'] = models.Subject.objects.all()
        # 查询结果数据集, 默认按下单时间排序
        query_set = query_set.order_by('-created_at')
        # paginate
        query_set, pager = paginate(query_set, page, 5)
        kwargs['orders'] = query_set
        kwargs['pager'] = pager
        return super(OrderReviewView, self).get_context_data(**kwargs)


class OrderRefundView(BaseStaffView):
    template_name = 'staff/order/refund.html'

    def get_context_data(self, **kwargs):

        # 把查询参数数据放到kwargs['query_data'], 以便template回显
        kwargs['query_data'] = self.request.GET.dict()
        refund_date_from = self.request.GET.get('refund_date_from')
        refund_date_to = self.request.GET.get('refund_date_to')
        name = self.request.GET.get('name')
        phone = self.request.GET.get('phone')
        order_id = self.request.GET.get('order_id')
        subject = self.request.GET.get('subject')
        status = self.request.GET.get('status')
        page = self.request.GET.get('page')
        export = self.request.GET.get('export', None)

        query_set = models.Order.objects.filter()
        # 退费申请区间
        if refund_date_from:
            try:
                date_from = parse_date(refund_date_from)
                query_set = query_set.filter(refund_at__gte=date_from)
            except:
                pass
        if refund_date_to:
            try:
                date_to = parse_date_next(refund_date_to)
                query_set = query_set.filter(refund_at__lt=date_to)
            except:
                pass
        # 家长姓名 or 学生姓名 or 老师姓名, 模糊匹配
        if name:
            query_set = query_set.filter(
                Q(parent__user__username__icontains=name) |
                Q(parent__student_name__icontains=name) |
                Q(teacher__name__icontains=name)
            )
        # 家长手机 or 老师手机, 模糊匹配
        if phone:
            query_set = query_set.filter(
                Q(parent__user__profile__phone__contains=phone) |
                Q(teacher__user__profile__phone__contains=phone)
            )
        # 后台系统订单号, 模糊匹配
        if order_id:
            query_set = query_set.filter(order_id__icontains=order_id)
        # 科目
        if subject:
            query_set = query_set.filter(subject=subject)
        # 订单状态
        if status:
            query_set = query_set.filter(refund_status=status)
        else:
            query_set = query_set.filter(refund_status__isnull=False)

        # 可用筛选条件数据集
        # 去除 审核被驳回 的状态, 前端不需要显示
        all_status = models.Order.REFUND_STATUS_CHOICES
        remove_status = [models.Order.REFUND_REJECTED]
        kwargs['status'] = []
        for key, text in all_status:
            if key in remove_status:
                continue
            else:
                kwargs['status'].append((key, text))
        kwargs['subjects'] = models.Subject.objects.all()
        # 查询结果数据集, 默认按下单时间排序
        query_set = query_set.order_by('-refund_at')
        if export is not None:
            # 导出操作, 直接给 query_set
            return query_set
        # 非导出操作, 继续分页显示
        query_set, pager = paginate(query_set, page)
        kwargs['pager'] = pager
        kwargs['orders'] = query_set
        return super(OrderRefundView, self).get_context_data(**kwargs)

    def get(self, request):
        context = self.get_context_data()
        export = self.request.GET.get('export', None)
        if export:
            query_set = context
            headers = (
                '申请时间',
                '订单号',
                '家长手机号',
                '学生姓名',
                '老师姓名',
                '老师手机号',
                '报课年级',
                '报课科目',
                '上课地址',
                '购买小时',
                '小时单价',
                '剩余小时',
                '退费小时',
                '奖学金',
                '退费金额',
                '状态',
                '退费原因',
                '是否排课',
            )
            columns = (
                lambda x: timezone.make_naive(x.refund_info().refunded_at),
                'order_id',
                'parent.user.profile.phone',
                'parent.student_name',
                'teacher.name',
                'teacher.user.profile.phone',
                'grade',
                'subject',
                'school',
                'hours',
                lambda x: x.price/100,
                'refund_info.remaining_hours',
                'refund_info.refund_hours',
                lambda x: x.coupon.amount/100 if x.coupon is not None else 0,
                lambda x: x.refund_info().refund_amount/100,
                'get_refund_status_display',
                'refund_info.reason',
                lambda x: '是' if x.is_timeslot_allocated() else '否',
            )
            return excel.excel_response(query_set, columns, headers, '退费审核记录.xls')
        return render(request, self.template_name, context)


class OrderRefundActionView(BaseStaffActionView):
    def get(self, request):
        action = self.request.GET.get('action')
        if action == 'preview-refund-info':
            return self.preview_refund_info(request)
        if action == 'get-refund-record':
            return self.get_refund_record(request)
        return HttpResponse("Not supported action.", status=404)

    def post(self, request):
        action = self.request.POST.get('action')
        if action == 'request-refund':
            return self.request_refund(request)
        if action == 'refund-approve':
            return self.refund_approve(request)
        if action == 'refund-reject':
            return self.refund_reject(request)
        return HttpResponse("Not supported action.", status=404)

    def preview_refund_info(self, request):
        order_id = request.GET.get('order_id')
        order = models.Order.objects.get(id=order_id)
        # 只要是已支付的, 都可以预览退费信息, 包括 审核中 和 已驳回
        if order.status == order.PAID:
            # 根据当前时间点,计算退费信息
            return JsonResponse({
                'ok': True,
                'remainingHours': order.remaining_hours(),          # 剩余小时
                'refundHours': order.preview_refund_hours(),        # 退费小时
                'refundAmount': order.preview_refund_amount()/100,  # 退费金额
                'reason': order.refund_info().reason if order.refund_info() is not None else ''
                # 退费原因
            })
        return JsonResponse({'ok': False, 'msg': '订单还未支付', 'code': 'order_01'})

    def get_refund_record(self, request):
        order_id = request.GET.get('order_id')
        order = models.Order.objects.get(id=order_id)
        if order.refund_info() is not None:
            record = order.refund_info()
            # 将之前申请退费时记录下来的退费信息返回给前端
            return JsonResponse({
                'ok': True,
                'remainingHoursRecord': record.remaining_hours,     # 剩余小时(申请退费时计算的)
                'refundHoursRecord': record.refund_hours,           # 退费小时(申请退费时计算的)
                'refundAmountRecord': record.refund_amount/100,     # 退费金额(申请退费时计算的)
                'reason': record.reason                             # 退费原因(申请退费时提交的)
            })
        return JsonResponse({'ok': False, 'msg': '订单无申请退费记录', 'code': 'order_02'})

    def request_refund(self, request):
        order_id = request.POST.get('order_id')
        order = models.Order.objects.get(id=order_id)
        reason = request.POST.get('reason')
        try:
            models.Order.objects.refund(order, reason, self.request.user)
            # 短信通知家长
            parent = order.parent
            student_name = parent.student_name or parent.user.profile.mask_phone()
            _try_send_sms(parent.user.profile.phone, smsUtil.TPL_STU_REFUND_REQUEST, {'studentname': student_name}, 3)
            # 短信通知老师
            teacher = order.teacher
            _try_send_sms(teacher.user.profile.phone, smsUtil.TPL_REFUND_NOTICE, {'username': teacher.name}, 2)

            return JsonResponse({'ok': True})
        except OrderStatusIncorrect as e:
            return JsonResponse({'ok': False, 'msg': '%s' % e})
        except RefundError as e:
            return JsonResponse({'ok': False, 'msg': '%s' % e})

    def refund_approve(self, request):
        order_id = request.POST.get('order_id')
        order = models.Order.objects.get(id=order_id)
        if order.last_refund_record() is not None:
            ok = order.last_refund_record().approve_refund()
            if ok:
                order.last_refund_record().last_updated_by = self.request.user
                order.save()
                # 短信通知家长
                parent = order.parent
                student_name = parent.student_name or parent.user.profile.mask_phone()
                amount_str = "%.2f"%(order.last_refund_record().refund_amount/100)
                _try_send_sms(parent.user.profile.phone, smsUtil.TPL_STU_REFUND_APPROVE, {'studentname': student_name, 'amount': amount_str}, 3)
                return JsonResponse({'ok': True})
        return JsonResponse({'ok': False, 'msg': '退费审核失败, 请检查订单状态', 'code': 'order_06'})

    def refund_reject(self, request):
        order_id = request.POST.get('order_id')
        order = models.Order.objects.get(id=order_id)
        if order.last_refund_record() is not None:
            ok = order.last_refund_record().reject_refund()
            if ok:
                order.last_refund_record().last_updated_by = self.request.user
                order.save()
                return JsonResponse({'ok': True})
        return JsonResponse({'ok': False, 'msg': '退费驳回失败, 请检查订单状态', 'code': 'order_07'})


class SchoolTimeslotView(BaseStaffView):
    template_name = 'staff/school/timeslot.html'

    def get_context_data(self, **kwargs):
        context = super(SchoolTimeslotView, self).get_context_data(**kwargs)
        schoolId = self.request.GET.get('schoolId', None)
        searchTime = self.request.GET.get('searchDate', None)
        searchName = self.request.GET.get('name', None)
        phone = self.request.GET.get('phone', None)

        schools = models.School.objects.filter(opened=True);

        timeslots = None
        stTime = None
        edTime = None
        if not searchTime:
            searchTime = datetime.datetime.now()
            stTime = datetime.datetime(searchTime.year, searchTime.month, searchTime.day)
        else:
            stTime = datetime.datetime.strptime(searchTime, '%Y-%m-%d')

        edTime = stTime + datetime.timedelta(days=1)

        timeslots = models.TimeSlot.objects.filter(start__gte=stTime, end__lt=edTime, deleted=False)
        if searchName:
            timeslots = timeslots.filter(Q(order__parent__user__username__icontains=searchName)|Q(order__teacher__user__username__icontains=searchName))
        if phone:
            timeslots = timeslots.filter(Q(order__parent__user__profile__phone__icontains=phone)|Q(order__teacher__user__profile__phone__icontains=phone))
        if not schoolId:
            if len(schools) > 0:
                schoolId = schools[0].id

        timeslots = timeslots.filter(order__school__id=schoolId).order_by('start')

        itemsLen = len(timeslots)
        ind = 0
        nextEqInd = 0
        while ind < itemsLen:
            itm = timeslots[ind]
            if itm.complaint:
                itm.complaint.content = JSONRenderer().render(itm.complaint.content)
            eqCount = 0
            nind = ind +1
            if nind > nextEqInd:
                while nind < itemsLen:
                    nitm = timeslots[nind]
                    if(itm.start == nitm.start) and (itm.end == nitm.end):
                        eqCount += 1
                        nextEqInd = nind
                        nind += 1
                    else:
                        nind += 1
                        break
                itm.eqCount = eqCount

            if ind > 0:
                oitm = timeslots[ind - 1]
                if(itm.start == oitm.start) and (itm.end == oitm.end):
                    itm.eqCount = -1
            ind += 1

        context['schools'] = schools
        context['timeslots'] = timeslots
        context['searchTime'] = stTime
        context['schoolId'] = schoolId
        context['name'] = searchName
        context['phone'] = phone
        context['weekday'] = ("周日","周一","周二","周三","周四","周五","周六")[int(stTime.strftime("%w"))]
        return context

    def get(self, request):
        context = self.get_context_data()
        return render(request, self.template_name, context)

    def post(self, request):
        if request.POST.get('action') == 'saveComplaint':
            timeslotId = request.POST.get('timeslotId', None)
            complaintId = request.POST.get('complaintId', None)
            complaintContent = request.POST.get('complaintContent', None)

            if not timeslotId:
                return JsonResponse({'ok': False, 'msg': '必须提供课程编号', 'code': -1})

            if not complaintId:
                cmp = models.TimeSlotComplaint(content=complaintContent)
                cmp.save()
                models.TimeSlot.objects.filter(id=timeslotId).update(complaint_id = cmp.id)
            else:
                models.TimeSlotComplaint.objects.filter(id=complaintId).update(content=complaintContent)
                models.TimeSlot.objects.filter(id=timeslotId).update(complaint_id = complaintId)

            return JsonResponse({'ok': True, 'msg': '', 'code': 0})

        if request.POST.get('action') == 'saveAttendace':
            timeslotId = request.POST.get('timeslotId', None)
            attendanceId = request.POST.get('attendanceId', None)
            attendanceValue = request.POST.get('attendanceValue', None)

            if not timeslotId:
                return JsonResponse({'ok': False, 'msg': '必须提供课程编号', 'code': -1})
            if not attendanceValue:
                return JsonResponse({'ok': False, 'msg': '必须提供考勤状态', 'code': -1})

            if not attendanceId:
                at = models.TimeSlotAttendance(record_type=attendanceValue)
                at.save()
                models.TimeSlot.objects.filter(id=timeslotId).update(attendance_id = at.id)
            else:
                models.TimeSlotAttendance.objects.filter(id=attendanceId).update(record_type=attendanceValue)
                models.TimeSlot.objects.filter(id=timeslotId).update(attendance_id = attendanceId)

            return JsonResponse({'ok': True, 'msg': '', 'code': 0})

        return JsonResponse({'ok': False, 'msg': '系统错误', 'code': -1})

class CouponConfigView(BaseStaffView):
    template_name = 'staff/coupon/config.html'

    def get_context_data(self, **kwargs):
        context = super(CouponConfigView, self).get_context_data(**kwargs)

        return context

    def get(self, request):
        context = self.get_context_data()
        couponRules = models.CouponRule.objects.order_by('id')
        couponGenerators = models.CouponGenerator.objects.order_by('-id')
        context['couponRule'] = list(couponRules)
        context['couponGenerator'] = list(couponGenerators) and couponGenerators[0]

        return render(request, self.template_name, context)

    def post(self, request):
        context = self.get_context_data()

        couponType = self.request.POST.get('couponType', None)
        used = self.request.POST.get('opened')
        if used == '1':
            used = True
        else:
            used = False
        couponName = self.request.POST.get('couponName')
        amount = self.request.POST.get('amount', 0)
        mini_course_count = self.request.POST.get('mini_course_count', 0)
        parent_phone = self.request.POST.get('parent_phone')
        expiredAt = self.request.POST.get('expiredAt', None)
        validatedStart = self.request.POST.get('validatedStart', None)
        couponRules = self.request.POST.get('couponRules')
        couponRules_list = None
        if couponRules:
            couponRules_list = json.loads(couponRules)
            models.CouponRule.objects.all().delete()
            for item in couponRules_list:
                models.CouponRule(content=item).save()

        if couponType == 'new':
            couponGenerators = models.CouponGenerator.objects.order_by('-id')
            gen = None
            if couponGenerators:
                gen = couponGenerators[0]
            else:
                gen = models.CouponGenerator()
            gen.activated = used
            if validatedStart:
                gen.validated_start = datetime.datetime.strptime(validatedStart, '%Y-%m-%d')
            if expiredAt:
                gen.expired_at = datetime.datetime.strptime(expiredAt, '%Y-%m-%d')
            try:
                gen.amount = int(amount)*100
            except:
                gen.amount = 0
            try:
                gen.mini_course_count = int(mini_course_count)
            except:
                gen.mini_course_count =  0

            gen.save()

        elif couponType == 'give':
            query_set = models.Parent.objects.filter()
            query_set = query_set.filter(user__profile__phone = parent_phone)
            if query_set.count() == 0:
                return JsonResponse({'ok': False, 'msg': '家长不存在', 'code': -1})

            if validatedStart:
                validated_start = datetime.datetime.strptime(validatedStart, '%Y-%m-%d')
            else:
                validated_start = timezone.now()
            if expiredAt:
                expired_at = datetime.datetime.strptime(expiredAt, '%Y-%m-%d')
            else:
                expired_at = timezone.now()
            try:
                amount = int(amount)
            except:
                amount = 0
            try:
                mini_course_count = int(mini_course_count)
            except:
                mini_course_count =  0
            cp = models.Coupon(parent=query_set[0], name=couponName, amount=amount*100,
                                                mini_course_count=mini_course_count,validated_start=validated_start,
                                                expired_at=expired_at,used=False)
            cp.save()

        return JsonResponse({'ok': True, 'msg': 'OK', 'code': 0})


class EvaluationView(BaseStaffView):
    template_name = 'staff/evaluation/evaluations.html'

    def get_context_data(self, **kwargs):

        # 把查询参数数据放到kwargs['query_data'], 以便template回显
        kwargs['query_data'] = self.request.GET.dict()
        name = self.request.GET.get('name')
        phone = self.request.GET.get('phone')
        status = self.request.GET.get('status')
        order_date = self.request.GET.get('order_date')
        evaluation_date = self.request.GET.get('evaluation_date')
        page = self.request.GET.get('page')

        query_set = models.Evaluation.objects.filter()
        # 家长姓名 or 学生姓名 or 老师姓名, 模糊匹配
        if name:
            query_set = query_set.filter(
                Q(order__parent__user__username__icontains=name) |
                Q(order__parent__student_name__icontains=name) |
                Q(order__teacher__name__icontains=name)
            )
        # 家长手机 or 老师手机, 模糊匹配
        if phone:
            query_set = query_set.filter(
                Q(order__parent__user__profile__phone__contains=phone) |
                Q(order__teacher__user__profile__phone__contains=phone)
            )
        # 测评状态
        if status:
            # 此处 status 是前端传过来的值, 需要进一步判断具体状态
            if status == models.Order.REFUND:
                # 已退费
                query_set = query_set.filter(order__status=models.Order.REFUND)
            else:
                query_set = query_set.filter(status=status)

        # 下单日期
        if order_date:
            try:
                date = datetime.datetime.strptime(order_date, '%Y-%m-%d')
                query_set = query_set.filter(order__created_at__date=date.date())
            except:
                pass
        # 测评时间
        if evaluation_date:
            try:
                date = datetime.datetime.strptime(evaluation_date, '%Y-%m-%d')
                query_set = query_set.filter(start__date=date.date())
            except:
                pass

        # 可用筛选条件数据集
        all_status = models.Evaluation.STATUS_CHOICES
        kwargs['status'] = []
        for key, text in all_status:
            kwargs['status'].append((key, text))
        kwargs['status'].append((models.Order.REFUND, '已退费'))
        # 查询结果数据集, 默认按下单时间排序
        query_set = query_set.order_by('-order__created_at')
        # paginate
        query_set, pager = paginate(query_set, page)
        kwargs['evaluations'] = query_set
        kwargs['pager'] = pager
        kwargs['daily_timeslots'] = models.WeeklyTimeSlot.DAILY_TIME_SLOTS
        return super(EvaluationView, self).get_context_data(**kwargs)


class EvaluationActionView(BaseStaffActionView):
    def post(self, request):
        action = self.request.POST.get('action')
        print(action)
        if action == 'schedule-evaluation':
            return self.schedule_evaluation(request)
        if action == 'complete-evaluation':
            return self.complete_evaluation(request)
        return HttpResponse("Not supported action.", status=404)

    def schedule_evaluation(self, request):
        eid = request.POST.get('eid')
        schedule_date = request.POST.get('schedule_date')
        schedule_time_index = request.POST.get('schedule_time')
        for index, slot in enumerate(models.WeeklyTimeSlot.DAILY_TIME_SLOTS, start=0):
            if index == int(schedule_time_index):
                date = datetime.datetime.strptime(schedule_date, '%Y-%m-%d')
                start = timezone.make_aware(datetime.datetime.combine(date, slot['start']))
                end = timezone.make_aware(datetime.datetime.combine(date, slot['end']))
                evaluation = models.Evaluation.objects.get(id=eid)
                if evaluation.schedule(start, end):
                    return JsonResponse({'ok': True})
                else:
                    return JsonResponse({'ok': False, 'msg': '测评已完成, 无法再次安排时间', 'code': 'evaluation_status'})
        return JsonResponse({'ok': False, 'msg': '安排测评时间失败, 请稍后重试或联系管理员', 'code': 'schedule_evaluation'})

    def complete_evaluation(self, request):
        eid = request.POST.get('eid')
        evaluation = models.Evaluation.objects.get(id=eid)
        if evaluation.complete():
            return JsonResponse({'ok': True})
        else:
            return JsonResponse({'ok': False, 'msg': '未安排测评时间, 无法设置完成状态', 'code': 'evaluation_status'})
            return JsonResponse({'ok': False, 'msg': '设置测评完成失败, 请稍后重试或联系管理员', 'code': 'complete_evaluation'})

