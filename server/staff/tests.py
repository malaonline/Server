from django.test import Client, TestCase
from django.core.urlresolvers import reverse


# Create your tests here.
class TestStaffWeb(TestCase):
    def setUp(self):
        pass

    def tearDown(self):
        pass

    def test_analysis(self):
        client = Client()
        client.login(username='test', password='mala-test')
        url = "/staff/analysis"
        response = client.get(url)
        self.assertEqual(200, response.status_code)

    def test_students(self):
        client = Client()
        client.login(username='test', password='mala-test')
        response = client.get(reverse('staff:students'))
        self.assertEqual(200, response.status_code)

    def test_student_schedule_manage(self):
        client = Client()
        client.login(username='test', password='mala-test')
        response = client.get(reverse('staff:student_schedule_manage'))
        self.assertEqual(200, response.status_code)

    def test_student_schedule_changelog(self):
        client = Client()
        client.login(username='test', password='mala-test')
        response = client.get(reverse('staff:student_schedule_changelog'))
        self.assertEqual(200, response.status_code)


    def test_teachers(self):
        client = Client()
        client.login(username='test', password='mala-test')
        response = client.get(reverse('staff:teachers'))
        self.assertEqual(200, response.status_code)

    def test_coupons_list(self):
        # 奖学金领用列表
        client = Client()
        client.login(username='test', password='mala-test')
        url = "/staff/coupons/list/"
        response = client.get(url)
        self.assertEqual(200, response.status_code)

    def test_coupon_config(self):
        # 奖学金设置
        client = Client()
        client.login(username='test', password='mala-test')
        response = client.get(reverse("staff:coupon_config"))
        self.assertEqual(response.status_code, 200)

    def test_bankcard_list(self):
        # 老师银行卡查询
        client = Client()
        client.login(username='test', password='mala-test')
        url = '/staff/teachers/bankcard/list/'
        response = client.get(url)
        self.assertEqual(200, response.status_code)

    def test_school_timeslot(self):
        # 中心课程列表
        client = Client()
        client.login(username='test', password='mala-test')
        response = client.get(reverse("staff:school_timeslot"))
        self.assertEqual(response.status_code, 200)

    def test_schools(self):
        # 中心设置
        client = Client()
        client.login(username='test', password='mala-test')
        response = client.get(reverse("staff:schools"))
        self.assertEqual(response.status_code, 200)

    def test_staff_school(self):
        # 新增中心
        client = Client()
        client.login(username='test', password='mala-test')
        response = client.get(reverse("staff:staff_school"))
        self.assertEqual(response.status_code, 200)

    def test_school_income_audit(self):
        # 新增中心
        client = Client()
        client.login(username='test', password='mala-test')
        response = client.get(reverse("staff:school_income_audit"))
        self.assertEqual(response.status_code, 200)

    def test_staff_region(self):
        # 新增中心
        client = Client()
        client.login(username='test', password='mala-test')
        response = client.get(reverse("staff:region_config", kwargs={'rid': 1}))
        self.assertEqual(response.status_code, 200)

    def test_live_course_classroom(self):
        # 创建教室
        client = Client()
        client.login(username='test', password='mala-test')
        response = client.get(reverse("staff:create_room"))
        self.assertEqual(response.status_code, 200)
        data = {'school': 1, 'name': 'test_room', 'capacity': 20}
        response = client.post(reverse("staff:create_room"), data=data)
        self.assertEqual(response.status_code, 200)

    def test_live_course_create_course(self):
        # 创建课程
        client = Client()
        client.login(username='test', password='mala-test')
        response = client.get(reverse("staff:live_course"))
        self.assertEqual(response.status_code, 200)
