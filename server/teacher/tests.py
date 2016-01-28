from django.test import TestCase
from django.test import Client
from django.core.urlresolvers import reverse
from django.contrib.auth.models import User, Group
from django.contrib.auth.hashers import make_password
from django.core.management import call_command
from django.conf import settings

from app.models import Teacher, Profile, Order, Parent, School, Region, Grade, Subject, TimeSlot
from teacher.views import FirstPage

import json
import datetime
from dateutil.tz import tzoffset

# 时区
custom_time_zone = tzoffset("北京时间", +28800)


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
                      price=200, hours=50, total=100, paid_at=datetime.datetime.now(tz=custom_time_zone),
                      status=Order.PAID)
        order.save()
        # 创建订单里的课程
        one_time_slot = TimeSlot(order=order, start=datetime.datetime(2016, 1, 1, tzinfo=custom_time_zone), end=datetime.datetime(2016, 1, 20, tzinfo=custom_time_zone))
        one_time_slot.save()
        one_time_slot = TimeSlot(order=order, start=datetime.datetime(2015, 12, 15, tzinfo=custom_time_zone), end=datetime.datetime(2015, 12, 30, tzinfo=custom_time_zone))
        one_time_slot.save()

    def tearDown(self):
        old_user = User.objects.get(username=self.teacher_name)
        profile = Profile.objects.get(user=old_user)
        teacher = Teacher.objects.get(user=old_user)
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
        response = client.get(reverse("teacher:my-school-timetable"))
        self.assertEqual(response.status_code, 200)

    def test_course_show(self):
        """
        测试课程的正确获得姿势
        """
        user = User.objects.get(username=self.teacher_name)
        teacher = Teacher.objects.get(user=user)
        order_set = Order.objects.filter(teacher=teacher)
        first_page = FirstPage()
        self.assertEqual(2, first_page.class_complete(order_set))
        self.assertEqual(1, first_page.class_waiting(order_set,
                                                     datetime.datetime(2015, 12, 20, tzinfo=custom_time_zone)))
        self.assertEqual(1, first_page.student_complete(order_set))
