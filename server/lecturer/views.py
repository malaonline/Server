import re
import logging

# django modules
from django.shortcuts import render, redirect
from django.http import HttpResponse, HttpResponseRedirect, JsonResponse
from django.views.generic import View, TemplateView
from django.utils.decorators import method_decorator
from django.contrib import auth
from django.core.urlresolvers import reverse

# local modules
from app import models
from app.utils.db import paginate
from .decorators import mala_lecturer_required, is_lecturer


logger = logging.getLogger('app')


@mala_lecturer_required
def home(request):
    return redirect('lecturer:index')


class LoginView(View):
    def get(self, request):
        if is_lecturer(request.user):
            return redirect('lecturer:index')
        return render(request, 'lecturer/login.html')

    def post(self, request):
        var = ('username', 'password',)
        vard = {}

        for k in var:
            v = request.POST.get(k, '')
            if not v:
                return JsonResponse({'error': k + ' is empty'})
            vard[k] = v

        user = auth.authenticate(
                username=vard['username'], password=vard['password'])
        if user is None:
            return JsonResponse({'error': 'username or password incorrect'})
        if not is_lecturer(user):
            return JsonResponse({'error': 'you are not authorized'})

        auth.login(request, user)
        return redirect('lecturer:index')


def logout(request):
    auth.logout(request)
    return redirect('lecturer:login')


class BaseLectureView(TemplateView):
    """
    Base view for lecturer management page views.
    """

    @method_decorator(mala_lecturer_required)
    def dispatch(self, request, *args, **kwargs):
        return super(BaseLectureView, self).dispatch(request, *args, **kwargs)


class IndexView(BaseLectureView):
    template_name = 'lecturer/index.html'


class LCTimeslotQuestionsView(BaseLectureView):
    '''
    双师直播课程-课时题组管理页面
    '''
    template_name = 'lecturer/timeslot/questions.html'

    def get_context_data(self, **kwargs):
        context = super(LCTimeslotQuestionsView, self).get_context_data(**kwargs)
        tsid = context.get('tsid')
        return context

