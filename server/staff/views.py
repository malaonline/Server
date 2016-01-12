import logging
import datetime

# django modules
from django.shortcuts import render, redirect
from django.http import HttpResponse, JsonResponse
from django.views.decorators.csrf import csrf_exempt
from django.views.decorators.http import require_POST
from django.views.generic import View, TemplateView
from django.utils.decorators import method_decorator
from django.contrib import auth

# local modules
from app import models
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
    # @method_decorator(csrf_exempt) # 不加csrf,不允许跨域访问,加上后可用客户端调用
    @method_decorator(require_POST)
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
        region = ''#self.request.GET.get('region')
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
                query_set = query_set.filter(user__date_joined__lte = date_to)
            except:
                pass
        if region and region.isdigit():
            query_set = query_set.filter(region_id = region)
        kwargs['teachers'] = query_set
        # 一些固定数据
        kwargs['status_choices'] = models.Teacher.STATUS_CHOICES
        kwargs['region_list'] = models.Region.objects.filter(opened=True)
        return super(TeacherView, self).get_context_data(**kwargs)

class TeacherActionView(BaseStaffActionView):
    def post(self, request):
        return JsonResponse({'success': True, 'msg': 'TODO', 'code': 0})

class StudentView(BaseStaffView):
    template_name = 'staff/student/students.html'

    def get_context_data(self, **kwargs):
        kwargs['parents'] = models.Parent.objects.all
        kwargs['centers'] = models.School.objects.filter(center=True)
        kwargs['grades'] = models.Grade.objects.all
        kwargs['subjects'] = models.Subject.objects.all
        return super(StudentView, self).get_context_data(**kwargs)

class SchoolView(BaseStaffView):
    template_name = 'staff/school/schools.html'

    def get_context_data(self, **kwargs):
        region = ''
        query_set = models.School.objects.filter()
        if region and region.isdigit():
            query_set = query_set.filter(region_id = region)

        kwargs['schools'] = query_set
        kwargs['region_list'] = models.Region.objects.filter(opened=True)
        return super(SchoolView, self).get_context_data(**kwargs)
