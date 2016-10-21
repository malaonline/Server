import os
from django.test import Client, TestCase
from django.core.urlresolvers import reverse


# Create your tests here.
class TestImpWeb(TestCase):
    def setUp(self):
        self.client = Client()
        self.client.login(username='test', password='mala-test')

    def tearDown(self):
        pass

    def test_login(self):
        response = self.client.get('/import_/login')
        self.assertEqual(302, response.status_code)

    def test_teachers(self):
        path = os.path.join(
                os.path.dirname(os.path.abspath(__file__)),
                '../static/import_/import_teachers_template.xlsx')

        with open(path, 'rb') as fp:
            response = self.client.post('/import_/teachers', {'excel_file': fp})
            self.assertEqual(302, response.status_code)

    def test_parents(self):
        path = os.path.join(
                os.path.dirname(os.path.abspath(__file__)),
                '../static/import_/import_students_template.xlsx')

        with open(path, 'rb') as fp:
            response = self.client.post('/import_/parents', {'excel_file': fp})
            self.assertEqual(302, response.status_code)
