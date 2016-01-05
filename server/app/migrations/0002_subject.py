
from django.db import migrations

def add_subject(apps, schema_editor):
    Subject = apps.get_model('app', 'Subject')
    subjects = ('数学', '英语', '语文',
            '物理', '化学', '地理',
            '历史', '政治', '生物',
    )
    print("添加课程类型")
    for name in subjects:
        subject = Subject(name=name)
        subject.save()
        print(" {name}".format(name=subject.name))


class Migration(migrations.Migration):
    dependencies = [
        ('app', '0001_initial'),
    ]

    operations = [
        migrations.RunPython(add_subject),
    ]
