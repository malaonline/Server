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

    chinanums = '一二三四五六七八九十'
    commission_percentages = [20,20,20,20,20,20,20,20,20,20]
    price_cfg = [2000,3000,4000,5000,6000,7000,8000,9000,10000,11000]
    region = Region.objects.get(name='许昌市')
    region.opened = True
    region.save()
    abilities = Ability.objects.all()

    # print("修正教师等级")
    for i in range(0,len(chinanums)):
        level, _ = Level.objects.get_or_create(id=i+1, defaults={'level_order': 0})
        level.name = chinanums[i]+'级'
        level.save()
        #print(" {level_name}".format(level_name=level.name))
        for ability in abilities:
            c = price_cfg[i]
            price, _ = Price.objects.get_or_create(region=region, level=level, ability=ability,
                                                   defaults={'price': c})
            price.price = c
            price.commission_percentage = commission_percentages[i]
            price.save()


class Migration(migrations.Migration):
    dependencies = [
        ('app', '0148_auto_20160615_1200'),
    ]

    operations = [
        migrations.RunPython(fix_level),
    ]
