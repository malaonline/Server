from django.test import TestCase
from rest_framework.authtoken.models import Token
from django.contrib.auth.models import User
from django.test import Client
from django.core.urlresolvers import reverse
from django.contrib.auth import authenticate
from django.core.management import call_command
import json
from app.models import Parent, Teacher, Checkcode, Profile
from app.views import Sms
from app.utils.algorithm import Tree, Node
from app.utils.types import parseInt

from teacher.views import information_complete_percent
from app.models import Region


# Create your tests here.
class TestApi(TestCase):
    def setUp(self):
        pass

    def tearDown(self):
        pass

    def test_token_key(self):
        # 测试token是否能正常创建
        user = User.objects.get(username="parent0")
        token = Token.objects.create(user=user)
        self.assertTrue(isinstance(token.key, str))

    def test_teacher_list(self):
        client = Client()
        url = "/api/v1/teachers"
        response = client.get(url)
        self.assertEqual(response.status_code, 200)
        url = "/api/v1/teachers?grade=4&subject=3&tags=2+6"
        response = client.get(url)
        self.assertEqual(response.status_code, 200)

    def test_teacher_detail(self):
        client = Client()
        url = "/api/v1/teachers"
        response = client.get(url)
        pk = json.loads(response.content.decode())['results'][0]['id']
        url = "/api/v1/teachers/%d" % pk
        response = client.get(url)
        self.assertEqual(response.status_code, 200)

    def test_tag_list(self):
        client = Client()
        url = "/api/v1/tags"
        response = client.get(url)
        self.assertEqual(response.status_code, 200)

    def test_grade_list(self):
        client = Client()
        url = "/api/v1/grades"
        response = client.get(url)
        self.assertEqual(response.status_code, 200)

    def test_memberservice_list(self):
        client = Client()
        url = "/api/v1/memberservices"
        response = client.get(url)
        self.assertEqual(response.status_code, 200)

    def test_weeklytimeslot_list(self):
        client = Client()
        url = "/api/v1/weeklytimeslots"
        response = client.get(url)
        self.assertEqual(response.status_code, 200)

    def test_policy(self):
        client = Client()
        url = "/api/v1/policy"
        response = client.get(url)
        self.assertEqual(response.status_code, 200)

    def test_teacher_weekly_time_slot(self):
        client = Client()
        url = "/api/v1/teachers/1/weeklytimeslots?school_id=1"
        response = client.get(url)
        self.assertEqual(response.status_code, 200)

    def test_get_token_key(self):
        client = Client()
        request_url = "/api/v1/token-auth"
        username = "parent1"
        password = "123123"
        user = authenticate(username=username, password=password)
        self.assertNotEqual(user, None)
        parent_user = User.objects.get(username=username)
        self.assertEqual(parent_user.is_active, 1)
        response = client.post(request_url, {"username": username,
                                             "password": password})
        self.assertEqual(response.status_code, 200)

        client2 = Client()
        response2 = client2.post(request_url, {"username": username,
                                               "password": password})
        self.assertEqual(response.content, response2.content)

    def test_modify_student_name(self):
        token_client = Client()
        token_request_url = "/api/v1/token-auth"
        username = "parent1"
        password = "123123"
        response = token_client.post(token_request_url, {"username": username,
                                                         "password": password})
        token = json.loads(response.content.decode())["token"]
        user = User.objects.get(username=username)
        parent = Parent.objects.get(user=user)
        user_token = Token.objects.get(user=user)
        self.assertEqual(user_token.key, token)

        call_command("add_groups_to_sample_users")

        client = Client()
        request_url = "/api/v1/parents/%d" % (parent.pk,)
        json_data = json.dumps({"student_name": "StudentNewName"})
        response = client.patch(request_url, content_type="application/json",
                                data=json_data,
                                **{"HTTP_AUTHORIZATION": " Token %s" % token})
        self.assertEqual(200, response.status_code)
        json_ret = json.loads(response.content.decode())
        self.assertEqual(json_ret["done"], "true")
        parent_after = Parent.objects.get(user=user)
        self.assertEqual(parent_after.student_name, "StudentNewName")

        parent_after.student_name = ""
        parent_after.save()
        response = client.patch(request_url, content_type="application/json",
                                data=json_data,
                                **{"HTTP_AUTHORIZATION": " Token %s" % token})
        self.assertEqual(200, response.status_code)
        self.assertEqual(response.content.decode(), '{"done":"true"}')
        json_ret = json.loads(response.content.decode())
        self.assertEqual(json_ret["done"], "true")

        request_url = "/api/v1/parents/%d" % (parent.pk,)
        school_name = '洛阳一中'
        json_data = json.dumps({"student_school_name": school_name})
        response = client.patch(request_url, content_type="application/json",
                                data=json_data,
                                **{"HTTP_AUTHORIZATION": " Token %s" % token})
        self.assertEqual(200, response.status_code)
        json_ret = json.loads(response.content.decode())
        self.assertEqual(json_ret["done"], "true")
        parent_after = Parent.objects.get(user=user)
        self.assertEqual(parent_after.student_school_name, school_name)

    def test_create_order(self):
        token_client = Client()
        token_request_url = "/api/v1/token-auth"
        username = "parent1"
        password = "123123"
        response = token_client.post(token_request_url, {"username": username,
                                                         "password": password})
        token = json.loads(response.content.decode())["token"]
        user = User.objects.get(username=username)
        user_token = Token.objects.get(user=user)
        self.assertEqual(user_token.key, token)

        client = Client()
        request_url = "/api/v1/orders"
        json_data = json.dumps({
            'teacher': 1, 'school': 1, 'grade': 1, 'subject': 1,
            'coupon': 2, 'hours': 14, 'weekly_time_slots': [3, 8],
            })
        response = client.post(request_url, content_type="application/json",
                               data=json_data,
                               **{"HTTP_AUTHORIZATION": " Token %s" % token})
        self.assertEqual(201, response.status_code)
        pk = json.loads(response.content.decode())['id']

        request_url = "/api/v1/orders/%d" % pk
        response = client.get(request_url, content_type='application/json',
                              **{'HTTP_AUTHORIZATION': ' Token %s' % token})
        self.assertEqual(200, response.status_code)

        json_ret = json.loads(response.content.decode())
        self.assertEqual(json_ret['status'], 'u')

    def test_create_comment(self):
        token_client = Client()
        token_request_url = "/api/v1/token-auth"
        username = "parent0"
        password = "123123"
        response = token_client.post(token_request_url, {"username": username,
                                                         "password": password})
        token = json.loads(response.content.decode())["token"]
        user = User.objects.get(username=username)

        parent = user.parent
        order = parent.order_set.all()[0]
        timeslot = order.timeslot_set.all()[0]

        client = Client()
        request_url = "/api/v1/comments"
        json_data = json.dumps({
            'timeslot': timeslot.pk, 'score': 5, 'content': 'Good.'})
        response = client.post(request_url, content_type="application/json",
                               data=json_data,
                               **{"HTTP_AUTHORIZATION": " Token %s" % token})
        self.assertEqual(201, response.status_code)
        pk = json.loads(response.content.decode())['id']

        request_url = "/api/v1/comments/%d" % pk
        response = client.get(request_url, content_type='application/json',
                              **{'HTTP_AUTHORIZATION': ' Token %s' % token})
        self.assertEqual(200, response.status_code)

        json_ret = json.loads(response.content.decode())
        self.assertEqual(json_ret['score'], 5)
        self.assertEqual(json_ret['content'], 'Good.')

        # Create a comment for a timeslot for a order not belongs to cur user
        user2 = User.objects.get(username='parent4')
        parent2 = user2.parent
        order2 = parent2.order_set.all()[0]
        timeslot2 = order2.timeslot_set.all()[0]

        request_url = "/api/v1/comments"
        json_data = json.dumps({
            'timeslot': timeslot2.pk, 'score': 5, 'content': 'Good.'})
        response = client.post(request_url, content_type="application/json",
                               data=json_data,
                               **{"HTTP_AUTHORIZATION": " Token %s" % token})

        self.assertEqual(400, response.status_code)

    def test_get_timeslots(self):
        token_client = Client()
        token_request_url = "/api/v1/token-auth"
        username = "parent1"
        password = "123123"
        response = token_client.post(token_request_url, {"username": username,
                                                         "password": password})
        token = json.loads(response.content.decode())["token"]
        user = User.objects.get(username=username)
        user_token = Token.objects.get(user=user)
        self.assertEqual(user_token.key, token)

        client = Client()
        request_url = "/api/v1/timeslots"
        response = client.get(request_url, content_type='application/json',
                              **{"HTTP_AUTHORIZATION": " Token %s" % token})
        self.assertEqual(200, response.status_code)


