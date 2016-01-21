# -*- coding: utf-8 -*-
from django import template

register = template.Library()


@register.inclusion_tag("teacher/tag/mala_header.html", takes_context=True)
def mala_page_header(context, title, user_name=None):
    # 如果context里有phone_name,就把user_name覆盖掉
    if "phone_name" in context:
        user_name = context.get("phone_name", user_name)
    else:
        if "user_name" in context:
            user_name = context.get("user_name", user_name)
    return {
        "title": title,
        "user_name": user_name
    }


@register.inclusion_tag("teacher/tag/mala_header_dark.html", takes_context=True)
def mala_page_header_dark(context, title, user_name=None):
    # 从context进行特别覆盖动作
    if "user_name" in context:
        user_name = context.get("user_name", user_name)
    return {
        "title": title,
        "user_name": user_name
    }
