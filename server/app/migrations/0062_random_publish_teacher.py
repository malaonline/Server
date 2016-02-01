import random
from django.db import migrations

def random_publish_teacher(apps, schema_editor):
    Teacher = apps.get_model('app', 'Teacher')
    teachers = Teacher.objects.filter(user__username__istartswith="test")

    for t in teachers:
        if random.randint(0,1):
            t.status = 4  # Teacher.INTERVIEW_OK
            t.published = random.randint(0,1)
            t.save()

class Migration(migrations.Migration):
    dependencies = [
        ('app', '0061_timeslot'),
    ]

    operations = [
        migrations.RunPython(random_publish_teacher),
    ]
