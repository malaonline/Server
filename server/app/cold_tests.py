import datetime
import unittest
import requests
import json

from django.conf import settings
from django.utils import timezone

from app.utils.types import parseInt, parse_date, parse_date_next
from app.utils.klx_api import *


class SimpleTest(unittest.TestCase):

    def test_parse_int(self):
        self.assertTrue(parseInt('123') == 123)
        self.assertTrue(parseInt('-123') == -123)
        self.assertTrue(parseInt('123asd') == 123)
        self.assertTrue(parseInt('-123asd') == -123)

    def test_parse_date(self):
        self.assertEqual(parse_date('2016-06-1', False), datetime.datetime(2016,6,1))
        self.assertEqual(parse_date('2016-06-1'), timezone.make_aware(datetime.datetime(2016,6,1)))
        self.assertEqual(parse_date('2016-12-08 23:09:25', False), datetime.datetime(2016,12,8,23,9,25))
        self.assertEqual(parse_date('2016-12-08 23:09:25'), timezone.make_aware(datetime.datetime(2016,12,8,23,9,25)))
        self.assertEqual(parse_date_next('2016-06-18', False), datetime.datetime(2016,6,19))
        self.assertEqual(parse_date_next('2016-12-31', False), datetime.datetime(2017,1,1))
        self.assertEqual(parse_date_next('2016-12-08 23:09:25', False), datetime.datetime(2016,12,8,23,9,26))
        self.assertEqual(parse_date_next('2016-12-08 23:09:59', False), datetime.datetime(2016,12,8,23,10,0))

    def test_kuailexue_api(self):
        # return # 待kuailexue把接口实现后解封
        test_stu_id = 'malaprd_277_2016'
        url = KLX_STUDY_URL_FMT.format(subject='math')
        params = klx_build_params({'username': test_stu_id}, True)
        print(url)

        # total-item-nums
        resp = requests.get(url + '/total-item-nums', params=params)
        if resp.status_code != 200:
            print('cannot reach kuailexue server, http_status is %s' % (resp.status_code))
        print(resp.url)
        ret_json = json.loads(resp.content.decode('utf-8'))
        if ret_json.get('code') == 0 and ret_json.get('data') is not None:
            print(ret_json.get('data'))
        else:
            print('get kuailexue wrong data, CODE: %s, MSG: %s' % (ret_json.get('code'), ret_json.get('message')))

        # total-exercise-nums
        resp = requests.get(url + '/total-exercise-nums', params=params)
        if resp.status_code != 200:
            print('cannot reach kuailexue server, http_status is %s' % (resp.status_code))
        ret_json = json.loads(resp.content.decode('utf-8'))
        print(resp.url)
        if ret_json.get('code') == 0 and ret_json.get('data') is not None:
            print(ret_json.get('data'))
        else:
            print('get kuailexue wrong data, CODE: %s, MSG: %s' % (ret_json.get('code'), ret_json.get('message')))
