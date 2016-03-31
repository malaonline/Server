from django.test import TestCase
from django.test import Client
from django.core.urlresolvers import reverse
from django.contrib.auth.models import User, Group
from django.contrib.auth.hashers import make_password
from django.core.management import call_command
from django.utils.timezone import make_aware
from django.conf import settings

from app.models import Teacher, Profile, Order, Parent, School, Region, Grade, Subject, TimeSlot, Ability, Highscore
from app.models import Tag, Certificate, Achievement, Account, Checkcode, OrderManager, Price, Level
from teacher.views import FirstPage, split_list
from teacher.views import information_complete_percent
from teacher.management.commands import create_fake_order

import json
import datetime
import random
from pprint import pprint as pp


class TestTeacherWeb(TestCase):
    def setUp(self):
        self.assertTrue(settings.FAKE_SMS_SERVER)
        call_command("build_groups_and_permissions")

    def tearDown(self):
        pass

    def test_verify_sms_code(self):
        phone = "18922405996"
        sms_code = Checkcode.generate(phone)
        client = Client()
        # 第一次
        response = client.post(reverse("teacher:login"),
                               {
                                   "phone": phone,
                                   "code": sms_code
                               })
        self.assertEqual(response.status_code, 200)
        self.assertEqual(json.loads(response.content.decode()), {
            "result": True, "url": "/teacher/information/complete"})
        # 第二次
        sms_code = Checkcode.generate(phone)
        second_client = Client()
        response = second_client.post(reverse("teacher:login"),
                                      {
                                          "phone": phone,
                                          "code": sms_code
                                      })
        self.assertEqual(json.loads(response.content.decode()), {
            "url": "/teacher/information/complete", "result": True})

        # 测试information_compelte_percent
        profile = Profile.objects.get(phone=phone)
        percent = information_complete_percent(profile.user)
        self.assertEqual(percent, 0)


