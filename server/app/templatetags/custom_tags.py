# -*- coding: utf-8 -*-
from django import template

register = template.Library()


@register.filter('num_range')
def num_range(value, offset=1):
    if not value:
        return None
    return range(offset, value+offset)

