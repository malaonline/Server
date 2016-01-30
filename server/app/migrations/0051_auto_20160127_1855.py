# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0050_auto_20160127_1800'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='comment',
            name='la_degree',
        ),
        migrations.RemoveField(
            model_name='comment',
            name='ma_degree',
        ),
        migrations.AddField(
            model_name='comment',
            name='score',
            field=models.PositiveIntegerField(default=5),
            preserve_default=False,
        ),
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
