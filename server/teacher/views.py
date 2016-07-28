import logging

# django modules
from django.core.files.base import ContentFile
from django.utils.decorators import method_decorator
from django.contrib.auth.decorators import user_passes_test, login_required
from django.contrib.auth import login, authenticate, _get_backends, logout
from django.contrib.auth.mixins import LoginRequiredMixin
from django.contrib.auth.views import redirect_to_login
from django.http import HttpResponse, JsonResponse, HttpResponseRedirect
from django.core.urlresolvers import reverse
from django.views.generic import View
from django.shortcuts import render, get_object_or_404, redirect
from django.contrib.auth.models import User
from django.conf import settings
from django.utils.timezone import make_aware, localtime
from django.utils import timezone
from urllib.parse import urlparse
from django.db.models import Q, Count, Sum, Avg
from django.core.paginator import Paginator
from django.conf import settings

from dateutil import relativedelta

import calendar
import json
import datetime
from pprint import pprint as pp

# local modules
from app import models
from . import forms
from app.templatetags.custom_tags import money_format, weekday_format
from app.utils.algorithm import check_bankcard_number, check_id_number
from app.utils.smsUtil import isValidPhone, isValidCode
from app.utils.db import paginate
from app.utils.klx_api import *
from app.tasks import registerKuaiLeXueUserByOrder

logger = logging.getLogger('app')

# Create your views here.

# 目前老师端的公共登录url,这里不能用reverse,不然会发生循环引用
LOGIN_URL = "web:teacher-index"


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


class BasicTeacherView(LoginRequiredMixin, View):
    login_url = LOGIN_URL
    redirect_field_name = "next"

    class TeacherInformationNotComplete(Exception):
        # 老师基本信息没有填完
        pass

    class TeacherQualifieldNotAudit(Exception):
        # 老师资格审核没有完成
        pass

    # 基础类,用于一些特定测试
    def not_teacher_role(self):
        return HttpResponseRedirect(reverse(LOGIN_URL))

    need_check_percent = True

    def check_percent(self, user):
        # 检查老师信息是否完成
        if self.need_check_percent:
            percent = information_complete_percent(user)
            if percent < 1:
                raise BasicTeacherView.TeacherInformationNotComplete()

    def redirect_with_next(self, request, redirect_url, redirect_field_name="next"):
        # 带有next的重定向
        next = request.build_absolute_uri()
        login_scheme, login_netloc = urlparse(redirect_url)[:2]
        current_scheme, current_netloc = urlparse(next)[:2]
        if ((not login_scheme or login_scheme == current_scheme) and
                (not login_netloc or login_netloc == current_netloc)):
            next = request.get_full_path()
        return redirect_to_login(
                next, redirect_url, redirect_field_name)

    need_check_qualifield_audit = True

    def check_qualifield_audit(self, teacher: models.Teacher):
        # 检查老师资格审核是否完成
        if self.need_check_qualifield_audit:
            if teacher.status != models.Teacher.INTERVIEW_OK or not teacher.status_confirm:
                raise BasicTeacherView.TeacherQualifieldNotAudit()

    def setSidebarContent(self, teacher, context):
        side_bar_content = SideBarContent(teacher)
        side_bar_content(context)

    # @method_decorator(user_passes_test(is_teacher_logined, login_url=LOGIN_URL))
    def get(self, request, *args, **kwargs):
        user = request.user
        try:
            teacher = user.teacher
            # 检查用户组是否拥有
            if not user.groups.filter(name="老师").exists():
                raise models.Teacher.DoesNotExist()
            self.check_percent(user)
            self.check_qualifield_audit(teacher)
            return self.handle_get(request, user, teacher, *args, **kwargs)
        except models.Teacher.DoesNotExist:
            # 用户没有老师这个角色
            return self.not_teacher_role()
        except BasicTeacherView.TeacherInformationNotComplete:
            # 老师基本信息没有填完
            path = request.build_absolute_uri()
            return self.redirect_with_next(request, redirect_url=reverse("teacher:complete-information"),
                                           redirect_field_name="next")
        except BasicTeacherView.TeacherQualifieldNotAudit:
            # 老师资料审核没有完成
            path = request.build_absolute_uri()
            return self.redirect_with_next(request, redirect_url=reverse("teacher:register-progress"),
                                           redirect_field_name="next")

    def handle_get(self, request, user, teacher, *args, **kwargs):
        raise Exception("get not implement")

    # @method_decorator(user_passes_test(is_teacher_logined, login_url=LOGIN_URL))
    def post(self, request, *args, **kwargs):
        user = request.user
        try:
            teacher = user.teacher
            self.check_percent(user)
            self.check_qualifield_audit(teacher)
            return self.handle_post(request, user, teacher, *args, **kwargs)
        except models.Teacher.DoesNotExist:
            return self.not_teacher_role()
        except BasicTeacherView.TeacherInformationNotComplete:
            # 老师基本信息没有填完
            path = request.build_absolute_uri()
            return self.redirect_with_next(request, redirect_url=reverse("teacher:complete-information"),
                                           redirect_field_name="next")
        except BasicTeacherView.TeacherQualifieldNotAudit:
            # 老师资料审核没有完成
            path = request.build_absolute_uri()
            return self.redirect_with_next(request, redirect_url=reverse("teacher:register-progress"),
                                           redirect_field_name="next")

    def handle_post(self, request, user, teacher, *args, **kwargs):
        raise Exception("post not implement")


