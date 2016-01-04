from django.test import TestCase
from rest_framework.authtoken.models import Token
from django.contrib.auth.models import User
from django.test import Client
from django.core.urlresolvers import reverse
from django.contrib.auth import authenticate
from django.contrib.auth.models import Group, Permission
from django.core.management import call_command
import json
from app.models import Parent


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
        # print(token.key)
        self.assertTrue(isinstance(token.key, str))

    def test_get_token_key(self):
        client = Client()
        request_url = "/api/v1/token-auth/"
        # request_url = "/api/v1/subjects/"
        username = "parent1"
        password = "123123"
        user = authenticate(username=username, password=password)
        self.assertNotEqual(user, None)
        parent_user = User.objects.get(username=username)
        # self.assertEqual(parent_user.password, password)
        self.assertEqual(parent_user.is_active, 1)
        response = client.post(request_url, {"username": username, "password": password})
        # print(response.status_code)
        response.render()
        print(response.content.decode())
        self.assertEqual(response.status_code, 200)

        client2 = Client()
        response2 = client2.post(request_url, {"username": username, "password": password})
        response2.render()
        # print(response2.content.decode())
        self.assertEqual(response.content, response2.content)

    def test_modify_student_name(self):
        token_client = Client()
        token_request_url = "/api/v1/token-auth/"
        username = "parent1"
        password = "123123"
        response = token_client.post(token_request_url, {"username": username, "password": password})
        response.render()
        token = json.loads(response.content.decode())["token"]
        print("get token:{token}".format(token=token))
        user = User.objects.get(username=username)
        parent = Parent.objects.get(user=user)
        user_token = Token.objects.get(user=user)
        self.assertEqual(user_token.key, token)

        call_command("add_groups_to_sample_users")

        # test 201
        client = Client()
        request_url = "/api/v1/parent/%d/" % (parent.pk, )
        print("the request_url is {request_url}".format(request_url=request_url))
        json_data = json.dumps({"student_name": "StudentNewName"})
        response = client.patch(request_url, content_type="application/json",
                                data=json_data,
                                **{"HTTP_AUTHORIZATION": " Token %s" % token})
        self.assertEqual(201, response.status_code)
        # print(response.status_code)
        response.render()
        # print(response.content)
        # print(response.content.decode())
        json_ret = json.loads(response.content.decode())
        # print(json_ret)
        # print(json_ret["done"])
        self.assertEqual(json_ret["done"], "false")
        # self.assertEqual(response.content.decode(), "{'done': 'false', 'reason': 'Student name already exits.'}")
        print(response._headers)
        parent_after = Parent.objects.get(user=user)
        self.assertEqual(parent_after.student_name, "student1")

        # test 200
        parent_after.student_name = ""
        parent_after.save()
        response = client.patch(request_url, content_type="application/json",
                                data=json_data,
                                **{"HTTP_AUTHORIZATION": " Token %s" % token})
        self.assertEqual(200, response.status_code)
        response.render()
        self.assertEqual(response.content.decode(), '{"done":"true"}')
        json_ret = json.loads(response.content.decode())
        self.assertEqual(json_ret["done"], "true")
        # print(response.status_code)
        # print(response.content)

