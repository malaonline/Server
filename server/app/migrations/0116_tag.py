# -*- coding: utf-8 -*-
import os
import re
from django.conf import settings

from django.core.files import File
from django.db import migrations, models

def add_tags(apps, schema_editor):
    Tag = apps.get_model('app', 'Tag')
    print('添加风格标记')
    tags = ['赏识教育', '100%进步率', '学员过千', '押题达人', '奥赛教练',
            '幽默风趣', '心理专家', '讲解生动', '公立学校老师', '最受学生欢迎',
            '80后名师', '英语演讲冠军',]
    for i, tag_name in enumerate(tags):
        tag, created = Tag.objects.get_or_create(pk=i + 1)
        tag.name = tag_name
        tag.save()


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0115_auto_20160314_1819'),
    ]

    operations = [
        migrations.RunPython(add_tags),
    ]
