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

