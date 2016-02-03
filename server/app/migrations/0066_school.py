from django.db import migrations


def set_opened(apps, schema_editor):
    School = apps.get_model('app', 'School')

    schools = School.objects.all().order_by('id')
    lth = len(schools) - 1
    for index, sc in enumerate(schools):
        if index < lth:
            sc.opened = True
            sc.save()


class Migration(migrations.Migration):
    dependencies = [
        ('app', '0065_timeslot'),
    ]

    operations = [
        migrations.RunPython(set_opened),
    ]
