# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0021_checkcode'),
    ]

    operations = [
        migrations.AddField(
            model_name='checkcode',
            name='verify_times',
            field=models.PositiveIntegerField(default=0),
        ),
        migrations.AlterField(
            model_name='checkcode',
            name='updated_at',
            field=models.DateTimeField(auto_now_add=True),
        ),
    ]
