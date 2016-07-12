import os
import random

from django.db import migrations


def fix_level(apps, schema_editor):
    Level = apps.get_model('app', 'Level')
    Subject = apps.get_model('app', 'Subject')
    Grade = apps.get_model('app', 'Grade')
    Ability = apps.get_model('app', 'Ability')
    Price = apps.get_model('app', 'Price')
    Region = apps.get_model('app', 'Region')

    level_id = 10                  # 十级
    new_commission_percentage = 0  # 无佣金
    new_price = 99*100             # 99元/时

    region = Region.objects.get(name='郑州市')
    abilities = Ability.objects.all()

    for ability in abilities:
        price, _ = Price.objects.get_or_create(region=region, level_id=level_id, ability=ability,
                                               defaults={'price': new_price})
        price.price = new_price
        price.commission_percentage = new_commission_percentage
        price.save()


class Migration(migrations.Migration):
    dependencies = [
        ('app', '0150_evaluation_reminded'),
    ]

    operations = [
        migrations.RunPython(fix_level),
    ]
