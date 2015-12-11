# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0013_grade_subjects'),
    ]

    operations = [
        migrations.CreateModel(
            name='Memberservice',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=30)),
                ('detail', models.CharField(max_length=1000)),
            ],
            options={
                'abstract': False,
            },
        ),
    ]
