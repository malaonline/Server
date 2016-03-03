from django.db import migrations, models
from django.utils import timezone


def confirm_some_timeslot(apps, schema_editor):
    Account = apps.get_model('app', 'Account')
    AccountHistory = apps.get_model('app', 'AccountHistory')
    TimeSlot = apps.get_model('app', 'TimeSlot')

    def _safe_get_account(teacher):
        # 获得老师账户,如果没有账户就创建一个
        try:
            account = teacher.user.account
        except AttributeError:
            account = Account(user=teacher.user)
            account.save()
        return account

    def _timeslot_hours(timeslot):
        return (timeslot.end - timeslot.start).seconds/3600

    def _confirm_timeslot(timeslot):
        """
        确认课时, 老师收入入账
        """
        teacher = timeslot.order.teacher
        account = _safe_get_account(teacher)
        amount = _timeslot_hours(timeslot) * timeslot.order.price
        amount = amount * (100 - timeslot.order.commission_percentage) // 100
        ah = AccountHistory(account=account, done=True)
        ah.amount = amount
        ah.timeslot = timeslot
        ah.submit_time = timeslot.end
        ah.save()

    passed_timeslots = TimeSlot.objects.filter(end__lt=timezone.now())

    for timeslot in passed_timeslots:
        _confirm_timeslot(timeslot)

class Migration(migrations.Migration):
    dependencies = [
        ('app', '0096_auto_20160301_2309'),
    ]

    operations = [
        migrations.RunPython(confirm_some_timeslot),
    ]
