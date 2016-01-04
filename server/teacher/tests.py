from django.test import TestCase
from django.test import Client
from django.core.urlresolvers import reverse


# Create your tests here.
class TestWebPage(TestCase):
    def test_register_show(self):
        client = Client()
        register_url = reverse("teacher:register")
        response = client.get(register_url)
        # response.render()
        self.assertEqual(response.status_code, 200)
