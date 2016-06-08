# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations
from django.contrib.auth.hashers import make_password
from django.core.management.sql import emit_post_migrate_signal

from app.utils import random_string

group_and_permission = [
    ("超级管理员", []),
    ("地区管理员", []),
    ("社区店管理员", []),
    ("出纳", []),
    ("客服", []),
    ("老师", []),
    ("家长", ['Can change parent', 'Can change profile']),
    ("学生", []),
    ("师资管理员", ['Can change teacher'])
]


def copy_student_name(apps, schema_editor):
    db_alias = schema_editor.connection.alias
    emit_post_migrate_signal(2, False, db_alias)

    Permission = apps.get_model('auth', 'Permission')
    Group = apps.get_model('auth', 'Group')

    global group_and_permission
    for group_name, permission_list in group_and_permission:
        new_group, group_create = Group.objects.get_or_create(name=group_name)
        new_group.save()

        for permission_name in permission_list:
            permission, _ = Permission.objects.get_or_create(
                    name=permission_name)
            permission.save()
            new_group.permissions.add(permission)

    Parent = apps.get_model('app', 'Parent')
    User = apps.get_model('auth', 'User')
    Profile = apps.get_model('app', 'Profile')
    Student = apps.get_model('app', 'Student')

    parents = Parent.objects.all()
    for parent in parents:
        username = random_string()[:30]
        salt = random_string()[:5]
        password = "malalaoshi"
        user = User(username=username)
        user.email = ""
        user.password = make_password(password, salt)
        user.save()

        student_group = Group.objects.get(name="学生")
        user.groups.add(student_group)

        profile = Profile(user=user, phone="fake" + random_string()[:16])
        profile.save()

        student = Student(name=parent.old_student_name,
                          school_name=parent.old_student_name,
                          user=user)
        student.save()


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0138_auto_20160607_1721'),
        ('contenttypes', '__latest__'),
    ]

    operations = [
        migrations.RunPython(copy_student_name),
    ]
