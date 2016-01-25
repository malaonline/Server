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
    Photo = apps.get_model('app', 'Photo')

    teachers = Teacher.objects.all()

    for i, teacher in enumerate(teachers):
        if i > 20:
            break

        for j in range(3):
            photo = Photo(teacher=teacher, public=True)
            name = 'img%d.jpg' % ((i + j) % 8)
            save_image_from_file(photo.img, name)
            photo.save()
            teacher.photo_set.add(photo)


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0046_memberservice'),
    ]

    operations = [
        migrations.RunPython(add_item),
    ]
