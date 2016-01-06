# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0028_remove_profile_role'),
    ]

    operations = [
        migrations.DeleteModel(
            name='Role',
        ),
    ]
