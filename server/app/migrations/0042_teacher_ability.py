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

    teachers = Teacher.objects.all()
    subjects = list(Subject.objects.all())

    for i, teacher in enumerate(teachers):
        subject = random.choice(subjects)

        abilities = list(subject.ability_set.all())

        for j in range(3):
            ability = random.choice(abilities)
            teacher.abilities.add(ability)


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0041_policy'),
    ]

    operations = [
        migrations.RunPython(add_teacher),
    ]
