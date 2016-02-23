import logging

# django modules
from django.core.files.base import ContentFile
from django.utils.decorators import method_decorator
from django.contrib.auth.decorators import user_passes_test, login_required
from django.contrib.auth import login, authenticate, _get_backends, logout
from django.http import HttpResponse, JsonResponse, HttpResponseRedirect
from django.core.urlresolvers import reverse
from django.views.generic import View
from django.shortcuts import render, get_object_or_404, redirect
from django.contrib.auth.models import User
from django.conf import settings
from django.utils.timezone import make_aware, localtime

from dateutil import relativedelta

import calendar
import json
import datetime
from pprint import pprint as pp

# local modules
from app import models
from . import forms
from app.templatetags.custom_tags import money_format
from app.utils.db import paginate

logger = logging.getLogger('app')

# Create your views here.

# 目前老师端的公共登录url,这里不能用reverse,不然会发生循环引用
LOGIN_URL = "/teacher/login"


class TeacherLogin(View):
    """
    老师用户注册页面 TW-1-1
    """

    def get(self, request):
        context = {}
        return render(request, 'teacher/register.html', context)


class VerifySmsCode(View):
    """
    检查短信验证码是否正确
    """

    def post(self, request):
        phone = request.POST.get("phone", None)
        code = request.POST.get("code", None)
        Profile = models.Profile
        CheckCode = models.Checkcode
        Teacher = models.Teacher
        new_user = True
        try:
            profile = Profile.objects.get(phone=phone)
            user = profile.user
            for backend, backend_path in _get_backends(return_tuples=True):
                user.backend = backend_path
                break
            teacher = Teacher.objects.get(user=user)
            new_user = False
        except Profile.DoesNotExist:
            # new user
            user = Teacher.new_teacher()
            teacher = user.teacher
            profile = teacher.user.profile
            profile.phone = phone
            profile.save()
        if CheckCode.verify_sms(phone, code) is True:
            # 验证通过
            percent = information_complete_percent(user)
            login(request, user)

            if percent < 1:
                # 如果老师信息没有完成,就填写老师信息
                return JsonResponse({
                    "result": True,
                    "url": reverse("teacher:complete-information")
                })
            else:
                if teacher.status != Teacher.INTERVIEW_OK:
                    return JsonResponse({
                        "result": True,
                        "url": reverse("teacher:register-progress")
                    })
                else:
                    return JsonResponse({
                        "result": True,
                        "url": reverse("teacher:first-page")
                    })
        else:
            # 验证失败
            return JsonResponse({
                "result": False
            })


def information_complete_percent(user: User):
    """
    计算用户信息完成度
    :param user:
    :return:
    """
    total = 4
    unfinished = 0
    Teacher = models.Teacher
    Profile = models.Profile
    teacher = Teacher.objects.get(user=user)
    profile = Profile.objects.get(user=user)
    if teacher.name == "":
        unfinished += 1
    if profile.gender == "u":
        unfinished += 1
    if teacher.region == None:
        unfinished += 1
    if len(teacher.abilities.all()) == 0:
        unfinished += 1
    return (total - unfinished) / total


# 完善老师的个人信息 TW-2-1
class CompleteInformation(View):
    def get(self, request):
        user = request.user
        teacher = models.Teacher.objects.get(user=user)
        profile = models.Profile.objects.get(user=user)

        name = teacher.name
        gender_dict = {"f": "女", "m": "男", "u": ""}
        gender = gender_dict.get(profile.gender, "")
        if teacher.region:
            region = teacher.region.name or ""
        else:
            region = ""
        ability_set_all = teacher.abilities.all()
        phone = profile.mask_phone()
        if len(ability_set_all) > 0:
            subclass = ability_set_all[0].subject.name
        else:
            subclass = ""
        # 初始化年级
        grade = [[False for i in range(6)],
                 [False for i in range(3)],
                 [False for i in range(3)]]
        grade_name = models.Grade.get_all_grades()
        grade_slot = {}
        for x, one_level in enumerate(grade_name):
            for y, one_grade in enumerate(one_level):
                grade_slot[one_grade] = (x, y)

        grade_list = [item.grade.name for item in list(teacher.abilities.all())]
        for one_grade in grade_list:
            x, y = grade_slot.get(one_grade, (0, 0))
            grade[x][y] = True

        context = {
            "name": name,
            "gender": gender,
            "region": region,
            "subclass": subclass,
            "grade": json.dumps(grade),
            "phone_name": phone
        }
        return render(request, 'teacher/complete_information.html', context)

    def post(self, request):
        user = request.user
        teacher = models.Teacher.objects.get(user=user)
        profile = models.Profile.objects.get(user=user)

        name = request.POST.get("name", "")
        gender = request.POST.get("gender", "")
        region = request.POST.get("region")
        subject = request.POST.get("subclass")
        grade = request.POST.get("grade")

        grade_list = json.loads(grade)

        teacher.name = name
        gender_dict = {"男": "m", "女": "f"}
        profile.gender = gender_dict.get(gender, "u")
        teacher.region = models.Region.objects.get(name=region)

        the_subject = models.Subject.objects.get(name=subject)
        grade_name_list = models.Grade.get_all_grades()
        page_grade_list = [["小学一年级", "小学二年级", "小学三年级", "小学四年级", "小学五年级", "小学六年级"],
                           ["初一", "初二", "初三"],
                           ["高一", "高二", "高三"]]
        grade_dict = {}
        for page_level, database_level in list(zip(page_grade_list, grade_name_list)):
            for page_grade, database_grade in list(zip(page_level, database_level)):
                grade_dict[page_grade] = database_grade

        # clear ability_set
        teacher.abilities.clear()

        for one_grade in grade_list:
            the_grade = models.Grade.objects.get(name=grade_dict.get(one_grade, one_grade))
            try:
                ability = models.Ability.objects.get(grade=the_grade, subject=the_subject)
            except models.Ability.DoesNotExist:
                # 如果这个年级不存在就跳过
                continue
            teacher.abilities.add(ability)
            ability.save()

        teacher.save()
        profile.save()

        return JsonResponse({"url": reverse("teacher:register-progress")})


class RegisterProgress(View):
    """
    显示注册进度
    """

    def get(self, request):
        context = {}
        try:
            teacher = models.Teacher.objects.get(user=request.user)
        except models.Teacher.DoesNotExist:
            return HttpResponseRedirect(reverse("teacher:register"))

        if settings.FIX_TEACHER_STATUS:
            teacher.status = teacher.INTERVIEW_OK
        context["progress"] = teacher.get_progress()
        context["text_list"] = teacher.build_progress_info()
        context["user_name"] = "{name} 老师".format(name=teacher.name)
        return render(request, "teacher/register_progress.html", context)


# 设置老师页面的通用上下文
def set_teacher_page_general_context(teacher, context):
    context["user_name"] = "{name} 老师".format(name=teacher.name)


