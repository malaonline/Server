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
    格式化金额, 默认两位小数输出.
        '+'表示如果是正数前面加'+'
        '/'表示单位为分除以100
        '0'表示不要小数输出整数
        'a'表示自动小数有则输出,没有则是整数(优先级大于0)
    """
    i = format.find('0') >= 0
    a = format.find('a') >= 0
    if amount is None or amount is '':
        return i and '0' or '0.00'
    if isinstance(amount, str):
        amount = float(amount)
    if format.find('/') >= 0:
        amount /= 100
    sign = ''
    if format.find('+') >= 0 and amount > 0:
        sign = '+'
    if a:
        return sign+"%0.9g"%amount
    elif i:
        return sign+"%.f"%amount
    else:
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


@register.simple_tag(name='menu_style', takes_context=True)
def menu_style_check(context, *args, **kwargs):
    request = context['request']
    active_style = 'menu_active'
    display_style = 'hidden'
    try:
        resolver_match = urlresolvers.resolve(request.path_info)
        url_name = resolver_match.url_name
        namespaces = resolver_match.namespaces
        for arg in args:
            tmp_path = '.'.join(namespaces)
            if len(tmp_path) > 0:
                tmp_path += ':' + url_name
            else:
                tmp_path = url_name
            s = arg.split(':')
            if len(s):
                temp_url_name = s[-1]
                for group in request.user.groups.all():
                    for staff_permission in group.staffpermission_set.all():
                        if staff_permission.allowed_url_name == 'all' \
                                or staff_permission.allowed_url_name == temp_url_name:
                            display_style = ''
            if tmp_path == arg:
                return active_style + ' ' + display_style
    except:
        return display_style

    return display_style


@register.simple_tag(name='menu_active', takes_context=True)
def menu_active(context, *args, **kwargs):
    '''
    only menu_active check, do not permissions check
    '''
    request = context['request']
    active_style = 'menu_active'
    display_style = ''
    try:
        resolver_match = urlresolvers.resolve(request.path_info)
        url_name = resolver_match.url_name
        namespaces = resolver_match.namespaces
        for arg in args:
            tmp_path = '.'.join(namespaces)
            if len(tmp_path) > 0:
                tmp_path += ':' + url_name
            else:
                tmp_path = url_name
            if tmp_path == arg:
                return active_style + ' ' + display_style
    except:
        return display_style

    return display_style
