# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0052_add_other_region2'),
    ]

    operations = [
        migrations.AddField(
            model_name='parent',
            name='student_school_name',
            field=models.CharField(default='', max_length=100),
        ),
    ]
