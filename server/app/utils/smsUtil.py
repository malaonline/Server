'''
短信API工具, 服务平台云片网(https://www.yunpian.com)
注意:
    1, 云片网不支持30秒内重发相同内容短信
'''

import logging
from django.conf import settings
import requests
import re
import json
from urllib.parse import urlencode

_logger = logging.getLogger('app')


_re_valid_phone = re.compile(r'^((((\+86)|(86))?(1)\d{10})|000\d+)$')
def isValidPhone(phone):
    return _re_valid_phone.match(phone) and True or False


_re_test_phone = re.compile(r'^000\d+$')
def isTestPhone(phone):
    return _re_test_phone.match(phone) and True or False


_re_valid_code = re.compile(r'^\d+$')
def isValidCode(code):
    return _re_valid_code.match(code) and True or False


class SendSMSError(Exception):
    pass


def sendSms(phone, msg):
    if not isValidPhone(phone):
        return False
    if isTestPhone(phone):
        return False
    # 现在会检查返回结果,对于出错情况,将抛出一个SendSMSError异常
    apikey = settings.YUNPIAN_API_KEY # get apikey by global settings
    params = {'apikey': apikey, 'mobile': phone, 'text': msg}
    url = "https://sms.yunpian.com/v1/sms/send.json"
    headers = {"Accept": "text/plain;charset=utf-8;", "Content-Type":"application/x-www-form-urlencoded;charset=utf-8;"}
    response = requests.post(url, headers=headers, data=params)
    if response.status_code != 200:
        _logger.error('cannot reach sms server, http_status is %s' % (response.status_code))
        raise SendSMSError("sms server status code is {status_code}".format(status_code=response.status_code))
    else:
        content = response.content.decode()
        data = json.loads(content)
        if data["code"] != 0:
            _logger.error('sms server response error, CODE: %s, MSG: %s(%s)' % (data.get('code'), data.get('msg'), data.get('detail')))
            raise SendSMSError("sms server error. {error_msg}".format(error_msg=content))
    return response


def sendCheckcode(phone, code):
    # msg = "【麻辣老师】您的验证码是{code}".format(check_code=str(code))
    # return sendSms(phone, msg)
    # 用模板	1273789【麻辣老师】您的验证码为#code#，请在1分钟内正确填写，请勿向任何人泄露。
    data = {'#code#': code}
    return _tpl_send_sms(phone, 1273789, data)
    # return try_send_sms(phone, 1273789, {'code': code}, 2)


def _tpl_send_sms(phone, tpl_id, tpl_value):
    """
    模板接口发短信
    tpl_value = {'#code#':'1234','#company#':'云片网'}
    """
    if not isValidPhone(phone):
        return False
    if isTestPhone(phone):
        return False
    apikey = settings.YUNPIAN_API_KEY # get apikey by global settings
    params = {'apikey': apikey, 'tpl_id': tpl_id, 'tpl_value': urlencode(tpl_value), 'mobile': phone}
    url = "https://sms.yunpian.com/v1/sms/tpl_send.json"
    headers = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}
    response = requests.post(url, headers=headers, data=params)
    if response.status_code != 200:
        _logger.error('cannot reach sms server, http_status is %s' % (response.status_code))
        raise SendSMSError("sms server status code is {status_code}".format(status_code=response.status_code))
    else:
        content = response.content.decode()
        data = json.loads(content)
        if data["code"] != 0:
            _logger.error('sms server response error, CODE: %s, MSG: %s(%s)' % (data.get('code'), data.get('msg'), data.get('detail')))
            raise SendSMSError("sms server error. {error_msg}".format(error_msg=content))
    return response


def tpl_send_sms(phone, tpl_id, params={}):
    _logger.debug("send sms to "+str(phone)+', '+str(tpl_id)+': '+str(params))
    data = {'#' + k + '#': v for k, v in params.items()}
    return _tpl_send_sms(phone, tpl_id, data)


"""
面试邀请
1274273【麻辣老师】尊敬的#username#，很高兴邀请您参加我们的面试，时间为#time#，地点为#adress#，如有疑问请致电010-57733349。
"""
TPL_INVITE_INTERVIEW = 1274273

"""
面试结果通知（通过）
1274275【麻辣老师】尊敬的#username#，很高兴地通知您，您已经通过我们的面试，您可以登录麻辣老师网站并完善您的个人资料。如有疑问请致电010-57733349。
"""
TPL_INTERVIEW_OK = 1274275

