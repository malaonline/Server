import os

from django.db import migrations


def add_order_level(apps, schema_editor):
    Level = apps.get_model('app', 'Level')
    print("添加教师等级的OrderLevel")
    level_map = {
        "一级": 1,
        "二级": 2,
        "三级": 3,
        "四级": 4,
        "五级": 5,
        "六级": 6
    }
    for one_level in Level.objects.all():
        one_level.level_order = level_map.get(one_level.name, 0)
        one_level.save()


class Migration(migrations.Migration):
    dependencies = [
        ('app', '0108_auto_20160310_1656'),
    ]

    operations = [
        migrations.RunPython(add_order_level),
    ]
