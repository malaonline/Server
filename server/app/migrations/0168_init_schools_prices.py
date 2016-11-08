# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import migrations
from app import models


def init_schools_prices(apps, schema_editor):
    Level = apps.get_model('app', 'Level')
    Grade = apps.get_model('app', 'Grade')
    School = apps.get_model('app', 'School')
    PriceConfig = apps.get_model('app', 'PriceConfig')

    def init_prices(self):
        if not self.priceconfig_set.exists():
            # using yuan for better look
            configs = [
                ('一年级', [170, 165, 160, 150]),
                ('二年级', [170, 165, 160, 150]),
                ('三年级', [180, 175, 170, 160]),
                ('四年级', [180, 175, 170, 160]),
                ('五年级', [180, 175, 170, 160]),
                ('六年级', [200, 195, 190, 180]),
                ('初一', [200, 195, 190, 180]),
                ('初二', [210, 200, 190, 180]),
                ('初三', [230, 220, 200, 190]),
                ('高一', [240, 235, 230, 220]),
                ('高二', [255, 250, 240, 235]),
                ('高三', [270, 265, 260, 250]),
            ]
            hours_ranges = [(1, 10), (11, 20), (21, 50), (51, 100)]
            price_configs = []
            for level_id in range(1, 10+1):
                # each level's price delta, 10 yuan
                price_delta = (level_id-1)*10
                level = Level.objects.get(pk=level_id)
                for config in configs:
                    grade_name = config[0]
                    grade = Grade.objects.get(name=grade_name)
                    prices = config[1]
                    for index, hours_range in enumerate(hours_ranges):
                        min_hours = hours_range[0]
                        max_hours = hours_range[1]
                        # real price is in fen unit
                        price = (prices[index] + price_delta)*100
                        price_config = PriceConfig(school_id=self.id, level=level,
                                                   grade=grade,
                                                   min_hours=min_hours,
                                                   max_hours=max_hours,
                                                   price=price, )
                        price_configs.append(price_config)
            PriceConfig.objects.bulk_create(price_configs)

    schools = School.objects.all()
    for school in schools:
        init_prices(school)


class Migration(migrations.Migration):

    dependencies = [
        ('app', '0167_auto_20160927_1647'),
    ]

    operations = [
        migrations.RunPython(init_schools_prices),
    ]
