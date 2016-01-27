import logging
import datetime

# django modules
from django.shortcuts import render, redirect, get_object_or_404
from django.http import HttpResponse, JsonResponse
from django.views.decorators.http import require_POST
from django.views.generic import View, TemplateView
from django.utils.decorators import method_decorator
from django.contrib import auth
from django.db.models import Q

# local modules
from app import models
from app.utils import smsUtil
from app.utils.types import parseInt
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
    PAGE_SIZE = 20

    @method_decorator(mala_staff_required)
    def dispatch(self, request, *args, **kwargs):
        return super(BaseStaffView, self).dispatch(request, *args, **kwargs)

    def paginate(self, query_set, page, page_size=0):
        if not page_size:
            page_size = self.PAGE_SIZE
        total_count = query_set.count()
        total_page = (total_count + page_size -1) // page_size
        if not isinstance(page, int):
            if page and isinstance(page, str) and page.isdigit():
                page_to = int(page)
            else:
                page_to = 1
        if page_to > total_page:
            page_to = total_page
        if page_to < 1:
            page_to = 1
        query_set = query_set[(page_to-1)*page_size:page_to*page_size]
        return query_set, {'page': page_to, 'total_page': total_page, 'total_count': total_count}


class BaseStaffActionView(View):
    """
    Base view for staff management action views.
    """

    defaultErrMeg = "操作失败,请稍后重试或联系管理员"

    # @method_decorator(csrf_exempt) # 不加csrf,不允许跨域访问
    @method_decorator(mala_staff_required)
    def dispatch(self, request, *args, **kwargs):
        return super(BaseStaffActionView, self).dispatch(request, *args, **kwargs)


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
        query_set, pager = self.paginate(query_set, page)
        kwargs['teachers'] = query_set
        kwargs['pager'] = pager
        # 一些固定数据
        kwargs['status_choices'] = models.Teacher.STATUS_CHOICES
        kwargs['region_list'] = models.Region.objects.filter(Q(opened=True)|Q(name='其它'))
        return super(TeacherView, self).get_context_data(**kwargs)


class TeacherUnpublishedView(BaseStaffView):
    """
    待上架老师列表view
    """
    template_name = 'staff/teacher/teachers_unpublished.html'

    def get_context_data(self, **kwargs):
        # 把查询参数数据放到kwargs['query_data'], 以便template回显
        kwargs['query_data'] = self.request.GET.dict()
        #
        name = self.request.GET.get('name')
        phone = self.request.GET.get('phone')
        page = self.request.GET.get('page')
        query_set = models.Teacher.objects.filter(status=models.Teacher.INTERVIEW_OK)
        if name:
            query_set = query_set.filter(name__icontains = name)
        if phone:
            query_set = query_set.filter(user__profile__phone__contains = phone)
        query_set = query_set.order_by('id')
        # paginate
        query_set, pager = self.paginate(query_set, page)
        kwargs['teachers'] = query_set
        kwargs['pager'] = pager
        # 一些固定数据
        # 省份列表
        kwargs['provinces'] = models.Region.objects.filter(superset_id__isnull=True)
        kwargs['grades'] = models.Grade.objects.filter(superset_id__isnull=True)
        kwargs['subjects'] = models.Subject.objects.all
        kwargs['levels'] = models.Level.objects.all
        return super(TeacherUnpublishedView, self).get_context_data(**kwargs)


class TeacherUnpublishedEditView(BaseStaffView):
    """
    待上架老师编辑页面view
    """
    template_name = 'staff/teacher/teachers_unpublished_edit.html'

    def get_context_data(self, **kwargs):
        teacherId = kwargs['tid']
        teacher = get_object_or_404(models.Teacher, id=teacherId)
        kwargs['teacher'] = teacher
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
            region = parseInt(region)
            if not region:
                teacher.region = None
            else:
                teacher.region_id = region
            teacher.teaching_age = parseInt(request.POST.get('teaching_age'))
            teacher.level_id = parseInt(request.POST.get('level'))
            teacher.experience = parseInt(request.POST.get('experience'))
            teacher.profession = parseInt(request.POST.get('profession'))
            teacher.interaction = parseInt(request.POST.get('interaction'))
            certIdHeld.save()
            profile.save()
            teacher.save()
            # 科目年级 & 风格标签
            # TODO
            # 头像 & 照片
            # TODO
            # 提分榜
            # TODO
            # 认证
            # TODO
            # 介绍语音, 介绍视频
            # TODO
            # 教学成果
            # TODO
        except Exception as ex:
            logger.error(ex)
            return JsonResponse({'ok': False, 'msg': BaseStaffActionView.defaultErrMeg, 'code': -1})
        return JsonResponse({'ok': True, 'msg': '', 'code': 0})

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
        return HttpResponse("Not supported request.", status=403)

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
        return JsonResponse({'list': weekly_time_slots, 'dailyTimeSlots': models.DAILY_TIME_SLOTS})

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


class SchoolsView(BaseStaffView):
    template_name = 'staff/school/schools.html'

    def get_context_data(self, **kwargs):
        context = super(SchoolsView, self).get_context_data(**kwargs)
        schoolId = self.request.GET.get('schoolId')
        center = self.request.GET.get('center')

        query_set = models.School.objects.filter()
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
        schoolId = self.request.GET.get('schoolId')

        school = None
        if schoolId:
            school = models.School.objects.get(id=schoolId)

        context['school'] = school
        context['schoolId'] = schoolId
        return context


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
