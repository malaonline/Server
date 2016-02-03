import logging

# django modules
from django.core.files.base import ContentFile
from django.utils.decorators import method_decorator
from django.contrib.auth.decorators import user_passes_test, login_required
from django.contrib.auth import login, authenticate, _get_backends, logout
from django.http import HttpResponse, JsonResponse, HttpResponseRedirect
from django.core.urlresolvers import reverse
from django.views.generic import View
from django.shortcuts import render, get_object_or_404
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth.models import User
from django.conf import settings
from django.utils.timezone import make_aware, localtime
from dateutil import relativedelta
from pprint import pprint as pp

import calendar
from collections import namedtuple
import json
import datetime

# local modules
from app import models

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
            "comprehensive_evaluation": gce.average_score(),
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
                                    make_aware(datetime.datetime(one_month[0].year, one_month[0].month, one_month[0].day, 0, 0, 0)),
                                    make_aware(datetime.datetime(one_month[-1].year, one_month[-1].month, one_month[-1].day, 23, 59, 59)),)
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

    def get(self, request):
        user = request.user
        teacher = models.Teacher.objects.get(user=user)

        total_page = self.total_page()
        current_page = self.current_page()
        default_page_list = [[item, False] for item in range(total_page)]
        default_page_list[current_page][1] = True
        context = {
            "student_list": self.current_student(),
            "page_list": default_page_list
        }
        set_teacher_page_general_context(teacher, context)
        return render(request, "teacher/my_students.html", context)

    def current_student(self):
        student_list = [
            ["胡晓璐", "初二", "0/20", "￥190/小时", "新生", True],
            ["胡晓璐", "小学一年级", "8/10", "￥190/小时", "续费", False],
            ["胡晓璐", "高一", "9/20", "￥190/小时", "正常", True],
            ["胡晓璐", "高三", "12/100", "￥190/小时", "正常", True],
            ["胡晓璐", "高二", "7/20", "￥190/小时", "正常", True],
            ["胡晓璐", "初二", "14/15", "￥190/小时", "续费", True],
            ["张子涵", "小学二年级", "8/10", "￥190/小时", "退费", False],
            ["汪小菲", "小学六年级", "19/20", "￥190/小时", "续费", False],
            ["孙大圣", "小学一年级", "12/100", "￥190/小时", "正常", False],
            ["刘宇", "高一", "20/20", "￥190/小时", "结课", False],
            ["赵一曼", "高三", "15/15", "￥190/小时", "结课", False],
        ]
        return student_list

    def total_page(self):
        return 5

    def current_page(self):
        return 3


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


class CertificateView(BaseTeacherView):
    """
    certifications overview
    """

    def get(self, request):
        context, teacher = self.getContextTeacher(request)
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
        highscore = None
        if teacher:
            highscores = models.Highscore.objects.filter(teacher=teacher)
        context = self.buildContextData(context, teacher)
        context["highscores"] = highscores
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
        highscore = None
        if teacher:
            highscores = models.Highscore.objects.filter(teacher=teacher)
        context = self.buildContextData(context, teacher)
        context["highscores"] = highscores
        return render(request, self.template_path, context)

    def buildContextData(self, context, teacher):
        context["teacher"] = teacher
        return context


class AchievementView(BaseTeacherView):
    """
    特殊成果
    """
    template_path = 'teacher/achievement/achievement.html'
    edit_template_path = 'teacher/achievement/achievement_edit.html'

    def get(self, request, action=None, id=None):
        context, teacher = self.getContextTeacher(request)
        if action == 'add':
            context['achieve_title'] = '新建'
            return render(request, self.edit_template_path, context)
        if action == 'edit' and id:
            achievement = models.Achievement.objects.get(id=id, teacher=teacher)
            context['achieve_title'] = '修改'
            context['achieve'] = achievement
            return render(request, self.edit_template_path, context)
        # 返回列表页
        achievements = models.Achievement.objects.filter(teacher=teacher)
        context["achievements"] = achievements
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
        title = request.POST.get('title')
        title = title and title.strip() or ''
        if not title:
            error_msg = '名称不能为空'
            return JsonResponse({'ok': False, 'msg': error_msg, 'code': 1})
        if len(title) > 10:
            error_msg = '名称不能超过10个字'
            return JsonResponse({'ok': False, 'msg': error_msg, 'code': 2})

        context, teacher = self.getContextTeacher(request)
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