class TestModels(TestCase):
    def setUp(self):
        call_command("build_groups_and_permissions")

    def tearDown(self):
        pass

    def test_new_teacher(self):
        new_teacher = Teacher.new_teacher()
        self.assertTrue(isinstance(new_teacher, User))

    def test_sms_verify(self):
        phone = "18922405996"
        sms_code = Sms().generateCheckcode(phone)
        self.assertTrue(Checkcode.verify_sms(phone, sms_code))
        self.assertFalse(Checkcode.verify_sms(phone, "error_code"))

    def test_other_region(self):
        """
        检查其它是否已经从数据库中移除
        """
        Region.objects.get(name="其他")
        with self.assertRaises(Region.DoesNotExist):
            Region.objects.get(name="其它")


class TestTeacherWeb(TestCase):
    def setUp(self):
        call_command("build_groups_and_permissions")

    def tearDown(self):
        pass

    def test_verify_sms_code(self):
        phone = "18922405996"
        sms_code = Sms().generateCheckcode(phone)
        client = Client()
        # 第一次
        response = client.post(reverse("teacher:verify-sms-code"),
                               {
                                   "phone": phone,
                                   "code": sms_code
                               })
        self.assertEqual(response.status_code, 200)
        self.assertEqual(json.loads(response.content.decode()), {
            "result": True, "url": "/teacher/information/complete/"})
        # 第二次
        second_client = Client()
        response = second_client.post(reverse("teacher:verify-sms-code"),
                                      {
                                          "phone": phone,
                                          "code": sms_code
                                      })
        self.assertEqual(json.loads(response.content.decode()), {
            "url": "/teacher/information/complete/", "result": True})

        # 测试information_compelte_percent
        profile = Profile.objects.get(phone=phone)
        percent = information_complete_percent(profile.user)


