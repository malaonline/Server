# -*- coding: utf-8 -*-
import os
import re
import random
from django.conf import settings

from django.core.files import File
from django.db import migrations, models


def add_teacher(apps, schema_editor):
    Teacher = apps.get_model('app', 'Teacher')
    Ability = apps.get_model('app', 'Ability')
    Subject = apps.get_model('app', 'Subject')
    Price = apps.get_model('app', 'Price')
    Level = apps.get_model('app', 'Level')
    Region = apps.get_model('app', 'Region')

    region = Region.objects.get(name='洛阳市')
    levels = list(Level.objects.all())
    abilities = Ability.objects.all()

    teachers = Teacher.objects.all()
    for teacher in teachers:
        teacher.region = region
        teacher.save()

    for i, level in enumerate(levels):
        p = 100 + i * 30
        for ability in abilities:
            c = p + random.randint(0, 10) * 10
            price = Price(region=region, ability=ability, level=level, price=c)
            price.save()

class Migration(migrations.Migration):

    dependencies = [
        ('app', '0044_auto_20160122_1903'),
    ]

    operations = [
        migrations.RunPython(add_teacher),
    ]
