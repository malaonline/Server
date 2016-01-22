# -*- coding: utf-8 -*-
import os

from django.db import migrations, models
from django.conf import settings
from django.contrib.auth.hashers import make_password


BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

def add_item(apps, schema_editor):
    Policy = apps.get_model('app', 'Policy')

    content = open(os.path.join(BASE_DIR, '../templates/teacher/doc/policy.html')).read()
    content = ''.join(content.split())
    policy, created = Policy.objects.get_or_create(pk=1)
    policy.content = content
    policy.save()

class Migration(migrations.Migration):

    dependencies = [
        ('app', '0040_superuser'),
    ]

    operations = [
        migrations.RunPython(add_item),
    ]