class TeacherLogin(View):
    """
    老师用户注册页面 TW-1-1
    """

    def post(self, request):
        # 登录,用短信验证
        phone = request.POST.get("phone", None)
        code = request.POST.get("code", None)
        next_url = request.GET.get("next", "")
        # print("the_next_url is {next_url}".format(next_url=next_url))
        Profile = models.Profile
        CheckCode = models.Checkcode
        Teacher = models.Teacher
        new_user = True
        try:
            profile = Profile.objects.get(phone=phone)
            if not profile.user.groups.filter(name="老师").exists():
                # 这是一个很罕见的手动修改数据库,导致数据破损的情况,这种情况为没有老师组的用户添加一个老师组
                teacher_group = models.Group.objects.get(name="老师")
                profile.user.groups.add(teacher_group)
        except Profile.DoesNotExist:
            # new user
            user = Teacher.new_teacher(phone)
            teacher = user.teacher
        else:
            user = profile.user
            # 注意,这段代码摘抄自django库本身
            for backend, backend_path in _get_backends(return_tuples=True):
                user.backend = backend_path
                break
            try:
                teacher = Teacher.objects.get(user=user)
                new_user = False
            except Teacher.DoesNotExist:
                # 这个手机号已经注册其它角色,但没注册过老师角色
                teacher = Teacher.add_teacher_role(user)

        verify_result, verify_code = CheckCode.verify(phone, code)
        verify_msg = CheckCode.verify_msg(verify_result, verify_code)
        if verify_result:
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
                if teacher.status != Teacher.INTERVIEW_OK or not teacher.status_confirm:
                    return JsonResponse({
                        "result": True,
                        "url": reverse("teacher:register-progress")
                    })
                else:
                    if next_url:
                        return JsonResponse({
                            "result": True,
                            "url": next_url
                        })
                    else:
                        return JsonResponse({
                            "result": True,
                            "url": reverse("teacher:first-page")
                        })
        else:
            # 验证失败
            return JsonResponse({
                "result": False,
                "msg": verify_msg,
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
    if teacher.region is None:
        unfinished += 1
    if len(teacher.abilities.all()) == 0:
        unfinished += 1
    return (total - unfinished) / total


# 完善老师的个人信息 TW-2-1
class InformationComplete(BasicTeacherView):
    need_check_percent = False
    need_check_qualifield_audit = False

    def handle_get(self, request, user, teacher, *args, **kwargs):
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
        return render(request, 'teacher/information_complete.html', context)

    class ParameterInvalid(Exception):
        pass

    def handle_post(self, request, user, teacher, *args, **kwargs):
        next_url = request.GET.get("next", "")
        profile = models.Profile.objects.get(user=user)

        name = request.POST.get("name", None)
        gender = request.POST.get("gender", None)
        region = request.POST.get("region", None)
        subject = request.POST.get("subclass", None)
        grade = request.POST.get("grade", None)

        def _check_parameter(target, msg):
            if not target:
                raise InformationComplete.ParameterInvalid(msg)

        check_list = [
            [name, "姓名未填写"],
            [gender, "性别未填写"],
            [region, "地区未填写"],
            [subject, "课目未填写"],
        ]

        try:
            for target, msg in check_list:
                _check_parameter(target=target, msg=msg)
        except InformationComplete.ParameterInvalid as e:
            # 存在没有填写的项目
            return JsonResponse({"done": False, "msg": str(e)})

        grade_list = json.loads(grade)

        if len(grade_list) == 0:
            return JsonResponse({"done": False, "msg": "授课年级没有选择"})

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

        # 初选淘汰的老师,再次填写资料,就会重新进入面试等待环节
        if teacher.status == models.Teacher.NOT_CHOSEN:
            teacher.status = models.Teacher.TO_CHOOSE

        teacher.save()
        profile.save()

        if next_url:
            return JsonResponse({"done": True, "url": next_url})
        else:
            return JsonResponse({"done": True, "url": reverse("teacher:register-progress")})


class RegisterProgress(BasicTeacherView):
    """
    显示注册进度
    """
    need_check_qualifield_audit = False
    def handle_get(self, request, user, teacher, *args, **kwargs):
        context = {}

        if settings.FIX_TEACHER_STATUS:
            teacher.status = teacher.INTERVIEW_OK
        context["progress"] = teacher.get_progress()
        audit_list = []
        for one_audit in teacher.auditrecord_set.all().order_by("create_at"):
            audit_list.append(one_audit.html_description())
        context["audit_list"] = audit_list
        context["hide_old_notify_list"] = True
        context["text_list"] = teacher.build_progress_info()
        context["user_name"] = "{name} 老师".format(name=teacher.name)
        return render(request, "teacher/register_progress.html", context)

    def handle_post(self, request, user, teacher, *args, **kwargs):
        # 已经确认注册进度
        teacher.status_confirm = True
        teacher.save()
        next_url = request.GET.get("next", "")
        logger.debug(request)
        logger.debug("the next_url is {next}".format(next=next_url))
        if next_url:
            return HttpResponseRedirect(next_url)
        else:
            return HttpResponseRedirect(reverse("teacher:first-page"))


# 设置老师页面的通用上下文
def set_teacher_page_general_context(teacher, context):
    context["user_name"] = "{name} 老师".format(name=teacher.name)


class FirstPage(BasicTeacherView):
    """
    通过面试的老师见到的第一个页面
    """
    def handle_get(self, request, user, teacher, *args, **kwargs):
        # user = request.user
        # teacher = models.Teacher.objects.get(user=user)
        profile = models.Profile.objects.get(user=user)
        # order_set = models.Order.objects.filter(teacher=teacher,
        #                                         status=models.Order.PAID)
        # gce = self.comprehensive_evaluation(order_set)
        current_student, complete_student = self.student_on_class(teacher)
        context = {
            "avatar": self.avatar(profile),
            "class_complete": self.class_complete(teacher),
            "class_waiting": self.class_waiting(teacher),
            "student_on_class": current_student,
            "student_complete": complete_student,
            "comprehensive_evaluation": "{:2.1f}".format(self.get_evaluation_avg_score(teacher)),
            "bad_review": self.get_bad_comment(teacher),
            "account_balance": self.account_balance(teacher),
            "total_revenue": self.total_revenue(teacher),
            "teacher_level": self.teacher_level(teacher),
            "information_complete_percent": self.information_complete_percent(teacher, profile)[0],
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
            return None

    def class_complete(self, teacher, current_data=make_aware(datetime.datetime.now())):
        # 已上课数量
        return models.TimeSlot.objects.filter(
            order__status=models.Order.PAID,
            order__teacher=teacher,
            end__lt=current_data,
            deleted=False,
        ).count()

    def class_waiting(self, teacher: models.Teacher, current_data=make_aware(datetime.datetime.now())):
        # 待上课数量
        return models.TimeSlot.objects.filter(
            order__status=models.Order.PAID,
            order__teacher=teacher,
            start__gt=current_data,
            deleted=False,
        ).count()

    def student_on_class(self, teacher: models.Teacher, current_data=make_aware(datetime.datetime.now())):
        # 上课中的学生,需要先汇总学生的所有订单,然后判断学生是否在上课'
        student_list = models.Parent.objects.filter(order__teacher=teacher, order__status=models.Order.PAID,
                                                    order__timeslot__isnull=False).distinct("pk")
        current_count = 0
        session_count = 0
        for one_student in student_list:
            if models.TimeSlot.objects.filter(order__teacher=teacher, order__parent=one_student,
                                              order__status=models.Order.PAID, end__gt=current_data).exists():
                # 统计上课学生
                current_count += 1
            else:
                # 统计结课学生
                session_count += 1
        return current_count, session_count

    def get_evaluation_avg_score(self, teacher: models.Teacher):
        # 获得平均评分
        avg_score = models.Comment.objects.filter(
            timeslot__order__teacher=teacher,
            timeslot__order__status=models.Order.PAID,
        ).aggregate(Avg("score"))
        return avg_score["score__avg"] or 0

    def get_bad_comment(self, teacher: models.Teacher):
        # 获得差评数
        return models.Comment.objects.filter(
            timeslot__order__teacher=teacher,
            timeslot__order__status=models.Order.PAID,
            score__lt=3,
        ).count()

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
        account_balance = teacher.safe_get_account().calculated_balance/100
        return "¥{:.2f}".format(account_balance)

    def total_revenue(self, teacher: models.Teacher):
        # 累计收入
        revenue = teacher.safe_get_account().accumulated_income
        revenue = revenue / 100
        return "¥{:.2f}".format(revenue)

    def teacher_level(self, teacher: models.Teacher):
        # 教师等级
        if teacher.level_id:
            teacher_level = teacher.level.name
            return "麻辣{teacher_level}老师".format(teacher_level=teacher_level)
        else:
            return "暂无级别"

    def information_complete_percent(self, teacher: models.Teacher, profile: models.Profile):
        # 资料完成度
        complete_percent = 0
        complete_list = []
        unfinished_list = []
        # 手机号码 5%
        if hasattr(profile, "phone") is True and profile.phone:
            complete_percent += 5
            complete_list.append("phone is {phone}".format(phone=profile.phone))
        else:
            unfinished_list.append("phone not finished")
        # 姓名 5%
        if hasattr(teacher, "name") is True and teacher.name:
            complete_percent += 5
            complete_list.append("name is {name}".format(name=teacher.name))
        else:
            unfinished_list.append("name not finished")
        # 性别 2%
        if hasattr(profile, "gender") is True and profile.gender != "u":
            complete_percent += 2
            complete_list.append("gender is {gender}".format(gender=profile.gender))
        else:
            unfinished_list.append("gender not finished")
        # 授课年级 3%, 教授科目 5%
        if teacher.abilities.all().exists():
            complete_percent += 8
            complete_list.append("abilities is {abilities}".format(abilities=teacher.abilities))
        else:
            unfinished_list.append("abilities not finished")
        # 所在城市, 5%
        if hasattr(teacher, "region") is True and teacher.region:
            complete_percent += 5
            complete_list.append("region is {region}".format(region=teacher.region))
        else:
            unfinished_list.append("region not finished")
        # 头像, 5%
        if profile.avatar_url():
            complete_percent += 5
            complete_list.append("avatar is {avatar}".format(avatar=profile.avatar_url()))
        else:
            unfinished_list.append("avatar not finished")
        # 判断是不是英语老师
        english_subject = models.Subject.get_english()
        # 证书权重
        cert_map = {
            models.Certificate.ID_HELD: 10,
            models.Certificate.ACADEMIC: 10,
            models.Certificate.TEACHING: 5,
            models.Certificate.OTHER: 10,
        }
        # 身份认证, 10%
        # 毕业证书, 10%
        # 教师资格证书, 5%
        # 其他证书, 英语老师,5%, 非英语老师 10%
        # 英语水平证书 5%, 只针对英语老师
        teacher_abilities = teacher.abilities.all()
        if teacher_abilities and teacher_abilities[0].subject == english_subject:
            # 英语老师权重很特别
            cert_map[models.Certificate.OTHER] = 5
            cert_map[models.Certificate.ENGLISH] = 5
            complete_list.append("is_english")

        for one_cert in teacher.certificate_set.all():
            if one_cert.verified is True:
                complete_percent += cert_map.pop(one_cert.type, 0)

        # 风格标记, 5%
        if hasattr(teacher, "tags") and teacher.tags.count() > 0:
            complete_percent += 5
            complete_list.append("tags is {tags}".format(tags=teacher.tags))
        else:
            unfinished_list.append("tags not finished")
        # 自我介绍,至少10个字符, 10%
        if teacher.introduce and len(teacher.introduce) > 9:
            complete_percent += 10
            complete_list.append("introduce is {introduce}".format(introduce=teacher.introduce))
        else:
            unfinished_list.append("introduce not finished")
        # 教龄, 5%
        # 注意, 教龄情况特殊,现在model是0,无法判断老师教龄为0年还是老师没有填写教龄
        # 所以目前先+5%,不做额外处理
        complete_percent += 5
        # if hasattr(teacher, "experience") and teacher.experience:
        #     complete_percent += 5
        #     complete_list.append("experience is {experience}".format(experience=teacher.experience))
        # else:
        #     unfinished_list.append("experience not finished")
        # 提分榜, 10%
        if teacher.highscore_set.all():
            complete_percent += 10
            complete_list.append("highscore is {highscore}".format(highscore=teacher.highscore_set))
        else:
            unfinished_list.append("highscore not finished")
        # 特殊成果, 5%
        if teacher.achievement_set.all().exists():
            complete_percent += 5
            complete_list.append("achievement is {achievement}".format(achievement=teacher.achievement_set.all()))
        else:
            unfinished_list.append("achievement not finished")

        return complete_percent, complete_list

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
        today = datetime.datetime.now()
        context["side_bar_my_school_time_url"] = reverse("teacher:my-school-timetable",
                                                         kwargs={"year": today.year,
                                                                 "month": "{day:02d}".format(day=today.month)}
                                                         )
        context["side_bar_my_student_url"] = reverse("teacher:my-students",
                                                     kwargs={"student_type": 0, "page_offset": 1})
        context["side_bar_my_evaluation_url"] = reverse("teacher:my-evaluation",
                                                    kwargs={"comment_type": 0, "page_offset": 1})
        information_complete, msg = self.is_teacher_information_not_complete(self.teacher)
        # if information_complete:
        #     logger.debug("资料填写检查: 完整, {tag}".format(tag=information_complete))
        # else:
        #     logger.debug("资料填写检查: >{msg}<, {tag}".format(msg=msg, tag=information_complete))
        context["information_not_complete"] = information_complete

    def _my_course_badge(self):
        # 我的课表旁边的徽章
        # 这里先不做
        return models.TimeSlot.objects.filter(
            order__teacher = self.teacher,
            web_visited = False,
            deleted=False,
            order__status=models.Order.PAID,
            start__gt=timezone.now()
        ).count()

    def _my_student_badge(self):
        # 我的学生旁边的徽章
        # 这里先注释掉,等李鑫想清楚再做
        # visited_parent = models.TeacherVistParent.objects.filter(
        #     teacher=self.teacher, web_visited=False
        # ).values("parent").count()
        # teachers_parent = models.Parent.objects.filter(
        #     order__teacher=self.teacher,
        #     order__status=models.Order.PAID,
        #     order__timeslot__isnull=False,
        # ).distinct("pk").count()
        # my_student = teachers_parent-visited_parent
        my_student=None
        return my_student

    def _my_evaluation_badge(self):
        # 我的评价旁边的徽章
        # 这里先不做
        return models.Comment.objects.filter(
            web_visited=False,
            timeslot__order__teacher=self.teacher,
            timeslot__order__status=models.Order.PAID,
        ).count()
        # my_evaluation = None
        # return my_evaluation

    def is_teacher_information_not_complete(self, teacher: models.Teacher):
        # 老师基本资料没有填完就返回False,否则返回True
        # 姓名
        if not teacher.name:
            return False, "姓名未填写"
        # 手机号
        profile = teacher.user.profile
        if not profile.phone:
            return False, "电话未填写"
        # 性别
        if profile.gender == "u":
            return False, "性别未指明"
        # 所在城市
        if not teacher.region:
            return False, "地区未填写"
        # 出生年月日
        if not profile.birthday:
            return False, "生日未填写"
        # 教龄
        if not teacher.teaching_age:
            return False, "教龄未填写"
        # 毕业院校
        if not teacher.graduate_school:
            return False, "毕业院校未填写"
        # 授课科目, 授课年级
        if not teacher.abilities.all().exists():
            return False, "授课科目或授课年级"
        # 风格标记
        if not teacher.tags.all().exists():
            return False, "风格标记未填写"
        # 自我介绍
        if not teacher.introduce:
            return False, "自我介绍未填写"
        return True, ""


class MySchoolTimetable(BasicTeacherView):
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
            self.today = timezone.now()

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
            if time_slot.is_passed:
                # 结束的课程
                self.class_state = 2
                # 上完课才做评价检查
                if time_slot.comment_id and time_slot.comment_id > 0:
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
        cs = MySchoolTimetable.CourseSet(order, timezone.now())
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

    def handle_get(self, request, user, teacher, year, month):
        # 思路,集中订单中的每堂课,映射到当月的日期中,由每天上课的数量来日期的状态.
        # 浏览完这个页面,所有老师为浏览的课程就显示成已浏览
        models.TimeSlot.objects.filter(
            order__teacher=teacher,
            web_visited = False,
            deleted=False,
            order__status=models.Order.PAID,
            start__gt=timezone.now()
        ).update(web_visited=True)
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

        def day_background_color(given_day: datetime.datetime, today: datetime):
            # 用于描述每日的底色,过去为灰色,未来为白色,今天是一种特定的深色
            # 1-过去, 2-今天, 3-未来
            new_given_day = datetime.date(given_day.year, given_day.month, given_day.day)
            new_today = datetime.date(today.year, today.month, today.day)
            if new_given_day > new_today:
                return 3
            elif new_given_day == new_today:
                return 2
            elif new_given_day < new_today:
                return 1

        def day_class_status(given_day: datetime.datetime, today: datetime):
            # 用于描述每日课程状况,过去的是灰色方块,未来是深色方块
            # 今天比较特殊,课程全部上完,为灰色,有课没上就是深色方块
            # 1-过去, 2-今天, 3-未来
            new_given_day = datetime.date(given_day.year, given_day.month, given_day.day)
            new_today = datetime.date(today.year, today.month, today.day)
            if new_given_day > new_today:
                return 3
            elif new_given_day == new_today:
                # 拆分成今天全上完了,就是上完了
                # 没有上完,状态就是还没上
                today_end = datetime.datetime(today.year, today.month, today.day, 23, 59, 59)
                if models.TimeSlot.objects.filter(order__teacher=teacher,
                                               end__range=(timezone.now(), today_end),
                                               deleted=False, order__status=models.Order.PAID).exists():
                    # 还有课要上
                    return 3
                else:
                    # 课都上完了
                    return 1
                # return 2
            elif new_given_day < new_today:
                return 1

        for index, day_item in enumerate(one_month):
            # 0-当天没课, 1-已经上过, 2-正在上,3-还没上
            day_statue = 0
            given_day = make_aware(datetime.datetime(day_item.year, day_item.month, day_item.day, 23, 59, 59))

            if cts.specific_day_count(given_day) > 0:
                day_statue = day_class_status(given_day, today)
            else:
                day_statue = 0
            is_pass = day_background_color(given_day, today)
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
        return render(request, "teacher/schedule/my_school_timetable.html", context)


class MyStudents(BasicTeacherView):
    """
    TW-5-2, 我的学生
    """

    def get_student_statistics(self, teacher: models.Teacher, student_type: int, per_page: int, offset: int):
        """
        不同类型的学生统计
        :param teacher: 老师
        :param student_type: 0 - 当前学生, 1 - 结课学生, 2 - 退费学生
        :param per_page: 分页每页显示的学生数目
        :param offset: 当前位置
        :return: { "student_statistics": [当前学生,结课学生,退费学生],}
        """
        # 先获得不同学生在这个老师下的最新订单
        # 退费统计
        refund_count = 0
        # 结课统计
        session_count = 0
        # 当前正在上课
        current_count = 0
        page_student_list = []

        refund_student_list = models.Parent.objects.filter(order__teacher=teacher, order__status=models.Order.REFUND,
                                                           order__timeslot__isnull=False).distinct("pk")
        # 统计退费学生
        refund_count = refund_student_list.count()
        # 不存在退费的学生
        student_list = models.Parent.objects.filter(order__teacher=teacher, order__status=models.Order.PAID,
                                                    order__timeslot__isnull=False).distinct("pk")
        if student_type == 2:
            # 填充退费学生信息
            page_student_list = refund_student_list
        for one_student in student_list:
            if models.TimeSlot.objects.filter(order__teacher=teacher, order__parent=one_student,
                                              order__status=models.Order.PAID, start__gt=timezone.now()).exists():
                # 统计上课学生
                current_count += 1
                # 填充上课学生信息
                if student_type == 0:
                    page_student_list.append(one_student)
            else:
                # 统计结课学生
                session_count += 1
                # 填充结课学生信息
                if student_type == 1:
                    page_student_list.append(one_student)
        # 填充信息
        p = Paginator(page_student_list, per_page)
        page_student_details = []
        # 找到特定分页进行详细处理
        for one_student in p.page(offset).object_list:
            one_details = {
                "name": one_student.student_name or one_student.user.profile.phone,
                "id": one_student.id,
                "mail": one_student.check_month_letter(teacher)[0]
            }

            complete_time_slot = models.TimeSlot.objects.filter(
                order__status=models.Order.PAID,
                order__teacher=teacher,
                order__parent=one_student,
                end__lt=timezone.now()
            ).count()*2
            all_time_slot = models.TimeSlot.objects.filter(
                order__status=models.Order.PAID,
                order__teacher=teacher,
                order__parent=one_student,
            ).count()*2
            # 设置进度
            one_details["progress"] = "{complete}/{total}".format(
                complete=complete_time_slot, total=all_time_slot
            )
            if student_type == 0:
                # 当前学生需要检查新生,续费,和正常三种情况
                if not models.TimeSlot.objects.filter(
                    order__status=models.Order.PAID,
                    order__teacher=teacher,
                    order__parent=one_student,
                    end__lt=timezone.now()
                ).exists():
                    one_details["state"] = "新生"
                else:
                    if complete_time_slot/all_time_slot > 0.8:
                        one_details["state"] = "续费"
                    else:
                        one_details["state"] = "正常"
                one_order = one_student.order_set.filter(
                    teacher=teacher,
                    status=models.Order.PAID
                ).order_by("-created_at").first()
            elif student_type == 1:
                one_details["state"] = "结课"
                one_order = one_student.order_set.filter(
                    teacher=teacher,
                    status=models.Order.PAID
                ).order_by("-created_at").first()
            elif student_type == 2:
                one_details["state"] = "退费"
                one_order = one_student.order_set.filter(
                    teacher=teacher,
                    status=models.Order.REFUND,
                ).order_by("-created_at").first()
            one_details["grade"] = one_order.grade.name
            one_details["price"] = "￥%.2f/小时" %(one_order.price/100)
            page_student_details.append(one_details)
        return {
            "student_statistics": [current_count, session_count, refund_count],
            "student_details": page_student_details,
            "num_pages": p.num_pages
        }

    def handle_get(self, request, user, teacher, student_type, page_offset):
        offset = int(page_offset)

        student_state = {0: ["新生", "正常", "续费"], 1: ["结课"], 2: ["退费"]}
        # filter_student_state = student_state[int(student_type)]
        # student_list, total_page = self.current_student(teacher, filter_student_state, 11, offset)

        ss = self.get_student_statistics(teacher, int(student_type), 11, offset)
        student_list = ss["student_details"]
        total_page = ss["num_pages"]

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
        context["page_offset"] = offset
        context["current_count"], context["session_count"], context["refund_count"] = ss["student_statistics"]
        # pp(context)
        return render(request, "teacher/my_students.html", context)


class MyEvaluation(BasicTeacherView):
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
                            "publish_date": localtime(comment.created_at).strftime("%Y-%m-%d %H:%M"),
                            "full_star": range(comment.score),
                            "empty_star": range(5 - comment.score),
                            "comment": comment.content,
                            "class_type": self.class_type + "1对1",
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

    def handle_get(self, request, user, teacher, comment_type, page_offset):
        # 一进来就把所有评价提示里的评价都设置为已阅读
        models.Comment.objects.filter(
            web_visited=False,
            timeslot__order__teacher=teacher,
            timeslot__order__status=models.Order.PAID,
        ).update(web_visited=True)
        context = {}
        page_offset = int(page_offset)
        comment_type = int(comment_type)
        set_teacher_page_general_context(teacher, context)
        side_bar_content = SideBarContent(teacher)
        side_bar_content(context)
        cres = CommentReply.CommentReplyErrorSession(request)
        if cres.is_error:
            context["comment_reply_error"] = cres.error["reply"][0]
            logger.debug("comment_reply_error is {error}".format(error=cres.error))
            logger.debug("comment-reply-error-id is {id}".format(id=cres.id))
        else:
            context["comment_reply_error"] = None
        comments_array, count_package, avg_score = self.get_comments(cres, teacher, comment_type)
        if comments_array:
            context["comments"] = comments_array[page_offset - 1]
        else:
            context["comments"] = []
        # 建立分页数据
        page_array = []
        for one_offset in range(len(comments_array)):
            page_array.append(
                {
                    "url": reverse("teacher:my-evaluation", kwargs={"comment_type": comment_type,
                                                                    "page_offset": one_offset + 1}),
                    "offset_id": one_offset + 1
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
                "good": "{:2.0f}".format(count_package[1] / count_package[0] * 100),
                "mid": "{:2.0f}".format(count_package[2] / count_package[0] * 100),
                "bad": "{:2.0f}".format(count_package[3] / count_package[0] * 100)
            }
        else:
            context["percent"] = {
                "good": "0",
                "mid": "0",
                "bad": "0"
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
                avg_score = bcl.sum_score / bcl.all_count
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
            }, {
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
            }, ], (1, 2, 3, 4, 4.8)

        comment_list, count_package, avg_score = _real(cres, teacher, comment_type)
        # comment_list, count_package, avg_score = _fake(cres)
        return split_list(comment_list, 4), count_package, avg_score


class CommentReply(BasicTeacherView):
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
    def handle_post(self, request, user, teacher, comment_type, page_offset, id):
        # print("comment reply id is {id}".format(id=id))
        comment_reply_form = forms.CommentReplyForm(request.POST)
        if comment_reply_form.is_valid():
            # 是正确的返回
            reply = comment_reply_form["reply"].data
            the_comment = models.Comment.objects.get(id=id)
            the_comment.reply = reply
            the_comment.save()
            # print("reply is {reply}".format(reply=reply))
            # print("reply's type is {the_type}".format(the_type=type(comment_reply_form["reply"])))
        else:
            # print("comment reply not illegal, {errors}".format(errors=comment_reply_form.errors))
            CommentReply.CommentReplyErrorSession.save(request, comment_reply_form.errors, id,
                                                       comment_reply_form["reply"].data)
            request.session["comment-reply-error"] = comment_reply_form.errors
            request.session["comment-reply-error-id"] = id
        return redirect("teacher:my-evaluation", comment_type=comment_type, page_offset=page_offset)


class GenerateSMS(View):
    # 登录后创建验证码.注意,这里不需要任何输入,验证码会发送到注册时给的手机号码.
    def post(self, request):
        user = request.user
        phone = user.profile.phone
        send_result, msg = models.Checkcode.generate(phone)
        # print("{result}: {msg}".format(result=send_result, msg=msg))
        if send_result is True:
            return JsonResponse({"sent": True})
        else:
            return JsonResponse({"sent": False, "msg": msg})


class WithdrawalRequest(BasicTeacherView):
    # 老师提现检测,里面的检测都通过,就进行转账
    def is_now_allowed_to_withdraw(self):
        now = timezone.localtime(timezone.now())
        # 如果是周二,就返回True,否则False
        withdraw_weekday = models.Config.objects.all()[0].withdraw_weekday
        return now.isoweekday() == withdraw_weekday  or settings.TEST_WITHDRAW

    def handle_post(self, request, user, teacher, *args, **kwargs):
    # def post(self, request):
    #     user = request.user
        phone = user.profile.phone
        code = request.POST.get("code", None)
        if self.is_now_allowed_to_withdraw():
            verify_result, verify_code = models.Checkcode.verify(phone, code)
            if verify_result is True:
                # 验证通过,进行转账操作,注意,这里要重新计算金额,然后转账
                balance = user.account.calculated_balance
                # 如果TEST_WITHDRAW为True,则余额为0也可以转账.
                if balance > 0 or settings.TEST_WITHDRAW:
                    withdraw = models.Withdrawal()
                    # withdraw.account = user.account
                    # withdraw.amount = balance
                    bankcard_list = user.account.bankcard_set.all()
                    if bankcard_list:
                        bankcard = bankcard_list[0]
                        withdraw.bankcard = bankcard
                    else:
                        return JsonResponse({
                            "verify": False, "msg": "没有可用的银行卡",
                        })
                    withdraw.save()
                    acc = models.AccountHistory.build_withdrawal_history(withdrawal=withdraw,
                                                                         account=user.account,
                                                                         amount=-balance)
                    request.session["withdraw_amount"] = balance
                    request.session["bank_name"] = bankcard.bank_name
                    request.session["bank_card_end_number"] = bankcard.mask_card_number()[-1]
                    request.session["expect_time"] = localtime(acc.submit_time + datetime.timedelta(days=2)).strftime("%Y-%m-%d %H:%M")
                    # 转账成功后,把数字存在session里
                    return JsonResponse({
                        "verify": True, "url": reverse("teacher:my-wallet-withdrawal-result")
                    })
                else:
                    return JsonResponse({
                        "verify": False, "msg": "可提金额为0"
                    })
            else:
                # 手机验证码不正确,不需要弹窗,直接提示
                return JsonResponse({
                    "verify": False, "pop": False, "msg": models.Checkcode.verify_msg(verify_result, verify_code)
                })
        else:
            # 非有效转账时间,需要弹窗提示
            withdraw_weekday = models.Config.objects.all()[0].withdraw_weekday
            _wday = withdraw_weekday or 2
            return JsonResponse({
                "verify": False, "pop": True, "msg": "请在每"+weekday_format(_wday)+"0:00-24:00进行提现!"
            })


class MyWalletBase(BasicTeacherView):
    # 我的钱包,基础类
    template_url = ""

    def __init__(self, **kwargs):
        super().__init__(**kwargs)
        self.request = None

    def handle_get(self, request, user, teacher, *args, **kwargs):
        context = {}
        side_bar_content = SideBarContent(teacher)
        side_bar_content(context)
        set_teacher_page_general_context(teacher, context)
        self.request = request
        self.get_handle(context, teacher)
        return render(request, self.template_url, context)

    def get_handle(self, context, teacher:models.Teacher):
        pass


class MyWalletWithdrawal(MyWalletBase):
    # 我的钱包
    template_url = "teacher/wallet/my_wallet_withdrawal.html"

    def get_handle(self, context, teacher:models.Teacher):
        # 第一张银行卡
        account = teacher.user.account
        bankcard_list = account.bankcard_set.all()
        if bankcard_list:
            bankcard = account.bankcard_set.all()[0]
            context["bank_card_number"] = " ".join(bankcard.mask_card_number())
            context["bank_name"] = bankcard.bank_name
        else:
            context["bank_card_number"] = "还没有绑定储蓄卡"
            context["bank_name"] = ""

        context["balance"] = "%.2f" % (account.calculated_balance/100)
        context["phone"] = json.dumps({"code": teacher.user.profile.mask_phone()})
        # pp(context)


class MyWalletWithdrawalResult(MyWalletBase):
    # 我的钱包,提现结果
    template_url = "teacher/wallet/my_wallet_withdrawal_result.html"

    def get_handle(self, context, teacher:models.Teacher):
        context["bank_card_end_number"] = self.request.session.pop("bank_card_end_number", "****")
        context["bank_name"] = self.request.session.pop("bank_name", "未知银行")
        context["withdraw_amount"] = "%.2f" % (self.request.session.pop("withdraw_amount", 0)/100)
        context["expect_time"] = self.request.session.pop("expect_time", "未知时间")

class MyWalletWithdrawalRecord(MyWalletBase):
    # 我的钱包,提现记录
    template_url = "teacher/wallet/my_wallet_withdrawal_record.html"

    def _fake_record_list(self):
        return [["2015-12-21 16:38:22",
                 "¥1280.00",
                 "中国银行 储蓄卡 (9923)",
                 "处理中", ],
                ["2015-12-21 16:38:22",
                 "¥16.00",
                 "中国银行 储蓄卡 (9923)",
                 "2015-12-22 18:00:00", ],
                ["2015-12-21 16:38:22",
                 "¥9.20",
                 "中国农业银行 储蓄卡 (1107)",
                 "处理中", ],
                ]

    def record_list(self, teacher: models.Teacher):
        account = teacher.user.account
        result = []
        for withdraw_item in models.Withdrawal.objects.filter(accounthistory__account=account).order_by("-accounthistory__submit_time"):
            result.append([
                localtime(withdraw_item.accounthistory.submit_time).strftime("%Y-%m-%d %H:%M:%S"),
                "¥%.2f" % (withdraw_item.accounthistory.amount/100),
                "{bank_name} 储蓄卡 ({last_code})".format(bank_name=withdraw_item.bankcard.bank_name,
                                                     last_code=withdraw_item.bankcard.mask_card_number()[-1]),
                withdraw_item.status_des,
            ])
        return result
        # return self._fake_record_list()

    def get_handle(self, context, teacher:models.Teacher):
        context["result_list"] = self.record_list(teacher)


class MyLevel(MyWalletBase):
    template_url = "teacher/my_level.html"

    def fake_context(self, context):
        context["all_level"] = ["一级", "二级", "三级"]
        context["current_level"] = "二级"
        context["level_record"] = ["2015-12-10 注册成为麻辣老师",
                                   "2015-12-10 升级为一级麻辣老师",
                                   "2015-12-10 升级为二级麻辣老师"]
        context["level_rights"] = [
            ["优秀麻辣老师", "1200.00/月", "40%"],
            ["中级麻辣老师", "1500.00/月", "35%"],
            ["高级麻辣老师", "2000.00/月", "30%"],
            ["麻辣合伙人", "10000.00/月", "20%"],
        ]
        context["evaluation_time"] = ["2015-12-01", "2016-03-01"]

    def get_handle(self, context, teacher:models.Teacher):
        self.fake_context(context)
        # 显示所有等级
        context["all_level"] = [one_level.name for one_level in models.Level.objects.all().order_by("level_order")]
        # 显示当前等级
        if not teacher.level_id:
            context["current_level"] = ""
        else:
            context["current_level"] = teacher.level.name
        # 显示等级变更记录
        operation = {}
        for key, val in models.LevelRecord.OPERATION_CHOICE:
            operation[key] = val
        level_record_list = teacher.levelrecord_set.all().order_by("-create_at")
        # print(level_record_list)

        splite_pos = 4
        level_record_list_str = []
        for one_level_record in level_record_list[:splite_pos]:
            level_record_list_str.append(
                "{time} {operation}为{level}麻辣老师".format(
                    time=one_level_record.create_at.strftime("%Y-%m-%d"),
                    operation=operation[one_level_record.operation],
                    level=one_level_record.to_level.name
                )
            )
        context["first_level_record"] = level_record_list_str
        level_record_list_str = []
        for one_level_record in level_record_list[splite_pos:]:
            level_record_list_str.append(
                "{time} {operation}为{level}麻辣老师".format(
                    time=one_level_record.create_at.strftime("%Y-%m-%d"),
                    operation=operation[one_level_record.operation],
                    level=one_level_record.to_level.name
                )
            )
        context["rest_level_record"] = level_record_list_str
        # 显示等级权益, 权益按照老师的第几个能力算?暂定随便找一个老师拥有的能力
        all_level_rights = []
        for one_price in models.Price.objects.filter(region=teacher.region, ability=teacher.abilities.all()[0]).order_by("level__level_order"):
            all_level_rights.append(
                [
                    "{level}麻辣老师".format(level=one_price.level.name),
                    "¥{price}/月".format(price=one_price.price/100),
                    "{percent}%".format(percent=one_price.commission_percentage),
                ]
            )
        context["level_rights"] = all_level_rights
        # 显示评估时间, 就显示当前如果record没有,初始时间是哪个?
        if level_record_list:
            last_evaluation = level_record_list[0].create_at
        else:
            last_evaluation = timezone.now()
        next_evaluation = last_evaluation+datetime.timedelta(days=90)
        context["evaluation_time"] = [last_evaluation.strftime("%Y-%m-%d"),
                                      next_evaluation.strftime("%Y-%m-%d")]


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


def is_information_complete(user: User):
    # 判断用户信息是否填写完整
    percent = information_complete_percent(user)
    if percent < 1:
        return False
    return True


def does_teacher_pass_qualifield_audit(user: User):
    # 判断老师资质审核是否结束
    teacher = user.teacher
    if teacher.status != models.Teacher.INTERVIEW_OK or not teacher.status_confirm:
        return False
    return True


class BaseTeacherView(View):
    """
    Base View for Teacher web client, require teacher being logined
    """

    @method_decorator(user_passes_test(is_teacher_logined, login_url=LOGIN_URL))
    @method_decorator(user_passes_test(is_information_complete, login_url='teacher:complete-information',
                                       redirect_field_name="next"))
    @method_decorator(user_passes_test(does_teacher_pass_qualifield_audit, login_url='teacher:register-progress',
                                       redirect_field_name="next"))
    def dispatch(self, request, *args, **kwargs):
        return super(BaseTeacherView, self).dispatch(request, *args, **kwargs)

    def getContextTeacher(self, request):
        context = {}
        teacher = get_object_or_404(models.Teacher, user=request.user)
        context['teacher'] = teacher
        context['teacherName'] = teacher.name
        context['title'] = ''
        set_teacher_page_general_context(teacher, context)
        return context, teacher

    def setSidebarContent(self, teacher, context):
        side_bar_content = SideBarContent(teacher)
        side_bar_content(context)

    def getNextStepUrl(self, teacher, pre_step='basic_info'):
        cert_items = None
        def _get_cert_items():
            return models.Certificate.objects.filter(teacher=teacher)
        def _is_cert_to_upload(cert_type, cert_items):
            is_found = False
            for cert in cert_items:
                if cert.type==cert_type:
                    is_found = True
                    if (not bool(cert.img) or (cert.audited and not cert.verified)):
                        return True
            if not is_found:
                return True
            return False
        if pre_step=='basic_info':
            cert_items = cert_items or _get_cert_items()
            if _is_cert_to_upload(models.Certificate.ID_HELD, cert_items):
                return reverse('teacher:certificate-id')
            pre_step = 'cert_id' # 继续下一步
        if pre_step=='cert_id':
            cert_items = cert_items or _get_cert_items()
            if _is_cert_to_upload(models.Certificate.ACADEMIC, cert_items):
                return reverse('teacher:certificate-academic')
            pre_step = 'cert_academic' # 继续下一步
        if pre_step=='cert_academic':
            cert_items = cert_items or _get_cert_items()
            if _is_cert_to_upload(models.Certificate.TEACHING, cert_items):
                return reverse('teacher:certificate-teaching')
            pre_step = 'cert_teaching' # 继续下一步
        if pre_step=='cert_teaching':
            if teacher.is_english_teacher():
                cert_items = cert_items or _get_cert_items()
                if _is_cert_to_upload(models.Certificate.ENGLISH, cert_items):
                    return reverse('teacher:certificate-english')
            pre_step = 'cert_english' # 继续下一步
        if pre_step=='cert_english':
            if models.Achievement.objects.filter(teacher=teacher).count() == 0:
                return reverse('teacher:achievement')
            pre_step = 'achievement' # 继续下一步
        if pre_step=='achievement':
            if models.Highscore.objects.filter(teacher=teacher).count() == 0:
                return reverse('teacher:highscore')
        return reverse('teacher:first-page')


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
        context['next_url'] = self.getNextStepUrl(teacher, 'cert_id')
        return render(request, self.template_path, context)

    def buildContextData(self, context, certIdHeld, certIdFront):
        context['cert_id_held'] = certIdHeld
        context['cert_id_front'] = certIdFront
        context['id_num'] = certIdHeld.name
        context['idHeldUrl'] = certIdHeld.img_url()
        context['idFrontUrl'] = certIdFront.img_url()
        context['verified'] = certIdHeld.verified
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
        if not check_id_number(id_num):
            error_msg = '身份证号不合法'
            if isJsonReq:
                return JsonResponse({'ok': False, 'msg': error_msg, 'code': -1})
            context['error_msg'] = error_msg
            return render(request, self.template_path, self.buildContextData(context, certIdHeld, certIdFront))
        certIdHeld.name = id_num

        idHeldImgFile = None
        idFrontImgFile = None
        if request.FILES:
            idHeldImgFile = request.FILES.get('idHeldImg')
            idFrontImgFile = request.FILES.get('idFrontImg')
        if not idHeldImgFile and not certIdHeld.img:
            error_msg = '手持身份证照片不能为空'
            if isJsonReq:
                return JsonResponse({'ok': False, 'msg': error_msg, 'code': -1})
            context['error_msg'] = error_msg
            return render(request, self.template_path, self.buildContextData(context, certIdHeld, certIdFront))
        if not idFrontImgFile and not certIdFront.img:
            error_msg = '身份证正面图片不能为空'
            if isJsonReq:
                return JsonResponse({'ok': False, 'msg': error_msg, 'code': -1})
            context['error_msg'] = error_msg
            return render(request, self.template_path, self.buildContextData(context, certIdHeld, certIdFront))
        if idHeldImgFile:
            held_img_content = ContentFile(idHeldImgFile.read())
            certIdHeld.img.save("idHeld" + str(teacher.id), held_img_content)
        if idFrontImgFile:
            front_img_content = ContentFile(idFrontImgFile.read())
            certIdFront.img.save("idFrontImg" + str(teacher.id), front_img_content)

        certIdHeld.audited = False
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
        context = self.buildContextData(context, cert, teacher)
        context['next_url'] = self.getNextStepUrl(teacher)
        return render(request, self.template_path, context)

    def buildContextData(self, context, cert, teacher=None):
        context['cert'] = cert
        context['cert_title'] = self.cert_title
        context['cert_name'] = self.cert_name
        context['name_val'] = self.get_cert_name(cert, teacher)
        context['cert_img_url'] = cert.img_url()
        context['hint_content'] = self.hint_content
        return context

    def get_cert_name(self, cert, teacher):
        return cert.name

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
        if len(name) > 15:
            return JsonResponse({'ok': False, 'msg': '证书名称不能超过15个字', 'code': 1})
        cert.name = name

        certImgFile = None
        if request.FILES:
            certImgFile = request.FILES.get('certImg')
        if not certImgFile and not cert.img:
            error_msg = '证书照片不能为空'
            if isJsonReq:
                return JsonResponse({'ok': False, 'msg': error_msg, 'code': 1})
            context['error_msg'] = error_msg
            return render(request, self.template_path, self.buildContextData(context, cert))
        if certImgFile:
            cert_img_content = ContentFile(certImgFile.read())
            cert.img.save("certImg" + str(self.cert_type) + str(teacher.id), cert_img_content)

        cert.audited = False
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

    def get_cert_name(self, cert, teacher):
        return cert.name or teacher.graduate_school

    def getNextStepUrl(self, teacher):
        return super(CertificateAcademicView, self).getNextStepUrl(teacher,'cert_academic')


class CertificateTeachingView(CertificateForOnePicView):
    cert_type = models.Certificate.TEACHING
    cert_title = '教师资质认证'
    cert_name = '证书名称'
    hint_content = "请上传有效期内的教师资格证书或同等资格证明"

    def getNextStepUrl(self, teacher):
        return super(CertificateForOnePicView, self).getNextStepUrl(teacher,'cert_teaching')


class CertificateEnglishView(CertificateForOnePicView):
    cert_type = models.Certificate.ENGLISH
    cert_title = '英语水平认证'
    cert_name = '证书名称'
    hint_content = "请上传你最具代表性的英语水平证书"

    def getNextStepUrl(self, teacher):
        return super(CertificateForOnePicView, self).getNextStepUrl(teacher,'cert_english')


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
        otherCerts = models.Certificate.objects.filter(teacher=teacher, type=models.Certificate.OTHER).order_by('id')
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
        if len(name) > 15:
            return JsonResponse({'ok': False, 'msg': '证书名称不能超过15个字', 'code': 1})
        cert.name = name

        certImgFile = None
        if request.FILES:
            certImgFile = request.FILES.get('certImg')
        if not certImgFile and not cert.img:
            error_msg = '证书照片不能为空'
            if isJsonReq:
                return JsonResponse({'ok': False, 'msg': error_msg, 'code': 1})
            context['error_msg'] = error_msg
            return render(request, self.template_path, self.buildContextData(context, teacher))
        if certImgFile:
            cert_img_content = ContentFile(certImgFile.read())
            cert.img.save("certImg" + str(cert.type) + str(teacher.id) + '_' + str(cert_img_content.size),
                          cert_img_content)

        cert.audited = False
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
        context['next_url'] = self.getNextStepUrl(teacher)

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
        introduce = self.request.POST.get('introduce', None)
        subclass = self.request.POST.get('subclass', None)

        tags = request.POST.get("selectedTags")

        tags_list = json.loads(tags)

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
        context['calculated_balance'] = account.calculated_balance
        if account.bankcard_set.all().exists() and account.calculated_balance > 0:
            context["can_withdrawal"] = True
        else:
            context["can_withdrawal"] = False
        context['bankcard'] = ''
        bankcards = models.BankCard.objects.filter(account=account)
        if bankcards.count() > 0:
            bankcard = bankcards[0]
            context['bankcard'] = bankcard
        histories = models.AccountHistory.objects.filter(account=account).order_by("-submit_time")
        # paginate
        histories, pager = paginate(histories, page_size=self.PAGE_SIZE)
        context['histories'] = histories
        context['pager'] = pager
        context['unbind_hint'] = '目前无法解除绑定，如需解除绑定请联系麻辣老师客服人员或致电%s'%(settings.SERVICE_SUPPORT_TEL)
        return render(request, self.template_path, context)

    def listAccountHistories(self, request, teacher):
        page = self.request.GET.get('page') and self.request.GET.get('page').strip() or 1
        account = teacher.safe_get_account()
        query_set = models.AccountHistory.objects.filter(account=account, valid=True).order_by("-submit_time")
        # paginate
        query_set, pager = paginate(query_set, page, page_size=self.PAGE_SIZE)
        histories = [{'submit_time': localtime(h.submit_time).strftime('%Y-%m-%d %H:%M'), 'positive': h.amount >=0, 'amount': money_format(h.amount), 'comment': h.comment} for h in query_set]
        return JsonResponse({'ok': True, 'list': histories, 'pager': pager.dict()})


class WalletBankcardView(BaseTeacherView):
    template_path = 'teacher/wallet/bankcard_add.html'
    success_template_path = 'teacher/wallet/bankcard_add_success.html'

    def get(self, request, step=None):
        context, teacher = self.getContextTeacher(request)
        self.setSidebarContent(teacher, context)
        if step == 'success':
            return render(request, self.success_template_path, context)
        account = teacher.safe_get_account()
        bankcards = models.BankCard.objects.filter(account=account)
        if bankcards.count() >= 1: # 只支持添加一张银行卡
            context['error_msg'] = "目前只支持添加一张银行卡。获取更多帮组，请致电麻辣老师客服 010-88776655"
            return render(request, self.success_template_path, context)
        certIdHeld, _ = models.Certificate.objects.get_or_create(teacher=teacher, type=models.Certificate.ID_HELD,
                                                                       defaults={'name': "", 'verified': False})
        if not certIdHeld.verified:
            context['failure'] = True
            context['error_msg'] = '只有通过<a href="{cert_url}">身份认证</a>才可以添加银行卡'.format(cert_url=reverse('teacher:certificate-id'))
            return render(request, self.success_template_path, context)
        context['id_num'] = certIdHeld.name # 身份证号
        context['phone'] = teacher.user.profile.phone
         # 所属省市
        context['provinces'] = models.Region.objects.filter(superset_id__isnull=True)
        return render(request, self.template_path, context)

    def post(self, request):
        context, teacher = self.getContextTeacher(request)
        account = teacher.safe_get_account()
        bankcards = models.BankCard.objects.filter(account=account)
        if bankcards.count() >= 1: # 只支持添加一张银行卡
            return JsonResponse({'ok': False, 'msg': '目前只支持添加一张银行卡', 'code': 1})
        certIdHeld, _ = models.Certificate.objects.get_or_create(teacher=teacher, type=models.Certificate.ID_HELD,
                                                                       defaults={'name': "", 'verified': False})
        if not certIdHeld.verified:
            return JsonResponse({'ok': False, 'msg': '只有通过身份认证才可以添加银行卡', 'code': 1})

        id_num = self.request.POST.get('id_num') and self.request.POST.get('id_num').strip() or ''
        card_number = self.request.POST.get('card_number') and self.request.POST.get('card_number').replace(' ','',) or ''
        phone = self.request.POST.get('phone') and self.request.POST.get('phone').strip() or ''
        checkcode = self.request.POST.get('checkcode') and self.request.POST.get('checkcode').strip() or ''
        province = request.POST.get('province', '').strip()
        city = request.POST.get('city', '').strip()
        district = request.POST.get('district', '').strip()
        region_id = district or city or province
        opening_bank = request.POST.get('opening_bank', '').strip()
        if not id_num or not card_number or not phone or not checkcode or not region_id or not opening_bank:
            return JsonResponse({'ok': False, 'msg': '参数不能为空', 'code': 1})
        if not check_id_number(id_num):
            return JsonResponse({'ok': False, 'msg': '身份证号不合法', 'code': 2})
        if not check_bankcard_number(card_number):
            return JsonResponse({'ok': False, 'msg': '银行卡号长度错误', 'code': 3})
        if not isValidPhone(phone):
            return JsonResponse({'ok': False, 'msg': '手机号错误', 'code': 4})
        if not isValidCode(checkcode):
            return JsonResponse({'ok': False, 'msg': '验证码格式错误', 'code': 5})
        if not models.Checkcode.verify(phone, checkcode)[0]:
            return JsonResponse({'ok': False, 'msg': '验证码错误', 'code': 6})
        region = get_object_or_404(models.Region, pk=region_id)
        # 重复检测
        oldone = models.BankCard.objects.filter(card_number=card_number).first()
        if oldone is not None:
            return JsonResponse({'ok': False, 'msg': '该银行卡已被添加过了, 如有疑问请联系客服人员', 'code': 7})
        # 添加银行卡
        account = teacher.safe_get_account()
        bankcard = models.BankCard(account=account)
        bankcard.card_number = card_number
        bankcard.bank_name = settings.DEFAULT_BANK_NAME # TODO: 获得银行卡对应银行名称
        bankcard.region = region
        bankcard.opening_bank = opening_bank
        bankcard.save()
        return JsonResponse({'ok': True, 'msg': '', 'code': 0})

class StudentLetter(BasicTeacherView):

    def handle_get(self, request, user, teacher, student_type, page_offset, student_id):
        context = {
            'student_type': student_type,
            'page_offset': page_offset,
            'student_id': student_id
        }
        self.setSidebarContent(teacher, context)
        student = None
        try:
            student = models.Parent.objects.get(id=student_id)
        except:
            context["error_code"] = -1
            return render(request, "teacher/letter/edit.html", context)

        if not student.check_month_letter(teacher):
            context["error_code"] = -2
            return render(request, "teacher/letter/edit.html", context)

        context["student_name"] = student.student_name or student.user.profile.phone

        return render(request, "teacher/letter/edit.html", context)

    def handle_post(self, request, user, teacher, student_type, page_offset, student_id):
        title = request.POST.get('title', None)
        content = request.POST.get('content', None)

        context = {
            'student_type': student_type,
            'page_offset': page_offset,
            'student_id': student_id
        }
        self.setSidebarContent(teacher, context)

        student = None
        try:
            student = models.Parent.objects.get(id=student_id)
        except:
            return JsonResponse({'ok': False, 'msg': 'student not find', 'code': -1})

        canLetter, letterId = student.check_month_letter(teacher)
        if not canLetter:
            return JsonResponse({'ok': False, 'msg': 'already have letter', 'id': letterId, 'code': -2})

        letter = models.Letter(teacher=teacher, parent=student, title=title, content=content)
        letterId = letter.save()

        return JsonResponse({'ok': True, 'id': letterId, 'code': 0})


class StudentLetterView(BasicTeacherView):
    def handle_get(self, request, user, teacher, student_type, page_offset, student_id, letter_id):
        context = {
            'student_type': student_type,
            'page_offset': page_offset,
            'student_id': student_id
        }
        self.setSidebarContent(teacher, context)
        student = None
        try:
            student = models.Parent.objects.get(id=student_id)
        except:
            context["error_code"] = -1
            return render(request, "teacher/letter/edit.html", context)

        letter = None
        try:
            context["letter"] = models.Letter.objects.get(id=letter_id)
        except:
            context["error_code"] = -2
            return render(request, "teacher/letter/view.html", context)

        context["student_name"] = student.student_name or student.user.profile.phone

        return render(request, "teacher/letter/view.html", context)


class KLXAccountView(BasicTeacherView):
    def handle_get(self, request, user, teacher, *args, **kwargs):
        context = {'klx_web_site': KLX_WEB_SITE}
        self.setSidebarContent(teacher, context)
        set_teacher_page_general_context(teacher, context)
        subject = teacher.subject()
        isSupported = subject.name in KLX_TEACHING_SUBJECTS
        context['subject'] = subject
        context['isSupported'] = isSupported
        if isSupported:
            # 注册快乐学用户, 刚下完订单时调用的异步注册任务可能失败
            orders = models.Order.objects.filter(status=models.Order.PAID, teacher=teacher)
            for order in orders:
                registerKuaiLeXueUserByOrder.delay(order.id) # 只调用一次
            # 构造返回数据
            klx_username = user.profile.klx_username
            if not klx_username:
                klx_username = klx_reg_teacher(teacher)
            klx_password = user.profile.klx_password
            context['klx_username'] = klx_username or ''
            context['klx_password'] = klx_password or ''
        return render(request, "teacher/kuailexue/account.html", context)


class ScheduleWeeklyConfigView(BasicTeacherView):
    '''
    授课时间(周表)设置
    '''
    def handle_get(self, request, user, teacher, *args, **kwargs):
        context = {'klx_web_site': KLX_WEB_SITE}
        self.setSidebarContent(teacher, context)
        set_teacher_page_general_context(teacher, context)
        weekly_time_slots = teacher.region.weekly_time_slots.all()
        # 老师自己设置的可用时间表
        available_weekly_times = set(teacher.weekly_time_slots.all())
        for s in weekly_time_slots:
            s.available = True if s in available_weekly_times else False
        context['weekly_time_slots'] = weekly_time_slots
        context['daily_time_slots'] = models.WeeklyTimeSlot.DAILY_TIME_SLOTS
        return render(request, "teacher/schedule/weekly_config.html", context)

    def handle_post(self, request, user, teacher, *args, **kwargs):
        new_ids_str = request.POST.get('new_ids')
        removed_ids_str = request.POST.get('removed_ids')
        new_ids = new_ids_str and [s for s in new_ids_str.split('+') if s and s.strip() != ''] or None
        removed_ids = removed_ids_str and [s for s in removed_ids_str.split('+') if s and s.strip() != ''] or None
        if not new_ids and not removed_ids:
            return JsonResponse({'ok': False, 'msg': '没有变化', 'code': 1})
        if new_ids:
            new_ts_set = models.WeeklyTimeSlot.objects.filter(id__in=new_ids)
            for s in new_ts_set:
                teacher.weekly_time_slots.add(s)
        if removed_ids:
            removed_ts_set = models.WeeklyTimeSlot.objects.filter(id__in=removed_ids)
            for s in removed_ts_set:
                teacher.weekly_time_slots.remove(s)
        return JsonResponse({'ok': True, 'msg': 'ok', 'code': 0})