class FirstPage(View):
    """
    通过面试的老师见到的第一个页面
    """

    def get(self, request):
        user = request.user
        teacher = models.Teacher.objects.get(user=user)
        profile = models.Profile.objects.get(user=user)
        order_set = models.Order.objects.filter(teacher=teacher)
        gce = self.comprehensive_evaluation(order_set)
        context = {
            "avatar": self.avatar(profile),
            "class_complete": self.class_complete(order_set),
            "class_waiting": self.class_waiting(order_set),
            "student_on_class": self.student_on_class(order_set),
            "student_complete": self.student_complete(order_set),
            "comprehensive_evaluation": "{:2.1f}".format(gce.average_score()),
            "bad_review": gce.bad_commit_count(),
            "account_balance": self.account_balance(teacher),
            "total_revenue": self.total_revenue(order_set),
            "teacher_level": self.teacher_level(),
            "information_complete_percent": self.information_complete_percent(),
            "complete_url": self.complete_url(),
        }
        set_teacher_page_general_context(teacher, context)
        sider_bar_content = SideBarContent(teacher)
        sider_bar_content(context)
        return render(request, "teacher/first_page.html", context)

    def avatar(self, profile):
        if profile.avatar:
            return profile.avatar.url
        else:
            return 'common/icons/none_body_profile.png'

    def class_complete(self, order_set, current_data=make_aware(datetime.datetime.now())):
        # 已上课数量
        complete_count = 0
        for one_order in order_set:
            if one_order.fit_statistical():
                # 处于PAID状态的单子才统计
                time_slot_set = one_order.timeslot_set.filter(deleted=False)
                for one_time_slot in time_slot_set:
                    if one_time_slot.is_complete(current_data):
                        complete_count += 1
        class_complete = complete_count
        return class_complete

    def class_waiting(self, order_set, current_data=make_aware(datetime.datetime.now())):
        # 待上课数量
        waiting_count = 0
        for one_order in order_set:
            if one_order.fit_statistical():
                # 处于PAID状态的单子才统计
                time_slot_set = one_order.timeslot_set.filter(deleted=False)
                for one_time_slot in time_slot_set:
                    if one_time_slot.is_waiting(current_data):
                        waiting_count += 1
        class_waiting = waiting_count
        return class_waiting

    def student_on_class(self, order_set, current_data=make_aware(datetime.datetime.now())):
        # 上课中的学生,需要先汇总学生的所有订单,然后判断学生是否在上课
        parent_set = set()
        parent_dict = {}
        for one_order in order_set:
            # 遍历所有的单子
            if one_order.fit_statistical():
                # 处于PAID状态的单子才统计
                parent_id = one_order.parent.id
                parent_time_slot = parent_dict.get(parent_id, [])
                parent_time_slot += list(one_order.timeslot_set.filter(deleted=False))
                parent_dict[parent_id] = parent_time_slot
        for parent_id, timeslot_list in parent_dict.items():
            time_slot_set = timeslot_list
            has_waiting_class = False
            has_complete_class = False
            for one_time_slot in time_slot_set:
                if has_waiting_class is True and has_complete_class is True:
                    # Order符合上课中的要求
                    parent_set.add(parent_id)
                    # 记录下来后直接跳出,看下一个order
                    break
                if one_time_slot.is_waiting(current_data):
                    # 找到一堂没有上的课
                    has_waiting_class = True
                if one_time_slot.is_complete(current_data):
                    # 找到一堂已经上的课
                    has_complete_class = True

        student_on_class = len(parent_set)

        return student_on_class

    def student_complete(self, order_set, current_data=make_aware(datetime.datetime.now())):
        # 已结课的学生,所有的time_slot都是完成的就算结课了,要先合并订单
        parent_set = set()
        parent_dict = {}
        for one_order in order_set:
            # 遍历所有的单子
            if one_order.fit_statistical():
                # 处于PAID状态的单子才统计
                parent_id = one_order.parent.id
                parent_time_slot = parent_dict.get(parent_id, [])
                parent_time_slot += list(one_order.timeslot_set.filter(deleted=False))
                parent_dict[parent_id] = parent_time_slot
        for parent_id, timeslot_list in parent_dict.items():
            time_slot_set = timeslot_list
            complete_order = True
            for one_time_slot in time_slot_set:
                if one_time_slot.is_complete(current_data) is False:
                    # 只要有一堂课处于未完成状态,就不符合要求
                    complete_order = False
                    break
            if complete_order is True:
                parent_set.add(parent_id)
        student_complete = len(parent_set)
        return student_complete

    class CollectOrderInfo:
        def __init__(self, current_data=make_aware(datetime.datetime.now())):
            self.score_list = []
            self.bad_commit = []
            self.current_date = current_data

        def __call__(self, one_timeslot: models.TimeSlot):
            if one_timeslot.is_complete(self.current_date):
                comment = one_timeslot.comment
                if comment:
                    self.score_list.append(comment.score)
                    if comment.is_bad_comment():
                        self.bad_commit.append(comment.score)

        def average_score(self):
            try:
                return sum(self.score_list) / len(self.score_list)
            except ZeroDivisionError:
                return 0

        def bad_commit_count(self):
            return len(self.bad_commit)

    def comprehensive_evaluation(self, order_set, current_data=make_aware(datetime.datetime.now())):
        # 综合评分,求所有timeslot的平均分,以及差评数
        gce = FirstPage.CollectOrderInfo()
        for one_order in order_set:
            # PAID过的才统计
            if one_order.fit_statistical():
                one_order.enum_timeslot(gce)
        return gce

    def account_balance(self, teacher: models.Teacher):
        # 账户余额
        account_balance = teacher.safe_get_account().balance
        return "¥{price}".format(price=account_balance)

    def total_revenue(self, order_set):
        # 累计收入
        revenue = 0
        for one_order in order_set:
            if one_order.fit_statistical():
                revenue += one_order.price * one_order.total
        revenue = revenue / 10000
        return "¥{revenue}万".format(revenue=revenue)

    def teacher_level(self):
        # 教师等级
        teacher_level = "中级教师"
        return "麻辣{teacher_level}".format(teacher_level=teacher_level)

    def information_complete_percent(self):
        # 资料完成度
        percent = 70
        return percent

    def complete_url(self):
        # 跳转到需要完善的url
        return reverse("teacher:basic_doc")


class SideBarContent:
    """
    专门用来填充边栏的内容
    """

    def __init__(self, teacher):
        self.teacher = teacher
        self.order_set = models.Order.objects.filter(teacher=teacher)

    def __call__(self, context: dict):
        """
        填充context
        :param context:
        :return:
        """
        context["side_bar_my_course"] = self._my_course_badge()
        context["side_bar_my_student"] = self._my_student_badge()
        context["side_bar_my_evaluation"] = self._my_evaluation_badge()
        context["side_bar_basic_data_notify"] = self._basic_data_notify()
        today = datetime.datetime.now()
        context["side_bar_my_school_time_url"] = reverse("teacher:my-school-timetable",
                                                         kwargs={"year": today.year,
                                                                 "month": "{day:02d}".format(day=today.month)}
                                                         )
        context["side_bar_my_student_url"] = reverse("teacher:my-students",
                                                     kwargs={"student_type": 0, "page_offset": 1})
        context["side_bar_my_evaluation"] = reverse("teacher:my-evaluation",
                                                    kwargs={"comment_type": 0, "page_offset": 1})

    def _my_course_badge(self):
        # 我的课表旁边的徽章
        my_course = 89
        return my_course

    def _my_student_badge(self):
        # 我的学生旁边的徽章
        my_student = 79
        return my_student

    def _my_evaluation_badge(self):
        # 我的评价旁边的徽章
        my_evaluation = 15
        return my_evaluation

    def _basic_data_notify(self):
        basic_data_notify = True
        return basic_data_notify


