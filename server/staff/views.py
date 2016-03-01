import logging
import datetime

# django modules
from django.core.files.base import ContentFile
from django.shortcuts import render, redirect, get_object_or_404
from django.http import HttpResponse, JsonResponse
from django.views.decorators.http import require_POST
from django.views.generic import View, TemplateView, ListView
from django.utils.decorators import method_decorator
from django.contrib import auth
from django.db.models import Q
from django.utils import timezone
from rest_framework.renderers import JSONRenderer

import json

# local modules
from app import models
from app.utils import smsUtil
from app.utils.types import parseInt
from app.utils.db import paginate
from .decorators import mala_staff_required, is_manager


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


#因为使用了listView而无法直接继承BaseStaffView
@method_decorator(mala_staff_required, name='dispatch')
class CouponsListView(ListView):
    model = models.Coupon
    template_name = 'staff/coupon/coupons_list.html'
    context_object_name = 'coupons_list'
    paginate_by = 10

    def get_context_data(self, **kwargs):
        context = super(CouponsListView, self).get_context_data(**kwargs)
        kwargs['statusList'] = [
            {'text':"状态",'value':""},
            {'text':"已使用",'value':"used"},
            {'text':"未使用",'value':"unused"},
            {'text':"已过期",'value':"expired"},
                                ]
        kwargs['typesList'] = [
            {'text':"类型",'value':""},
            {'text':"注册",'value':"reg"},
            {'text':"抽奖",'value':"lotto"},
                                ]
        kwargs['name'] = self.request.GET.get('name',None)
        kwargs['phone'] = self.request.GET.get('phone',None)
        kwargs['dateFrom'] = self.request.GET.get('dateFrom',None)
        kwargs['dateTo'] = self.request.GET.get('dateTo',None)
        kwargs['type'] = self.request.GET.get('type',None)
        kwargs['status'] = self.request.GET.get('status',None)
        return super(CouponsListView, self).get_context_data(**kwargs)

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
            coupons_list = coupons_list.filter(parent__user__username__icontains=name)
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
                date_from = datetime.datetime.strptime(dateFrom, '%Y-%m-%d')
                coupons_list = coupons_list.filter(created_at__gte = date_from)
            except:
                pass
        if dateTo:
            try:
                date_to = datetime.datetime.strptime(dateTo, '%Y-%m-%d')
                date_to += datetime.timedelta(days=1)
                coupons_list = coupons_list.filter(created_at__lte = date_to)
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
                date_from = datetime.datetime.strptime(reg_date_from, '%Y-%m-%d')
                query_set = query_set.filter(user__date_joined__gte = date_from)
            except:
                pass
        if reg_date_to:
            try:
                date_to = datetime.datetime.strptime(reg_date_to, '%Y-%m-%d')
                date_to += datetime.timedelta(days=1)
                query_set = query_set.filter(user__date_joined__lte = date_to)
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
        query_set, pager = paginate(query_set, page)
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
        grades_all = models.Grade.objects.all()
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
            certIdHeld, created = models.Certificate.objects.get_or_create(teacher=teacher, type=models.Certificate.ID_HELD,
                                                                  defaults={'name':"",'verified':False})
            profile = teacher.user.profile
            # 基本信息
            teacher.name = request.POST.get('name')
            certIdHeld.name = request.POST.get('id_num')
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
            teacher.level_id = parseInt(request.POST.get('level'), 1)
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
            certIdHeldOk = request.POST.get('certIdHeldOk')
            if certIdHeldOk and certIdHeldOk=='True':
                certIdHeld.verified = True
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
                cert.name = name
                if certOk and certOk=='True':
                    cert.verified = True
                else:
                    cert.verified = False
                if certImg:
                    _img_content = ContentFile(certImg.read())
                    cert.img.save("certOther"+str(teacher.id)+'_'+str(_img_content.size), _img_content)
                cert.save()
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
                _img_content = ContentFile(certImg.read())
                newCert.img.save("certOther"+str(teacher.id)+'_'+str(_img_content.size), _img_content)
                newCert.save()
            # TODO: 资质认证修改后, 发邮件或短信通知
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
        certOk = request.POST.get('cert'+type_str+'Ok')
        if certOk and certOk=='True':
            cert.verified = True
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


