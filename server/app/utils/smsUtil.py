from django.conf import settings
import requests
import re
import json


def isValidPhone(phone):
    return re.match(r'^((((\+86)|(86))?(1)\d{10})|000\d+)$', phone)


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
    url = "http://yunpian.com/v1/sms/send.json"
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
    SITE_NAME = '麻辣老师'
    msg = "【"+SITE_NAME+"】您的验证码是"+str(checkcode)
    return sendSms(phone, msg)
