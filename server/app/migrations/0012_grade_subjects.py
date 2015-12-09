# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0011_auto_20151208_1206'),
    ]

    operations = [
        migrations.AddField(
            model_name='grade',
            name='subjects',
            field=models.ManyToManyField(to='app.Subject'),
        ),
    ]
