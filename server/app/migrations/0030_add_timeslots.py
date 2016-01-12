import os

from django.db import migrations


def add_timeslots(apps, schema_editor):
    Region = apps.get_model('app', 'Region')
    WeeklyTimeSlot = apps.get_model('app', 'WeeklyTimeSlot')

    timeslots = list(WeeklyTimeSlot.objects.all())
    regions = Region.objects.filter(opened=True)

    for region in regions:
        region.weekly_time_slots.clear()
        for timeslot in timeslots:
            region.weekly_time_slots.add(timeslot)


class Migration(migrations.Migration):
    dependencies = [
        ('app', '0029_delete_role'),
    ]

    operations = [
        migrations.RunPython(add_timeslots),
    ]
