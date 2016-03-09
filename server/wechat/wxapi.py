import logging
import json
import requests
from Crypto.Hash import SHA

# django modules
from django.conf import settings

# local modules
from app import models

def wx_signature(params_obj):
    keys = params_obj.keys()
    sorted_keys = sorted(keys)
    buf = []
    for key in sorted_keys:
        buf.append(key+'='+str(params_obj[key]))
    content = '&'.join(buf)
    return SHA.new(content.encode()).hexdigest()


def get_token_from_weixin():
    wx_url = 'https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential'
    wx_url += '&appid=' + settings.WEIXIN_APPID
    wx_url += '&secret=' + settings.WEIXIN_APP_SECRET

    req = requests.get(wx_url)
    if req.status_code == 200:
        ret = json.loads(req.text)
        if "access_token" in ret:
            models.WeiXinToken.objects.all().delete()
            wxToken = models.WeiXinToken(token=ret['access_token'], token_type=models.WeiXinToken.ACCESS_TOKEN)
            if "expires_in" in ret:
                wxToken.expires_in = ret['expires_in']
            wxToken.save()
            return {'ok': True, 'token': ret['access_token'], 'code': 0}
        else:
            return {'ok': False, 'msg': '获取微信token出错!', 'code': -1}
    else:
        return {'ok': False, 'msg': '获取微信token出错，请联系管理员!', 'code': -1}


def get_wx_jsapi_ticket_from_weixin(access_token):
    wx_url = 'https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token={token}&type=jsapi'\
        .format(token=access_token)

    req = requests.get(wx_url)
    if req.status_code == 200:
        ret = json.loads(req.text)
        if "ticket" in ret:
            models.WeiXinToken.objects.filter(token_type=models.WeiXinToken.JSAPI_TICKET).delete()
            tk_obj = models.WeiXinToken(token_type=models.WeiXinToken.JSAPI_TICKET)
            tk_obj.token = ret['ticket']
            if "expires_in" in ret:
                tk_obj.expires_in = ret['expires_in']
            tk_obj.save()
            return {'ok': True, 'ticket': ret['ticket'], 'code': 0}
        else:
            return {'ok': False, 'msg': '获取微信jsapi_ticket出错!', 'code': -1}
    else:
        return {'ok': False, 'msg': '获取微信jsapi_ticket出错，请联系管理员!', 'code': -1}
