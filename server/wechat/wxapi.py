import logging
import json
import requests
from Crypto.Hash import SHA
import xmltodict

# django modules
from django.conf import settings
from django.core.urlresolvers import reverse

# local modules
from app import models
from app.utils import random_string, get_request_ip, get_server_host


__all__ = [
    "make_nonce_str",
    "wx_signature",
    "wx_get_token",
    "wx_get_jsapi_ticket",
    "wx_pay_unified_order",
    ]
logger = logging.getLogger('app')


def make_nonce_str():
    return random_string().replace('-','')

def wx_dict2xml(d):
    return xmltodict.unparse({'xml': d}, full_document=False)

def wx_xml2dict(xmlstr):
    return xmltodict.parse(xmlstr)['xml']

def wx_signature(params_obj):
    keys = params_obj.keys()
    sorted_keys = sorted(keys)
    buf = []
    for key in sorted_keys:
        buf.append(key+'='+str(params_obj[key]))
    content = '&'.join(buf)
    return SHA.new(content.encode()).hexdigest()


def wx_get_token():
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


def wx_get_jsapi_ticket(access_token):
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


_WX_PAY_MESSAGE_FORMAT = 'weixin_pay_unified_order return: [{code}] {msg}.'

def wx_pay_unified_order(order, request, wx_openid):
    """
    参考: https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1
    """
    wx_url = 'https://api.mch.weixin.qq.com/pay/unifiedorder'

    params = {}
    params['appid'] = settings.WEIXIN_APPID
    params['mch_id'] = settings.WEIXIN_MERCHANT_ID
    params['device_info'] = 'WEB'  # 终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
    params['nonce_str'] = make_nonce_str()
    params['body'] = '课程购买'
    # params['detail'] = ''        # not required
    # params['attach'] = ''        # not required
    # params['fee_type'] = 'CNY'   # not required, 默认人民币：CNY
    params['out_trade_no'] = order.order_id   # Order model记录的ID
    params['total_fee'] = order.to_pay  # 订单总金额，单位为分
    sp_ip = get_request_ip(request)     # APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
    params['spbill_create_ip'] = sp_ip
    # params['time_start'] = ''    # not required, yyyyMMddHHmmss
    # params['time_expire'] = ''   # not required, yyyyMMddHHmmss
    # params['goods_tag'] = ''     # not required, 代金券或立减优惠功能的参数
    # params['product_id'] = ''    # not required
    # params['limit_pay'] = ''     # not required, no_credit--指定不能使用信用卡支付
    # TODO: 接收微信支付异步通知回调地址
    params['notify_url'] = get_server_host(request) + reverse('wechat:wx_pay_notify')
    params['trade_type'] = 'JSAPI'      # JSAPI，NATIVE，APP
    params['openid'] = wx_openid        # trade_type=JSAPI，此参数必传，用户在商户appid下的唯一标识。
    print(params)
    # 签名
    params['sign'] = wx_signature(params)

    req_xml_str = wx_dict2xml(params)

    resp = requests.post(wx_url, data=req_xml_str.encode('utf-8'))
    if resp.status_code == 200:
        resp_dict = wx_xml2dict(resp.content.decode('utf-8'))
        return_code = resp_dict['return_code']
        if return_code != 'SUCCESS':
            msg = resp_dict['return_msg']
            logger.error(_WX_PAY_MESSAGE_FORMAT.format(code=return_code, msg=msg))
            return {'ok': False, 'msg': msg, 'code': 1}
        given_resp_sign = resp_dict.pop('sign', None)
        calculated_resp_sign = wx_signature(resp_dict)
        print(given_resp_sign==calculated_resp_sign)
        result_code = resp_dict['result_code']
        if result_code != 'SUCCESS':
            msg = resp_dict['err_code_des']
            logger.error(_WX_PAY_MESSAGE_FORMAT.format(code=return_code, msg=msg))
            return {'ok': False, 'msg': msg, 'code': 1}
        prepay_id = resp_dict['prepay_id']
        print(prepay_id)
        logger.info(_WX_PAY_MESSAGE_FORMAT.format(code=return_code, msg=msg))
        return {'ok': True, 'msg': '', 'code': 0, 'data': resp_dict}
    else:
        return {'ok': False, 'msg': '网络请求出错!', 'code': -1}
