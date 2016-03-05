from django.test import TestCase

# Create your tests here.

from django.test import TestCase, Client
from django.core.urlresolvers import reverse
from django.conf import settings

class TestIndex(TestCase):
    def test_index(self):
        client = Client()
        response = client.get(reverse('web:index'))
        self.assertEqual(response.status_code, 200)
    def test_index_teacher(self):
        client = Client()
        response = client.get(reverse('web:teacher-index'))
        self.assertEqual(response.status_code, 200)