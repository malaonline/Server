# -*- coding: utf-8 -*-
from django import template
from django.core import urlresolvers

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


_weekday_dict = {
    1: "周一",
    2: "周二",
    3: "周三",
    4: "周四",
    5: "周五",
    6: "周六",
    7: "周日",
}

@register.filter('weekday_format')
def weekday_format(weekday):
    return _weekday_dict.get(weekday, "")

@register.simple_tag(name='menu_light', takes_context=True)
def menu_light_check(context, *args, **kwargs):
    request = context['request']
    returnCss = 'menu_high_light'
    try:
        resolver_match = urlresolvers.resolve(request.path_info)
        url_name = resolver_match.url_name
        namespaces = resolver_match.namespaces
        for arg in args:
            tmpPath = ''
            for namespace in namespaces:
                if len(tmpPath) > 0:
                    tmpPath += '.' + namespace
                else:
                    tmpPath += namespace
            tmpPath += ':' + url_name
            print(tmpPath)
            print(tmpPath == arg)
            if tmpPath == arg:
                return returnCss
    except:
        return ''

    return ''
