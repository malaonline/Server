# -*- coding: utf-8 -*-
import os
import re
import random
from django.conf import settings

from django.core.files import File
from django.db import migrations, models


def add_item(apps, schema_editor):
    Memberservice = apps.get_model('app', 'Memberservice')
    service_names = ['问题答疑', '自习陪读', '学习报告', '心理辅导']

    for name in service_names:
        service, created = Memberservice.objects.get_or_create(name=name)
        if created:
            service.detail = (name + '详情') * 5
            service.save()


class Migration(migrations.Migration):
    dependencies = [
        ('app', '0044_auto_20160122_1903'),
    ]

    operations = [
        migrations.RunPython(add_item),
    ]
