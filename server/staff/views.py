import logging
import datetime

# django modules
from django.db.models.functions import Coalesce
from django.shortcuts import render, redirect
from django.http import HttpResponse, JsonResponse
from django.views.decorators.csrf import csrf_exempt
from django.views.decorators.http import require_POST
from django.views.generic import View, TemplateView
from django.utils.decorators import method_decorator
from django.contrib import auth
from django.db.models import Q

# local modules
from app import models
from app.utils import smsUtil
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

    # @method_decorator(csrf_exempt) # 不加csrf,不允许跨域访问,加上后可用客户端调用
    @method_decorator(require_POST)
    @method_decorator(mala_staff_required)
    def dispatch(self, request, *args, **kwargs):
        return super(BaseStaffActionView, self).dispatch(request, *args, **kwargs)

class TeacherView(BaseStaffView):
    template_name = 'staff/teacher/teachers.html'

    PAGE_SIZE = 20

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
        query_set, page, total_page, total_count = self.paginate(query_set, page)
        kwargs['teachers'] = query_set
        kwargs['page'] = page
        kwargs['total_page'] = total_page
        kwargs['total_count'] = total_count
        # 一些固定数据
        kwargs['status_choices'] = models.Teacher.STATUS_CHOICES
        kwargs['region_list'] = models.Region.objects.filter(opened=True)
        return super(TeacherView, self).get_context_data(**kwargs)

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
        return query_set, page_to, total_page, total_count

class TeacherOfflineView(BaseStaffView):
    """
    待上架老师列表view
    """
    template_name = 'staff/teacher/teachers_offline.html'

class TeacherActionView(BaseStaffActionView):

    NO_TEACHER_FORMAT = "没有查到老师, ID={id}"

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

    def updateTeacherStatus(self, request, new_status):
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

class BackCostView(BaseStaffView):
    template_name = 'staff/order/backcost.html'

    def get_context_data(self, **kwargs):
        return super(BackCostView, self).get_context_data(**kwargs)

class OrderReviewView(BaseStaffView):
    template_name = 'staff/order/review.html'

    def get_context_data(self, **kwargs):

        # 把查询参数数据放到kwargs['query_data'], 以便template回显
        kwargs['query_data'] = self.request.GET.dict()
        print(kwargs['query_data'])
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
