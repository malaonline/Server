import logging

# django modules
from django.shortcuts import render, redirect
from django.http import HttpResponse, JsonResponse
from django.views.decorators.http import require_POST
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
    return render(request, 'staff/login.html', context)

def students(request):
    return render(request, 'staff/students.html')

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
