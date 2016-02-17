# -*- coding: utf-8 -*-
from django import template

register = template.Library()


@register.filter('num_range')
def num_range(value, offset=1):
    if not value:
        return None
    return range(offset, value+offset)


@register.filter('money_format')
def money_format(amount, format='+/'):
    """
    格式化金额, 两位小数输出. '+'表示如果是正数前面加'+', '/'表示单位为分除以100
    """
    if amount is None:
        return '0.00'
    if isinstance(amount, str):
        amount = float(amount)
    if format.find('/') >= 0:
        amount /= 100
    sign = ''
    if format.find('+') >= 0 and amount > 0:
        sign = '+'
    return sign+"%.2f"%amount
