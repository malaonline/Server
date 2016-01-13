import os
from datetime import time

from django.db import migrations

def add_weekly_time_slot(apps, schema_editor):
    WeeklyTimeSlot = apps.get_model('app', 'WeeklyTimeSlot')

    for weekday in range(1, 8):
        for start in ((8, 0), (10, 10), (13, 30), (15, 40), (19, 0)):
            end = (start[0] + 2, start[1])
            weekly_time_slot = WeeklyTimeSlot(weekday=weekday, start=time(*start),
                    end=time(*end))
            weekly_time_slot.save()

class Migration(migrations.Migration):
    dependencies = [
        ('app', '0007_role'),
    ]

    operations = [
        migrations.RunPython(add_weekly_time_slot),
    ]
