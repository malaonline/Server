from django.test import Client, TestCase
from django.core.urlresolvers import reverse


# Create your tests here.
class TestImpWeb(TestCase):
    def setUp(self):
        pass

    def tearDown(self):
        pass

    def test_login(self):
        client = Client()
        client.login(username='test', password='mala-test')
        url = "/import_/login"
        response = client.get(url)
        self.assertEqual(200, response.status_code)
