# -*- coding: utf-8 -*-
from django import template

register = template.Library()


@register.filter('num_range')
def num_range(value):
    return range(1, value+1)

