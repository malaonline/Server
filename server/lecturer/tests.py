import json
from app import models
from django.test import Client, TestCase
from django.contrib.auth.hashers import make_password
from django.contrib.auth.models import User
from django.core.urlresolvers import reverse


# Create your tests here.
class TestLecturerWeb(TestCase):
    def _init_test_lecturer(self):
        if hasattr(self, '_lecturer'):
            return
        self.lecturer = "lecturer_oUP1zwTO9"
        self.lecturer_pswd = "123"
        user_data = {
            'password': make_password(self.lecturer_pswd),
            'is_staff': False,
            'is_superuser': False,
        }
        user, _ = User.objects.get_or_create(username=self.lecturer,
                                             defaults=user_data)
        _lecturer, _ = models.Lecturer.objects.get_or_create(
            user=user,
            defaults={
                "subject": models.Subject.get_english(),
                "name": "kaoru"
            })
        self._lecturer = _lecturer

    def setUp(self):
        self.client = Client()
        self._init_test_lecturer()
        self.client.login(username=self.lecturer, password=self.lecturer_pswd)

    def tearDown(self):
        pass

    def test_home(self):
        response = self.client.get(reverse('lecturer:home'))
        self.assertEqual(302, response.status_code)

    def test_index(self):
        response = self.client.get(reverse('lecturer:index'))
        self.assertEqual(200, response.status_code)

    def test_login(self):
        client = Client()
        response = client.get(reverse('lecturer:login'))
        self.assertEqual(200, response.status_code)

    def test_login_auth(self):
        client = Client()
        data = {'username': self.lecturer, 'password': self.lecturer_pswd}
        response = client.post(reverse('lecturer:login'), data=data)
        self.assertEqual(302, response.status_code)

    def test_logout(self):
        client = Client()
        client.login(username=self.lecturer, password=self.lecturer_pswd)
        response = client.get(reverse('lecturer:logout'))
        self.assertEqual(302, response.status_code)

    def test_timeslots(self):
        response = self.client.get(reverse('lecturer:timeslots'))
        self.assertEqual(200, response.status_code)

    def test_living(self):
        response = self.client.get(reverse('lecturer:living'))
        self.assertEqual(200, response.status_code)

    def test_timeslot_questions(self):
        response = self.client.get(
            reverse('lecturer:timeslot-questions', kwargs={'tsid': 1}))
        self.assertEqual(200, response.status_code)

        # update test
        response = self.client.post(
            reverse('lecturer:timeslot-questions', kwargs={'tsid': 0}),
            data={'gids': ''}
        )
        self.assertEqual(404, response.status_code)
        # TODO: create test LiveCourse

    def test_exercise_store(self):
        response = self.client.get(reverse('lecturer:exercise-store'))
        self.assertEqual(200, response.status_code)
        data = {
            "group": '{"exercises":[{"analyse":"题目解析","solution":"选项1","id":"","title":"题目","options":[{"text":"选项1","id":""},{"text":"选项2","id":""},{"text":"选项3","id":""},{"text":"选项4","id":""}]}],"desc":"题组描述","id":"","title":"题组名称"}'}
        response = self.client.post(reverse('lecturer:exercise-store'), data)
        self.assertEqual(200, response.status_code)

    def test_api_exercise_store(self):
        url = reverse('lecturer:api-exercise-store')
        response = self.client.get(url)
        self.assertEqual(200, response.status_code)

        url = reverse('lecturer:api-exercise-store') + '?action=group_list'
        response = self.client.get(url)
        self.assertEqual(200, response.status_code)

        url = reverse('lecturer:api-exercise-store') + '?action=group&gid=1'
        response = self.client.get(url)
        self.assertEqual(200, response.status_code)
