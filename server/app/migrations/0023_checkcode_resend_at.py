# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0022_auto_20151229_1756'),
    ]

    operations = [
        migrations.AddField(
            model_name='checkcode',
            name='resend_at',
            field=models.DateTimeField(blank=True, null=True, default=None),
        ),
    ]
