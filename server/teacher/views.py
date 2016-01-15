import logging

# django modules
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

class CertificateView(BaseTeacherView):
    """
    certifications overview
    """
    def get(self, request):
        context = {}
        teacher = get_object_or_404(models.Teacher, user=request.user)
        context['teacherName'] = teacher.name
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
    def get(self, request):
        context = {}
        teacher = get_object_or_404(models.Teacher, user=request.user)
        context['teacherName'] = teacher.name
        return render(request, 'teacher/certificate/certificate_id.html', context)