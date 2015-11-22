
from django.db import migrations

def add_subject(apps, schema_editor):
    Subject = apps.get_model('app', 'Subject')
    subjects = ('语文', '数学', '英语',
            '物理', '化学', '生物',
            '历史', '地理', '政治',
    )
    for name in subjects:
        subject = Subject(name=name)
        subject.save()
        print(subject.name)


class Migration(migrations.Migration):
    dependencies = [
        ('app', '0001_initial'),
    ]

    operations = [
        migrations.RunPython(add_subject),
    ]
