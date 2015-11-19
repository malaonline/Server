import os
from datetime import time

from django.db import migrations

def add_weekly_time_slot(apps, schema_editor):
    WeeklyTimeSlot = apps.get_model('app', 'WeeklyTimeSlot')

    for weekday in range(1, 8):
        for start in (8, 10, 13, 15, 17, 19):
            end = start + 2
            weekly_time_slot = WeeklyTimeSlot(weekday=weekday, start=time(start),
                    end=time(end))
            weekly_time_slot.save()

class Migration(migrations.Migration):
    dependencies = [
        ('app', '0001_initial'),
        ('app', '0002_subject'),
        ('app', '0003_region'),
        ('app', '0004_grade'),
        ('app', '0005_gradesubject'),
        ('app', '0006_level'),
        ('app', '0007_role'),
    ]

    operations = [
        migrations.RunPython(add_weekly_time_slot),
    ]
