import datetime
import re

from django.utils.timezone import make_aware

_re_number = re.compile(r'^-?\d+')

_re_date = re.compile(r'^\d+-\d+-\d+$')
_re_date_h = re.compile(r'^\d+-\d+-\d+ \d+$')
_re_date_h_m = re.compile(r'^\d+-\d+-\d+ \d+:\d+$')
_re_date_full = re.compile(r'^\d+-\d+-\d+ \d+:\d+:\d+$')

DATE_P_FORMAT = '%Y-%m-%d'
DATE_P_FORMAT_WITH_HH = '%Y-%m-%d %H'
DATE_P_FORMAT_WITH_HH_MM = '%Y-%m-%d %H:%M'
DATE_P_FORMAT_FULL = '%Y-%m-%d %H:%M:%S'

def parseInt(nums, default='NaN'):
    """
    (parseInt(None) == 'NaN')
    (parseInt('') == 'NaN')
    (parseInt(123) == 123)
    (parseInt(-123) == -123)
    (parseInt('123') == 123)
    (parseInt('-123') == -123)
    (parseInt('123asd') == 123)
    (parseInt('-123asd') == -123)
    (parseInt(234.234) == 234)
    (parseInt(-234.234) == -234)
    (parseInt('234.234') == 234)
    (parseInt('-234.234') == -234)
    (parseInt('asd') == 'NaN')
    (parseInt('-asd') == 'NaN')
    """
    if nums is None or nums is '':
        return default
    if isinstance(nums, int):
        return nums
    if isinstance(nums, float):
        return int(nums)
    d = _re_number.search(nums)
    return int(d.group(0)) if d else default


def parse_date(s, to_aware=True):
    d = None
    if _re_date.match(s):
        d = datetime.datetime.strptime(s, DATE_P_FORMAT)
    if _re_date_h.match(s):
        d = datetime.datetime.strptime(s, DATE_P_FORMAT_WITH_HH)
    if _re_date_h_m.match(s):
        d = datetime.datetime.strptime(s, DATE_P_FORMAT_WITH_HH_MM)
    if _re_date_full.match(s):
        d = datetime.datetime.strptime(s, DATE_P_FORMAT_FULL)
    if d is not None and to_aware:
        return make_aware(d)
    return d


def parse_date_next(s, to_aware=True):
    d = None
    if _re_date.match(s):
        d = datetime.datetime.strptime(s, DATE_P_FORMAT)
        d += datetime.timedelta(days=1)
        return d
    if _re_date_h.match(s):
        d = datetime.datetime.strptime(s, DATE_P_FORMAT_WITH_HH)
        d += datetime.timedelta(hours=1)
        return d
    if _re_date_h_m.match(s):
        d = datetime.datetime.strptime(s, DATE_P_FORMAT_WITH_HH_MM)
        d += datetime.timedelta(minutes=1)
        return d
    if _re_date_full.match(s):
        d = datetime.datetime.strptime(s, DATE_P_FORMAT_FULL)
        d += datetime.timedelta(seconds=1)
    if d is not None and to_aware:
        return make_aware(d)
    return d
