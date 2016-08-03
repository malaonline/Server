import datetime
import unittest
import requests
import json
import logging

from django.conf import settings
from django.utils import timezone

from app.utils.types import parseInt, parse_date, parse_date_next
from app.utils.klx_api import *


_console = logging.getLogger('console')


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
        _console.info(p)
        pp = klx_build_params(p, True)
        _console.info(pp['sign'])
        p = {
            'uid': 'prd_693',
            'password': '816593',
            'role': 2,
            'name': ''
        }
        _console.info(p)
        pp2 = klx_build_params(p, True)
        _console.info(pp2['sign'])
        self.assertEqual(pp['sign'], pp2['sign'])

    def test_kuailexue_api1(self):
        # return # 待kuailexue把接口实现后解封
        test_stu_id = 'malaprd_277_2016'
        url = KLX_STUDY_URL_FMT.format(subject='math')
        params = klx_build_params({'username': test_stu_id}, True)
        _console.warning(url)

        def _request_kuailexue_api_data(type):
            resp = requests.get(url + '/' + type, params=params)
            if resp.status_code != 200:
                _console.error('cannot reach kuailexue server, http_status is %s' % (resp.status_code))
            _console.warning(resp.url)
            ret_json = json.loads(resp.content.decode('utf-8'))
            if ret_json.get('code') == 0 and ret_json.get('data') is not None:
                _console.info(ret_json.get('data'))
            else:
                _console.error('get kuailexue wrong data, CODE: %s, MSG: %s' % (ret_json.get('code'), ret_json.get('message')))

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

    def test_kuailexue_api2(self):
        # return # 待kuailexue把接口实现后解封
        settings.TESTING = False
        test_stu_id = 'test_student_1'
        test_stu_name = '测试学生1'
        test_tea_id = 'debug_210_7VQy5'
        test_teat_name = '测试老师1'
        test_teat_pswd = '892673'
        klx_stu = klx_register(KLX_ROLE_STUDENT, test_stu_id, test_stu_name)
        _console.info(klx_stu)
        self.assertIsNotNone(klx_stu)
        klx_tea = klx_register(KLX_ROLE_TEACHER, test_tea_id, test_teat_name, test_teat_pswd)
        _console.info(klx_tea)
        self.assertIsNotNone(klx_tea)
        ok = klx_relation(klx_tea, klx_stu)
        self.assertTrue(ok)
