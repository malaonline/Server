# -*- coding: utf-8 -*-
import os
import re
from django.conf import settings

from django.core.files import File
from django.db import migrations, models

base_path = os.path.join(os.path.abspath(os.path.dirname(__file__)),
        'thumbnails')

def save_image_from_file(field, name):
    path = os.path.join(base_path, name)
    f = open(path, 'rb')
    field.save(name, File(f), save=True)


def add_teacher(apps, schema_editor):
    Tag = apps.get_model('app', 'Tag')
    Teacher = apps.get_model('app', 'Teacher')
    Profile = apps.get_model('app', 'Profile')
    User = apps.get_model('auth', 'User')
    Level = apps.get_model('app', 'Level')
    Region = apps.get_model('app', 'Region')
    School = apps.get_model('app', 'School')
    WeeklyTimeSlot = apps.get_model('app', 'WeeklyTimeSlot')

    #print('开放洛阳市')
    region = Region.objects.get(name='洛阳市')
    region.opened = True
    region.save()

    #print('添加学校')
    school = School(name='洛阳中心店', address='南京路1号', region=region,
            center=True, longitude=900, latitude=900)
    name = 'img0.jpg'
    save_image_from_file(school.thumbnail, name)
    school.save()

    school = School(name='洛阳社区一店', address='南京路21号', region=region,
            center=False, longitude=910, latitude=920)
    name = 'img1.jpg'
    save_image_from_file(school.thumbnail, name)
    school.save()

    school = School(name='洛阳社区二店', address='南京路89号', region=region,
            center=False, longitude=990, latitude=980)
    name = 'img2.jpg'
    save_image_from_file(school.thumbnail, name)
    school.save()

    school = School(name='洛阳社区三店', address='南京路121号', region=region,
            center=False, longitude=710, latitude=820)
    name = 'img3.jpg'
    save_image_from_file(school.thumbnail, name)
    school.save()

    #print('添加风格标记')
    tags = ['赏识教育', '100%进步率', '学员过千', '押题达人', '奥赛教练',
            '幽默风趣', '心理专家', '讲解生动', '公立学校老师', '最受学生欢迎',
            '80后名师', '英语演讲冠军',]
    for tag_name in tags:
        tag, created = Tag.objects.get_or_create(name=tag_name)
        if created:
            tag.save()

    #print("添加老师用户")
    levels = list(Level.objects.all())
    tags = list(Tag.objects.all())
    names = '赵钱孙李周吴郑王冯陈褚卫蒋沈韩杨'
    schools = list(School.objects.all())
    timeslots = list(WeeklyTimeSlot.objects.all())

    for i in range(settings.SAMPLE_DATA_LENGTH):
        username = 'test%d' % i
        user = User.objects.get(username=username)
        if not hasattr(user, 'teacher'):
            name = names[i % len(names)] + '老师'
            level = levels[i % len(levels)]

            teacher = Teacher(user=user, name=name, degree='h', public=True,
                    level = level)

            teacher.save()
            #print(" {name}".format(name=username))

            teacher.tags.add(tags[i % len(tags)])
            teacher.tags.add(tags[(i + 3) % len(tags)])

            for school in schools:
                teacher.schools.add(school)

            for timeslot in timeslots:
                teacher.weekly_time_slots.add(timeslot)


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0026_parents'),
    ]

    operations = [
        migrations.RunPython(add_teacher),
    ]
