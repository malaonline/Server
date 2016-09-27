#
# 快乐学API
#
import json
import logging
import random
import hashlib
import requests
import urllib

from django.conf import settings

from app.models import Student
from app.utils import random_chars
from app.utils.algorithm import verify_sha1_sig, sign_sha1

__all__ = [
    "KLX_WEB_SITE",
    "KLX_STUDY_URL_FMT",
    "KLX_REPORT_SUBJECTS",
    "KLX_TEACHING_SUBJECTS",
    "KLX_MATH_ABILITY_KEYS",
    "KLX_ROLE_TEACHER",
    "KLX_ROLE_STUDENT",
    "klx_subject_name",
    "klx_sign_params",
    "klx_verify_sign",
    "klx_build_params",
    "klx_register",
    "klx_relation",
    "klx_reg_student",
    "klx_reg_teacher",
    ]
_logger = logging.getLogger('app')
_console = logging.getLogger('console')

KLX_WEB_SITE = settings.KUAILEXUE_WEB_SITE
KLX_STUDY_URL_FMT = '{0!s}/{{subject}}/{1!s}'.format(settings.KUAILEXUE_SERVER, settings.KUAILEXUE_API_ID)
KLX_REPORT_SUBJECTS = settings.KUAILEXUE_REPORT_SUPPORTED_SUBJECTS
KLX_TEACHING_SUBJECTS = settings.KUAILEXUE_TEACHING_SUBJECTS
KLX_MATH_ABILITY_KEYS = ('abstract', 'reason', 'appl', 'spatial', 'calc', 'data')

_klx_common_params = {
    'api_id': settings.KUAILEXUE_API_ID,
    'api_version': 1,
    'partner': settings.KUAILEXUE_PARTNER
}

_klx_default_password = '123456'

KLX_ROLE_TEACHER = 1
KLX_ROLE_STUDENT = 2


_ctx = {'_is_cold_testing': None}
def _get_is_cold_testing():
    if _ctx['_is_cold_testing'] is None:
        _console.debug('_init_is_cold_testing')
        _ctx['_is_cold_testing'] = hasattr(settings, "COLD_TESTING") and settings.COLD_TESTING
    return _ctx['_is_cold_testing']


def klx_subject_name(name):
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
    s = ''.join(['{0!s}={1!s}'.format(key, params[key] or '') for key in sorted(params)
                        if key != sign_key])
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
    p = _klx_common_params.copy()
    p.update(params)
    if sign:
        klx_sign_params(p)
    return p


def _klx_make_password(n=6):
    return ''.join(random.sample('0123456789', n))


def klx_register(role, uid, name, password=None, subject=None):
    '''

    :param role: 1：老师， 2：学生
    :param uid: models.User.id
    :param name: Student Name or Teacher Name
    :param password: 默认123456
    :param subject: 当role为老师时，该参数必传
    :return: kuailexue username
    '''
    klx_url = settings.KUAILEXUE_SERVER + '/third-partner/register'
    # _logger.debug(klx_url)
    params = {
        'role': role,
        'uid': uid,
        'name': name,
    }
    if password is not None:
        params['password'] = password
    if subject is not None:
        params['subject'] = subject
    params = klx_build_params(params, True)
    if settings.TESTING:
        return klx_verify_sign(params) and '1' or '0'
    # _logger.debug(params)
    try:
        resp = requests.post(klx_url, data=params, timeout=10)
    except Exception as err:
        _logger.error('cannot reach kuailexue server')
        _logger.exception(err)
        return None
    if resp.status_code != 200:
        _logger.error('cannot reach kuailexue server, http_status is {0!s}'.format((resp.status_code)))
        # raise KuailexueServerError('cannot reach kuailexue server, http_status is %s' % (resp.status_code))
        return None
    if _get_is_cold_testing():
        _console.warning(klx_url+'?'+urllib.parse.urlencode(params))
    ret_json = json.loads(resp.content.decode('utf-8'))
    if _get_is_cold_testing():
        _console.info(ret_json)
    if ret_json.get('data') is not None: # code == 0, 用户已存在时code != 0
        ret_data = ret_json.get('data')
        return ret_data.get('username')  # (仅供参考)目前返回格式是 KUAILEXUE_PARTNER+uid+'_'+${YYYY}
    else:
        req_url = klx_url+'?'+urllib.parse.urlencode(params)
        _logger.error('kuailexue reponse data error, CODE: {0!s}, MSG: {1!s}. (URL={2!s})'.format(ret_json.get('code'), ret_json.get('message'), req_url))
        # raise KuailexueDataError('get kuailexue wrong data, CODE: %s, MSG: %s' % (ret_json.get('code'), ret_json.get('message')))
        return None


