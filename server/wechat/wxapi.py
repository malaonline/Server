import logging
import json
import requests
import xmltodict
import random
import string
import hashlib

# django modules
from django.conf import settings
from django.core.urlresolvers import reverse
from django.utils import timezone

# local modules
from app import models
from app.utils import random_string, get_request_ip, get_server_host


__all__ = [
    "make_nonce_str",
    "wx_dict2xml",
    "wx_signature",
    "wx_sign_for_pay",
    "wx_get_token",
    "wx_get_jsapi_ticket",
    "wx_pay_unified_order",
    "wx_pay_order_query",
    "resolve_wx_pay_notify",
    "wx_send_tpl_msg",
    "WX_SUCCESS",
    "WX_FAIL",
    "WX_PAYERROR",
    "WX_AUTH_URL",
    "WX_TPL_MSG_URL",
    ]
logger = logging.getLogger('app')
_WX_PAY_UNIFIED_ORDER_LOG_FMT = 'weixin_pay_unified_order return: [{code}] {msg}.'
_WX_PAY_QUERY_ORDER_LOG_FMT = 'weixin_pay_query_order return: [{code}] {msg}.'
_WX_PAY_RESULT_NOTIFY_LOG_FMT = 'weixin_pay_result_notify return: [{code}] {msg}.'


WX_SUCCESS = 'SUCCESS'
WX_FAIL = 'FAIL'
WX_PAYERROR = 'PAYERROR'

WX_AUTH_URL = 'https://open.weixin.qq.com/connect/oauth2/authorize?appid='+settings.WEIXIN_APPID
# 微信模板消息
WX_TPL_MSG_URL = 'https://api.weixin.qq.com/cgi-bin/message/template/send?access_token={token}'

def make_nonce_str():
    return ''.join(random.choice(string.ascii_letters + string.digits) for _ in range(15))

def wx_dict2xml(d):
    return xmltodict.unparse({'xml': d}, full_document=False)

def wx_xml2dict(xmlstr):
    return xmltodict.parse(xmlstr)['xml']

def wx_signature(data):
    string = '&'.join(['%s=%s' % (key.lower(), data[key]) for key in sorted(data) if data[key] is not None and data[key] is not ''])
    return hashlib.sha1(string.encode('utf-8')).hexdigest()

def wx_sign_for_pay(params):
    content = '&'.join(['%s=%s' % (key, params[key]) for key in sorted(params) if params[key] is not None and params[key] is not ''])
    content += '&key=' + settings.WEIXIN_KEY
    return hashlib.md5(content.encode('utf-8')).hexdigest().upper()


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
    # 签名
    params['sign'] = wx_sign_for_pay(params)
    logger.debug(params)

    req_xml_str = wx_dict2xml(params)

    resp = requests.post(wx_url, data=req_xml_str.encode('utf-8'))
    if resp.status_code == 200:
        resp_dict = wx_xml2dict(resp.content.decode('utf-8'))
        logger.debug(resp_dict)
        return_code = resp_dict['return_code']
        if return_code != WX_SUCCESS:
            msg = resp_dict['return_msg']
            logger.error(_WX_PAY_UNIFIED_ORDER_LOG_FMT.format(code=return_code, msg=msg))
            return {'ok': False, 'msg': msg, 'code': 1}
        given_resp_sign = resp_dict.pop('sign', None)
        calculated_resp_sign = wx_sign_for_pay(resp_dict)
        logger.debug(given_resp_sign==calculated_resp_sign)
        if given_resp_sign!=calculated_resp_sign:
            return {'ok': False, 'msg': '签名失败'}
        result_code = resp_dict['result_code']
        if result_code != WX_SUCCESS:
            msg = resp_dict['err_code_des']
            logger.error(_WX_PAY_UNIFIED_ORDER_LOG_FMT.format(code=resp_dict['err_code'], msg=msg))
            return {'ok': False, 'msg': msg, 'code': 1}
        # prepay_id = resp_dict['prepay_id']
        # print(prepay_id)
        logger.info(_WX_PAY_UNIFIED_ORDER_LOG_FMT.format(code=return_code, msg=''))
        # create charge object
        _create_charge_object(params, order, resp_dict)
        return {'ok': True, 'msg': '', 'code': 0, 'data': resp_dict}
    else:
        return {'ok': False, 'msg': '网络请求出错!', 'code': -1}


def _create_charge_object(pre_req_params, order, wx_pay_resp_dict):
    charge = models.Charge.objects.create()
    charge.order = order
    charge.ch_id = wx_pay_resp_dict['prepay_id']
    charge.created = timezone.now()
    charge.app = pre_req_params['appid']
    charge.channel = models.Charge.WX_PUB_MALA
    charge.order_no = order.order_id
    charge.client_ip = pre_req_params['spbill_create_ip']
    charge.amount = order.to_pay
    charge.currency = 'cny'
    charge.subject = ''
    charge.body = pre_req_params['body']
    charge.extra = json.dumps({'openid': pre_req_params['openid']})
    charge.transaction_no = ''
    charge.failure_code = ''
    charge.failure_msg = ''
    charge.metadata = ''
    charge.credential = ''
    charge.description = ''
    charge.save()


