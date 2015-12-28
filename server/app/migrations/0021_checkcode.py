# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0020_policy'),
    ]

    operations = [
        migrations.CreateModel(
            name='Checkcode',
            fields=[
                ('id', models.AutoField(serialize=False, auto_created=True, verbose_name='ID', primary_key=True)),
                ('phone', models.CharField(unique=True, max_length=20)),
                ('checkcode', models.CharField(max_length=30)),
                ('updated_at', models.DateTimeField(auto_now=True)),
            ],
            options={
                'abstract': False,
            },
        ),
    ]
