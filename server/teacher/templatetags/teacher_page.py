# -*- coding: utf-8 -*-
from django import template

register = template.Library()


@register.inclusion_tag("teacher/tag/mala_header.html", takes_context=True)
def mala_page_header(context, title):
    return {"title": title}

