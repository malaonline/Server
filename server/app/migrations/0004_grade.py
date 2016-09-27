import os

from django.db import migrations


def add_grade(apps, schema_editor):
    Grade = apps.get_model('app', 'Grade')
    #print("添加年级")
    p = Grade(name='小学',leaf=False)
    p.save()
    for g in ('一', '二', '三', '四', '五', '六'):
        name = '{0!s}年级'.format(g)
        t = Grade(name=name, superset=p, leaf=True)
        t.save()
        #print(" {name}".format(name=t.name))

    p = Grade(name='初中',leaf=False)
    p.save()
    for g in ('一', '二', '三'):
        name = '初{0!s}'.format(g)
        t = Grade(name=name, superset=p, leaf=True)
        t.save()
        #print(" {name}".format(name=t.name))

    p = Grade(name='高中',leaf=False)
    p.save()
    for g in ('一', '二', '三'):
        name = '高{0!s}'.format(g)
        t = Grade(name=name, superset=p, leaf=True)
        t.save()
        #print(" {name}".format(name=t.name))


class Migration(migrations.Migration):
    dependencies = [
        ('app', '0003_region'),
    ]

    operations = [
        migrations.RunPython(add_grade),
    ]
