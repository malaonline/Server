# -*- coding: utf-8 -*-
from django import template

register = template.Library()


@register.filter('num_range')
def num_range(value, offset=1):
    if not value:
        return None
    return range(offset, value+offset)


@register.filter('sub_list')
def sub_list(arr, range):
    """
    arr is a list, range is 'a:b', return arr[a:b]
    """
    s = range.split(':')
    a = s[0]=='' and 0 or int(s[0])
    b = s[1]=='' and len(arr) or int(s[0])%len(arr)
    return arr[a:b]


@register.filter('money_format')
def money_format(amount, format='+/'):
    """
    格式化金额, 两位小数输出. '+'表示如果是正数前面加'+', '/'表示单位为分除以100
    """
    if amount is None or amount is '':
        return '0.00'
    if isinstance(amount, str):
        amount = float(amount)
    if format.find('/') >= 0:
        amount /= 100
    sign = ''
    if format.find('+') >= 0 and amount > 0:
        sign = '+'
    return sign+"%.2f"%amount


@register.filter('weekday_format')
def weekday_format(weekday):

    weekday_dict = {
        1: "周一",
        2: "周二",
        3: "周三",
        4: "周四",
        5: "周五",
        6: "周六",
        7: "周日"
    }

    return weekday_dict.get(weekday, "")
