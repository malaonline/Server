from django.db import migrations


def add_item(apps, schema_editor):
    TimeSlot = apps.get_model('app', 'TimeSlot')

    timeslots = TimeSlot.objects.filter(start__second__gt=0)
    for ts in timeslots:
        ts.start = ts.start.replace(second=0, microsecond=0)
        ts.end = ts.end.replace(second=0, microsecond=0)
        ts.save()


class Migration(migrations.Migration):
    dependencies = [
        ('app', '0064_auto_20160201_1843'),
    ]

    operations = [
        migrations.RunPython(add_item),
    ]
