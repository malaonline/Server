# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0036_school_opened'),
    ]

    operations = [
        migrations.CreateModel(
            name='Achievement',
            fields=[
                ('id', models.AutoField(serialize=False, primary_key=True, auto_created=True, verbose_name='ID')),
                ('title', models.CharField(max_length=30)),
                ('img', models.ImageField(upload_to='achievements', blank=True, null=True)),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.AddField(
            model_name='teacher',
            name='audio',
            field=models.FileField(upload_to='audio', blank=True, null=True),
        ),
        migrations.AddField(
            model_name='teacher',
            name='experience',
            field=models.PositiveSmallIntegerField(blank=True, null=True),
        ),
        migrations.AddField(
            model_name='teacher',
            name='interaction',
            field=models.PositiveSmallIntegerField(blank=True, null=True),
        ),
        migrations.AddField(
            model_name='teacher',
            name='profession',
            field=models.PositiveSmallIntegerField(blank=True, null=True),
        ),
        migrations.AddField(
            model_name='teacher',
            name='video',
            field=models.FileField(upload_to='video', blank=True, null=True),
        ),
        migrations.AddField(
            model_name='achievement',
            name='teacher',
            field=models.ForeignKey(to='app.Teacher'),
        ),
    ]
