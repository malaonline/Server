# -*- coding: utf-8 -*-
from django import template

register = template.Library()


@register.inclusion_tag("teacher/tag/mala_header.html", takes_context=True)
def mala_page_header(context, title, user_name=None):
    return {
        "title": title,
        "user_name": user_name
    }

