
from django.db import migrations


def fix_level(apps, schema_editor):
    Level = apps.get_model('app', 'Level')

    levels = Level.objects.all()

    for l in levels:
        l.level_order = l.id
        l.save()


class Migration(migrations.Migration):
    dependencies = [
        ('app', '0152_merge'),
    ]

    operations = [
        migrations.RunPython(fix_level),
    ]
