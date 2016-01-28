# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0049_achievement_set'),
    ]

    operations = [
        migrations.AlterField(
            model_name='order',
            name='paid_at',
            field=models.DateTimeField(blank=True, null=True),
        ),
        migrations.AlterField(
            model_name='order',
            name='status',
            field=models.CharField(choices=[('u', '待付款'), ('p', '已付款'), ('d', '已取消'), ('n', '没出现'), ('c', '已确认')], default='u', max_length=2),
        ),
    ]
