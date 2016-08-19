import logging
import re

# django modules
from django.shortcuts import render, redirect
from django.http import HttpResponse, HttpResponseRedirect
from django.views.generic import View, TemplateView
from django.utils.decorators import method_decorator
from django.contrib import auth
from django.contrib.auth.mixins import AccessMixin
from django.core.urlresolvers import reverse
from django.db import IntegrityError, transaction

# local modules
from app import models
from app.utils.db import paginate
from app.utils.excel import read_excel_sheet
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

    def get_context_data(self, **kwargs):
        region = self.request.GET.get('region')
        page = self.request.GET.get('page')

        query_set = models.Teacher.objects.filter(imported=True)
        if region and region.isdigit():
            query_set = query_set.filter(region_id = region)
        query_set = query_set.order_by('-user__date_joined')
        # paginate
        query_set, pager = paginate(query_set, page)
        kwargs['teachers'] = query_set
        kwargs['pager'] = pager
        return super(TeacherView, self).get_context_data(**kwargs)

    def post(self, request):
        region = models.Region.objects.get(name='郑州市') # TODO:
        result_msg = ''
        excel_file = None
        if request.FILES:
            excel_file = request.FILES.get('excel_file')
        if not excel_file:
            result_msg = "请选择文件"
            return HttpResponseRedirect(reverse('import_:teachers') + '?result_msg=' + result_msg)
        datas = read_excel_sheet(file_content=excel_file.read(),titles_list=['name', 'gender', 'phone', 'subject', 'grades'])
        # print(datas)
        Profile = models.Profile
        num = 1
        for row in datas:
            try:
                phone = str(int(row['phone'])) # 电话号码excel读入成为float(XXX.0)了
                has_teacher = models.Teacher.objects.filter(user__profile__phone=phone).exists()
                if has_teacher:
                    result_msg = "第%s个老师(%s)已存在" % (num, phone)
                    break
                gender = row['gender']
                subject = models.Subject.objects.get(name=row['subject'])
                grade_names = row['grades'] and [s for s in re.split('[，, ]', row['grades']) if s != ''] or []
                grades = [models.Grade.objects.get(name=g) for g in grade_names]
                with transaction.atomic():
                    new_user = models.Teacher.new_teacher(phone)
                    new_teacher = new_user.teacher
                    new_teacher.name = row['name']
                    new_user.profile.gender = gender == '男' and Profile.MALE or (gender == '女' and Profile.FEMALE or Profile.UNKNOWN)
                    new_teacher.abilities.clear()
                    ability_set = models.Ability.objects.filter(subject=subject, grade__in=grades)
                    for ability in ability_set:
                        new_teacher.abilities.add(ability)
                    new_teacher.imported = True
                    new_teacher.status = models.Teacher.INTERVIEW_OK
                    new_teacher.region = region
                    new_user.profile.save()
                    new_teacher.save()
            except Exception as ex:
                logger.error(ex)
                result_msg = "导入第%s个老师(%s)时失败" % (num, phone)
                break

        return HttpResponseRedirect(reverse('import_:teachers') + '?result_msg=' + result_msg)


class ParentView(BaseStaffView):
    template_name = 'import_/parents.html'

    def get_context_data(self, **kwargs):
        region = self.request.GET.get('region')
        page = self.request.GET.get('page')

        query_set = models.Parent.objects.filter(imported=True)
        if region and region.isdigit():
            query_set = query_set.filter(region_id = region)
        query_set = query_set.order_by('-user__date_joined')
        # paginate
        query_set, pager = paginate(query_set, page)
        kwargs['parents'] = query_set
        kwargs['pager'] = pager
        return super(ParentView, self).get_context_data(**kwargs)

    def post(self, request):
        region = models.Region.objects.get(name='郑州市') # TODO:
        result_msg = ''
        excel_file = None
        if request.FILES:
            excel_file = request.FILES.get('excel_file')
        if not excel_file:
            result_msg = "请选择文件"
            return HttpResponseRedirect(reverse('import_:parents') + '?result_msg=' + result_msg)
        datas = read_excel_sheet(file_content=excel_file.read(),titles_list=['name', 'phone'])
        # print(datas)
        Profile = models.Profile
        num = 1
        for row in datas:
            try:
                phone = str(int(row['phone'])) # 电话号码excel读入成为float(XXX.0)了
                has_parent = models.Parent.objects.filter(user__profile__phone=phone).exists()
                if has_parent:
                    result_msg = "第%s个学生(%s)已存在" % (num, phone)
                    break
                with transaction.atomic():
                    new_user = models.Parent.new_parent()
                    new_parent = new_user.parent
                    new_user.profile.phone = phone
                    new_parent.student_name = row['name']
                    new_parent.imported = True
                    new_user.profile.save()
                    new_parent.save()
            except Exception as ex:
                logger.error(ex)
                result_msg = "导入第%s个学生(%s)时失败" % (num, phone)
                break

        return HttpResponseRedirect(reverse('import_:parents') + '?result_msg=' + result_msg)


class OrderView(BaseStaffView):
    template_name = 'import_/orders.html'
