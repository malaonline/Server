import os

from django.db import migrations


def add_level(apps, schema_editor):
    Level = apps.get_model('app', 'Level')
    #print("添加教师等级")
    for name in ('一级', '二级', '三级', '四级', '五级', '六级'):
        level = Level(name=name)
        level.save()
        #print(" {level_name}".format(level_name=level.name))


class Migration(migrations.Migration):
    dependencies = [
        ('app', '0004_grade'),
    ]

    operations = [
        migrations.RunPython(add_level),
    ]
