# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models

def add_user(apps, schema_editor):
    User = apps.get_model('auth', 'User')
    for i in range(50):
        username = 'test%d' % i
        email = '%s@malalaoshi.com' % username
        user, created = User.objects.get_or_create(username=username)
        if created:
            user.email = email
            user.password = '123123'
            user.save()

class Migration(migrations.Migration):

    dependencies = [
        ('app', '0016_auto_20151212_0014'),
    ]

    operations = [
        migrations.RunPython(add_user),
    ]
