# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0010_auto_20151207_1236'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='ability',
            name='level',
        ),
        migrations.RemoveField(
            model_name='teacher',
            name='active',
        ),
        migrations.AddField(
            model_name='teacher',
            name='level',
            field=models.ForeignKey(blank=True, null=True, on_delete=django.db.models.deletion.SET_NULL, to='app.Level'),
        ),
        migrations.AddField(
            model_name='teacher',
            name='public',
            field=models.BooleanField(default=False),
        ),
    ]
