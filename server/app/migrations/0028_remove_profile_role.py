# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0027_teachers'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='profile',
            name='role',
        ),
    ]