class TeacherWithdrawalView(BaseStaffView):
    template_name = 'staff/teacher/teacher_withdrawal_list.html'

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
        date_from = self.request.GET.get('date_from')
        date_to = self.request.GET.get('date_to')
        status = self.request.GET.get('status')
        name = self.request.GET.get('name')
        phone = self.request.GET.get('phone')
        page = self.request.GET.get('page')
        query_set = models.Withdrawal.objects.select_related('account__user__teacher', 'account__user__profile').filter(account__user__teacher__isnull=False)
        if date_from:
            try:
                date_from = datetime.datetime.strptime(date_from, '%Y-%m-%d')
                query_set = query_set.filter(submit_time__gte = date_from)
            except:
                pass
        if date_to:
            try:
                date_to = datetime.datetime.strptime(date_to, '%Y-%m-%d')
                date_to += datetime.timedelta(days=1)
                query_set = query_set.filter(submit_time__lte = date_to)
            except:
                pass
        if status:
            query_set = query_set.filter(status = status)
        if name:
            query_set = query_set.filter(account__user__teacher__name__contains = name)
        if phone:
            query_set = query_set.filter(account__user__profile__phone__contains = phone)
        query_set = query_set.order_by('-submit_time')
        # paginate
        query_set, pager = paginate(query_set, page)
        kwargs['withdrawals'] = query_set
        kwargs['pager'] = pager
        # 一些固定数据
        kwargs['status_choices'] = models.Withdrawal.STATUS_CHOICES
        return super(TeacherWithdrawalView, self).get_context_data(**kwargs)


class TeacherActionView(BaseStaffActionView):

    NO_TEACHER_FORMAT = "没有查到老师, ID={id}"

    def get(self, request):
        action = self.request.GET.get('action')
        if action == 'list-region':
            return self.listSubRegions(request)
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
            teacher.save()
            # TODO: send notice (sms) to teacher
            return JsonResponse({'ok': True, 'msg': 'OK', 'code': 0})
        except models.Teacher.DoesNotExist as e:
            msg = self.NO_TEACHER_FORMAT.format(id=tid)
            logger.error(msg)
            return JsonResponse({'ok': False, 'msg': msg, 'code': 1})
        # except Exception as err:
        #     logger.error(err)
        #     return JsonResponse({'ok': False, 'msg': self.defaultErrMeg, 'code': -1})

    def listSubRegions(self, request):
        """
        获取下级地区列表
        :param request:
        :return:
        """
        sid = request.GET.get('sid')
        query_set = models.Region.objects.filter()
        if not sid:
            query_set = query_set.filter(superset_id__isnull=True)
        else:
            query_set = query_set.filter(superset_id=sid)
        regions = []
        for region in query_set:
            regions.append({'id': region.id, 'name': region.name})
        return JsonResponse({'list': regions})

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
            .filter(order__teacher_id=teacher.id, start__gte=from_time, end__lte=to_time)
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
            teacher.status = new_status
            teacher.save()
            # send notice (sms) to teacher
            profile = models.Profile.objects.get(user=teacher.user)
            phone = profile.phone
            if phone:
                if new_status == models.Teacher.NOT_CHOSEN:
                    smsUtil.sendSms(phone, '【麻辣老师】很遗憾，您未通过老师初选。')
                elif new_status == models.Teacher.TO_INTERVIEW:
                    smsUtil.sendSms(phone, '【麻辣老师】您已通过初步筛选，请按照约定时间参加面试。')
                elif new_status == models.Teacher.INTERVIEW_OK:
                    smsUtil.sendSms(phone, '【麻辣老师】恭喜您，已通过老师面试，稍后会有工作人员跟您联系。')
                elif new_status == models.Teacher.INTERVIEW_FAIL:
                    smsUtil.sendSms(phone, '【麻辣老师】很遗憾，您未通过老师面试。')
                else:
                    pass
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
        status = self.request.GET.get('status',None)
        searchDateOri = self.request.GET.get('searchDateOri',None)
        searchDateNew = self.request.GET.get('searchDateNew',None)

        query_set = models.TimeSlot.objects.filter(
            Q(deleted=True) |
            Q(transferred_from__isnull=False)
                                                   )
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
        # 类型匹配
        if status == "transfered":
            query_set = query_set.filter(
                trans_to_set__isnull = False
            )
        if status == "suspended":
            query_set = query_set.filter(
                Q(deleted=True) &
                Q(trans_to_set__isnull = True)
            )
        if searchDateOri:
            stTime = datetime.datetime.strptime(searchDateOri, '%Y-%m-%d')
            query_set = query_set.filter(
                Q(trans_to_set__isnull=False) &
                Q(start__date=stTime.date())
            )
        if searchDateNew:
            stTime = datetime.datetime.strptime(searchDateNew, '%Y-%m-%d')
            query_set = query_set.filter(
                Q(deleted=False) &
                Q(start__date=stTime.date())
            )
        # 可用筛选条件数据集
        kwargs['statusList'] = [
            {'text':"全部",'value':""},
            {'text':"调课",'value':"transfered"},
            {'text':"停课",'value':"suspended"},
                                ]
        # 查询结果数据集
        kwargs['timeslots'] = query_set
        return super(StudentScheduleChangelogView, self).get_context_data(**kwargs)

    def get(self, request):
        context = self.get_context_data()
        return render(request, self.template_name, context)


