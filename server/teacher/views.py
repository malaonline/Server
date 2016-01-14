from django.shortcuts import render

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

