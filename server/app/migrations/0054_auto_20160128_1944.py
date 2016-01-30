# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0053_auto_20160128_1943'),
    ]

    operations = [
        migrations.RenameField(
            model_name='timeslot',
            old_name='timeSlotAttendance',
            new_name='attendance',
        ),
        migrations.RenameField(
            model_name='timeslot',
            old_name='timeSlotComplaint',
            new_name='complaint',
        ),
    ]
