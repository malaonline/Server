# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations
from app import models


def init_schools_prices(apps, schema_editor):
    schools = models.School.objects.all()
    for school in schools:
        school.init_prices()


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0167_auto_20160927_1647'),
    ]

    operations = [
        migrations.RunPython(init_schools_prices),
    ]
