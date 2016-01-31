from django.db import migrations


def add_item(apps, schema_editor):
    Order = apps.get_model('app', 'Order')
    TimeSlot = apps.get_model('app', 'TimeSlot')

    for order in Order.objects.filter(status='p'):
        tss = Order.objects.allocate_timeslots(order, force=True)
        for ts in tss:
            timeslot = TimeSlot(order=order, start=ts['start'], end=ts['end'])
            timeslot.save()


class Migration(migrations.Migration):
    dependencies = [
        ('app', '0060_order'),
    ]

    operations = [
        migrations.RunPython(add_item),
    ]
