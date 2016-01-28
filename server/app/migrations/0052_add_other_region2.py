from django.db import migrations


def add_other_region(apps, schema_editor):
    Region = apps.get_model('app', 'Region')
    # 这是一个省级的对象
    # 相当于 "其他": {}
    region = Region.objects.get(name="其它")
    region.name = "其他"
    region.save()
    print("改名其它->其他")


class Migration(migrations.Migration):
    dependencies = [
        ('app', '0051_auto_20160127_1855'),
    ]
    operations = [
        migrations.RunPython(add_other_region)
    ]
