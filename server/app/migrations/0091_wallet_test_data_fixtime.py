from django.db import migrations, models
import datetime
import random


def wallet_test_data_fixtime(apps, schema_editor):
    AccountHistory = apps.get_model('app', 'AccountHistory')

    accHists = list(AccountHistory.objects.filter(account__user__username__istartswith="test"))

    for ah in accHists:
        ah.submit_time = ah.submit_time + datetime.timedelta(seconds=random.randint(-24*60*60, 0))
        ah.save()

class Migration(migrations.Migration):
    dependencies = [
        ('app', '0090_wallet_test_data'),
    ]

    operations = [
        migrations.RunPython(wallet_test_data_fixtime),
    ]
