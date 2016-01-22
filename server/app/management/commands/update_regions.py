from django.core.management.base import BaseCommand
from django.conf import settings
import json
import os
from app.models import Region


def dfs(root, deep, superset=None, leaf=True):
    if isinstance(root, dict):
        for k, v in root.items():
            s = dfs(k, deep, superset, not v)
            dfs(v, deep + 1, s)

    elif isinstance(root, list):
        for k in root:
            dfs(k, deep, superset, True)
    else:
        region, _ = Region.objects.get_or_create(name=root, superset=superset, admin_level=deep, leaf=leaf)
        region.save()
        print("{tab}{name}".format(tab="".join([" " for tab in range(deep-1)]), name=region.name))
        return region


def add_region():
    if settings.UNITTEST is True:
        data_file = "regions_for_test.json"
    else:
        data_file = "regions.txt"
    regions = json.load(open(os.path.join(os.path.dirname(__file__),
                                          data_file)))
    print("添加省份")
    dfs(regions, 1)


class Command(BaseCommand):
    help = "Update Region Infomation"

    def handle(self, *args, **options):
        add_region()
        # TODO 移除已经没有的省市,并做老师关联检查