class MySchoolTimetable(View):
    """
    TW-5-1, 查看课表上的内容
    """

    class CollectTimeSlot:
        """
        用来遍历和分类TimeSlot
        """
        # 时间格式
        time_formula = "%Y%m%d"

        def __init__(self):
            self.time_slot_dict = {}

        def __call__(self, one_time_slot: models.TimeSlot):
            start_day = one_time_slot.start.strftime(self.time_formula)
            self.time_slot_dict[start_day] = self.time_slot_dict.get(start_day, 0) + 1

        def specific_day_count(self, specific_day: datetime.datetime) -> int:
            # 得到指定某天的数量
            day_key = specific_day.strftime(self.time_formula)
            return self.time_slot_dict.get(day_key, 0)

    class GetTimeSlotProgress:
        """
        遍历slot来获得上课进度
        """

        def __init__(self):
            self.complete_class = 0
            self.total_class = 0
            self.today = make_aware(datetime.datetime.now())

        def __call__(self, one_time_slot: models.TimeSlot):
            if one_time_slot.is_complete(self.today):
                self.complete_class += 1
            self.total_class += 1

    class OneCourseDetail:
        """
        生成一条上课描述
        """

        def __call__(self, time_slot: models.TimeSlot):
            order = self.order
            today = self.today
            # 上课时间
            time_slot_start = localtime(time_slot.start)
            time_slot_end = localtime(time_slot.end)
            self.duration = "{start_hour:02d}:{start_min:02d}-{end_hour:02d}:{end_min:02d}".format(
                start_hour=time_slot_start.hour, start_min=time_slot_start.minute,
                end_hour=time_slot_end.hour, end_min=time_slot_end.minute)
            # 上课级别
            self.subclass_level = "{grade}{subject}".format(grade=order.grade, subject=order.subject)
            # 学生名称
            self.student_name = order.parent.student_name
            # 上课地点
            self.center = order.school.name
            # 课程进度
            gtsp = MySchoolTimetable.GetTimeSlotProgress()
            order.enum_timeslot(gtsp)
            self.progress = "第{complete}/{total}次课".format(complete=gtsp.complete_class, total=gtsp.total_class)
            # 默认评价为未评价,只有课程上完才去检查评价
            # 评价状态, 0-未评价, 1-已评价
            self.comment_state = 0
            # 上课状态, 0-待上课, 1-上课中, 2-已完成
            if time_slot.is_waiting(today):
                # 等待中的课程
                self.class_state = 0
            if time_slot.is_running(today):
                # 进行中的课程
                self.class_state = 1
            if time_slot.is_complete(today):
                # 结束的课程
                self.class_state = 2
                # 上完课才做评价检查
                if time_slot.comment:
                    self.comment_state = 1
            # 获得这个TimeSlot的key
            self.key = time_slot.start.strftime(MySchoolTimetable.CollectTimeSlot.time_formula)
            # start时间,用于排序
            self.start = time_slot.start

        def __init__(self, order: models.Order, today=make_aware(datetime.datetime.now())):
            self.order = order
            self.today = today
            # 上课时间
            self.duration = ""
            # 上课级别
            self.subclass_level = ""
            # 学生名称
            self.student_name = ""
            # 上课地点
            self.center = ""
            self.progress = ""
            # 默认评价为未评价,只有课程上完才去检查评价
            # 评价状态, 0-未评价, 1-已评价
            self.comment_state = 0
            # 上课状态, 0-待上课, 1-上课中, 2-已完成
            self.class_state = 0
            # 开始时间
            self.start = ""
            # 获得这个TimeSlot的key
            self.key = ""

        def dict_data(self):
            return {"duration": self.duration, "subclass_level": self.subclass_level,
                    "student_name": self.student_name, "center": self.center,
                    "progress": self.progress, "class_state": self.class_state,
                    "comment_state": self.comment_state, "start": self.start}

    class CourseSet:
        # 用来收集每条TimeSlot
        def __init__(self, the_order: models.Order, today=make_aware(datetime.datetime.now())):
            self.order = the_order
            self.time_slot_dict = {}
            self.today = today

        def __call__(self, time_slot: models.TimeSlot):
            ocd = MySchoolTimetable.OneCourseDetail(self.order, self.today)
            ocd(time_slot)
            slot_list = self.time_slot_dict.get(ocd.key, [])
            slot_list.append(ocd.dict_data())
            self.time_slot_dict[ocd.key] = slot_list

    def get_course_plan(self, order: models.Order):
        # 得到一个订单里所有TimeSlot的映射
        cs = MySchoolTimetable.CourseSet(order)
        order.enum_timeslot(cs)
        return cs.time_slot_dict

    def merge_dict(self, source: dict, dest: dict):
        """
        合并两个字典,主要处理val是list的合并情况
        :param source: {str:list}
        :param dest: {str:list}
        :return:
        """
        for key, val in dest.items():
            old_data_set = source.get(key, [])
            old_data_set += val
            source[key] = old_data_set

    def clear_up_time_slot_set(self, time_slot_set: dict, month_start: datetime.datetime, month_end: datetime.datetime):
        # 清理time_slot
        # 移除不是本月的键值
        remove_key = []
        for key, val in time_slot_set.items():
            current_time = make_aware(datetime.datetime.strptime(key, MySchoolTimetable.CollectTimeSlot.time_formula))
            if month_start <= current_time <= month_end:
                # 对符合要求的进行排序
                time_slot_set[key] = sorted(val, key=lambda item: item["start"])
                # 移除datetime
                for one_val in time_slot_set[key]:
                    one_val.pop("start", "")
            else:
                # 记录下不符合要求的
                remove_key.append(key)
        # 移除不符合要求
        for key in remove_key:
            time_slot_set.pop(key)

    def get(self, request, year, month):
        # 思路,集中订单中的每堂课,映射到当月的日期中,由每天上课的数量来日期的状态.
        user = request.user
        teacher = models.Teacher.objects.get(user=user)
        # 获得这个老师的每堂课
        order_set = models.Order.objects.filter(teacher=teacher)
        # 用于记录页面右边的数据
        time_slot_details = {}
        cts = MySchoolTimetable.CollectTimeSlot()
        for one_order in order_set:
            if one_order.fit_school_time():
                # 已经付过费的才统计
                one_order.enum_timeslot(cts)
                self.merge_dict(time_slot_details, self.get_course_plan(one_order))
        # 整理数据
        year = int(year)
        month = int(month)
        cal = calendar.Calendar()
        one_month = [item for item in cal.itermonthdates(year, month)]
        one_week = []
        one_month_with_weeks = []
        today = make_aware(datetime.datetime.now())
        # 清理time_slot_details
        self.clear_up_time_slot_set(time_slot_details,
                                    make_aware(
                                        datetime.datetime(one_month[0].year, one_month[0].month, one_month[0].day, 0, 0,
                                                          0)),
                                    make_aware(
                                        datetime.datetime(one_month[-1].year, one_month[-1].month, one_month[-1].day,
                                                          23, 59, 59)), )
        week_day_map = ["星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日", ]
        for index, day_item in enumerate(one_month):
            # 0-当天没课, 1-已经上过, 2-正在上,3-还没上
            day_statue = 0
            given_day = make_aware(datetime.datetime(day_item.year, day_item.month, day_item.day, 23, 59, 59))
            if cts.specific_day_count(given_day) > 0:
                if given_day > today:
                    day_statue = 3
                if given_day == today:
                    day_statue = 2
                if given_day < today:
                    day_statue = 1
            else:
                day_statue = 0
            # 以今天为准,是不是过去的日子
            is_pass = False
            if given_day < today:
                is_pass = True
            else:
                is_pass = False
            one_week.append(("{day:02d}".format(day=day_item.day), day_statue, is_pass,
                             day_item.strftime(MySchoolTimetable.CollectTimeSlot.time_formula),
                             day_item.strftime("%Y年%m月%d日"), week_day_map[len(one_week)], len(one_week)
                             ))
            if len(one_week) == 7:
                one_month_with_weeks.append(one_week)
                one_week = []
        # 得到下一个月和前一个月的url
        current_month = datetime.datetime(year, month, 1)
        pre_month = current_month - relativedelta.relativedelta(months=1)
        pre_month_url = reverse("teacher:my-school-timetable",
                                kwargs={"year": pre_month.year, "month": "{day:02d}".format(day=pre_month.month)})
        next_month = current_month + relativedelta.relativedelta(months=1)
        next_month_url = reverse("teacher:my-school-timetable",
                                 kwargs={"year": next_month.year, "month": "{day:02d}".format(day=next_month.month)})
        context = {
            "data_list": one_month_with_weeks,
            "current": current_month,
            "pre_month_url": pre_month_url,
            "next_month_url": next_month_url,
            "dynamic_title": "我的课表 {year}-{month}".format(year=year, month=month),
            "time_slot_data": json.dumps(time_slot_details),
        }
        set_teacher_page_general_context(teacher, context)
        side_bar_content = SideBarContent(teacher)
        side_bar_content(context)
        return render(request, "teacher/my_school_timetable.html", context)