# Create your tests here.
class TestWebPage(TestCase):
    teacher_name = "teacher name"
    teacher_password = "I'm password"
    teacher_email = "teacher@mail.com"
    teacher_phone = "18922405996"
    teacher_salt = "I'm salt"

    parent_name = "parent name"
    parent_password = "I'm password"
    parent_email = "parent@mail.com"
    parent_phone = "18922405997"
    parent_salt = "I'm salt"

    first_init = False

    def setUp(self):
        self.assertTrue(settings.FAKE_SMS_SERVER)
        if self.first_init is False:
            call_command("build_groups_and_permissions")
            self.first_init = True
        # 创建老师
        teacher_user = User.objects.create(username=self.teacher_name)
        teacher_user.password = make_password(self.teacher_password, self.teacher_salt)
        teacher_user.email = self.teacher_email
        teacher_user.save()
        profile = Profile(user=teacher_user, phone=self.teacher_phone)
        profile.save()
        teacher = Teacher(user=teacher_user)
        teacher.save()
        teacher_group = Group.objects.get(name="老师")
        teacher_user.groups.add(teacher_group)
        teacher_user.save()
        profile.save()
        teacher.save()
        teacher_account = Account(user=teacher_user)
        teacher_account.save()
        # 为老师创建能力
        grade = Grade.objects.get(name="高三")
        subject = Subject.objects.get(name="英语")
        ability = Ability.objects.get(grade=grade, subject=subject)
        teacher.abilities.add(ability)
        # 设置面试记录
        teacher.status = Teacher.INTERVIEW_OK
        teacher.status_confirm = True
        # 设置性别
        profile.gender = "f"
        profile.save()
        # 设置区域
        other_region = Region.objects.get(name="其他")
        teacher.region = other_region
        # 设置老师级别
        teacher_level = Level.objects.all()[0]
        teacher.level = teacher_level
        # 为老师创建对应价格
        price = Price(region=other_region, ability=ability, level=teacher_level,
                      price=1, salary=2, commission_percentage=3)
        price.save()
        # 设置老师名称
        teacher.name = self.teacher_name
        teacher.save()
        # 创建家长
        parent_user = User.objects.create(username=self.parent_name)
        parent_user.password = make_password(self.parent_password, self.parent_salt)
        parent_user.email = self.parent_email
        parent_user.save()
        parent_profile = Profile(user=parent_user, phone=self.parent_phone)
        parent_profile.save()
        parent_group = Group.objects.get(name="家长")
        parent_user.groups.add(parent_group)
        parent_user.save()
        parent_profile.save()
        parent = Parent(user=parent_user)
        parent.save()
        # 创建订单
        school = School(name="逗比中学",
                        address="逗比路", region=Region.objects.get(name="其他"),
                        center=True, longitude=0, latitude=0, opened=False)
        school.save()
        # 为老师添加学校
        teacher.schools.add(school)
        order = Order(parent=parent, teacher=teacher, school=school,
                      grade=Grade.objects.get(name="一年级"),
                      subject=Subject.objects.get(name="数学"),
                      coupon=None,
                      price=200, hours=50, total=100, paid_at=make_aware(datetime.datetime.now()),
                      status=Order.PAID)
        order.save()
        # 创建订单里的课程
        one_time_slot = TimeSlot(order=order, start=make_aware(datetime.datetime(2016, 1, 1, 8, 0, 0)),
                                 end=make_aware(datetime.datetime(2016, 1, 1, 10, 0, 0)))
        one_time_slot.save()
        one_time_slot = TimeSlot(order=order, start=make_aware(datetime.datetime(2015, 12, 30, 15, 0, 0)),
                                 end=make_aware(datetime.datetime(2015, 12, 30, 17, 0, 0)))
        one_time_slot.save()
        one_time_slot = TimeSlot(order=order, start=make_aware(datetime.datetime(2015, 12, 20, 11, 0, 0)),
                                 end=make_aware(datetime.datetime(2015, 12, 20, 12, 0, 0)))
        one_time_slot.save()
        # 检查订单的数目是否正确
        order = Order.objects.get(teacher=teacher)
        self.assertEqual(3, len(order.timeslot_set.filter(deleted=False)))

    def tearDown(self):
        old_user = User.objects.get(username=self.teacher_name)
        profile = Profile.objects.get(user=old_user)
        teacher = Teacher.objects.get(user=old_user)
        Order.objects.filter(teacher=teacher).delete()
        teacher.delete()
        profile.delete()
        old_user.delete()

    def test_register_show(self):
        client = Client()
        register_url = reverse("teacher:login")
        response = client.get(register_url)
        self.assertEqual(response.status_code, 200)


    def test_information_complete(self):
        client = Client()
        client.login(username=self.teacher_name, password=self.teacher_password)
        response = client.get(reverse("teacher:complete-information"))
        self.assertEqual(response.status_code, 200)
        post_client = Client()
        post_client.login(username=self.teacher_name, password=self.teacher_password)
        post_response = post_client.post(reverse("teacher:complete-information"),
                                         {
                                             "name": "曹亚文",
                                             "gender": "m",
                                             "region": "其他",
                                             "subclass": "数学",
                                             "grade": '["小学一年级", "小学二年级"]'
                                         })
        self.assertEqual(post_response.status_code, 200)
        self.assertEqual(json.loads(post_response.content.decode("utf-8")),
                         {"url": "/teacher/register/progress"})

    def test_register_progress(self):
        client = Client()
        client.login(username=self.teacher_name, password=self.teacher_password)
        response = client.get(reverse("teacher:register-progress"))
        self.assertEqual(response.status_code, 200)

    def test_first_page(self):
        client = Client()
        client.login(username=self.teacher_name, password=self.teacher_password)
        response = client.get(reverse("teacher:first-page"))
        self.assertEqual(response.status_code, 200, response.content.decode())

    def test_my_school_timetable(self):
        client = Client()
        client.login(username=self.teacher_name, password=self.teacher_password)
        response = client.get(reverse("teacher:my-school-timetable",
                                      kwargs={"year": "2016", "month": "01"}))
        time_slot_data = json.loads(response.context["time_slot_data"])
        self.assertTrue("20160101" in time_slot_data)
        self.assertTrue("20151230" in time_slot_data)
        self.assertEqual(1, len(time_slot_data["20160101"]))
        self.assertEqual(1, len(time_slot_data["20151230"]))
        self.assertEqual(response.status_code, 200)

    def the_parent(self):
        return User.objects.get(username=self.parent_name).parent

    def the_teacher(self):
        return User.objects.get(username=self.teacher_name).teacher

    def test_my_students(self):
        client = Client()
        client.login(username=self.teacher_name, password=self.teacher_password)
        teacher = self.the_teacher()
        parent = self.the_parent()
        school = teacher.schools.all()[0]
        grade = teacher.abilities.all()[0].grade
        subject = teacher.abilities.all()[0].subject
        hours = 5
        coupon = None
        # 创建订单
        # TODO: 这个地方的OrderManager有问题
        new_order = Order.objects.create(parent, teacher, school, grade, subject, hours, coupon)
        # 对订单进行退费

        for student_type in range(3):
            response = client.get(reverse("teacher:my-students", kwargs={
                "student_type": student_type, "page_offset": 1
            }))
            self.assertEqual(response.status_code, 200)

    def test_course_show(self):
        """
        测试课程的正确获得姿势
        """
        user = User.objects.get(username=self.teacher_name)
        teacher = Teacher.objects.get(user=user)
        order_set = Order.objects.filter(teacher=teacher)
        first_page = FirstPage()
        self.assertEqual(3, first_page.class_complete(teacher))
        self.assertEqual(2, first_page.class_waiting(teacher,
                                                     make_aware(datetime.datetime(2015, 12, 20, 16, 0, 0))))
        self.assertEqual(1, first_page.class_waiting(teacher,
                                                     make_aware(datetime.datetime(2016, 1, 1, 5, 0, 0))))
        current_student, complete_student = first_page.student_on_class(teacher)
        self.assertEqual(1, complete_student)

    def test_create_new_teacher(self):
        """
        创建老师的测试内容
        :return:
        """
        new_teacher = Teacher.new_teacher("12345")

    def test_split_list(self):
        """
        测试切分列表
        :return:
        """
        the_list = split_list(list(range(10)), 3)
        self.assertEqual(4, len(the_list))
        self.assertEqual([[0, 1, 2], [3, 4, 5], [6, 7, 8], [9]], the_list)

    def test_teacher_account(self):
        teacher = random.choice(list(Teacher.objects.filter(user__username__istartswith="test")))
        account = teacher.safe_get_account()
        self.assertIsNotNone(account.calculated_balance)
        self.assertIsNotNone(account.accumulated_income)
        self.assertIsNotNone(account.anticipated_income)

    def test_my_evaluation(self):
        for comment_type in range(4):
            client = Client()
            client.login(username=self.teacher_name, password=self.teacher_password)
            response = client.get(reverse("teacher:my-evaluation", kwargs={
                "comment_type": comment_type, "page_offset": 1
            }))
            self.assertEqual(response.status_code, 200)

    def test_information_complete_percent(self):
        new_user = Teacher.new_teacher("54321")
        profile = new_user.profile
        teacher = new_user.teacher
        fp = FirstPage()
        # 空白
        # self.assertEqual(fp.information_complete_percent(teacher, profile)[0], 0)
        teaching_age_patch = 5
        # 电话
        profile.phone = "18922405996"
        self.assertEqual(fp.information_complete_percent(teacher, profile)[0], 5+teaching_age_patch)
        # 姓名
        teacher.name = "曹亚文"
        self.assertEqual(fp.information_complete_percent(teacher, profile)[0], 10+teaching_age_patch)
        # 性别
        profile.gender = "f"
        self.assertEqual(fp.information_complete_percent(teacher, profile)[0], 12+teaching_age_patch)
        # 城市
        teacher.region = Region.objects.get(name="洛阳市")
        self.assertEqual(fp.information_complete_percent(teacher, profile)[0], 17+teaching_age_patch)
        # 能力,包括年级和学科
        grade = Grade.objects.get(name="高二")
        subject = Subject.objects.get(name="英语")
        ability = Ability.objects.get(grade=grade, subject=subject)
        teacher.abilities.add(ability)
        self.assertEqual(fp.information_complete_percent(teacher, profile)[0], 25+teaching_age_patch)
        # 是英语老师
        self.assertTrue("is_english" in fp.information_complete_percent(teacher, profile)[1])
        # 自我简介,10个字以上
        teacher.introduce = "012345678"
        self.assertEqual(fp.information_complete_percent(teacher, profile)[0], 25+teaching_age_patch)
        teacher.introduce = "0123456789"
        self.assertEqual(fp.information_complete_percent(teacher, profile)[0], 35+teaching_age_patch)
        # 提分榜
        highscore = Highscore(teacher=teacher, name="好学生", increased_scores=200, school_name="好学校", admitted_to="高级好学校")
        highscore.save()
        teacher.highscore_set.add(highscore)
        self.assertEqual(fp.information_complete_percent(teacher, profile)[0], 45+teaching_age_patch)
        # 教龄
        teacher.teaching_age = 5
        self.assertEqual(fp.information_complete_percent(teacher, profile)[0], 50)
        # 风格
        teacher.tags.add(Tag.objects.get(name="幽默风趣"))
        self.assertEqual(fp.information_complete_percent(teacher, profile)[0], 55)

        def _add_cert(percent, cert):
            id_held = cert
            id_held.save()
            teacher.certificate_set.add(id_held)
            self.assertEqual(fp.information_complete_percent(teacher, profile)[0], percent)

        # 身份证手持照
        _add_cert(65, Certificate(teacher=teacher, name="身份证手持照", type=Certificate.ID_HELD, verified=True))
        # 毕业证书
        _add_cert(75, Certificate(teacher=teacher, name="毕业证书", type=Certificate.ACADEMIC, verified=True))
        # 教师资格证书
        _add_cert(80, Certificate(teacher=teacher, name="教师资格证书", type=Certificate.TEACHING, verified=True))
        # 英语水平
        _add_cert(85, Certificate(teacher=teacher, name="英语水平证书", type=Certificate.ENGLISH, verified=True))
        # 其它证书
        _add_cert(90, Certificate(teacher=teacher, name="其他证书", type=Certificate.OTHER, verified=True))
        # 特殊成果
        ach = Achievement(teacher=teacher, title="残疾人长跑一等奖")
        ach.save()
        teacher.achievement_set.add(ach)
        self.assertEqual(fp.information_complete_percent(teacher, profile)[0], 95)
        # 头像
        pass

    def check_page_accesibility(self, tag_name):
        client = Client()
        client.login(username=self.teacher_name, password=self.teacher_password)
        register_url = reverse(tag_name)
        response = client.get(register_url)
        # response.render()
        self.assertEqual(response.status_code, 200)

    def test_my_level(self):
        # 我的级别
        self.check_page_accesibility("teacher:my-level")

    def test_withdraw_record(self):
        # 查看提现记录
        self.check_page_accesibility("teacher:my-wallet-withdrawal-record")

    def test_withdraw_result(self):
        # 查看提现结果
        self.check_page_accesibility("teacher:my-wallet-withdrawal-result")

    def test_withdraw(self):
        # 提现界面
        self.check_page_accesibility("teacher:my-wallet-withdrawal")

    def test_wallet(self):
        # 我的钱包界面
        self.check_page_accesibility("teacher:wallet")

    def test_basic_doc(self):
        # 基本信息页面
        self.check_page_accesibility("teacher:basic_doc")

    def test_highscore(self):
        # 提分榜页面
        self.check_page_accesibility("teacher:highscore")

    def test_withdrawal_request(self):
        client = Client()
        client.login(username=self.teacher_name, password=self.teacher_password)
        response = client.post(reverse('teacher:generate-sms'))
        self.assertEqual(response.status_code, 200)

        checkcode = Checkcode.objects.get(phone=self.teacher_phone)
        response = client.post(reverse('teacher:withdrawal_request'), {
            'code': checkcode.checkcode,
            })
        self.assertEqual(response.status_code, 200)

    def test_sms_generate(self):
        # 检查api生成是否正确
        client = Client()
        response = client.post(reverse("sms"), {"action": "send", "phone": "18922405996"})
        self.assertEqual(response.status_code, 200)
        response = client.post(reverse("sms"), json.dumps({"action": "send", "phone": "18922405996"}),
                               "application/json")
        self.assertEqual(response.status_code, 200)
        # 错误数据输入后的检测
        response = client.post(reverse("sms"), {"action": "send", "phone": "18922405996"},
                               "application/json")
        self.assertEqual(400, response.status_code)

    def test_teacher_login(self):
        client = Client()
        # 未获得验证码的时候,进行的验证
        response = client.post(reverse("teacher:login"), {"phone": "18922405996", "code": "1111"})
        self.assertEqual(response.status_code, 200)
        self.assertFalse(json.loads(response.content.decode())["result"])
        # 获得验证码以后,进行的验证
        response = client.post(reverse("sms"), {"action": "send", "phone": "18922405996"})
        self.assertEqual(response.status_code, 200)
        response = client.post(reverse("teacher:login"), {"phone": "18922405996", "code": "1111"})
        self.assertEqual(response.status_code, 200)
        self.assertTrue(json.loads(response.content.decode())["result"])


class TestCommands(TestCase):
    def setUp(self):
        self.assertTrue(settings.FAKE_SMS_SERVER)
        call_command("mala_all")

    def tearDown(self):
        pass

    def test_create_fake_order(self):
        pass
        # call_command("create_fake_order")
        # cfo = create_fake_order.Command()
        # now = datetime.datetime.now()
        # # print("build_time_array result")
        # # pp(cfo.build_time_array(datetime.datetime.now(), 0, [[[1,2,3], [4,5,6]]]))
        # start_time, end_time = cfo.build_time_array(now, 0, [[[1,2,3], [4,5,6]]])[0]
        # self.assertEqual(datetime.datetime(now.year, now.month, now.day, 1,2,3), start_time)
        # self.assertEqual(datetime.datetime(now.year, now.month, now.day, 4,5,6), end_time)

    def test_create_fake_comment(self):
        pass
        # call_command("create_fake_order")
        # call_command("create_fake_comment")
        # for one_teacher in Teacher.objects.all():
        #     for one_order in one_teacher.order_set.all():
        #         for one_time_slot in one_order.timeslot_set.all():
        #             # one_time_slot = one_teacher.order_set.all()[0].timeslot_set.all()[0]
        #             self.assertNotEqual(one_time_slot.comment, None)
