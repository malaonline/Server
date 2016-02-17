from django.db import migrations


def set_reschduled(apps, schema_editor):
    TimeSlot = apps.get_model('app', 'TimeSlot')

    # 添加一个停课的timeslot
    timeslot1 = TimeSlot.objects.get('pk=3')
    timeslot1.deleted = True
    timeslot1.save()

    #添加一个调课的timeslot
    timeslot2 = TimeSlot.objects.get('pk=2')
    timeslot3 = TimeSlot.objects.get('pk=3')
    timeslot2.deleted = True
    timeslot3.transferred_from = timeslot2
    timeslot2.save()
    timeslot3.save()

class Migration(migrations.Migration):
    dependencies = [
        ('app', '0077_auto_20160217_1918'),
    ]

    operations = [
        migrations.RunPython(set_reschduled),
    ]
