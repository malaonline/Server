# -*- coding: utf-8 -*-
# Generated by Django 1.9.5 on 2016-06-13 09:13
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0143_auto_20160608_1715'),
    ]

    operations = [
        migrations.AddField(
            model_name='profile',
            name='klx_username',
            field=models.CharField(blank=True, default=None, max_length=255, null=True),
        ),
    ]