from django.test import TestCase
from django.test import Client
from django.core.urlresolvers import reverse
from django.contrib.auth.models import User, Group
from django.contrib.auth.hashers import make_password
from django.core.management import call_command
from django.utils.timezone import make_aware
from django.conf import settings

from app.models import Teacher, Profile, Order, Parent, School, Region, Grade, Subject, TimeSlot
from teacher.views import FirstPage, split_list
from teacher.management.commands import create_fake_order

import json
import datetime
import random
from pprint import pprint as pp


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
    parent_phone = "18922405996"
    parent_salt = "I'm salt"

    first_init = False

    def setUp(self):
        self.assertTrue(settings.FIX_SMS_CODE)
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
        register_url = reverse("teacher:register")
        response = client.get(register_url)
        # response.render()
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
                         {"url": "/teacher/register/progress/"})

    def test_register_progress(self):
        client = Client()
        client.login(username=self.teacher_name, password=self.teacher_password)
        response = client.get(reverse("teacher:register-progress"))
        self.assertEqual(response.status_code, 200)

    def test_first_page(self):
        client = Client()
        client.login(username=self.teacher_name, password=self.teacher_password)
        response = client.get(reverse("teacher:first-page"))
        self.assertEqual(response.status_code, 200)

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

    def test_my_students(self):
        client = Client()
        client.login(username=self.teacher_name, password=self.teacher_password)
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
        self.assertEqual(3, first_page.class_complete(order_set))
        self.assertEqual(2, first_page.class_waiting(order_set,
                                                     make_aware(datetime.datetime(2015, 12, 20, 16, 0, 0))))
        self.assertEqual(1, first_page.class_waiting(order_set,
                                                     make_aware(datetime.datetime(2016, 1, 1, 5, 0, 0))))
        self.assertEqual(1, first_page.student_complete(order_set))

    def test_create_new_teacher(self):
        """
        创建老师的测试内容
        :return:
        """
        new_teacher = Teacher.new_teacher()

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


class TestCommands(TestCase):
    def setUp(self):
        self.assertTrue(settings.FIX_SMS_CODE)
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
