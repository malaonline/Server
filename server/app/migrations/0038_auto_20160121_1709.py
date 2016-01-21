# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0037_auto_20160121_1227'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='grade',
            name='subjects',
        ),
        migrations.AddField(
            model_name='teacher',
            name='abilities',
            field=models.ManyToManyField(to='app.Ability'),
        ),
        migrations.AlterUniqueTogether(
            name='ability',
            unique_together=set([('grade', 'subject')]),
        ),
        migrations.RemoveField(
            model_name='ability',
            name='teacher',
        ),
    ]
