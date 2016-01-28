# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models
from django.conf import settings


class Migration(migrations.Migration):

    dependencies = [
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
        ('app', '0053_parent_student_school_name'),
    ]

    operations = [
        migrations.CreateModel(
            name='TimeSlotAttendance',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, primary_key=True, auto_created=True)),
                ('created_at', models.DateTimeField(auto_now_add=True)),
                ('last_updated_at', models.DateTimeField(auto_now=True)),
                ('record_type', models.CharField(default='a', choices=[('c', '10分钟内'), ('d', '10-30分钟'), ('e', '30分钟以上'), ('b', '缺勤'), ('a', '正常出勤')], max_length=1)),
                ('last_updated_by', models.ForeignKey(to=settings.AUTH_USER_MODEL, null=True, blank=True)),
                ('time_slot', models.ForeignKey(to='app.TimeSlot')),
            ],
            options={
                'abstract': False,
            },
        ),
        migrations.CreateModel(
            name='TimeSlotComplaint',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, primary_key=True, auto_created=True)),
                ('content', models.CharField(max_length=500)),
                ('created_at', models.DateTimeField(auto_now_add=True)),
                ('last_updated_at', models.DateTimeField(auto_now=True)),
                ('last_updated_by', models.ForeignKey(to=settings.AUTH_USER_MODEL, null=True, blank=True)),
                ('time_slot', models.ForeignKey(to='app.TimeSlot')),
            ],
            options={
                'abstract': False,
            },
        ),
    ]
