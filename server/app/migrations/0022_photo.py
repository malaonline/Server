# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0021_checkcode'),
    ]

    operations = [
        migrations.CreateModel(
            name='Photo',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('img', models.ImageField(blank=True, null=True, upload_to='photos')),
                ('order', models.PositiveIntegerField(default=0)),
                ('public', models.BooleanField(default=False)),
                ('teacher', models.ForeignKey(to='app.Teacher')),
            ],
            options={
                'abstract': False,
            },
        ),
    ]