class MyStudents(View):
    """
    TW-5-2, 我的学生
    """

    class CollectTimeSlotGroupByStudent:
        """
        收集所有Order的所有TimeSlot,并按照学生进行分组
        """

        def __init__(self):
            self.group_by_student = {}
            self.order = None

        def __call__(self, time_slot: models.TimeSlot):
            """
            用dict来归类学生
            """
            order = self.order
            one_student_description = self.group_by_student.get(order.parent.student_name, {})
            if not one_student_description:
                # 需要初始化一些数据
                one_student_description["name"] = order.parent.student_name
                one_student_description["grade"] = order.grade
                one_student_description["price"] = "￥{price}/小时".format(price=order.price)
            old_data_array = one_student_description.get("time_slot_list", [])
            # print("old_data_array's type is {old_data_array}".format(old_data_array=old_data_array))
            old_data_array.append(time_slot)
            new_data_array = old_data_array
            one_student_description["time_slot_list"] = new_data_array
            self.group_by_student[order.parent.student_name] = one_student_description

    class GetTimeSlotProgress(MySchoolTimetable.GetTimeSlotProgress):
        """
        统计time slot的进度
        """
        pass

    def get(self, request, student_type, page_offset):
        user = request.user
        teacher = models.Teacher.objects.get(user=user)

        offset = int(page_offset)

        student_state = {0: ["新生", "正常", "续费"], 1: ["结课"], 2: ["退费"]}
        filter_student_state = student_state[int(student_type)]
        student_list, total_page = self.current_student(teacher, filter_student_state, 11, offset)

        default_page_list = [[item + 1, False, reverse("teacher:my-students",
                                                       kwargs={"student_type": student_type, "page_offset": item + 1})]
                             for item in range(total_page)]
        default_page_list[offset - 1][1] = True
        context = {
            "student_list": student_list,
            "page_list": default_page_list
        }
        set_teacher_page_general_context(teacher, context)
        side_bar_content = SideBarContent(teacher)
        side_bar_content(context)
        # 设置三种学生的url
        current_student_url = reverse("teacher:my-students", kwargs={"student_type": 0, "page_offset": 1})
        class_ending_student_url = reverse("teacher:my-students", kwargs={"student_type": 1, "page_offset": 1})
        refund_student_url = reverse("teacher:my-students", kwargs={"student_type": 2, "page_offset": 1})
        context["current_student_url"] = current_student_url
        context["class_ending_student_url"] = class_ending_student_url
        context["refund_student_url"] = refund_student_url
        context["student_type"] = student_type
        # pp(context)
        return render(request, "teacher/my_students.html", context)

    def refund_student(self, teacher: models.Teacher, page_size=11, offset=1):
        # 提取退费的单子,然后合并出结果
        ctsgb = MyStudents.CollectTimeSlotGroupByStudent()
        for order in models.Order.objects.filter(teacher=teacher, status=models.Order.REFUND):
            ctsgb.order = order
            order.enum_timeslot(ctsgb)
        student_list = []
        for name, des in ctsgb.group_by_student.items():
            gtsp = MyStudents.GetTimeSlotProgress()
            for time_slot_list in des["time_slot_list"]:
                gtsp(time_slot_list)
            des["progress"] = "{complete}/{total}".format(complete=gtsp.complete_class,
                                                          total=gtsp.total_class)
            percent = gtsp.complete_class / gtsp.total_class
            des["state"] = "退费"
            # 填充结果
            one_row = [name, des["grade"], des["progress"], des["price"], des["state"], True]
            student_list.append(one_row)
        # 分页切割
        partition_list = split_list(student_list, page_size)
        if not partition_list:
            # 设置一个空的结果
            partition_list = [[]]
        return partition_list[offset - 1], len(partition_list)

    def current_student(self, teacher: models.Teacher, filter_student_state: list, page_size=11, offset=1):
        if "退费" in filter_student_state:
            return self.refund_student(teacher, page_size, offset)
        else:
            return self.normal_student(teacher, filter_student_state, page_size, offset)

    def normal_student(self, teacher: models.Teacher, filter_student_state: list, page_size=11, offset=1):
        # 思路,先汇总订单,然后按照学生分类,再进行分页和定位当前的位置
        ctsgb = MyStudents.CollectTimeSlotGroupByStudent()
        for order in models.Order.objects.filter(teacher=teacher):
            if order.status == models.Order.PAID:
                ctsgb.order = order
                order.enum_timeslot(ctsgb)
        student_list = []
        for name, des in ctsgb.group_by_student.items():
            gtsp = MyStudents.GetTimeSlotProgress()
            for time_slot_list in des["time_slot_list"]:
                gtsp(time_slot_list)
            des["progress"] = "{complete}/{total}".format(complete=gtsp.complete_class,
                                                          total=gtsp.total_class)
            percent = gtsp.complete_class / gtsp.total_class
            if percent == 0:
                des["state"] = "新生"
            if 0 < percent < 0.8:
                des["state"] = "正常"
            if 0.8 <= percent < 1:
                des["state"] = "续费"
            if percent == 1:
                des["state"] = "结课"
            # 填充结果
            one_row = [name, des["grade"], des["progress"], des["price"], des["state"], True]
            student_list.append(one_row)
        # 过滤学生
        filter_student_list = []
        for one_student in student_list:
            if one_student[4] in filter_student_state:
                filter_student_list.append(one_student)
        # 分页切割
        partition_list = split_list(filter_student_list, page_size)
        if not partition_list:
            # 设置一个空的结果
            partition_list = [[]]
        return partition_list[offset - 1], len(partition_list)

        # student_list = [
        #     ["胡晓璐", "初二", "0/20", "￥190/小时", "新生", True],
        #     ["胡晓璐", "小学一年级", "8/10", "￥190/小时", "续费", False],
        #     ["胡晓璐", "高一", "9/20", "￥190/小时", "正常", True],
        #     ["胡晓璐", "高三", "12/100", "￥190/小时", "正常", True],
        #     ["胡晓璐", "高二", "7/20", "￥190/小时", "正常", True],
        #     ["胡晓璐", "初二", "14/15", "￥190/小时", "续费", True],
        #     ["张子涵", "小学二年级", "8/10", "￥190/小时", "退费", False],
        #     ["汪小菲", "小学六年级", "19/20", "￥190/小时", "续费", False],
        #     ["孙大圣", "小学一年级", "12/100", "￥190/小时", "正常", False],
        #     ["刘宇", "高一", "20/20", "￥190/小时", "结课", False],
        #     ["赵一曼", "高三", "15/15", "￥190/小时", "结课", False],
        # ]
        # return student_list, 3


