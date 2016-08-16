import os
import json
from collections import OrderedDict

from django.db import migrations
from django.conf import settings


def dfs(apps, root, deep, superset=None, leaf=True):
    Region = apps.get_model('app', 'Region')
    if isinstance(root, dict):
        for k, v in root.items():
            s = dfs(apps, k, deep, superset, not v)
            dfs(apps, v, deep + 1, s)

    elif isinstance(root, list):
        for k in root:
            dfs(apps, k, deep, superset, True)
    else:
        region = Region(
                name=root, superset=superset, admin_level=deep, leaf=leaf)
        region.save()
        return region


def add_region(apps, schema_editor):
    if settings.TESTING:
        data_file = "regions_for_test.json"
    else:
        data_file = "regions.txt"
    regions = json.load(open(
        os.path.join(os.path.dirname(__file__), data_file)),
        object_pairs_hook=OrderedDict)
    dfs(apps, regions, 1)


class Migration(migrations.Migration):
    dependencies = [
        ('app', '0002_subject'),
    ]

    operations = [
        migrations.RunPython(add_region),
    ]
