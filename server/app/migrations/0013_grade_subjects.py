import os

from django.db import migrations

def add_subjects(apps, schema_editor):
    Grade = apps.get_model('app', 'Grade')
    Subject = apps.get_model('app', 'Subject')


    for grade in Grade.objects.all():
        grade.subjects.clear()
        for subject in Subject.objects.all():
            if not (('小学' in grade.name or '年级' in grade.name) and
                    subject.name in ('物理', '化学', '生物', '历史', '地理',
                        '政治')):
                grade.subjects.add(subject)
        grade.save()

class Migration(migrations.Migration):
    dependencies = [
        ('app', '0012_grade_subjects'),
    ]

    operations = [
        migrations.RunPython(add_subjects),
    ]
