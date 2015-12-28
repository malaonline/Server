from django.db import migrations, models

def add_policy(apps, schema_editor):
    Policy = apps.get_model('app', 'Policy')
    policy, created = Policy.objects.get_or_create(pk=1)
    if created:
        policy.content = '<html><h1>Title</h1><p>Demo </p></html>'
        policy.save()

class Migration(migrations.Migration):

    dependencies = [
        ('app', '0019_policy'),
    ]

    operations = [
        migrations.RunPython(add_policy),
    ]
