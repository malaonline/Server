from django.conf import settings
import requests

def sendSms(phone, msg):
    apikey = settings.YUNPIAN_API_KEY # get apikey by global settings
    params = {'apikey': apikey, 'mobile': phone, 'text': msg}
    url = "http://yunpian.com/v1/sms/send.json"
    headers = {"Accept": "text/plain;charset=utf-8;", "Content-Type":"application/x-www-form-urlencoded;charset=utf-8;"}
    return requests.post(url, headers=headers, data=params)

def sendCheckcode(phone, checkcode):
    SITE_NAME = '麻辣老师'
    msg = "【"+SITE_NAME+"】您的验证码是"+str(checkcode)
    return sendSms(phone, msg)
