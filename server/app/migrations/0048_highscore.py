import random

from django.db import migrations


def add_item(apps, schema_editor):
    Teacher = apps.get_model('app', 'Teacher')
    Highscore = apps.get_model('app', 'Highscore')

    teachers = Teacher.objects.all()

    for i, teacher in enumerate(teachers):
        if i > 20:
            break

        names = ['明明', '新新', '辉辉', '成成']
        schools = ['北京大学', '清华大学', '香港大学', '中山大学']
        for j in range(4):
            score = 80 + random.randint(0, 10) * 10
            highscore = Highscore(teacher=teacher, name=names[j],
                                  increased_scores=score,
                                  school_name='洛阳一中',
                                  admitted_to=schools[j])
            highscore.save()


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0047_photo_set'),
    ]

    operations = [
        migrations.RunPython(add_item),
    ]
