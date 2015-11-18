import os
from datetime import time

from django.db import migrations

def add_timetable(apps, schema_editor):
    TimeTable = apps.get_model('app', 'TimeTable')

    for weekday in range(1, 8):
        for start in (8, 10, 13, 15, 17, 19):
            end = start + 2
            time_table = TimeTable(weekday=weekday, start=time(start),
                    end=time(end))
            time_table.save()

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
        migrations.RunPython(add_timetable),
    ]