"""
面试结果通知（失败）
1274283
【麻辣老师】尊敬的#username#，抱歉地通知您，您未能通过我们的面试，感谢您对麻辣老师的关注。如有疑问请致电010-57733349。
"""
TPL_INTERVIEW_FAIL = 1274283

"""
完善资料被驳回
1274289
【麻辣老师】尊敬的#username#，您的资料填写有误，请登录麻辣老师网站修改并重新提交。
"""
TPL_INFO_WRONG = 1274289

"""
绑定银行卡
1274305
【麻辣老师】您的验证码为#code#，请勿向任何人泄露。
"""
TPL_BIND_BANKCARD = 1274305

"""
教师上架
1274309
【麻辣老师】尊敬的#username#，恭喜您成为麻辣老师，您的资料已经可以被学生查看。如有疑问请致电010-57733349。
"""
TPL_PUBLISH_TEACHER = 1274309

"""
教师级别变更
1274315
【麻辣老师】尊敬的#username#，您的级别变更为#grade#。
"""
TPL_LEVEL_CHANGE = 1274315

"""
学生退费
1274317
【麻辣老师】尊敬的#username#，您的学生发生退费，请登录麻辣老师网站查看相关信息。如有疑问请致电010-57733349。
"""
TPL_REFUND_NOTICE = 1274317

"""
确认提现
1274305
【麻辣老师】您的验证码为#code#，请勿向任何人泄露。
"""
TPL_WITHDRAW_CONFIRM = 1274305

"""
提现申请通过
1274319
【麻辣老师】尊敬的#username#，您的提现申请已经通过审核，预计2个工作日到账。
"""
TPL_WITHDRAW_APPROVE = 1274319

"""
提现申请驳回
1274321
【麻辣老师】尊敬的#username#，抱歉地通知您，您的提现申请未通过审核。如有疑问请致电010-57733349。
"""
TPL_WITHDRAW_REJECT = 1274321

"""
新增收入提醒
1274323
【麻辣老师】您有一笔新的课时费被确认，金额为#money#，请访问麻辣老师网站并到我的钱包中查看。
"""
TPL_COURSE_INCOME = 1274323

"""
付款成功（老师）
1280621
【麻辣老师】尊敬的#username#，您的课程被购买，学生姓名#studentname#，#grade#，#coursetime#，共#number#节课。
"""
TPL_TEACHER_COURSE_PAID = 1280621

'''
安排用户测评建档（老师）
1435971
【麻辣老师】您有一堂#subject#建档测评课已预约#datetime#
'''
TPL_TEACHER_EVALUATE = 1435971

'''
调课成功后短信提醒 (老师)
1458571
【麻辣老师】您在#olddate#的#subject#课程已调整到#newdate#，记得按时上课哦。
e.g. 【麻辣老师】您在11月12日10:10－12:10的初三数学课程已调整到11月13日16:10-18：10，记得按时上课哦。
'''
TPL_TEA_TRANSFER_COURSE = 1458571



###############################################################################
## 下面模板是给家长发的, 上面的是给老师发的
###############################################################################
"""
退费申请
1275527
【麻辣老师】尊敬的#studentname#，您的退费申请已经受理。如非本人操作，请及时与工作人员联系或致电010-57733349。
"""
TPL_STU_REFUND_REQUEST = 1275527

"""
退费成功
1275525
【麻辣老师】尊敬的#studentname#，您已经退费成功，金额为#amount#。如有疑问请致电010-57733349。
"""
TPL_STU_REFUND_APPROVE = 1275525

"""
付款成功（家长）
1280611
【麻辣老师】尊敬的#studentname#，恭喜您付款成功，订单号#orderid#，金额#amount#元。你可以前往客户端查看课程信息。
"""
TPL_STU_PAY_SUCCESS = 1280611

"""
付款失败（家长）
1280619
【麻辣老师】抱歉！订单购买失败，您的费用不会被扣除。请重新购买或致电咨询。电话：010-57733349。
"""
TPL_STU_PAY_FAIL = 1280619

'''
安排用户测评建档（家长）
1435965
【麻辣老师】您已预约#datatime#的#subject#课程测评建档服务
NOTE: 短信模板上datatime单词拼写错了, 请将错就错
'''
TPL_STU_EVALUATE = 1435965

'''
调课成功后短信提醒 (家长,学生)
1458583
【麻辣老师】您在#olddate#的#subject#课程已调整到#newdate#，记得按时参加哦。
e.g. 【麻辣老师】您在11月12日10:10－12:10的初三数学课程已调整到11月13日16:10-18：10，记得按时参加哦。
'''
TPL_STU_TRANSFER_COURSE = 1458583
