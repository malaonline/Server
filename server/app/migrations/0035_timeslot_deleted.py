# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0034_order_order_id'),
    ]

    operations = [
        migrations.AddField(
            model_name='timeslot',
            name='deleted',
            field=models.BooleanField(default=False),
        ),
    ]
