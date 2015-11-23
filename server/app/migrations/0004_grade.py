import os

from django.db import migrations

def add_grade(apps, schema_editor):
    Grade = apps.get_model('app', 'Grade')

    p = Grade(name='小学',leaf=False)
    p.save()
    for g in ('一', '二', '三', '四', '五', '六'):
        name = '%s年级' % g
        t = Grade(name=name, superset=p, leaf=True)
        t.save()
        print(t.name)

    p = Grade(name='初中',leaf=False)
    p.save()
    for g in ('一', '二', '三', '四'):
        name = '初%s' % g
        t = Grade(name=name, superset=p, leaf=True)
        t.save()
        print(t.name)

    p = Grade(name='高中',leaf=False)
    p.save()
    for g in ('一', '二', '三'):
        name = '高%s' % g
        t = Grade(name=name, superset=p, leaf=True)
        t.save()
        print(t.name)


class Migration(migrations.Migration):
    dependencies = [
        ('app', '0003_region'),
    ]

    operations = [
        migrations.RunPython(add_grade),
    ]
