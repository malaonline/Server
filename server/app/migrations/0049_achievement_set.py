import os

from django.core.files import File
from django.db import migrations

base_path = os.path.join(os.path.abspath(os.path.dirname(__file__)),
                         'avatars')


def save_image_from_file(field, name):
    path = os.path.join(base_path, name)
    f = open(path, 'rb')
    field.save(name, File(f), save=True)


def add_item(apps, schema_editor):
    Teacher = apps.get_model('app', 'Teacher')
    Achievement = apps.get_model('app', 'Achievement')

    teachers = Teacher.objects.all()

    for i, teacher in enumerate(teachers):
        if i > 20:
            break

        titles = ['特级教师', '全国奥数总冠军']
        for j in range(2):
            achievement = Achievement(teacher=teacher, title=titles[j])
            name = 'img{0:d}.jpg'.format(((i + j) % 8))
            save_image_from_file(achievement.img, name)
            achievement.save()


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0048_highscore'),
    ]

    operations = [
        migrations.RunPython(add_item),
    ]
