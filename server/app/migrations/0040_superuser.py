# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
from django.conf import settings
from django.contrib.auth.hashers import make_password


def add_user(apps, schema_editor):
    User = apps.get_model('auth', 'User')
    username = 'test'
    password = 'mala-test'
    email = 'test@malalaoshi.com'

    user, created = User.objects.get_or_create(username=username)
    if created:
        user.email = email
        user.password = make_password(password, settings.PASSWORD_SALT)
        user.is_staff = True
        user.is_superuser = True
        user.save()

class Migration(migrations.Migration):

    dependencies = [
        ('app', '0039_ability'),
    ]

    operations = [
        migrations.RunPython(add_user),
    ]