class MyEvaluation(View):
    # 显示评价 TW-6-1
    class BuildCommentList:
        def __init__(self, cres):
            self.comment_list = []
            self.name = ""
            self.class_type = ""
            self.cres = cres
            self.all_count = 0
            self.good_count = 0
            self.mid_count = 0
            self.bad_count = 0
            self.sum_score = 0

        def count_comment(self, comment: models.Comment):
            self.all_count += 1
            self.sum_score += comment.score
            if comment.is_high_praise():
                self.good_count += 1
            if comment.is_mediu_evaluation():
                self.mid_count += 1
            if comment.is_bad_comment():
                self.bad_count += 1

        def set_order(self, order):
            self.name = order.parent.student_name
            self.class_type = "{grade}{subject}".format(grade=order.grade.name, subject=order.subject)

        def is_valid(self, comment):
            # 过滤器,用来过滤不同档次的comment
            return True

        def __call__(self, time_slot: models.TimeSlot):
            if time_slot.is_complete() and hasattr(time_slot, "comment"):
                comment = time_slot.comment
                if comment:
                    self.count_comment(comment)
                    if self.is_valid(comment):
                        if self.cres.is_error and self.cres.id == time_slot.comment_id:
                            the_form = forms.CommentReplyForm(initial={"reply": self.cres.reply})
                        else:
                            the_form = forms.CommentReplyForm()
                        one_comment = {
                            "name": self.name,
                            "publish_date": comment.created_at.strftime("%Y-%M-%d %H:%M"),
                            "full_star": range(comment.score),
                            "empty_star": range(5 - comment.score),
                            "comment": comment.content,
                            "class_type": self.class_type+"1对1",
                            "form": the_form,
                            "action_url": "reply/comment/{id}".format(id=comment.id),
                            "form_id": "reply_form_{id}".format(id=comment.id),
                            "reply_id": "reply_{id}".format(id=comment.id),
                            "reply_content": comment.reply
                        }
                        self.comment_list.append(one_comment)


        def get_sorted_comment_list(self):
            return sorted(self.comment_list, key=lambda comment: comment["publish_date"])

    class BuildGoodCommentList(BuildCommentList):
        # 建立好评列表
        def is_valid(self, comment):
            return comment.is_high_praise()

    class BuildMidCommentList(BuildCommentList):
        # 建立中评列表
        def is_valid(self, comment):
            return comment.is_mediu_evaluation()

    class BuildBadCommentList(BuildCommentList):
        # 建立差评列表
        def is_valid(self, comment):
            return comment.is_bad_comment()

    comment_type_map = {0: BuildCommentList,
                        1: BuildGoodCommentList,
                        2: BuildMidCommentList,
                        3: BuildBadCommentList}

    def get(self, request, comment_type, page_offset):
        user = request.user
        teacher = models.Teacher.objects.get(user=user)
        context = {}
        page_offset = int(page_offset)
        comment_type = int(comment_type)
        set_teacher_page_general_context(teacher, context)
        side_bar_content = SideBarContent(teacher)
        side_bar_content(context)
        cres = CommentReply.CommentReplyErrorSession(request)
        if cres.is_error:
            context["comment_reply_error"] = cres.error["reply"][0]
            print("comment_reply_error is {error}".format(error=cres.error))
            print("comment-reply-error-id is {id}".format(id=cres.id))
        comments_array, count_package, avg_score = self.get_comments(cres, teacher, comment_type)
        if comments_array:
            context["comments"] = comments_array[page_offset-1]
        else:
            context["comments"] = []
        # 建立分页数据
        page_array = []
        for one_offset in range(len(comments_array)):
            page_array.append(
                {
                    "url": reverse("teacher:my-evaluation", kwargs={"comment_type": comment_type,
                                                                    "page_offset": one_offset+1}),
                    "offset_id": one_offset+1
                }
            )
        context["page_array"] = page_array
        context["current_page"] = page_offset
        context["current_comment_type"] = comment_type
        context["all_comment"] = reverse("teacher:my-evaluation", kwargs={
            "comment_type": 0, "page_offset": 1
        })
        context["good_comment"] = reverse("teacher:my-evaluation", kwargs={
            "comment_type": 1, "page_offset": 1
        })
        context["mid_comment"] = reverse("teacher:my-evaluation", kwargs={
            "comment_type": 2, "page_offset": 1
        })
        context["bad_comment"] = reverse("teacher:my-evaluation", kwargs={
            "comment_type": 3, "page_offset": 1
        })
        context["comment_count"] = {
            "all": count_package[0],
            "good": count_package[1],
            "mid": count_package[2],
            "bad": count_package[3]
        }
        if count_package[0] > 0:
            context["percent"] = {
                "good":  "{:2.0f}".format(count_package[1]/count_package[0]*100),
                "mid":  "{:2.0f}".format(count_package[2]/count_package[0]*100),
                "bad":  "{:2.0f}".format(count_package[3]/count_package[0]*100)
            }
        else:
            context["percent"] = {
                "good":  "0",
                "mid":  "0",
                "bad":  "0"
            }
        context["avg_score"] = "{:2.1f}".format(avg_score)
        return render(request, "teacher/my_evaluation.html", context)

    def get_comments(self, cres, teacher, comment_type):
        def _real(cres, teacher, comment_type):
            """
            真实数据
            """
            paid_order = teacher.order_set.filter(status=models.Order.PAID)
            comment_list = []
            # bcl = MyEvaluation.BuildCommentList(cres)
            bcl = self.comment_type_map[comment_type](cres)
            for one_order in paid_order:
                bcl.set_order(one_order)
                one_order.enum_timeslot(bcl)
            comment_list = bcl.get_sorted_comment_list()
            if bcl.all_count > 0:
                avg_score = bcl.sum_score/bcl.all_count
            else:
                avg_score = 0
            return comment_list, (bcl.all_count, bcl.good_count, bcl.mid_count, bcl.bad_count), avg_score

        def _fake(cres):
            """
            调界面样式用的伪造数据,速度快
            """
            if cres.is_error:
                the_form = forms.CommentReplyForm(initial={"reply": cres.reply})
            else:
                the_form = forms.CommentReplyForm()
            return [{
                "name": "刘晓伟",
                "full_star": range(3),
                "empty_star": range(2),
                "comment": "这家伙很懒,什么话也没有留下!",
                "publish_date": "2015-12-30 16:00",
                "class_type": "初二数学1对1",
                "form": the_form,
                "action_url": "reply/comment/50",
                "form_id": "reply_form_50",
                "reply_id": "reply_50",
                "reply_content": "hello, I'm reply.",
            },{
                "name": "刘晓伟",
                "full_star": range(3),
                "empty_star": range(2),
                "comment": "这家伙很懒,什么话也没有留下!",
                "publish_date": "2015-12-30 16:00",
                "class_type": "初二数学1对1",
                "form": the_form,
                "action_url": "reply/comment/51",
                "form_id": "reply_form_51",
                "reply_id": "reply_51",
            },], (1, 2, 3, 4, 4.8)
        comment_list, count_package, avg_score = _real(cres, teacher, comment_type)
        # comment_list, count_package, avg_score = _fake(cres)
        return split_list(comment_list, 4), count_package, avg_score