def wx_pay_order_query(wx_order_id=None, order_id=None):
    """
    参考: https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_2
    """
    wx_url = 'https://api.mch.weixin.qq.com/pay/orderquery'

    if not wx_order_id and not order_id:
        return {'ok': False, 'msg': '没有订单号!', 'code': -4}
    params = {}
    params['appid'] = settings.WEIXIN_APPID
    params['mch_id'] = settings.WEIXIN_MERCHANT_ID
    # [微信的订单号, 商户系统内部的订单号]二选一, 优先使用'微信的订单号'
    if wx_order_id:
        params['transaction_id'] = wx_order_id  # 微信的订单号
    if order_id:
        params['out_trade_no'] = order_id       # 商户系统内部的订单号
    params['nonce_str'] = make_nonce_str()
    # 签名
    params['sign'] = wx_sign_for_pay(params)
    logger.debug(params)

    req_xml_str = wx_dict2xml(params)

    resp = requests.post(wx_url, data=req_xml_str.encode('utf-8'))
    if resp.status_code == 200:
        resp_dict = wx_xml2dict(resp.content.decode('utf-8'))
        logger.debug(resp_dict)
        return_code = resp_dict['return_code']
        if return_code != WX_SUCCESS:
            msg = resp_dict['return_msg']
            logger.error(_WX_PAY_QUERY_ORDER_LOG_FMT.format(code=return_code, msg=msg))
            return {'ok': False, 'msg': msg, 'code': 1}
        given_resp_sign = resp_dict.pop('sign', None)
        calculated_resp_sign = wx_sign_for_pay(resp_dict)
        logger.debug(given_resp_sign==calculated_resp_sign)
        if given_resp_sign!=calculated_resp_sign:
            return {'ok': False, 'msg': '签名失败'}
        result_code = resp_dict['result_code']
        if result_code != WX_SUCCESS:
            msg = resp_dict['err_code_des']
            logger.error(_WX_PAY_QUERY_ORDER_LOG_FMT.format(code=resp_dict['err_code'], msg=msg))
            return {'ok': False, 'msg': msg, 'code': 1}
        # trade_state = resp_dict['trade_state']
        # print(trade_state)
        """
            SUCCESS—支付成功
            REFUND—转入退款
            NOTPAY—未支付
            CLOSED—已关闭
            REVOKED—已撤销（刷卡支付）
            USERPAYING--用户支付中
            PAYERROR--支付失败(其他原因，如银行返回失败)
        """
        logger.info(_WX_PAY_QUERY_ORDER_LOG_FMT.format(code=return_code, msg=''))
        transaction_id = resp_dict['transaction_id']
        out_trade_no = resp_dict['out_trade_no']
        _set_charge_transaction_no(out_trade_no, transaction_id)
        return {'ok': True, 'msg': '', 'code': 0, 'data': resp_dict}
    else:
        return {'ok': False, 'msg': '网络请求出错!', 'code': -1}


def _set_charge_transaction_no(order_no, transaction_id):
    charge = models.Charge.objects.get(order__order_id=order_no)
    charge.transaction_no = transaction_id
    charge.save()


def resolve_wx_pay_notify(request):
    req_dict = wx_xml2dict(request.body.decode('utf-8'))
    logger.debug(req_dict)
    return_code = req_dict['return_code']
    if return_code != WX_SUCCESS:
        msg = req_dict['return_msg']
        logger.error(_WX_PAY_RESULT_NOTIFY_LOG_FMT.format(code=return_code, msg=msg))
        return {'ok': False, 'msg': msg, 'code': 1}
    given_resp_sign = req_dict.pop('sign', None)
    calculated_resp_sign = wx_sign_for_pay(req_dict)
    logger.debug(given_resp_sign==calculated_resp_sign)
    if given_resp_sign!=calculated_resp_sign:
        return {'ok': False, 'msg': '签名失败'}
    result_code = req_dict['result_code']
    if result_code != WX_SUCCESS:
        msg = req_dict['err_code_des']
        logger.error(_WX_PAY_RESULT_NOTIFY_LOG_FMT.format(code=req_dict['err_code'], msg=msg))
        return {'ok': False, 'msg': msg, 'code': 1}
    logger.info(_WX_PAY_RESULT_NOTIFY_LOG_FMT.format(code=return_code, msg=''))
    # openid = req_dict['openid']
    transaction_id = req_dict['transaction_id']
    out_trade_no = req_dict['out_trade_no']
    _set_charge_transaction_no(out_trade_no, transaction_id)
    return {'ok': True, 'msg': '', 'code': 0, 'data': req_dict}


def wx_send_tpl_msg(token, tpl_id, openid, data, detail_url=''):
    wx_url = WX_TPL_MSG_URL.format(token=token)
    ct = {
        'access_token': token,
        'touser': openid,
        'template_id': tpl_id,
        'url': detail_url,
        'topcolor': "#FF0000",
        "data": data
    }
    resp = requests.post(wx_url, data=json.dumps(ct))
    if resp.status_code == 200:
        ret_json = json.loads(resp.content.decode('utf-8'))
    else:
        ret_json = {'ok': False}
    logger.debug("wx_send_tpl_msg:")
    logger.debug(ret_json)
    return ret_json
