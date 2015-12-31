# -*- coding: utf-8 -*-
import os
import re

from django.core.files import File
from django.db import migrations, models
from django.conf import settings
from django.contrib.auth.hashers import make_password
from django.contrib.auth.models import Permission, Group


base_path = os.path.join(os.path.abspath(os.path.dirname(__file__)),
        'avatars')


def save_image_from_file(field, name):
    path = os.path.join(base_path, name)
    f = open(path, 'rb')
    field.save(name, File(f), save=True)


def add_parents(apps, schema_editor):
    Teacher = apps.get_model('app', 'Teacher')
    Profile = apps.get_model('app', 'Profile')
    Role = apps.get_model('app', 'Role')
    User = apps.get_model('auth', 'User')
    # Group = apps.get_model('auth', 'Group')
    # Permission = apps.get_model('auth', "Permission")
    Parent = apps.get_model('app', 'Parent')
    #
    # parent_group, group_create = Group.objects.get_or_create(name="Parents Group")
    #
    # try:
    #     change_parent = Permission.objects.get(name="Can change parent")
    #     parent_group.permision.add(change_parent)
    # except Permission.DoesNotExist:
    #     print("change parent permission does not exist")
    #
    # parent_group.save()

    parent_name_formula = settings.SAMPLE_PARENT_USER_FORMULA
    print("添加家长用户")
    # 添加用户
    for i in range(settings.SAMPLE_DATA_LENGTH):
        username = parent_name_formula.format(id=i)
        email = "{name}@malalaoshi.com".format(name=username)
        user, created = User.objects.get_or_create(username=username)
        if created:
            user.email = email
            user.password = make_password("123123", settings.PASSWORD_SALT)
            user.save()

    # 将用户赋予家长属性
    role = Role.objects.get(name='家长')
    for i in range(settings.SAMPLE_DATA_LENGTH):
        username = parent_name_formula.format(id=i)
        user = User.objects.get(username=username)
        if not hasattr(user, 'profile'):
            phone = '0001%d' % i
            profile = Profile(user=user, phone=phone, role=role)
            profile.gender = 'm' if i % 2 else 'f'
            name = 'img%d.jpg' % (i % 8)
            save_image_from_file(profile.avatar, name)
            profile.save()
            print(" {name}".format(name=username))
        if not hasattr(user, 'parent'):
            parent = Parent(user=user, student_name="student%d" % (i,))
            parent.save()
        # if parent_group not in user.groups:
        # parent_group.user_set.add(user)
        # user.groups.add(parent_group)
        # user.save()


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0020_policy'),
    ]

    operations = [
        migrations.RunPython(add_parents),
    ]
