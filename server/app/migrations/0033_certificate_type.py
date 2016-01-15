# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0032_auto_20160112_1155'),
    ]

    operations = [
        migrations.AddField(
            model_name='certificate',
            name='type',
            field=models.IntegerField(blank=True, choices=[(1, '身份证手持照'), (2, '身份证正面'), (3, '学历认证'), (4, '教师资格证'), (5, '英语水平证书'), (6, '其他资质认证')], null=True),
        ),
    ]
