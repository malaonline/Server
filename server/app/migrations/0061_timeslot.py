import datetime

from django.db import migrations
from django.utils import timezone


def _weekly_date_to_minutes(date):
    return date.weekday() * 24 * 60 + date.hour * 60 + date.minute


def _delta_minutes(weekly_ts, cur_min):
    return (
            (weekly_ts.weekday - 1) * 24 * 60 + weekly_ts.start.hour * 60 +
            weekly_ts.start.minute - cur_min + 7 * 24 * 60) % (7 * 24 * 60)


def add_item(apps, schema_editor):
    Order = apps.get_model('app', 'Order')
    TimeSlot = apps.get_model('app', 'TimeSlot')

    for order in Order.objects.filter(status='p'):
        weekly_time_slots = list(order.weekly_time_slots.all())
        grace_time = datetime.timedelta(days=2)  # TimeSlot.GRACE_TIME
        date = timezone.localtime(timezone.now()) + grace_time
        date = date.replace(second=0, microsecond=0)
        date += datetime.timedelta(minutes=1)

        cur_min = _weekly_date_to_minutes(date)
        weekly_time_slots.sort(
                key=lambda x: _delta_minutes(x, cur_min))

        n = len(weekly_time_slots)
        h = order.hours
        i = 0
        tss = []
        while h > 0:
            weekly_ts = weekly_time_slots[i % n]
            start = date + datetime.timedelta(
                    minutes=_delta_minutes(weekly_ts, cur_min)
                    ) + datetime.timedelta(days=7 * (i // n))

            end = start + datetime.timedelta(
                    minutes=(weekly_ts.end.hour - weekly_ts.start.hour) * 60 +
                    weekly_ts.end.minute - weekly_ts.start.minute)

            tss.append(dict(start=start, end=end))
            i = i + 1
            # for now, 1 time slot include 2 hours
            h = h - 2

        for ts in tss:
            timeslot = TimeSlot(
                    order=order, start=ts['start'], end=ts['end'])
            timeslot.save()


class Migration(migrations.Migration):
    dependencies = [
        ('app', '0060_order'),
    ]

    operations = [
        migrations.RunPython(add_item),
    ]