def klx_relation(klx_teacher, klx_students):
    '''

    :param klx_teacher: 老师username
    :param klx_students: 学生usernames，多个以逗号隔开
    :return: True or False
    '''
    klx_url = settings.KUAILEXUE_SERVER + '/third-partner/relation'
    params = {
        'tea_username': klx_teacher,
        'stu_usernames': klx_students,
    }
    params = klx_build_params(params, True)
    if settings.TESTING:
        return klx_verify_sign(params)
    try:
        # resp = requests.post(klx_url, data=params, timeout=10)
        resp = requests.get(klx_url, params=params, timeout=10)
    except Exception as err:
        _logger.error('cannot reach kuailexue server')
        _logger.exception(err)
        return False
    if _get_is_cold_testing():
        _console.warning('\n{0!s}'.format((resp.url)))
    if resp.status_code != 200:
        _logger.error('cannot reach kuailexue server, http_status is {0!s}'.format((resp.status_code)))
        # raise KuailexueServerError('cannot reach kuailexue server, http_status is %s' % (resp.status_code))
        return False
    ret_json = json.loads(resp.content.decode('utf-8'))
    if _get_is_cold_testing():
        _console.info(ret_json)
    if ret_json.get('code') == 0 or ret_json.get('message', '').lower() == 'success':
        return True
    else:
        req_url = klx_url+'?'+urllib.parse.urlencode(params)
        _logger.error('kuailexue reponse data error, CODE: {0!s}, MSG: {1!s}. (URL={2!s})'.format(ret_json.get('code'), ret_json.get('message'), req_url))
        # raise KuailexueDataError('get kuailexue wrong data, CODE: %s, MSG: %s' % (ret_json.get('code'), ret_json.get('message')))
        return False


def klx_reg_student(parent, student=None):
    '''
    把mala中的student注册到kuailexue中, 返回快乐学用户名。若已注册直接返回已注册的用户名
    :param parent: models.Parent
    :param student: models.Student
    :return: student's klx_username or None
    '''
    # 目前购买课程等等都是parent做的
    if student is None:
        student = parent.students.first()
    if student is None:
        student = Student.new_student().student
        parent.students.add(student)
        parent.save()
    o_klx_username = student.user.profile.klx_username
    if o_klx_username:
        if not student.user.profile.klx_password:
            student.user.profile.klx_password = _klx_default_password
            student.user.profile.save()
        return o_klx_username
    role = KLX_ROLE_STUDENT
    uid = '{0!s}_{1!s}_{2!s}'.format(settings.ENV_TYPE, student.user_id, random_chars(5))
    name = student.name or 'mala_{0!s}'.format((student.user_id))
    password = student.user.profile.klx_password or _klx_make_password()
    klx_username = klx_register(role,uid,name,password=password)
    if klx_username:
        student.user.profile.klx_username = klx_username
        student.user.profile.klx_password = password
        student.user.profile.save()
    return klx_username


def klx_reg_teacher(teacher):
    '''
    把mala中的teacher注册到kuailexue中, 返回快乐学用户名。若已注册直接返回已注册的用户名
    :param teacher: models.Teacher
    :return: teacher's klx_username or None
    '''
    o_klx_username = teacher.user.profile.klx_username
    if o_klx_username:
        if not teacher.user.profile.klx_password:
            teacher.user.profile.klx_password = _klx_default_password
            teacher.user.profile.save()
        return o_klx_username
    role = KLX_ROLE_TEACHER
    uid = '{0!s}_{1!s}_{2!s}'.format(settings.ENV_TYPE, teacher.user_id, random_chars(5))
    name = teacher.name or 'mala_{0!s}'.format((teacher.user_id))
    password = teacher.user.profile.klx_password or _klx_make_password()
    subject = teacher.subject()
    if subject.name not in KLX_TEACHING_SUBJECTS:
        return None
    klx_subject = klx_subject_name(subject.name)
    klx_username = klx_register(role,uid,name,password=password,subject=klx_subject)
    if klx_username:
        teacher.user.profile.klx_username = klx_username
        teacher.user.profile.klx_password = password
        teacher.user.profile.save()
    return klx_username

