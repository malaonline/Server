import re
import json
import logging

# django modules
from django.shortcuts import render, redirect, get_object_or_404
from django.http import HttpResponse, HttpResponseRedirect, JsonResponse
from django.views.generic import View, TemplateView
from django.utils.decorators import method_decorator
from django.forms import model_to_dict
from django.contrib import auth
from django.core import exceptions
from django.core.urlresolvers import reverse
from django.utils import timezone
from django.db import IntegrityError, transaction

# local modules
from app import models
from app.utils.db import paginate
from .decorators import mala_lecturer_required, is_lecturer
from .serializers import QuestionGroupSerializer, QuestionSerializer

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
    group_serializer = QuestionGroupSerializer()
    question_serializer = QuestionSerializer()

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

    def post(self, request):
        return self.get(request)

    def get(self, request):
        action = self.request_params.get('action')

        if action == 'group_list':
            return self.get_question_group_list()
        elif action == 'group':
            return self.get_questions_of_group()

        return self.json_res(ok=False, code=-1, msg='不支持该方法')

    def get_question_group_list(self):
        lecturer = self.get_lecturer()
        question_groups = models.QuestionGroup.objects.filter(
            deleted=False, created_by=lecturer).order_by('pk')
        gl = [self.group_serializer.to_representation(qg)
              for qg in question_groups]
        return self.json_res(data=gl)

    def get_questions_of_group(self):
        gid = self.request_params.get('gid')
        question_group = models.QuestionGroup.objects.filter(
            pk=gid, deleted=False).first()
        if not question_group:
            return self.json_res(ok=False, code=1, msg="[404]找不到该对象")

        group_dict = self.group_serializer.to_representation(question_group)

        questions = question_group.questions.filter(
            deleted=False).order_by('pk')
        ql = []
        for q in questions:
            q_dict = self.question_serializer.to_representation(q)

            q_opts = q.questionoption_set.all().order_by('pk')
            opt_list = [model_to_dict(o, ['id', 'text']) for o in q_opts]
            q_dict['options'] = opt_list

            ql.append(q_dict)

        group_dict['questions'] = ql
        return self.json_res(data=group_dict)


class LCTimeslotQuestionsView(BaseLectureView):
    '''
    双师直播课程 - 课时题组管理页面
    '''
    template_name = 'lecturer/timeslot/questions.html'

    def get_context_data(self, **kwargs):
        context = super(LCTimeslotQuestionsView, self
                        ).get_context_data(**kwargs)
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
        old_groups = lc_timeslot.question_groups.filter(deleted=False)
        context['old_groups'] = old_groups
        return context

    def post(self, request, *args, **kwargs):
        tsid = kwargs.get('tsid')
        gids = request.POST.get('gids')
        gid_list = gids and gids.split(',') or []
        lcts = get_object_or_404(models.LiveCourseTimeSlot, pk=tsid)
        try:
            with transaction.atomic():
                lcts.question_groups.clear()
                if gid_list:
                    for g in models.QuestionGroup.objects.filter(
                            id__in=gid_list, deleted=False):
                        lcts.question_groups.add(g)
                lcts.save()
        except IntegrityError as err:
            logger.error(err)
            return JsonResponse(
                {'ok': False, 'msg': '操作失败, 请稍后重试或联系管理员', 'code': -1})
        return JsonResponse({'ok': True, 'msg': 'OK', 'code': 0})


class ExerciseStore(BaseLectureView):
    '''
    双师直播课程 - 题库题组管理页面
    '''
    template_name = 'lecturer/exercise/store.html'

    def _build_group(self, params, lecturer):
        '''build half-saved question-group'''
        gid = params.get('id')
        if gid:
            group = models.QuestionGroup.objects.get(id=gid)
        else:
            group = models.QuestionGroup.objects.create(created_by=lecturer)
        group.title = (params.get('title', '')).strip()
        group.description = (params.get('desc', '')).strip()
        return group

    def _build_quesiton(self, q_dict, lecturer):
        '''build half-saved question'''
        qid = q_dict.get('id')
        if qid:
            question = models.Question.objects.get(id=qid)
        else:
            question = models.Question.objects.create(created_by=lecturer)
        question.title = (q_dict.get('title', '')).strip()
        question.explanation = (q_dict.get('analyse', '')).strip()
        return question

    def _build_option(self, o_dict, question):
        '''return saved question-option'''
        oid = o_dict.get('id')
        o_text = (o_dict.get('text', '')).strip()
        if oid:
            q_opt = models.QuestionOption.objects.get(id=oid)
            q_opt.text = o_text
            q_opt.save()
        else:
            q_opt = models.QuestionOption.objects.create(
                question=question, text=o_text)
        return q_opt

    def post(self, request, *args, **kwargs):
        jsonstr = request.POST.get('group')
        try:
            params = json.loads(jsonstr)
        except Exception as ex:
            return JsonResponse({'ok': False, 'msg': '参数格式错误', 'code': 1})
        lecturer = self.get_lecturer()
        try:
            with transaction.atomic():
                group = self._build_group(params, lecturer)
                # 处理题组中的题目
                questions = params.get('exercises')
                group.questions.clear()  # 清除旧的题组关联的题目
                for q in questions:
                    question = self._build_quesiton(q, lecturer)
                    # 处理题目的选项
                    options = q.get('options')
                    solution_str = (q.get('solution', '')).strip()
                    old_optids = [o.id for o in
                                  question.questionoption_set.all()]
                    solution = question.solution
                    for o in options:
                        q_opt = self._build_option(o, question)
                        old_optids = [i for i in old_optids if i != q_opt.id]
                        # 题目的正确选项
                        if q_opt.text == solution_str:
                            solution = q_opt
                    # 处理被删除选项
                    old_optids and models.QuestionOption.objects.filter(
                        id__in=old_optids).delete()
                    question.solution = solution
                    question.save()
                    group.questions.add(question)  # 把题目添加到题组
                group.save()
        except IntegrityError as err:
            logger.error(err)
            return JsonResponse(
                {'ok': False, 'msg': '操作失败, 请稍后重试或联系管理员', 'code': -1})

        return JsonResponse({'ok': True, 'msg': 'OK', 'code': 0})


class TimeslotsView(BaseLectureView):
    '''
    双师直播课程 - 课程安排，课程列表页
    '''
    template_name = 'lecturer/timeslot/list.html'

    def get_context_data(self, **kwargs):
        context = super(TimeslotsView, self).get_context_data(**kwargs)

        context['query_data'] = self.request.GET.dict()
        page = self.request.GET.get('page')
        lecturer = self.request.user.lecturer
        timeslots = models.LiveCourseTimeSlot.objects.filter(
            live_course__lecturer=lecturer)
        timeslots, pager = paginate(timeslots, page)
        page = pager.number
        for idx, timeslot in enumerate(timeslots):
            timeslot.idx = pager.page_size * (page - 1) + idx + 1
            timeslot.status = -1  # 已结束
            if timeslot.start > timezone.now():
                timeslot.status = 1  # 未开始
            elif timeslot.start <= timezone.now() <= timeslot.end:
                timeslot.status = 0  # 进行中
        context['timeslots'] = timeslots
        context['pager'] = pager
        return context


class LivingView(BaseLectureView):
    '''
    双师直播课程 - 开始上课首页
    '''
    template_name = 'lecturer/living/index.html'
