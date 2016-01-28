# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0052_timeslotattendance_timeslotcomplaint'),
    ]

    operations = [
        migrations.RemoveField(
            model_name='timeslotattendance',
            name='time_slot',
        ),
        migrations.RemoveField(
            model_name='timeslotcomplaint',
            name='time_slot',
        ),
        migrations.AddField(
            model_name='timeslot',
            name='timeSlotAttendance',
            field=models.ForeignKey(blank=True, null=True, to='app.TimeSlotAttendance'),
        ),
        migrations.AddField(
            model_name='timeslot',
            name='timeSlotComplaint',
            field=models.ForeignKey(blank=True, null=True, to='app.TimeSlotComplaint'),
        ),
    ]
