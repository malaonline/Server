import os
import json
import itertools
import datetime

from django.conf import settings
from django.contrib.auth.models import User, Group, Permission
from django.contrib.auth import authenticate
from django.core.urlresolvers import reverse
from django.test import Client, TestCase, SimpleTestCase
from django.test.client import BOUNDARY, MULTIPART_CONTENT, encode_multipart
from django.utils import timezone

from rest_framework.authtoken.models import Token

from app.models import Parent, Teacher, Checkcode, Profile, TimeSlot, Order, \
        WeeklyTimeSlot, AuditRecord, Coupon, School, Region, Subject, Grade, \
        Ability
from app.utils.algorithm import Tree, Node, verify_sig
from app.utils.types import parseInt, parse_date, parse_date_next
import app.utils.klx_api as klx
from app.tasks import send_push

app_path = os.path.abspath(os.path.dirname(__file__))


# Create your tests here.
class TestApi(TestCase):
    def setUp(self):
        # 确保单元测试不会发送短信
        self.assertTrue(settings.FAKE_SMS_SERVER)

    def tearDown(self):
        pass

    def test_token_key(self):
        # 测试token是否能正常创建
        user = User.objects.get(username="parent0")
        token = Token.objects.create(user=user)
        self.assertTrue(isinstance(token.key, str))

    def test_concrete_timeslots(self):
        hours = 2
        weekly_time_slots = list(WeeklyTimeSlot.objects.filter(
                weekday=1, start=datetime.time(8, 0)))
        teacher = Teacher.objects.all()[0]
        timeslots = Order.objects.concrete_timeslots(
                hours, weekly_time_slots, teacher)
        self.assertEqual(len(timeslots), 1)
        ts = timeslots[0]
        self.assertEqual(timezone.localtime(ts['start']).hour, 8)

    def test_sms_login(self):
        phone = '0001'
        code = '1111'
        client = Client()
        sms_url = reverse('sms')
        # parent login or register via sms
        parent_group, _new = Group.objects.get_or_create(name="家长")
        self.assertIsNotNone(parent_group)
        # (1) default content_type
        # send
        data = {'action': "send", 'phone': phone}
        response = client.post(sms_url, data=data)
        self.assertEqual(response.status_code, 200)
        json_ret = json.loads(response.content.decode())
        self.assertTrue(json_ret["sent"])
        # verify
        data = {'action': "verify", 'phone': phone, 'code': code}
        response = client.post(sms_url, data=data)
        self.assertEqual(response.status_code, 200)
        json_ret = json.loads(response.content.decode())
        self.assertTrue(json_ret["verified"])
        token = json_ret.get("token")
        self.assertTrue(isinstance(token, str) and token != '')
        # (2) json content_typ
        # send
        content_type = "application/json"
        data = {'action': "send", 'phone': phone}
        data = json.dumps(data)
        response = client.post(sms_url, data=data, content_type=content_type)
        self.assertEqual(response.status_code, 200)
        json_ret = json.loads(response.content.decode())
        self.assertTrue(json_ret["sent"])
        # verify
        data = {'action': "verify", 'phone': phone, 'code': code}
        data = json.dumps(data)
        response = client.post(sms_url, data=data, content_type=content_type)
        self.assertEqual(response.status_code, 200)
        json_ret = json.loads(response.content.decode())
        self.assertTrue(json_ret["verified"])
        token = json_ret.get("token")
        self.assertTrue(isinstance(token, str) and token != '')

    def test_teacher_list(self):
        client = Client()
        url = "/api/v1/teachers"
        response = client.get(url)
        self.assertEqual(response.status_code, 200)
        url = "/api/v1/teachers?grade=4&subject=3&tags=2+6"
        response = client.get(url)
        self.assertEqual(response.status_code, 200)
        url = "/api/v1/teachers?grade=4&subject=3&tags=2+6&school=1"
        response = client.get(url)
        self.assertEqual(response.status_code, 200)

    def test_teacher_detail(self):
        client = Client()
        url = "/api/v1/teachers"
        response = client.get(url)
        response_content = json.loads(response.content.decode())
        if response_content['results'] is []:
            logger.debug(response_content)
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
        username = "parent0"
        password = "123123"
        client.login(username=username, password=password)

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
        username = "parent1"
        password = "123123"
        user = User.objects.get(username=username)
        parent = user.parent

        client = Client()
        client.login(username=username, password=password)
        request_url = "/api/v1/parents/%d" % (parent.pk,)
        json_data = json.dumps({"student_name": "StudentNewName"})
        response = client.patch(request_url, content_type="application/json",
                                data=json_data)
        self.assertEqual(200, response.status_code)
        json_ret = json.loads(response.content.decode())
        self.assertEqual(json_ret["done"], "true")
        parent_after = Parent.objects.get(user=user)
        self.assertEqual(parent_after.student_name, "StudentNewName")

        parent_after.student_name = ""
        parent_after.save()
        response = client.patch(request_url, content_type="application/json",
                                data=json_data)
        self.assertEqual(200, response.status_code)
        self.assertEqual(response.content.decode(), '{"done":"true"}')
        json_ret = json.loads(response.content.decode())
        self.assertEqual(json_ret["done"], "true")

        request_url = "/api/v1/parents/%d" % (parent.pk,)
        school_name = '洛阳一中'
        json_data = json.dumps({"student_school_name": school_name})
        response = client.patch(request_url, content_type="application/json",
                                data=json_data)
        self.assertEqual(200, response.status_code)
        json_ret = json.loads(response.content.decode())
        self.assertEqual(json_ret["done"], "true")
        parent_after = Parent.objects.get(user=user)
        self.assertEqual(parent_after.student_school_name, school_name)

    def test_modify_user_avatar(self):
        username = "parent1"
        password = "123123"
        user = User.objects.get(username=username)

        change_profile_perm = Permission.objects.get(name='Can change profile')
        user.user_permissions.add(change_profile_perm)
        user.save()

        client = Client()
        client.login(username=username, password=password)
        request_url = "/api/v1/profiles/%d" % (user.profile.pk,)
        img_name = 'img0'  # NOTE: seq is 0 not 1, seq of the user 'parent1'
        img_path = os.path.join(
                app_path, 'migrations', 'avatars', img_name + '.jpg')
        # print(img_path)
        img_fd = open(img_path, 'rb')
        data = {'avatar': img_fd}
        encoded_data = encode_multipart(BOUNDARY, data)
        response = client.patch(request_url, content_type=MULTIPART_CONTENT,
                                data=encoded_data)
        self.assertEqual(200, response.status_code)
        json_ret = json.loads(response.content.decode())
        self.assertEqual(json_ret["done"], "true")
        profile_after = Profile.objects.get(user=user)
        # print(profile_after.avatar_url())
        self.assertTrue(profile_after.avatar.url.find(img_name) >= 0)


    def test_modify_avatar_by_teacher(self):
        # Teacher role not allowed to modify avatar.
        username = "test1"
        password = "123123"
        user = User.objects.get(username=username)

        change_profile_perm = Permission.objects.get(name='Can change profile')
        user.user_permissions.add(change_profile_perm)
        user.save()

        client = Client()
        client.login(username=username, password=password)
        request_url = "/api/v1/profiles/%d" % (user.profile.pk,)
        img_name = 'img0'  # NOTE: seq is 0 not 1, seq of the user 'parent1'
        img_path = os.path.join(
                app_path, 'migrations', 'avatars', img_name + '.jpg')
        # print(img_path)
        img_fd = open(img_path, 'rb')
        data = {'avatar': img_fd}
        encoded_data = encode_multipart(BOUNDARY, data)
        response = client.patch(request_url, content_type=MULTIPART_CONTENT,
                                data=encoded_data)
        self.assertEqual(409, response.status_code)

    def test_concrete_time_slots(self):
        client = Client()
        url = ("/api/v1/concrete/timeslots" +
                "?hours=100&weekly_time_slots=3+8+18&teacher=1")
        response = client.get(url)
        self.assertEqual(response.status_code, 200)

    def test_send_push(self):
        send_push('Hello')

    def test_coupons(self):
        done = False
        for i in range(5):
            client = Client()
            username = "parent%d" % i
            password = "123123"
            client.login(username=username, password=password)

            request_url = "/api/v1/coupons"
            response = client.get(request_url, content_type='application/json')
            self.assertEqual(200, response.status_code)

            json_ret = json.loads(response.content.decode())
            for coupon in json_ret['results']:
                if coupon['used']:
                    done = True
                if coupon['expired_at'] <= timezone.now().timestamp():
                    done = True

            request_url = "/api/v1/coupons?only_valid=true"
            response = client.get(request_url, content_type='application/json')
            self.assertEqual(200, response.status_code)

            json_ret = json.loads(response.content.decode())
            for coupon in json_ret['results']:
                self.assertFalse(coupon['used'])
                self.assertTrue(
                        coupon['expired_at'] > timezone.now().timestamp())
            if done:
                break
        self.assertTrue(done)

    def test_create_order(self):
        client = Client()
        username = "parent2"
        password = "123123"
        client.login(username=username, password=password)

        subject = Subject.objects.get(pk=1)
        grade = Grade.objects.get(pk=2)
        ability = Ability.objects.get(subject=subject, grade=grade)
        teacher_id = 2
        teacher = Teacher.objects.get(pk=teacher_id)
        # make sure the sample teacher is published
        teacher.published = True
        teacher.save()
        school = School.objects.get(pk=1)
        hours = 14
        prices_set = teacher.level.priceconfig_set.filter(
            deleted=False,
            school=school,
            grade=grade,
            min_hours__lte=hours,
        ).order_by('-min_hours')
        price_obj = prices_set.first()
        price = price_obj.price

        coupon = Coupon.objects.get(pk=2)
        # 保留原始过期时间
        org_expired_at = coupon.expired_at
        coupon.used = False
        coupon.save()

        request_url = "/api/v1/orders"
        json_data = json.dumps({
            'teacher': teacher.id,
            'school': 1,
            'grade': grade.id,
            'subject': subject.id,
            'coupon': coupon.id,
            'hours': hours,
            'weekly_time_slots': [3, 8],
        })
        # 设置奖学金课时条件不满足
        coupon.mini_course_count = hours + 5
        coupon.mini_total_price = 0
        coupon.save()
        response = client.post(request_url, content_type="application/json",
                               data=json_data,)
        # 奖学金校验失败
        self.assertEqual(200, response.status_code)
        json_ret = json.loads(response.content.decode())
        self.assertFalse(json_ret['ok'])
        self.assertEqual(-2, json_ret['code'])
        self.assertFalse(coupon.used)

        # 设置奖学金最低订单价格不满足
        coupon.mini_course_count = 0
        coupon.mini_total_price = price * hours + 500
        coupon.save()
        response = client.post(request_url, content_type="application/json",
                               data=json_data, )
        # 奖学金校验失败
        self.assertEqual(200, response.status_code)
        json_ret = json.loads(response.content.decode())
        self.assertFalse(json_ret['ok'])
        self.assertEqual(-2, json_ret['code'])
        self.assertFalse(coupon.used)

        # 设置奖学金可使用时间不满足
        coupon.mini_course_count = 0
        coupon.mini_total_price = 0
        coupon.expired_at = timezone.now() - datetime.timedelta(minutes=1)
        coupon.save()
        response = client.post(request_url, content_type="application/json",
                               data=json_data, )
        # 奖学金校验失败
        self.assertEqual(200, response.status_code)
        json_ret = json.loads(response.content.decode())
        self.assertFalse(json_ret['ok'])
        self.assertEqual(-2, json_ret['code'])
        self.assertFalse(coupon.used)

        # 设置奖学金限制条件为满足
        coupon.mini_total_price = 0
        coupon.mini_course_count = 0
        coupon.expired_at = org_expired_at
        coupon.save()

        response = client.post(request_url, content_type="application/json",
                               data=json_data, )
        # 奖学金校验成功
        self.assertEqual(201, response.status_code)
        coupon = Coupon.objects.get(pk=2)
        self.assertTrue(coupon.used)
        pk = json.loads(response.content.decode())['id']

        request_url = "/api/v1/orders/%d" % pk
        response = client.get(request_url, content_type='application/json')
        self.assertEqual(200, response.status_code)

        json_ret = json.loads(response.content.decode())
        self.assertEqual(json_ret['status'], 'u')

        # Test create charge object
        json_data = json.dumps({
            'action': 'pay', 'channel': 'alipay',
            })
        response = client.patch(request_url, content_type="application/json",
                                data=json_data)
        self.assertEqual(200, response.status_code)

        data = json.loads(response.content.decode())

        charge_id = data['id']

        json_data = json.dumps({
            "id": "evt_ugB6x3K43D16wXCcqbplWAJo",
            "created": 1440407501,
            "livemode": False,
            "type": "charge.succeeded",
            "data": {
                "object": {
                    "id":  charge_id,
                    "object": "charge",
                    "created": 1440407501,
                    "livemode": True,
                    "paid": True,
                    "refunded": False,
                    "app": "app_urj1WLzvzfTK0OuL",
                    "channel": "upacp",
                    "order_no": "123456789",
                    "client_ip": "127.0.0.1",
                    "amount": 100,
                    "amount_settle": 0,
                    "currency": "cny",
                    "subject": "Your Subject",
                    "body": "Your Body",
                    "extra": {
                        },
                    "time_paid": 1440407501,
                    "time_expire": 1440407501,
                    "time_settle": None,
                    "transaction_no": "1224524301201505066067849274",
                    "refunds": {
                        "object": "list",
                        "url": "/v1/charges/ch_Xsr7u35O3m1Ged2ODmi4Lw/refunds",
                        "has_more": False,
                        "data": [
                            ]
                        },
                    "amount_refunded": 0,
                    "failure_code": None,
                    "failure_msg": None,
                    "metadata": {
                        },
                    "credential": {
                        },
                    "description": None
                    }
                },
            "object": "event",
            "pending_webhooks": 0,
            "request": "iar_qH4y1KbTy5eLGm1uHSTS00s"
            })

        request_url = '/api/v1/charge_succeeded'

        response = client.post(
                request_url, content_type="application/json", data=json_data)
        self.assertEqual(200, response.status_code)

        order = Order.objects.get(pk=pk)
        self.assertEqual(order.status, 'p')

        request_url = "/api/v1/subject/1/record"
        response = client.get(request_url)
        self.assertEqual(200, response.status_code)
        json_ret = json.loads(response.content.decode())
        self.assertTrue(json_ret['evaluated'])

        # Available to oneself
        request_url = "/api/v1/teachers/" + str(teacher_id) + "/weeklytimeslots?school_id=1"
        response = client.get(request_url)
        self.assertEqual(response.status_code, 200)
        data = json.loads(response.content.decode())

        for value in data.values():
            for d in value:
                self.assertTrue(d['available'])

        # Concrete time slot
        hours = 6
        weekly_time_slots = list(
                WeeklyTimeSlot.objects.filter(pk__in=[3, 8, 20]))
        teacher = Teacher.objects.get(pk=teacher_id)
        timeslots = Order.objects.concrete_timeslots(
                hours, weekly_time_slots, teacher)
        self.assertEqual(len(timeslots), 3)
        wts = weekly_time_slots[2]
        for ts in timeslots:
            self.assertEqual(
                    timezone.localtime(ts['start']).isoweekday(), wts.weekday)
            self.assertEqual(
                    timezone.localtime(ts['start']).time(), wts.start)

        # Available time for other parent
        client = Client()
        username = "parent1"
        password = "123123"
        client.login(username=username, password=password)
        request_url = "/api/v1/teachers/" + str(teacher_id) + "/weeklytimeslots?school_id=1"
        response = client.get(request_url)
        self.assertEqual(response.status_code, 200)
        data = json.loads(response.content.decode())

        for value in data.values():
            for d in value:
                if d['id'] in [3, 8]:
                    self.assertFalse(d['available'])
                else:
                    self.assertTrue(d['available'])

        # Available time for other parent for different school
        request_url = "/api/v1/teachers/" + str(teacher_id) + "/weeklytimeslots?school_id=2"
        response = client.get(request_url)
        self.assertEqual(response.status_code, 200)
        data = json.loads(response.content.decode())

        for value in data.values():
            for d in value:
                if d['id'] in [3, 4, 8, 9]:
                    self.assertFalse(d['available'])
                else:
                    self.assertTrue(d['available'])

        # Timeslot in order creation has been occupied
        client = Client()
        username = "parent0"
        password = "123123"
        client.login(username=username, password=password)

        request_url = "/api/v1/orders"
        json_data = json.dumps({
            'teacher': teacher_id, 'school': 1, 'grade': 2, 'subject': 1,
            'coupon': None, 'hours': 14, 'weekly_time_slots': [3, 6],
        })
        response = client.post(request_url, content_type="application/json",
                               data=json_data, )
        self.assertEqual(200, response.status_code)
        json_ret = json.loads(response.content.decode())
        self.assertFalse(json_ret['ok'])
        self.assertEqual(-1, json_ret['code'])

    def test_cancel_order(self):
        client = Client()
        username = "parent2"
        password = "123123"
        client.login(username=username, password=password)

        parent = Parent.objects.get(user__username=username)
        teacher = Teacher.objects.order_by('?').first()
        school = School.objects.order_by('?').first()
        ability = Ability.objects.filter(grade_id=2).first()
        teacher.abilities.add(ability)
        grade = ability.grade
        subject = teacher.subject()
        hours = 2
        order = Order.objects.create(
                parent=parent, teacher=teacher, school=school,
                grade=grade, subject=subject, hours=hours, coupon=None)
        order.save()

        request_url = "/api/v1/orders/%s" % order.id
        response = client.delete(request_url)
        self.assertEqual(200, response.status_code)
        json_ret = json.loads(response.content.decode())
        self.assertTrue(json_ret['ok'])
        canceled_order = Order.objects.get(id=order.id)
        self.assertEqual(canceled_order.status, Order.CANCELED)

        # test with coupon
        coupon = Coupon.objects.filter(parent=parent, used=False).order_by('?').first()
        if coupon is None:
            return
        order = Order.objects.create(
                parent=parent, teacher=teacher, school=school,
                grade=grade, subject=subject, hours=hours, coupon=coupon)
        order.save()
        used_coupon = Coupon.objects.get(id=coupon.id)
        self.assertTrue(used_coupon.used)

        request_url = "/api/v1/orders/%s" % order.id
        response = client.delete(request_url)
        self.assertEqual(200, response.status_code)
        json_ret = json.loads(response.content.decode())
        self.assertTrue(json_ret['ok'])
        canceled_order = Order.objects.get(id=order.id)
        self.assertEqual(canceled_order.status, Order.CANCELED)
        canceled_coupon = Coupon.objects.get(id=coupon.id)
        self.assertFalse(canceled_coupon.used)

    def test_subject_record(self):
        client = Client()
        username = "parent1"
        password = "123123"
        client.login(username=username, password=password)
        request_url = "/api/v1/subject/2/record"
        response = client.get(request_url)
        self.assertEqual(200, response.status_code)
        json_ret = json.loads(response.content.decode())
        self.assertFalse(json_ret['evaluated'])

    def test_create_comment(self):
        username = "parent0"
        password = "123123"
        user = User.objects.get(username=username)

        parent = user.parent
        order = parent.order_set.all()[0]
        timeslot = order.timeslot_set.filter(deleted=False)[0]

        client = Client()
        client.login(username=username, password=password)
        request_url = "/api/v1/comments"
        json_data = json.dumps({
            'timeslot': timeslot.pk, 'score': 5, 'content': 'Good.'})
        response = client.post(request_url, content_type="application/json",
                               data=json_data)
        self.assertEqual(201, response.status_code)
        pk = json.loads(response.content.decode())['id']

        request_url = "/api/v1/comments/%d" % pk
        response = client.get(request_url, content_type='application/json')
        self.assertEqual(200, response.status_code)

        json_ret = json.loads(response.content.decode())
        self.assertEqual(json_ret['score'], 5)
        self.assertEqual(json_ret['content'], 'Good.')

        # Create a comment for a timeslot for a order not belongs to cur user
        user2 = User.objects.get(username='parent4')
        parent2 = user2.parent
        order2 = parent2.order_set.all()[0]
        timeslot2 = order2.timeslot_set.filter(deleted=False)[0]

        request_url = "/api/v1/comments"
        json_data = json.dumps({
            'timeslot': timeslot2.pk, 'score': 5, 'content': 'Good.'})
        response = client.post(request_url, content_type="application/json",
                               data=json_data)

        self.assertEqual(400, response.status_code)

    def test_timeslots_second(self):
        timeslots = TimeSlot.objects.filter(start__second__gt=0)
        self.assertEqual(len(timeslots), 0)
        timeslots = TimeSlot.objects.filter(end__second__gt=0)
        self.assertEqual(len(timeslots), 0)

    def test_orders_timeslots(self):
        def weekly_2_mins(weekly):
            return ((weekly.weekday - 1) * 24 * 60 + weekly.start.hour * 60 +
                    weekly.start.minute, (weekly.weekday - 1) * 24 * 60 +
                    weekly.end.hour * 60 + weekly.end.minute)

        orders = Order.objects.filter(status='p')
        for order in orders:
            timeslots = order.timeslot_set.filter(deleted=False)
            weekly_time_slots = order.weekly_time_slots.all()
            mins = [weekly_2_mins(x) for x in weekly_time_slots]
            for timeslot in timeslots:
                timeslot.start = timezone.localtime(timeslot.start)
                timeslot.end = timezone.localtime(timeslot.end)
                cur_min = (
                        timeslot.start.weekday() * 24 * 60 +
                        timeslot.start.hour * 60 + timeslot.start.minute,
                        timeslot.end.weekday() * 24 * 60 +
                        timeslot.end.hour * 60 + timeslot.end.minute)
                self.assertIn(cur_min, mins)

    def test_teacher_timeslot(self):
        teachers = Teacher.objects.all()

        for teacher in teachers:
            orders = teacher.order_set.filter(status='p')
            timeslots = list(
                    itertools.chain(
                        *(order.timeslot_set.filter(deleted=False)
                            for order in orders)))
            timeslots.sort(key=lambda x: (x.start, x.end))
            for i, ts in enumerate(timeslots):
                if i == 0:
                    continue
                pre_ts = timeslots[i - 1]
                self.assertLessEqual(pre_ts.end, ts.start)
                if pre_ts.order.school != ts.order.school:
                    self.assertLessEqual(
                            pre_ts.end + ts.TRAFFIC_TIME, ts.start)

    def test_get_timeslots(self):
        username = "parent0"
        password = "123123"
        client = Client()
        client.login(username=username, password=password)
        request_url = "/api/v1/timeslots"
        response = client.get(request_url, content_type='application/json')
        self.assertEqual(200, response.status_code)

        json_ret = json.loads(response.content.decode())
        self.assertIn('is_expired', json_ret['results'][0])

    def test_get_passed_timeslots(self):
        username = "parent0"
        password = "123123"
        client = Client()
        client.login(username=username, password=password)
        request_url = "/api/v1/timeslots?for_review=true"
        response = client.get(request_url, content_type='application/json')
        self.assertEqual(200, response.status_code)

    def test_audit_record(self):
        teacher = Teacher.objects.all()[0]
        teacher.status = Teacher.TO_CHOOSE
        teacher.set_status(teacher.user, teacher.TO_INTERVIEW)
        #print(AuditRecord.objects.all())

    def test_teacher_profile(self):
        teachers = Teacher.objects.all()
        for teacher in teachers:
            user = teacher.user
            profile = user.profile
            self.assertIsNotNone(profile)

    def test_kuailexue_study_report(self):
        username = "parent1"
        password = "123123"
        client = Client()
        client.login(username=username, password=password)

        params = klx.klx_build_params({'uid': '12345678'}, False)
        klx.klx_sign_params(params)
        self.assertTrue(klx.klx_verify_sign(params))

        request_url = "/api/v1/study_report"
        response = client.get(request_url, content_type='application/json')
        self.assertEqual(200, response.status_code)
        math_id = Subject.objects.get(name='数学').id
        request_url = "/api/v1/study_report/%s"%(math_id)
        response = client.get(request_url, content_type='application/json')
        self.assertEqual(200, response.status_code)

    def test_schools(self):
        client = Client()
        request_url = "/api/v1/schools"
        region = Region.objects.filter(opened=True).first()
        response = client.get(request_url + '?region='+str(region.id), content_type='application/json')
        self.assertEqual(200, response.status_code)
        teacher = Teacher.objects.first()
        response = client.get(request_url + '?teacher='+str(teacher.id), content_type='application/json')
        self.assertEqual(200, response.status_code)
        pass


