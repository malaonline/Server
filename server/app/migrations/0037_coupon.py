import os
from datetime import time

from django.utils import timezone
from django.db import migrations

def add_item(apps, schema_editor):
    Coupon = apps.get_model('app', 'Coupon')
    Parent = apps.get_model('app', 'Parent')

    parents = Parent.objects.all()

    for i, parent in enumerate(parents):
        coupon = Coupon(parent=parent, name='新生奖学金', amount=120,
                expired_at=timezone.now(), used=i % 7 == 0)
        coupon.save()
        if i % 2 == 0:
            coupon = Coupon(parent=parent, name='优惠奖学金', amount=100,
                    expired_at=timezone.now(), used=i % 3 == 0)
            coupon.save()

class Migration(migrations.Migration):
    dependencies = [
        ('app', '0036_school_opened'),
    ]

    operations = [
        migrations.RunPython(add_item),
    ]
