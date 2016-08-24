
from django.db import migrations


def fix_teacher_schools(apps, schema_editor):
    Teacher = apps.get_model('app', 'Teacher')
    School = apps.get_model('app', 'School')

    teachers = Teacher.objects.all()

    # 如果老师没有关联学校, 就把她所在地区的所有学校和她关联
    for t in teachers:
        if t.schools.count() == 0 and t.region is not None:
            schools = School.objects.filter(region=t.region)
            if len(schools) == 0:
                continue
            for s in schools:
                t.schools.add(s)
            t.save()


class Migration(migrations.Migration):
    dependencies = [
        ('app', '0159_auto_20160819_1756'),
    ]

    operations = [
        migrations.RunPython(fix_teacher_schools),
    ]