class CommentReply(View):
    class CommentReplyErrorSession:
        def __init__(self, request):
            self.error = request.session.pop("comment-reply-error", None)
            self.id = request.session.pop("comment-reply-error-id", None)
            self.reply = request.session.pop("comment-reply-error-reply", None)
            if self.error:
                self.is_error = True
            else:
                self.is_error = False

        @staticmethod
        def save(request, error, identify, reply):
            request.session["comment-reply-error"] = error
            request.session["comment-reply-error-id"] = identify
            request.session["comment-reply-error-reply"] = reply

    # 回复评价
    def post(self, request, comment_type, page_offset, id):
        print("comment reply id is {id}".format(id=id))
        comment_reply_form = forms.CommentReplyForm(request.POST)
        if comment_reply_form.is_valid():
            # 是正确的返回
            reply = comment_reply_form["reply"].data
            the_comment = models.Comment.objects.get(id=id)
            the_comment.reply = reply
            the_comment.save()
            print("reply is {reply}".format(reply=reply))
            print("reply's type is {the_type}".format(the_type=type(comment_reply_form["reply"])))
        else:
            print("comment reply not illegal, {errors}".format(errors=comment_reply_form.errors))
            CommentReply.CommentReplyErrorSession.save(request, comment_reply_form.errors, id,
                                                       comment_reply_form["reply"].data)
            request.session["comment-reply-error"] = comment_reply_form.errors
            request.session["comment-reply-error-id"] = id
        return redirect("teacher:my-evaluation", comment_type=comment_type, page_offset=page_offset)


def split_list(array: list, segment_size):
    """
    按照segment_size切分列表array
    :param array: 给定的列表
    :param segment_size: 需要切分的大小
    :return:
    """
    count = 0
    sub_array = []
    ret_array = []
    for item in array:
        sub_array.append(item)
        if len(sub_array) >= segment_size:
            ret_array.append(sub_array)
            sub_array = []
    if sub_array:
        ret_array.append(sub_array)
    return ret_array


class TeacherLogout(View):
    """
    登出
    """

    def get(self, request):
        logout(request)
        return HttpResponseRedirect(redirect_to=reverse("teacher:register"))


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

    def getContextTeacher(self, request):
        context = {}
        teacher = get_object_or_404(models.Teacher, user=request.user)
        context['teacher'] = teacher
        context['teacherName'] = teacher.name
        return context, teacher

    def setSidebarContent(self, teacher, context):
        side_bar_content = SideBarContent(teacher)
        side_bar_content(context)


class CertificateView(BaseTeacherView):
    """
    certifications overview
    """

    def get(self, request):
        context, teacher = self.getContextTeacher(request)
        self.setSidebarContent(teacher, context)
        context['isEnglishTeacher'] = teacher.is_english_teacher()
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
    template_path = 'teacher/certificate/certificate_id.html'

    def get(self, request):
        context, teacher = self.getContextTeacher(request)
        self.setSidebarContent(teacher, context)
        certIdHeld, created = models.Certificate.objects.get_or_create(teacher=teacher, type=models.Certificate.ID_HELD,
                                                                       defaults={'name': "", 'verified': False})
        certIdFront, created = models.Certificate.objects.get_or_create(teacher=teacher,
                                                                        type=models.Certificate.ID_FRONT,
                                                                        defaults={'name': "", 'verified': False})
        context = self.buildContextData(context, certIdHeld, certIdFront)
        return render(request, self.template_path, context)

    def buildContextData(self, context, certIdHeld, certIdFront):
        context['id_num'] = certIdHeld.name
        context['idHeldUrl'] = certIdHeld.img_url()
        context['idFrontUrl'] = certIdFront.img_url()
        return context

    def post(self, request):
        context, teacher = self.getContextTeacher(request)
        certIdHeld, created = models.Certificate.objects.get_or_create(teacher=teacher, type=models.Certificate.ID_HELD,
                                                                       defaults={'name': "", 'verified': False})
        certIdFront, created = models.Certificate.objects.get_or_create(teacher=teacher,
                                                                        type=models.Certificate.ID_FRONT,
                                                                        defaults={'name': "", 'verified': False})
        isJsonReq = request.POST.get('format') == 'json'
        if certIdHeld.verified:
            error_msg = '已通过认证的不能更改'
            if isJsonReq:
                return JsonResponse({'ok': False, 'msg': error_msg, 'code': -1})
            context['error_msg'] = error_msg
            return render(request, self.template_path, self.buildContextData(context, certIdHeld, certIdFront))

        id_num = request.POST.get('id_num')
        if not id_num:
            error_msg = '身份证号不能为空'
            if isJsonReq:
                return JsonResponse({'ok': False, 'msg': error_msg, 'code': -1})
            context['error_msg'] = error_msg
            return render(request, self.template_path, self.buildContextData(context, certIdHeld, certIdFront))
        certIdHeld.name = id_num

        if request.FILES:
            idHeldImgFile = request.FILES.get('idHeldImg')
            if idHeldImgFile:
                held_img_content = ContentFile(idHeldImgFile.read())
                certIdHeld.img.save("idHeld" + str(teacher.id), held_img_content)
            idFrontImgFile = request.FILES.get('idFrontImg')
            if idFrontImgFile:
                front_img_content = ContentFile(idFrontImgFile.read())
                certIdFront.img.save("idFrontImg" + str(teacher.id), front_img_content)

        certIdHeld.save()
        certIdFront.save()

        if isJsonReq:
            return JsonResponse({'ok': True, 'msg': '', 'code': 0, 'idHeldUrl': certIdHeld.img_url(),
                                 'idFrontUrl': certIdFront.img_url()})
        context = self.buildContextData(context, certIdHeld, certIdFront)
        return render(request, self.template_path, context)


class CertificateForOnePicView(BaseTeacherView):
    """
    page of certificate for only one pic is needed
    """
    template_path = 'teacher/certificate/certificate_simple.html'
    # cert_types = ['academic', 'teaching', 'english']
    cert_type = 0
    cert_title = '证书标题'
    cert_name = '证书名字'
    hint_content = "提示内容"

    def get(self, request):
        context, teacher = self.getContextTeacher(request)
        self.setSidebarContent(teacher, context)
        cert, created = models.Certificate.objects.get_or_create(teacher=teacher, type=self.cert_type,
                                                                 defaults={'name': "", 'verified': False})
        context = self.buildContextData(context, cert)
        return render(request, self.template_path, context)

    def buildContextData(self, context, cert):
        context['cert_title'] = self.cert_title
        context['cert_name'] = self.cert_name
        context['name_val'] = cert.name
        context['cert_img_url'] = cert.img_url()
        context['hint_content'] = self.hint_content
        return context

    def post(self, request):
        context, teacher = self.getContextTeacher(request)
        cert, created = models.Certificate.objects.get_or_create(teacher=teacher, type=self.cert_type,
                                                                 defaults={'name': "", 'verified': False})
        isJsonReq = request.POST.get('format') == 'json'
        if cert.verified:
            error_msg = '已通过认证的不能更改'
            if isJsonReq:
                return JsonResponse({'ok': False, 'msg': error_msg, 'code': -1})
            context['error_msg'] = error_msg
            return render(request, self.template_path, self.buildContextData(context, cert))

        name = request.POST.get('name')
        if not name:
            error_msg = '证书名称不能为空'
            if isJsonReq:
                return JsonResponse({'ok': False, 'msg': error_msg, 'code': 1})
            context['error_msg'] = error_msg
            return render(request, self.template_path, self.buildContextData(context, cert))
        cert.name = name

        if request.FILES:
            certImgFile = request.FILES.get('certImg')
            if certImgFile:
                cert_img_content = ContentFile(certImgFile.read())
                cert.img.save("certImg" + str(self.cert_type) + str(teacher.id), cert_img_content)

        cert.save()

        if isJsonReq:
            return JsonResponse({'ok': True, 'msg': '', 'code': 0, 'cert_img_url': cert.img_url()})
        context = self.buildContextData(context, cert)
        return render(request, self.template_path, context)


