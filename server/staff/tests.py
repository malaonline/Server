from app import models
from django.test import Client, TestCase
from django.core.urlresolvers import reverse


# Create your tests here.
class TestStaffWeb(TestCase):
    def setUp(self):
        self.client = Client()
        self.client.login(username='test', password='mala-test')

    def tearDown(self):
        pass

    def test_index(self):
        response = self.client.get(reverse('staff:index'))
        self.assertEqual(200, response.status_code)

    def test_login(self):
        client = Client()
        response = client.get(reverse('staff:login'))
        self.assertEqual(200, response.status_code)

    def test_login_auth(self):
        client = Client()
        data = {'username': 'test', 'password': 'mala-test'}
        response = client.post(reverse('staff:login_auth'), data=data)
        self.assertEqual(302, response.status_code)

    def test_logout(self):
        client = Client()
        client.login(username='test', password='mala-test')
        response = client.get(reverse('staff:logout'))
        self.assertEqual(302, response.status_code)

    def test_analysis(self):
        url = "/staff/analysis"
        response = self.client.get(url)
        self.assertEqual(200, response.status_code)

    def test_students(self):
        response = self.client.get(reverse('staff:students'))
        self.assertEqual(200, response.status_code)

    def test_student_schedule_manage(self):
        response = self.client.get(reverse('staff:student_schedule_manage'))
        self.assertEqual(200, response.status_code)

    def test_student_schedule_changelog(self):
        response = self.client.get(reverse('staff:student_schedule_changelog'))
        self.assertEqual(200, response.status_code)

    def test_student_schedule_action(self):
        pass  # TODO

    def test_teachers(self):
        response = self.client.get(reverse('staff:teachers'))
        self.assertEqual(200, response.status_code)

    def test_teachers_unpublished(self):
        response = self.client.get(reverse('staff:teachers_unpublished'))
        self.assertEqual(200, response.status_code)

    def test_teachers_published(self):
        response = self.client.get(reverse('staff:teachers_published'))
        self.assertEqual(200, response.status_code)

    def test_teacher_unpublished_edit(self):
        teacher = models.Teacher.objects.filter(published=False)[0]
        response = self.client.get(reverse('staff:teachers_unpublished_edit', kwargs={'tid': teacher.pk}))
        self.assertEqual(200, response.status_code)
        # TODO: update unpublished teacher

    def test_teacher_published_edit(self):
        teacher = models.Teacher.objects.filter(published=True)[0]
        response = self.client.get(reverse('staff:teachers_published_edit', kwargs={'tid': teacher.pk}))
        self.assertEqual(200, response.status_code)
        # TODO: update unpublished teacher

    def test_teachers_action(self):
        for action in ('list-highscore', 'list-achievement', 'get-weekly-schedule', 'get-course-schedule', 'get-subject-grades-range'):
            response = self.client.get(reverse('staff:teachers_action') + '?action=%s' % action)
            self.assertEqual(200, response.status_code)

    def test_bankcard_list(self):
        response = self.client.get(reverse('staff:teachers_bankcard_list'))
        self.assertEqual(200, response.status_code)

    def test_teachers_income_list(self):
        response = self.client.get(reverse('staff:teachers_income_list'))
        self.assertEqual(200, response.status_code)

    def test_teachers_income_detail(self):
        teacher = models.Teacher.objects.filter(published=True)[0]
        response = self.client.get(reverse('staff:teachers_income_detail', kwargs={'tid': teacher.pk}))
        self.assertEqual(200, response.status_code)

    def test_teachers_withdrawal_list(self):
        response = self.client.get(reverse('staff:teachers_withdrawal_list'))
        self.assertEqual(200, response.status_code)

    def test_schools(self):
        # 中心设置
        response = self.client.get(reverse("staff:schools"))
        self.assertEqual(response.status_code, 200)

    def test_staff_school(self):
        # 新增中心
        response = self.client.get(reverse("staff:staff_school"))
        self.assertEqual(response.status_code, 200)

    def test_school_timeslot(self):
        # 中心课程列表
        response = self.client.get(reverse("staff:school_timeslot"))
        self.assertEqual(response.status_code, 200)

    def test_orders_review(self):
        response = self.client.get(reverse("staff:orders_review"))
        self.assertEqual(response.status_code, 200)

    def test_orders_refund(self):
        response = self.client.get(reverse("staff:orders_refund"))
        self.assertEqual(response.status_code, 200)

    def test_orders_action(self):
        for action in ('preview-refund-info', 'get-refund-record'):
            response = self.client.get(reverse(
                "staff:orders_action") + '?action=%s&order_id=%s' % (
                    action, 1))
            self.assertEqual(response.status_code, 200)
        # TODO: update action

    def test_coupons_list(self):
        response = self.client.get(reverse('staff:coupons_list'))
        self.assertEqual(200, response.status_code)

    def test_coupon_config(self):
        # 奖学金设置
        response = self.client.get(reverse("staff:coupon_config"))
        self.assertEqual(response.status_code, 200)
        # TODO: update config

    def test_evaluations(self):
        response = self.client.get(reverse('staff:evaluations'))
        self.assertEqual(200, response.status_code)

    def test_evaluations_action(self):
        pass  # TODO:

    def test_level_price_cfg(self):
        response = self.client.get(reverse('staff:level_price_cfg'))
        self.assertEqual(200, response.status_code)
        # TODO: update

    def test_level_salary_cfg(self):
        response = self.client.get(reverse('staff:level_salary_cfg'))
        self.assertEqual(200, response.status_code)
        # TODO: update

    def test_school_account_info(self):
        response = self.client.get(reverse('staff:school_account_info'))
        self.assertEqual(200, response.status_code)
        # TODO: update

    def test_school_price_cfg(self):
        response = self.client.get(reverse('staff:school_price_cfg'))
        self.assertEqual(200, response.status_code)
        # TODO: update

    def test_school_income_records(self):
        response = self.client.get(reverse("staff:school_income_records"))
        self.assertEqual(response.status_code, 200)

    def test_school_income_audit(self):
        response = self.client.get(reverse("staff:school_income_audit"))
        self.assertEqual(response.status_code, 200)
        # TODO: update

    def test_region_config(self):
        response = self.client.get(reverse("staff:region_config", kwargs={'rid': 1}))
        self.assertEqual(response.status_code, 200)
        response = self.client.get(
                reverse("staff:region_config", kwargs={'rid': 1}) + '?action=open')
        self.assertEqual(response.status_code, 200)

    def test_live_course_create_course(self):
        # 创建课程
        response = self.client.get(reverse("staff:live_course"))
        self.assertEqual(response.status_code, 200)

    def test_live_course_classroom(self):
        # 创建教室
        response = self.client.get(reverse("staff:create_room"))
        self.assertEqual(response.status_code, 200)
        data = {'school': 1, 'name': 'test_room', 'capacity': 20}
        response = self.client.post(reverse("staff:create_room"), data=data)
        self.assertEqual(response.status_code, 200)
