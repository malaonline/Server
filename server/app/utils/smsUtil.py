from django.conf import settings
import requests
import re
import json
import urllib


def isValidPhone(phone):
    return re.match(r'^((((\+86)|(86))?(1)\d{10})|000\d+)$', phone) and True or False


def isTestPhone(phone):
    return re.match(r'^000\d+$', phone) and True or False


def isValidCode(code):
    return re.match(r'^\d+$', code)


class SendSMSError(Exception):
    pass


def sendSms(phone, msg):
    # 现在会检查返回结果,对于出错情况,将抛出一个SendSMSError异常
    apikey = settings.YUNPIAN_API_KEY # get apikey by global settings
    params = {'apikey': apikey, 'mobile': phone, 'text': msg}
    url = "https://sms.yunpian.com/v1/sms/send.json"
    headers = {"Accept": "text/plain;charset=utf-8;", "Content-Type":"application/x-www-form-urlencoded;charset=utf-8;"}
    response = requests.post(url, headers=headers, data=params)
    if response.status_code != 200:
        raise SendSMSError("sms server status code is {status_code}".format(status_code=response.status_code))
    else:
        content = response.content.decode()
        data = json.loads(content)
        if data["code"] != 0:
            raise SendSMSError("sms server error. {error_msg}".format(error_msg=content))
    return response


def sendCheckcode(phone, checkcode):
    msg = "【麻辣老师】您的验证码是{check_code}".format(check_code=str(checkcode))
    return sendSms(phone, msg)


def tpl_send_sms(phone, tpl_id, tpl_value):
    """
    模板接口发短信
    tpl_value = {'#code#':'1234','#company#':'云片网'}
    """
    apikey = settings.YUNPIAN_API_KEY # get apikey by global settings
    params = {'apikey': apikey, 'tpl_id': tpl_id, 'tpl_value': tpl_value, 'mobile': phone}
    url = "https://sms.yunpian.com/v1/sms/tpl_send.json"
    headers = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}
    response = requests.post(url, headers=headers, data=params)
    if response.status_code != 200:
        raise SendSMSError("sms server status code is {status_code}".format(status_code=response.status_code))
    else:
        content = response.content.decode()
        data = json.loads(content)
        if data["code"] != 0:
            raise SendSMSError("sms server error. {error_msg}".format(error_msg=content))
    return response
