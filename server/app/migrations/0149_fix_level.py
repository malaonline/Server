import os

from django.db import migrations


def add_level(apps, schema_editor):
    Level = apps.get_model('app', 'Level')
    print("修正教师等级")
    chinanums = '一二三四五六七八九十'
    for i in range(0,len(chinanums)):
        level, _ = Level.objects.get_or_create(id=i+1)
        level.name = chinanums[i]+'级'
        level.save()
        #print(" {level_name}".format(level_name=level.name))


class Migration(migrations.Migration):
    dependencies = [
        ('app', '0148_auto_20160615_1200'),
    ]

    operations = [
        migrations.RunPython(add_level),
    ]
