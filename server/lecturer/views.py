import re
import logging

# django modules
from django.shortcuts import render, redirect
from django.http import HttpResponse, HttpResponseRedirect, JsonResponse
from django.views.generic import View, TemplateView
from django.utils.decorators import method_decorator
from django.forms import model_to_dict
from django.contrib import auth
from django.core import exceptions
from django.core.urlresolvers import reverse
from django.utils import timezone

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


class LecturerBasedMixin(object):
    def get_lecturer(self):
        try:
            lecturer = self.request.user.lecturer
        except (AttributeError, exceptions.ObjectDoesNotExist) as e:
            raise e
        return lecturer


class BaseLectureView(LecturerBasedMixin, TemplateView):
    """
    Base view for lecturer management page views.
    """

    @method_decorator(mala_lecturer_required)
    def dispatch(self, request, *args, **kwargs):
        return super(BaseLectureView, self).dispatch(request, *args, **kwargs)


class IndexView(BaseLectureView):
    template_name = 'lecturer/index.html'


class ApiExerciseStore(LecturerBasedMixin, View):
    '''
    题库接口API
    提供题组列表、题组内题目列表等接口
    '''
    _params = None

    @method_decorator(mala_lecturer_required)
    def dispatch(self, request, *args, **kwargs):
        return super(ApiExerciseStore, self).dispatch(request, *args, **kwargs)

    def json_res(self, ok=True, code=0, msg='', data=None):
        return JsonResponse(dict(ok=ok, code=code, msg=msg, data=data))

    @property
    def request_params(self):
        if self._params is None:
            _p = self.request.GET.copy()
            _p.update(self.request.POST)
            self._params = _p
        return self._params

    def get(self, request):
        action = self.request_params.get('action')

        if action == 'group_list':
            return self.get_question_group_list()

        return self.json_res(ok=False, code=-1, msg='不支持该方法')

    def get_question_group_list(self):
        lecturer = self.get_lecturer()
        question_groups = models.QuestionGroup.objects.filter(
            deleted=False, created_by=lecturer)
        gl = [model_to_dict(qg, fields=['id', 'title', 'description'])
              for qg in question_groups]
        return self.json_res(data=gl)


class LCTimeslotQuestionsView(BaseLectureView):
    '''
    双师直播课程 - 课时题组管理页面
    '''
    template_name = 'lecturer/timeslot/questions.html'

    def get_context_data(self, **kwargs):
        context = super(LCTimeslotQuestionsView, self).get_context_data(**kwargs)
        tsid = context.get('tsid')
        lc_timeslot = models.LiveCourseTimeSlot.objects.filter(pk=tsid).first()
        context['lc_timeslot'] = lc_timeslot
        if not lc_timeslot:
            context['error_msg'] = "未找到该课时"
            return context
        lecturer = self.get_lecturer()
        lc = lc_timeslot.live_course
        context['course_name'] = lc.name
        context['lecturer_name'] = lc.lecturer.name
        context['date'] = timezone.localtime(
            lc_timeslot.start).strftime('%Y-%m-%d')
        context['start'] = timezone.localtime(
            lc_timeslot.start).strftime('%H:%M')
        context['end'] = timezone.localtime(
            lc_timeslot.end).strftime('%H:%M')
        return context


class ExerciseStore(BaseLectureView):
    '''
    双师直播课程 - 题库题组管理页面
    '''
    template_name = 'lecturer/questions/index.html'

