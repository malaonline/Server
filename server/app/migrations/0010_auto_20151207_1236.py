# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0009_auto_20151127_1826'),
    ]

    operations = [
        migrations.CreateModel(
            name='Highscore',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=200)),
                ('increased_scores', models.IntegerField(default=0)),
                ('school_name', models.CharField(max_length=300)),
                ('admitted_to', models.CharField(max_length=300)),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.AddField(
            model_name='teacher',
            name='teaching_age',
            field=models.PositiveIntegerField(default=0),
        ),
        migrations.AddField(
            model_name='highscore',
            name='teacher',
            field=models.ForeignKey(to='app.Teacher'),
        ),
    ]
