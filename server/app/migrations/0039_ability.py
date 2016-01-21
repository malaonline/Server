import os

from django.db import migrations

def add_item(apps, schema_editor):
    Grade = apps.get_model('app', 'Grade')
    Subject = apps.get_model('app', 'Subject')
    Ability = apps.get_model('app', 'Ability')

    for grade in Grade.objects.all():
        for subject in Subject.objects.all():
            if not (('小学' in grade.name or '年级' in grade.name) and
                    subject.name in ('物理', '化学', '生物', '历史', '地理',
                        '政治')):
                ability, created = Ability.objects.get_or_create(grade=grade,
                        subject=subject)
                if created:
                    ability.save()

class Migration(migrations.Migration):
    dependencies = [
        ('app', '0038_auto_20160121_1709'),
    ]

    operations = [
        migrations.RunPython(add_item),
    ]
