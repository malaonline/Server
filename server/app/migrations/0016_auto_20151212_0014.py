# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0015_auto_20151211_1203'),
    ]

    operations = [
        migrations.AlterField(
            model_name='grade',
            name='superset',
            field=models.ForeignKey(blank=True, default=None, null=True, on_delete=django.db.models.deletion.SET_NULL, related_name='subset', to='app.Grade'),
        ),
    ]
