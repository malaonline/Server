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

    def test_kuailexue_api0(self):
        p = {
            'uid': 'prd_693',
            'password': '816593',
            'role': 2,
            'name': None
        }
        # print(p)
        pp = klx_build_params(p, True)
        # print(pp['sign'])
        p = {
            'uid': 'prd_693',
            'password': '816593',
            'role': 2,
            'name': ''
        }
        # print(p)
        pp2 = klx_build_params(p, True)
        # print(pp2['sign'])
        self.assertEqual(pp['sign'], pp2['sign'])

    def test_kuailexue_api(self):
        # return # 待kuailexue把接口实现后解封
        test_stu_id = 'malaprd_277_2016'
        url = KLX_STUDY_URL_FMT.format(subject='math')
        params = klx_build_params({'username': test_stu_id}, True)
        print(url)

        def _request_kuailexue_api_data(type):
            resp = requests.get(url + '/' + type, params=params)
            if resp.status_code != 200:
                print('cannot reach kuailexue server, http_status is %s' % (resp.status_code))
            print(resp.url)
            ret_json = json.loads(resp.content.decode('utf-8'))
            if ret_json.get('code') == 0 and ret_json.get('data') is not None:
                print(ret_json.get('data'))
            else:
                print('get kuailexue wrong data, CODE: %s, MSG: %s' % (ret_json.get('code'), ret_json.get('message')))

        # total-item-nums 指定学生累计答题数、正确答题数
        _request_kuailexue_api_data('total-item-nums')

        # total-exercise-nums 指定学生累计答题次数（即答题次数）及完成率
        _request_kuailexue_api_data('total-exercise-nums')

        # error-knowledge-point 错题知识点分布
        _request_kuailexue_api_data('error-knowledge-point')

        # items-trend 按月显示练习量走势
        _request_kuailexue_api_data('items-trend')

        # knowledge-point-accuracy 指定学生一级/二级知识点正确率
        _request_kuailexue_api_data('knowledge-point-accuracy')

        # ability-structure 能力结构分析
        _request_kuailexue_api_data('ability-structure')

        # my-average-score 各知识点全部用户平均得分率及指定学生得分率
        _request_kuailexue_api_data('my-average-score')
