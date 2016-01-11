# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0029_delete_role'),
    ]

    operations = [
        migrations.AddField(
            model_name='teacher',
            name='region',
            field=models.ForeignKey(to='app.Region', blank=True, null=True),
        ),
        migrations.AddField(
            model_name='teacher',
            name='status',
            field=models.IntegerField(choices=[(1, '待处理'), (2, '初选淘汰'), (3, '邀约面试'), (4, '面试通过'), (5, '面试失败')], default=1),
        ),
    ]