class CertificateAcademicView(CertificateForOnePicView):
    cert_type = models.Certificate.ACADEMIC
    cert_title = '学历认证'
    cert_name = '毕业院校'
    hint_content = "请上传最新的毕业证或学位证书照片"


class CertificateTeachingView(CertificateForOnePicView):
    cert_type = models.Certificate.TEACHING
    cert_title = '教师资质认证'
    cert_name = '证书名称'
    hint_content = "请上传有效期内的教师资格证书或同等资格证明"


class CertificateEnglishView(CertificateForOnePicView):
    cert_type = models.Certificate.ENGLISH
    cert_title = '英语水平认证'
    cert_name = '证书名称'
    hint_content = "请上传你最具代表性的英语水平证书"


class CertificateOthersView(BaseTeacherView):
    """
    page of others certifications
    """
    template_path = 'teacher/certificate/certificate_others.html'

    def get(self, request):
        context, teacher = self.getContextTeacher(request)
        self.setSidebarContent(teacher, context)
        context = self.buildContextData(context, teacher)
        return render(request, self.template_path, context)

    def buildContextData(self, context, teacher):
        otherCerts = models.Certificate.objects.filter(teacher=teacher, type=models.Certificate.OTHER)
        context['otherCerts'] = otherCerts
        return context

    def post(self, request):
        if request.POST.get('action') == 'delete':
            return self.doDeleteCert(request)

        context, teacher = self.getContextTeacher(request)
        isJsonReq = request.POST.get('format') == 'json'
        cert = None
        id = request.POST.get('id')
        if id:
            cert = models.Certificate.objects.get(id=id, teacher=teacher)
        else:
            cert = models.Certificate(teacher=teacher, type=models.Certificate.OTHER, verified=False)
        name = request.POST.get('name')
        if not name:
            error_msg = '证书名称不能为空'
            if isJsonReq:
                return JsonResponse({'ok': False, 'msg': error_msg, 'code': 1})
            context['error_msg'] = error_msg
            return render(request, self.template_path, self.buildContextData(context, teacher))
        cert.name = name

        if request.FILES:
            certImgFile = request.FILES.get('certImg')
            if certImgFile:
                cert_img_content = ContentFile(certImgFile.read())
                cert.img.save("certImg" + str(cert.type) + str(teacher.id) + '_' + str(cert_img_content.size),
                              cert_img_content)

        cert.save()

        if isJsonReq:
            return JsonResponse({'ok': True, 'msg': '', 'code': 0})
        context = self.buildContextData(context, teacher)
        return render(request, self.template_path, context)

    """
    return message format: {'ok': False, 'msg': msg, 'code': 1}
    """

    def doDeleteCert(self, request):
        context, teacher = self.getContextTeacher(request)
        certId = request.POST.get('certId')
        if not certId:
            return JsonResponse({'ok': False, 'msg': '参数错误', 'code': 1})
        try:
            cert = models.Certificate.objects.get(id=certId, teacher=teacher)
            if cert.type and cert.type != models.Certificate.OTHER:
                return JsonResponse({'ok': False, 'msg': '不支持删除该类型的证书', 'code': 4})
            cert.delete()
            return JsonResponse({'ok': True, 'msg': '', 'code': 0})
        except models.Certificate.DoesNotExist as e:
            logger.warning(e)
            return JsonResponse({'ok': False, 'msg': '没有找到相应的记录', 'code': 2})
        except Exception as err:
            logger.error(err)
            return JsonResponse({'ok': False, 'msg': '请求失败,请稍后重试,或联系管理员!', 'code': -1})


class HighscoreView(BaseTeacherView):
    """
    提分榜
    """
    template_path = 'teacher/highscore/highscore.html'

    def get(self, request):
        context, teacher = self.getContextTeacher(request)
        self.setSidebarContent(teacher, context)
        highscore = None
        profile = None
        if teacher:
            highscores = models.Highscore.objects.filter(teacher=teacher)

        ability_set_all = teacher.abilities.all()
        if len(ability_set_all) > 0:
            subclass = ability_set_all[0].subject.name
        else:
            subclass = ""

        context = self.buildContextData(context, teacher)
        context["highscores"] = highscores
        context["profile"] = profile
        context["subclass"] = subclass
        context["systags"] = models.Tag.objects.all()

        return render(request, self.template_path, context)

    def buildContextData(self, context, teacher):
        return context

    def post(self, request):
        if request.POST.get('action') == 'delete':
            return self.doDelHighscore(request)

        if request.POST.get('action') == 'add':
            return self.addNewHighscore(request)

        context, teacher = self.getContextTeacher(request)
        highscore = None

        return render(request, self.template_path, context)

    """
    增加一行
    """

    def addNewHighscore(self, request):
        context, teacher = self.getContextTeacher(request)
        name = request.POST.get('name')
        increased_scores = request.POST.get('increased_scores')
        school_name = request.POST.get('school_name')
        admitted_to = request.POST.get('admitted_to')
        highscore = models.Highscore(teacher=teacher, name=name, increased_scores=increased_scores,
                                     school_name=school_name, admitted_to=admitted_to)
        highscore.save()
        return JsonResponse({'ok': True, 'msg': '', 'code': 0})

    """
    return message format: {'ok': False, 'msg': msg, 'code': 1}
    """

    def doDelHighscore(self, request):
        context, teacher = self.getContextTeacher(request)
        delIds = request.POST.get('ids')
        if not delIds:
            return JsonResponse({'ok': False, 'msg': '参数错误', 'code': 1})
        delIds = delIds.split(",");
        delIds = list(map(int, filter(lambda x: x, delIds)))
        try:
            delObjs = models.Highscore.objects.filter(id__in=delIds)
            allIsSelf = True
            for p in delObjs:
                if p.teacher.id != teacher.id:
                    allIsSelf = False
            if not allIsSelf:
                return JsonResponse({'ok': False, 'msg': '只能删除自己的记录', 'code': -1})

            ret = models.Highscore.objects.filter(id__in=delIds).delete()

            return JsonResponse({'ok': True, 'msg': '', 'code': 0})
        except Exception as err:
            logger.error(err)
            return JsonResponse({'ok': False, 'msg': '请求失败,请稍后重试,或联系管理员!', 'code': -1})


