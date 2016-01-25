from django.test import TestCase
from django.test import Client
from django.core.urlresolvers import reverse
from django.contrib.auth.models import User, Group
from django.contrib.auth.hashers import make_password
from django.core.management import call_command
from app.models import Teacher, Profile
import json


# Create your tests here.
class TestWebPage(TestCase):
    name = "user_name"
    password = "I'm password"
    email = "teacher@mail.com"
    phone = "18922405996"
    salt = "I'm salt"
    first_init = False

    def setUp(self):
        if self.first_init is False:
            call_command("build_groups_and_permissions")
            self.first_init = True

        new_user = User.objects.create(username=self.name)
        new_user.password = make_password(self.password, self.salt)
        new_user.email = self.email
        new_user.save()
        profile = Profile(user=new_user, phone=self.phone)
        profile.save()
        teacher = Teacher(user=new_user)
        teacher.save()
        teacher_group = Group.objects.get(name="老师")
        new_user.groups.add(teacher_group)
        new_user.save()
        profile.save()
        teacher.save()

    def tearDown(self):
        old_user = User.objects.get(username=self.name)
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
        client.login(username=self.name, password=self.password)
        response = client.get(reverse("teacher:complete-information"))
        self.assertEqual(response.status_code, 200)
        post_client = Client()
        post_client.login(username=self.name, password=self.password)
        post_response = post_client.post(reverse("teacher:complete-information"),
                                         {
                                             "name": "曹亚文",
                                             "gender": "m",
                                             "region": "其它",
                                             "subclass": "数学",
                                             "grade": '["小学一年级", "小学二年级"]'
                                         })
        self.assertEqual(post_response.status_code, 200)
        self.assertEqual(json.loads(post_response.content.decode("utf-8")),
                         {"url": "/teacher/register/progress/"})

    def test_register_progress(self):
        client = Client()
        client.login(username=self.name, password=self.password)
        response = client.get(reverse("teacher:register-progress"))
        self.assertEqual(response.status_code, 200)

    def test_first_page(self):
        client = Client()
        client.login(username=self.name, password=self.password)
        response = client.get(reverse("teacher:first-page"))
        self.assertEqual(response.status_code, 200)

    def test_my_school_timetable(self):
        client = Client()
        client.login(username=self.name, password=self.password)
        response = client.get(reverse("teacher:my-school-timetable"))
        self.assertEqual(response.status_code, 200)

