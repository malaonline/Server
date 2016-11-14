from django.test import TestCase, Client
from django.core.urlresolvers import reverse


# Create your tests here.
class TestIndex(TestCase):
    def test_index(self):
        client = Client()
        response = client.get(reverse('web:patriarch-index'))
        self.assertEqual(response.status_code, 200)

    def test_index_teacher(self):
        client = Client()
        response = client.get(reverse('web:teacher-index'))
        self.assertEqual(response.status_code, 200)

    def test_app_ad(self):
        client = Client()
        response = client.get(reverse('web:app-ad'))
        self.assertEqual(response.status_code, 200)