class SchoolsView(BaseStaffView):
    template_name = 'staff/school/schools.html'

    def get_context_data(self, **kwargs):
        context = super(SchoolsView, self).get_context_data(**kwargs)
        schoolId = self.request.GET.get('schoolId')
        center = self.request.GET.get('center')

        query_set = models.School.objects.filter().order_by('-id')
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
        if not schoolId:
            schoolId = self.request.POST.get('schoolId', None)

        if schoolId == 'None':
            schoolId = None

        context['region_list'] = models.Region.objects.filter(opened=True)
        context['schoolId'] = schoolId
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

        # 这个版本写死
        for serviceName in ["问题答疑", "心里辅导", "实时答疑", "自习陪读", "考前串讲", "茶水饮料", "实习报告", "作业辅导"]:
            mbService, created = models.Memberservice.objects.get_or_create(name=serviceName)
            school.member_services.add(mbService)

        school.phone = self.request.POST.get('phone', None)
        school.name = self.request.POST.get('schoolName', None)
        school.center = True if self.request.POST.get('center', '0') == '1' else False
        school.opened = True if self.request.POST.get('opened', '0') == '1' else False
        school.class_seat = self.request.POST.get('class_seat', None)
        school.study_seat = self.request.POST.get('study_seat', None)
        school.longitude = self.request.POST.get('longitude', None)
        school.latitude = self.request.POST.get('latitude', None)
        school.address = self.request.POST.get('address', None)
        regionId = self.request.POST.get('regionId', None)
        school.region = models.Region.objects.get(id=regionId)
        school.save()

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
                date_from = datetime.datetime.strptime(order_date_from, '%Y-%m-%d')
                query_set = query_set.filter(created_at__gte=date_from)
            except:
                pass
        if order_date_to:
            try:
                date_to = datetime.datetime.strptime(order_date_to, '%Y-%m-%d')
                date_to += datetime.timedelta(days=1)
                query_set = query_set.filter(created_at__lte=date_to)
            except:
                pass

        # 可用筛选条件数据集
        kwargs['status'] = models.Order.STATUS_CHOICES
        kwargs['schools'] = models.School.objects.filter(center=True)
        kwargs['grades'] = models.Grade.objects.all()
        kwargs['subjects'] = models.Subject.objects.all()
        # 查询结果数据集
        kwargs['orders'] = query_set
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

        query_set = models.Order.objects.filter()
        # 退费申请区间
        # todo: 需要增加 model 的字段, refund_at, 用于申请退费操作
        if refund_date_from:
            try:
                date_from = datetime.datetime.strptime(refund_date_from, '%Y-%m-%d')
                query_set = query_set.filter(created_at__gte=date_from)
            except:
                pass
        if refund_date_to:
            try:
                date_to = datetime.datetime.strptime(refund_date_to, '%Y-%m-%d')
                date_to += datetime.timedelta(days=1)
                query_set = query_set.filter(created_at__lte=date_to)
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
        # todo: 应该只查询某些状态的订单(待处理: 退费审核中, 已退费: 退费成功, 已驳回: 退费被驳回)
        if status:
            query_set = query_set.filter(status=status)

        # 可用筛选条件数据集
        kwargs['status'] = models.Order.STATUS_CHOICES
        kwargs['subjects'] = models.Subject.objects.all()
        # 查询结果数据集
        # todo: 应该只显示某些状态的订单(待处理: 退费审核中, 已退费: 退费成功, 已驳回: 退费被驳回)
        kwargs['orders'] = query_set
        return super(OrderRefundView, self).get_context_data(**kwargs)


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
                gen.amount = int(amount)
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
            models.Coupon.objects.get_or_create(parent=query_set[0], name=couponName, amount=amount,
                                                mini_course_count=mini_course_count,validated_start=validated_start,
                                                expired_at=expired_at,used=False)

        return JsonResponse({'ok': True, 'msg': 'OK', 'code': 0})
