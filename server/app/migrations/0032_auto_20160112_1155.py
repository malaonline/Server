# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0031_merge'),
    ]

    operations = [
        migrations.AlterField(
            model_name='order',
            name='coupon',
            field=models.ForeignKey(blank=True, null=True, to='app.Coupon'),
        ),
        migrations.AlterField(
            model_name='order',
            name='parent',
            field=models.ForeignKey(blank=True, null=True, to='app.Parent'),
        ),
    ]