class TestAlgorithm(TestCase):
    def test_tree_insert(self):
        tree = Tree()
        tree.root = Node("a")
        tree.insert_val("a", "b", "c")
        tree.insert_val("b", "d", "e")
        self.assertEqual(tree.get_val("d").val, "d")
        self.assertEqual(tree.get_path("d"), ["a", "b", "d"])
        self.assertEqual(tree.get_path("e"), ["a", "b", "e"])
        self.assertEqual(tree.get_path("c"), ["a", "c"])
        self.assertEqual(tree.get_path("b"), ["a", "b"])

    def testParseInt(self):
        self.assertTrue(parseInt(None) == 'NaN')
        self.assertTrue(parseInt('') == 'NaN')
        self.assertTrue(parseInt(123) == 123)
        self.assertTrue(parseInt(-123) == -123)
        self.assertTrue(parseInt('123') == 123)
        self.assertTrue(parseInt('-123') == -123)
        self.assertTrue(parseInt('123asd') == 123)
        self.assertTrue(parseInt('-123asd') == -123)
        self.assertTrue(parseInt(234.234) == 234)
        self.assertTrue(parseInt(-234.234) == -234)
        self.assertTrue(parseInt('234.234') == 234)
        self.assertTrue(parseInt('-234.234') == -234)
        self.assertTrue(parseInt('asd') == 'NaN')
        self.assertTrue(parseInt('-asd') == 'NaN')
