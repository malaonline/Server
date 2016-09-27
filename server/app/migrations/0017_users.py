# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
from django.conf import settings
from django.contrib.auth.hashers import make_password


def add_user(apps, schema_editor):
    User = apps.get_model('auth', 'User')
    for i in range(settings.SAMPLE_DATA_LENGTH):
        username = 'test{0:d}'.format(i)
        email = '{0!s}@malalaoshi.com'.format(username)
        user, created = User.objects.get_or_create(username=username)
        if created:
            user.email = email
            user.password = make_password("123123", settings.PASSWORD_SALT)
            user.save()


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0016_auto_20151212_0014'),
    ]

    operations = [
        migrations.RunPython(add_user),
    ]
