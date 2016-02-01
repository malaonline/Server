# -*- coding: utf-8 -*-
# Generated by Django 1.9.1 on 2016-02-01 10:43
from __future__ import unicode_literals

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0063_auto_20160201_1830'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='comment',
            name='time_slot',
        ),
        migrations.AddField(
            model_name='timeslot',
            name='comment',
            field=models.OneToOneField(blank=True, null=True, on_delete=django.db.models.deletion.CASCADE, to='app.Comment'),
        ),
        migrations.AlterField(
            model_name='timeslot',
            name='attendance',
            field=models.OneToOneField(blank=True, null=True, on_delete=django.db.models.deletion.CASCADE, to='app.TimeSlotAttendance'),
        ),
        migrations.AlterField(
            model_name='timeslot',
            name='complaint',
            field=models.OneToOneField(blank=True, null=True, on_delete=django.db.models.deletion.CASCADE, to='app.TimeSlotComplaint'),
        ),
    ]
