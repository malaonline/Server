from django.db import migrations, models
import random


def add_wallet_test_data(apps, schema_editor):
    Teacher = apps.get_model('app', 'Teacher')
    Account = apps.get_model('app', 'Account')
    AccountHistory = apps.get_model('app', 'AccountHistory')

    def safe_get_account(teacher):
        # 获得老师账户,如果没有账户就创建一个
        try:
            account = teacher.user.account
        except AttributeError:
            account = Account(user=teacher.user)
            account.save()
        return account

    teachers = list(Teacher.objects.filter(user__username__istartswith="test"))

    for teacher in teachers:
        account = safe_get_account(teacher)
        num = random.randint(0,21)
        for i in range(num):
            if i==0:
                ah = AccountHistory(account=account)
                ah.amount = random.randint(1000,20000)
                ah.done = True
                ah.save()
                continue
            ah = AccountHistory(account=account)
            ah.amount = random.randint(-100,100)
            ah.done = True
            ah.save()

class Migration(migrations.Migration):
    dependencies = [
        ('app', '0089_auto_20160224_1449'),
    ]

    operations = [
        migrations.AlterField(
            model_name='accounthistory',
            name='submit_time',
            field=models.DateTimeField(auto_now_add=True),
        ),
        migrations.RunPython(add_wallet_test_data),
    ]
