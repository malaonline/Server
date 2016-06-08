#
# 快乐学API
#
import json
import logging
import requests
import hashlib

from django.conf import settings

from app.utils.algorithm import verify_sha1_sig, sign_sha1

__all__ = [
    "KLX_STUDY_URL_FMT",
    "KLX_SUPPORTED_SUBJECTS",
    "KLX_MATH_ABILITY_KEYS",
    "klx_subject_en",
    "klx_sign_params",
    "klx_verify_sign",
    "klx_build_params",
    "klx_register",
    "klx_relation",
    ]
_logger = logging.getLogger('app')

KLX_STUDY_URL_FMT = '%s/{subject}/%s' % (settings.KUAILEXUE_SERVER, settings.KUAILEXUE_API_ID,)
KLX_SUPPORTED_SUBJECTS = settings.KUAILEXUE_REPORT_SUPPORTED_SUBJECTS.split(',')
KLX_MATH_ABILITY_KEYS = ('abstract', 'reason', 'appl', 'spatial', 'calc', 'data')

_KLX_COMMON_PARAMS = {
    'api_id': settings.KUAILEXUE_API_ID,
    'api_version': 1,
    'partner': settings.KUAILEXUE_PARTNER
}


def klx_subject_en(name):
    map = {
        '数学': "math",
        '英语': "english",
        '语文': "chinese",
        '物理': "physics",
        '化学': "chemistry",
        '地理': "geography",
        '政治': "politics",
        '生物': "biology",
        '历史': "history",
    }
    return map.get(name)


def _klx_param_hash(params, sign_key="sign"):
    s = ''.join(['%s=%s' % (key, params[key]) for key in sorted(params)
                        if key != sign_key and params[key] is not None and params[key] is not ''])
    hs = hashlib.md5(s.encode('utf-8')).hexdigest()
    return hs


def klx_sign_params(params, sign_key="sign"):
    body = _klx_param_hash(params)
    pri_key = settings.KUAILEXUE_API_PRI_KEY
    params[sign_key] = sign_sha1(body.encode('utf-8'), pri_key).decode('utf-8')


def klx_verify_sign(params, sign_key="sign"):
    body = _klx_param_hash(params)
    pub_key = settings.KUAILEXUE_API_PUB_KEY
    return (verify_sha1_sig(body.encode('utf-8'), params[sign_key].encode('utf-8'), pub_key))


def klx_build_params(params, sign=False):
    p = _KLX_COMMON_PARAMS.copy()
    p.update(params)
    if sign:
        klx_sign_params(params)
    return p


def klx_register(uid, name):
    '''
    :param uid: models.User.id
    :param name: Student Name or Teacher Name
    :return: kuailexue uid
    '''
    klx_url = settings.KUAILEXUE_SERVER + '/third-partner/register'
    params = {
        'partner': settings.KUAILEXUE_PARTNER,
        'uid': uid,
        'name': name,
    }
    resp = requests.get(klx_url, data=params)
    if resp.status_code != 200:
        _logger.error('cannot reach kuailexue server, http_status is %s' % (resp.status_code))
        # raise KuailexueServerError('cannot reach kuailexue server, http_status is %s' % (resp.status_code))
        return False
    ret_json = json.loads(resp.content.decode('utf-8'))
    if ret_json.get('code') == 0 and ret_json.get('data') is not None:
        ret_data = ret_json.get('data')
        return ret_data.get('username')
    else:
        _logger.error('kuailexue reponse data error, CODE: %s, MSG: %s' % (ret_json.get('code'), ret_json.get('message')))
        # raise KuailexueDataError('get kuailexue wrong data, CODE: %s, MSG: %s' % (ret_json.get('code'), ret_json.get('message')))
        return False


def klx_relation(klx_teacher_uid, klx_stu_uids):
    klx_url = settings.KUAILEXUE_SERVER + '/third-partner/relation'
    params = {
        'partner': settings.KUAILEXUE_PARTNER,
        'tea_username': klx_teacher_uid,
        'stu_usernames': klx_stu_uids,
    }
    resp = requests.get(klx_url, data=params)
    if resp.status_code != 200:
        _logger.error('cannot reach kuailexue server, http_status is %s' % (resp.status_code))
        # raise KuailexueServerError('cannot reach kuailexue server, http_status is %s' % (resp.status_code))
        return False
    ret_json = json.loads(resp.content.decode('utf-8'))
    if ret_json.get('code') == 0 and ret_json.get('message', '').lower() == 'success':
        return True
    else:
        _logger.error('kuailexue reponse data error, CODE: %s, MSG: %s' % (ret_json.get('code'), ret_json.get('message')))
        # raise KuailexueDataError('get kuailexue wrong data, CODE: %s, MSG: %s' % (ret_json.get('code'), ret_json.get('message')))
        return False

