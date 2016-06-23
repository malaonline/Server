from django.db import migrations


def add_other_region(apps, schema_editor):
    Region = apps.get_model('app', 'Region')
    # 这是一个省级的对象
    # 相当于 "其它": {}
    region = Region(name="其它", superset=None, admin_level=1, leaf=True)
    region.save()
    #print("添加地区-其它")


class Migration(migrations.Migration):
    dependencies = [
        ('app', '0041_policy'),
    ]
    operations = [
        migrations.RunPython(add_other_region)
    ]
