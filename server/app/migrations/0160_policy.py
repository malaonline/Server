# -*- coding: utf-8 -*-
import os

from django.db import migrations


BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))


def add_item(apps, schema_editor):
    Policy = apps.get_model('app', 'StaticContent')

    content = open(
        os.path.join(BASE_DIR, '../templates/teacher/doc/policy.html')).read()
    content = ''.join(content.split())
    policy, created = Policy.objects.get_or_create(name='policy')
    policy.content = content
    policy.save()

    content_wx = open(
        os.path.join(BASE_DIR,
                     '../templates/teacher/doc/policy_wx.html')).read()
    content_wx = ''.join(content_wx.split())
    policy_wx, created = Policy.objects.get_or_create(name='wxpolicy')
    policy_wx.content = content_wx
    policy_wx.save()

class Migration(migrations.Migration):

    dependencies = [
        ('app', '0159_auto_20160819_1756'),
    ]

    operations = [
        migrations.RunPython(add_item),
    ]
