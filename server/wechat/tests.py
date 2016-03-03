from django.test import TestCase, Client
from django.core.urlresolvers import reverse
from django.conf import settings

class TestWechatPage(TestCase):
    def test_teachers(self):
        client = Client()
        response = client.get(reverse('wechat:teachers'))
        self.assertEqual(response.status_code, 200)
    def TestWechatSchools(self):
        client = Client()
        response = client.get(reverse('wechat:schools'))
        self.assertEqual(response.status_code, 200)
