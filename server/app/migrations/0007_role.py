import os

from django.db import migrations


def add_role(apps, schema_editor):
    Role = apps.get_model('app', 'Role')
    print("添加角色类型")
    for name in ('超级管理员', '地区管理员', '社区店管理员', '出纳', '客服',
            '老师', '家长', '学生', '师资管理员'):
        role = Role(name=name)
        role.save()
        #print(" {role_name}".format(role_name=role.name))


class Migration(migrations.Migration):
    dependencies = [
        ('app', '0006_level'),
    ]

    operations = [
        migrations.RunPython(add_role),
    ]