class TestModels(TestCase):
    def setUp(self):
        self.assertTrue(settings.FAKE_SMS_SERVER)

    def tearDown(self):
        pass

    def test_get_save_account(self):
        new_teacher = Teacher.new_teacher("12345")
        new_teacher.teacher.safe_get_account()

    def test_new_teacher(self):
        new_teacher = Teacher.new_teacher("12345")
        self.assertTrue(isinstance(new_teacher, User))

    def test_sms_verify(self):
        phone = "18922405996"
        send_result, sms_code = Checkcode.generate(phone)
        self.assertTrue(Checkcode.verify(phone, sms_code)[0])
        self.assertFalse(Checkcode.verify(phone, "error_code")[0])

    def test_other_region(self):
        """
        检查其它是否已经从数据库中移除
        """
        Region.objects.get(name="其他")
        with self.assertRaises(Region.DoesNotExist):
            Region.objects.get(name="其它")

    def test_school_price(self):
        school = School.objects.all().first()
        school.priceconfig_set.clear()
        school.init_prices()
        self.assertTrue(school.priceconfig_set.count() > 0)


class TestAlgorithm(SimpleTestCase):
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

    def test_parse_date(self):
        self.assertEqual(parse_date('2016-06-1', False), datetime.datetime(2016,6,1))
        self.assertEqual(parse_date('2016-06-1'), timezone.make_aware(datetime.datetime(2016,6,1)))
        self.assertEqual(parse_date('2016-06-18', False), datetime.datetime(2016,6,18))
        self.assertEqual(parse_date('2016-06-18', True), timezone.make_aware(datetime.datetime(2016,6,18)))
        self.assertEqual(parse_date('2016-12-08', False), datetime.datetime(2016,12,8))
        self.assertEqual(parse_date('2016-12-08'), timezone.make_aware(datetime.datetime(2016,12,8)))
        self.assertEqual(parse_date('2016-12-08 4', False), datetime.datetime(2016,12,8,4))
        self.assertEqual(parse_date('2016-12-08 4', True), timezone.make_aware(datetime.datetime(2016,12,8,4)))
        self.assertEqual(parse_date('2016-12-08 23', False), datetime.datetime(2016,12,8,23))
        self.assertEqual(parse_date('2016-12-08 23'), timezone.make_aware(datetime.datetime(2016,12,8,23)))
        self.assertEqual(parse_date('2016-12-08 05:24', False), datetime.datetime(2016,12,8,5,24))
        self.assertEqual(parse_date('2016-12-08 05:24', True), timezone.make_aware(datetime.datetime(2016,12,8,5,24)))
        self.assertEqual(parse_date('2016-12-08 23:09:25', False), datetime.datetime(2016,12,8,23,9,25))
        self.assertEqual(parse_date('2016-12-08 23:09:25'), timezone.make_aware(datetime.datetime(2016,12,8,23,9,25)))
        self.assertEqual(parse_date_next('2016-06-1', False), datetime.datetime(2016,6,2))
        self.assertEqual(parse_date_next('2016-06-18', False), datetime.datetime(2016,6,19))
        self.assertEqual(parse_date_next('2016-12-31', False), datetime.datetime(2017,1,1))
        self.assertEqual(parse_date_next('2016-12-08 4', False), datetime.datetime(2016,12,8,5))
        self.assertEqual(parse_date_next('2016-12-08 23', False), datetime.datetime(2016,12,9,0))
        self.assertEqual(parse_date_next('2016-12-08 05:24', False), datetime.datetime(2016,12,8,5,25))
        self.assertEqual(parse_date_next('2016-12-08 05:59', False), datetime.datetime(2016,12,8,6,0))
        self.assertEqual(parse_date_next('2016-12-08 23:09:25', False), datetime.datetime(2016,12,8,23,9,26))
        self.assertEqual(parse_date_next('2016-12-08 23:09:59', False), datetime.datetime(2016,12,8,23,10,0))

    def test_verify_sig(self):
        sig = (
                b'PcU0SMJhbPObiIVinNnalZOjI02koWozxLrxa3WQW3rK/n7I+EuVGuXvh' +
                b'sq2MIfUaNiHZDgRFYybGtKr1uuFzEXjA4PwmnDHfWgwRPdjgseoU0eke6' +
                b'ZqGpklBRVTbF6PUy6/vAqur4xb7h1wpdrteUpCPafzDmVPsQLicdojJ/T' +
                b'F9ACjQW8gTNiS6tE9gL5hxy0RJ3/okRJo6dz2pvJBWkjCrgp/r98z/LQi' +
                b'jA1o//atZrH63+DcL/GwEOgaymqbodzusXF+g6WMJ/GTJgjdPRHvpO9UA' +
                b'AUKkOQqvwthJvsXIH/L1xqvy+tFpo2J0Ptwg85bowKoyy1qC5ak3sqWqw' +
                b'==')
        data = ('{"id":"evt_04qN8cXQvIhssduhS4hpqd9p","created":1427555016,' +
                '"livemode":false,"type":"account.summary.available","data"' +
                ':{"object":{"acct_id":"acct_0eHSiDyzv9G09ejT","object":"ac' +
                'count_daily_summary","acct_display_name":"xx公司","created' +
                '":1425139260,"summary_from":1425052800,"summary_to":142513' +
                '9199,"charges_amount":1000,"charges_count":100}},"object":' +
                '"event","pending_webhooks":2,"request":null,"scope":"acct_' +
                '1234567890123456","acct_id":"acct_1234567890123456"}')
        data = data.encode('utf-8')
        pubkey = b'''-----BEGIN PUBLIC KEY-----
        MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzs8SiPoFQT9K0lWa6WSx
        0d6UnA391KM2aFwijY0AK7r+MiAe07ivenopzFL3dqIRhQjuP7d30V85kWydN5UZ
        cm/tZgm4K+8wttb988hOrzSjtPOMghHK+bnDwE8FIB+ZbHAZCEVhNfE6i9kLGbHH
        Q617+mxUTJ3yEZG9CIgke475o2Blxy4UMsRYjo2gl5aanzmOmoZcbiC/R5hXSQUH
        XV9/VzA7U//DIm8Xn7rerd1n8+KWCg4hrIIu/A0FKm8zyS4QwAwQO2wdzGB0h15t
        uFLhjVz1W5ZPXjmCRLzTUoAvH12C6YFStvS5kjPcA66P1nSKk5o3koSxOumOs0iC
        EQIDAQAB
        -----END PUBLIC KEY-----'''
        self.assertTrue(verify_sig(data, sig, pubkey))
