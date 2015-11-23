import os

from django.db import migrations

def add_gradesubject(apps, schema_editor):
    Grade = apps.get_model('app', 'Grade')
    Subject = apps.get_model('app', 'Subject')
    GradeSubject = apps.get_model('app', 'GradeSubject')

    for grade in Grade.objects.all():
        for subject in Subject.objects.all():
            grade_subject = GradeSubject(grade=grade, subject=subject)
            grade_subject.save()
            print(str(grade_subject))

class Migration(migrations.Migration):
    dependencies = [
        ('app', '0004_grade'),
    ]

    operations = [
        migrations.RunPython(add_gradesubject),
    ]
