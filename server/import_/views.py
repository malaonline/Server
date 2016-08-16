import logging

# django modules
from django.shortcuts import render, redirect
from django.http import HttpResponse
from django.views.generic import View, TemplateView
from django.utils.decorators import method_decorator
from django.contrib import auth
from django.contrib.auth.mixins import AccessMixin

# local modules
from .decorators import mala_staff_required, is_manager


logger = logging.getLogger('app')


@mala_staff_required
def index(request):
    return redirect('import_:teachers')

class LoginView(View):
    def get(self, request):
        if is_manager(request.user):
            return redirect('import_:index')
        return render(request, 'import_/login.html')

    def post(self, request):
        username = request.POST.get('username')
        password = request.POST.get('password')
        goto_page = request.POST.get('next')
        logger.debug('try to login, username: '+username+', password: '+password+', goto_page: '+str(goto_page))
        # TODO: 错误信息包含‘错误码’，错误描述可能会变
        if not username or not password:
            return login(request, {'errors': '请输入用户名和密码'})
        # 登录前需要先验证
        newUser=auth.authenticate(username=username,password=password)
        if newUser is not None:
            if not is_manager(newUser):
                return login(request, {'errors': '你不是管理员呀'})
            auth.login(request, newUser)
            if goto_page:
                return redirect(goto_page)
            else:
                return redirect('import_:index')
        return login(request, {'errors': '用户名或密码错误'})


def logout(request):
    auth.logout(request)
    return redirect('import_:login')


class StaffRoleRequiredMixin(AccessMixin):
    def dispatch(self, request, *args, **kwargs):
        url_name = self.request.resolver_match.url_name
        for group in self.request.user.groups.all():
            for staff_permission in group.staffpermission_set.all():
                if staff_permission.allowed_url_name == 'all' \
                        or staff_permission.allowed_url_name == url_name:
                    return super(StaffRoleRequiredMixin, self).dispatch(
                            request, *args, **kwargs)

        return HttpResponse("Not Allowed.", status=403)


class BaseStaffView(StaffRoleRequiredMixin, TemplateView):
    """
    Base view for staff management page views.
    """

    @method_decorator(mala_staff_required)
    def dispatch(self, request, *args, **kwargs):
        return super(BaseStaffView, self).dispatch(request, *args, **kwargs)


class BaseStaffActionView(StaffRoleRequiredMixin, View):
    """
    Base view for staff management action views.
    """

    defaultErrMeg = "操作失败,请稍后重试或联系管理员"

    # @method_decorator(csrf_exempt) # 不加csrf,不允许跨域访问
    @method_decorator(mala_staff_required)
    def dispatch(self, request, *args, **kwargs):
        return super(BaseStaffActionView, self).dispatch(
                request, *args, **kwargs)


class TeacherView(BaseStaffView):
    template_name = 'import_/teachers.html'


class ParentView(BaseStaffView):
    template_name = 'import_/parents.html'


class OrderView(BaseStaffView):
    template_name = 'import_/orders.html'
