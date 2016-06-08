# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations
from django.contrib.auth.hashers import make_password
from django.conf import settings


def _add_test_user_into_group(apps, test_user_format, count, group_name,
                              newUserData=None):
    Group = apps.get_model('auth', 'Group')
    User = apps.get_model('auth', 'User')

    user_group = Group.objects.get(name=group_name)
    for i in range(count):
        username = test_user_format.format(id=i)
        try:
            if newUserData:
                user, created = User.objects.get_or_create(
                        username=username, defaults=newUserData)
            else:
                user = User.objects.get(username=username)
        except User.DoesNotExist:
            print("{user} not exist".format(user=test_user_format))
            continue
        user.groups.add(user_group)


def add_test_user_into_group(apps, schema_editor):
    _add_test_user_into_group(apps, settings.SAMPLE_PARENT_USER_FORMULA,
                              settings.SAMPLE_DATA_LENGTH, '家长')

    _add_test_user_into_group(apps, settings.SAMPLE_TEACHER_USER_FORMULA,
                              settings.SAMPLE_DATA_LENGTH, '老师')

    _add_test_user_into_group(
            apps, 't_manager{id}', 10, '师资管理员',
            {'password': make_password("123", settings.PASSWORD_SALT)})


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0139_copy_student_name'),
    ]

    operations = [
        migrations.RunPython(add_test_user_into_group),
    ]
