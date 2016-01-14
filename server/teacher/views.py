import logging

# django modules
from django.utils.decorators import method_decorator
from django.contrib.auth.decorators import user_passes_test
from django.http import HttpResponse, JsonResponse
from django.views.generic import View
from django.shortcuts import render

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

class CertificationView(BaseTeacherView):
    """
    certifications overview
    """
    def get(self, request):
        return HttpResponse('TODO')