class BasicDocument(BaseTeacherView):
    """
    基本资料
    """
    template_path = 'teacher/doc/basic.html'

    def get(self, request):
        context, teacher = self.getContextTeacher(request)
        self.setSidebarContent(teacher, context)
        profile = models.Profile.objects.get(user=teacher.user)

        context = self.buildContextData(context, teacher)
        ability_set_all = teacher.abilities.all()
        if len(ability_set_all) > 0:
            subclass = ability_set_all[0].subject.name
        else:
            subclass = ""

        grade = [[False for i in range(6)],
                 [False for i in range(3)],
                 [False for i in range(3)]]
        grade_name = models.Grade.get_all_grades()
        grade_slot = {}
        for x, one_level in enumerate(grade_name):
            for y, one_grade in enumerate(one_level):
                grade_slot[one_grade] = (x, y)

        grade_list = [item.grade.name for item in list(teacher.abilities.all())]
        for one_grade in grade_list:
            x, y = grade_slot.get(one_grade, (0, 0))
            grade[x][y] = True

        tags = models.Tag.objects.all()

        itemsLen = len(tags)
        ind = 0
        while ind < itemsLen:
            itm = tags[ind]
            ind += 1
            if itm in teacher.tags.all():
                itm.ck = 1

        if profile.birthday:
            context["birthday_y"] = profile.birthday.year
            context["birthday_m"] = profile.birthday.month
            context["birthday_d"] = profile.birthday.day

        context["grade"] = json.dumps(grade)
        context["systags"] = tags
        context["profile"] = profile
        context["subclass"] = subclass
        context["phone"] = profile.mask_phone()

        return render(request, self.template_path, context)

    def buildContextData(self, context, teacher):
        context["teacher"] = teacher
        return context

    def post(self, request):
        user = request.user
        teacher = models.Teacher.objects.get(user=user)
        profile = models.Profile.objects.get(user=user)

        birthday_y = int(self.request.POST.get('birthday_y', 0))
        birthday_m = int(self.request.POST.get('birthday_m', 0))
        birthday_d = int(self.request.POST.get('birthday_d', 0))

        teachingAge = self.request.POST.get('teachingAge', 0)
        graduate_school = self.request.POST.get('graduate_school', None)
        introduce = self.request.POST.get('graduate_school', None)
        subclass = self.request.POST.get('subclass', None)

        grade = request.POST.get("selectedGrand")
        tags = request.POST.get("selectedTags")

        grade_list = json.loads(grade)
        tags_list = json.loads(tags)

        the_subject = models.Subject.objects.get(name=subclass)
        grade_name_list = models.Grade.get_all_grades()
        page_grade_list = [["小学一年级", "小学二年级", "小学三年级", "小学四年级", "小学五年级", "小学六年级"],
                           ["初一", "初二", "初三"],
                           ["高一", "高二", "高三"]]
        grade_dict = {}
        for page_level, database_level in list(zip(page_grade_list, grade_name_list)):
            for page_grade, database_grade in list(zip(page_level, database_level)):
                grade_dict[page_grade] = database_grade

        # clear ability_set
        teacher.abilities.clear()
        for one_grade in grade_list:
            the_grade = models.Grade.objects.get(name=grade_dict.get(one_grade, one_grade))
            try:
                ability = models.Ability.objects.get(grade=the_grade, subject=the_subject)
            except models.Ability.DoesNotExist:
                # 如果这个年级不存在就跳过
                continue
            teacher.abilities.add(ability)
            ability.save()

        teacher.tags.clear()
        for tagId in tags_list:
            tag = models.Tag.objects.get(id=tagId)
            teacher.tags.add(tag)

        if birthday_y > 0 and birthday_m > 0 and birthday_d > 0:
            profile.birthday = datetime.datetime(birthday_y, birthday_m, birthday_d)

        teacher.introduce = introduce
        teacher.teaching_age = teachingAge
        teacher.graduate_school = graduate_school
        teacher.save()
        profile.save()

        return JsonResponse({'ok': True, 'msg': 'OK', 'code': 0})

class AchievementView(BaseTeacherView):
    """
    特殊成果
    """
    MAX_COUNT = 5
    template_path = 'teacher/achievement/achievement.html'
    edit_template_path = 'teacher/achievement/achievement_edit.html'

    def get(self, request, action=None, id=None):
        context, teacher = self.getContextTeacher(request)
        self.setSidebarContent(teacher, context)
        if action == 'add':
            context['achieve_title'] = '新建'
            return render(request, self.edit_template_path, context)
        if action == 'edit' and id:
            achievement = models.Achievement.objects.get(id=id, teacher=teacher)
            context['achieve_title'] = '修改'
            context['achieve'] = achievement
            return render(request, self.edit_template_path, context)
        # 返回列表页
        achievements = models.Achievement.objects.filter(teacher=teacher).order_by('id')
        context["achievements"] = achievements
        context['MAX_COUNT'] = 5
        return render(request, self.template_path, context)

    def post(self, request, action=None, id=None):
        if action == 'add':
            return self.doSave(request, None)
        if action == 'edit':
            return self.doSave(request, id)
        if action == 'delete':
            return self.doDelete(request, id)
        return HttpResponse('', status=403)

    def doDelete(self, request, achieveId):
        context, teacher = self.getContextTeacher(request)
        if not achieveId:
            return JsonResponse({'ok': False, 'msg': '参数错误', 'code': 1})
        try:
            achievement = models.Achievement.objects.get(id=achieveId, teacher=teacher)
            achievement.delete()
            return JsonResponse({'ok': True, 'msg': '', 'code': 0})
        except models.Achievement.DoesNotExist as e:
            logger.warning(e)
            return JsonResponse({'ok': False, 'msg': '没有找到相应的记录', 'code': 2})
        except Exception as err:
            logger.error(err)
            return JsonResponse({'ok': False, 'msg': '请求失败,请稍后重试,或联系管理员!', 'code': -1})

    def doSave(self, request, id):
        context, teacher = self.getContextTeacher(request)
        if not id: # check max count when adding new one
            old_count = models.Achievement.objects.filter(teacher=teacher).count()
            if old_count >= self.MAX_COUNT:
                error_msg = '最多添加'+str(self.MAX_COUNT)+'个特殊成果'
                return JsonResponse({'ok': False, 'msg': error_msg, 'code': 3})

        title = request.POST.get('title')
        title = title and title.strip() or ''
        if not title:
            error_msg = '名称不能为空'
            return JsonResponse({'ok': False, 'msg': error_msg, 'code': 1})
        if len(title) > 10:
            error_msg = '名称不能超过10个字'
            return JsonResponse({'ok': False, 'msg': error_msg, 'code': 2})

        achievement = None
        if id:
            achievement = models.Achievement.objects.get(id=id, teacher=teacher)
        else:
            achievement = models.Achievement(teacher=teacher)

        achieveImgFile = None
        if request.FILES:
            achieveImgFile = request.FILES.get('achieveImg')
        if not achievement.img and not achieveImgFile:
            error_msg = '请选择图片'
            return JsonResponse({'ok': False, 'msg': error_msg, 'code': 4})

        achievement.title = title
        if achieveImgFile:
            img_content = ContentFile(achieveImgFile.read())
            achievement.img.save("achievement" + str(teacher.id) + '_' + str(img_content.size), img_content)

        achievement.save()
        return JsonResponse({'ok': True, 'msg': '', 'code': 0})


class WalletView(BaseTeacherView):

    template_path = 'teacher/wallet/wallet.html'
    PAGE_SIZE = 10

    def get(self, request, action=None):
        context, teacher = self.getContextTeacher(request)
        if action == 'histories':
            return self.listAccountHistories(request, teacher)
        self.setSidebarContent(teacher, context)
        account = teacher.safe_get_account()
        context['account'] = account
        histories = models.AccountHistory.objects.filter(account=account).order_by("-submit_time")
        # paginate
        histories, pager = paginate(histories, page_size=self.PAGE_SIZE)
        context['histories'] = histories
        context['pager'] = pager
        return render(request, self.template_path, context)

    def listAccountHistories(self, request, teacher):
        page = self.request.GET.get('page') and self.request.GET.get('page').strip() or 1
        account = teacher.safe_get_account()
        query_set = models.AccountHistory.objects.filter(account=account).order_by("-submit_time")
        # paginate
        query_set, pager = paginate(query_set, page, page_size=self.PAGE_SIZE)
        histories = [{'submit_time': localtime(h.submit_time).strftime('%Y-%m-%d %H:%M'), 'positive': h.amount >=0, 'amount': money_format(h.amount), 'comment': h.comment} for h in query_set]
        return JsonResponse({'ok': True, 'list': histories, 'pager': pager})

