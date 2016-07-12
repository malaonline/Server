import os
from datetime import time

from django.db import migrations


def edit_weekly_time_slot(apps, schema_editor):
    WeeklyTimeSlot = apps.get_model('app', 'WeeklyTimeSlot')

    weekly_time_slots = list(
        WeeklyTimeSlot.objects.all().order_by('weekday', 'start')
    )
    index = 0

    for weekday in range(1, 8):
        for start in ((8, 0), (10, 30), (14, 00), (16, 30), (19, 0)):
            end = (start[0] + 2, start[1])
            one_weekly_time_slot = weekly_time_slots[index]
            one_weekly_time_slot.start = time(*start)
            one_weekly_time_slot.end = time(*end)
            one_weekly_time_slot.save()
            index += 1


class Migration(migrations.Migration):
    dependencies = [
        ('app', '0059_auto_20160130_2118'),
    ]

    operations = [
        migrations.RunPython(edit_weekly_time_slot),
    ]
