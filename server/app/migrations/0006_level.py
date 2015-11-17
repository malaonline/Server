import os

from django.db import migrations

def add_level(apps, schema_editor):
    Level = apps.get_model('app', 'Level')

    for name in ('初级', '中级', '高级', '麻辣合伙人'):
        level = Level(name=name)
        level.save()
        print(level.name)

class Migration(migrations.Migration):
    dependencies = [
        ('app', '0001_initial'),
        ('app', '0002_subject'),
        ('app', '0003_region'),
        ('app', '0004_grade'),
        ('app', '0005_gradesubject'),
    ]

    operations = [
        migrations.RunPython(add_level),
    ]
