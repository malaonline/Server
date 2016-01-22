# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0043_teacher_ability'),
    ]

    operations = [
        migrations.AddField(
            model_name='price',
            name='ability',
            field=models.ForeignKey(default=1, to='app.Ability'),
        ),
        migrations.AlterUniqueTogether(
            name='price',
            unique_together=set([('region', 'ability', 'level')]),
        ),
        migrations.RemoveField(
            model_name='price',
            name='grade',
        ),
        migrations.RemoveField(
            model_name='price',
            name='subject',
        ),
    ]
