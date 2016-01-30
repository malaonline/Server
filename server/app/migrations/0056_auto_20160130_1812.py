# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0054_auto_20160128_1944'),
    ]

    operations = [
        migrations.RenameField(
            model_name='teacher',
            old_name='public',
            new_name='published',
        ),
    ]
