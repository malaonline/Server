from django.db import migrations, models
from django.utils import timezone


def confirm_some_timeslot(apps, schema_editor):
    Ability = apps.get_model('app', 'Ability')
    Price = apps.get_model('app', 'Price')
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
        ability = Ability.objects.get(grade=timeslot.order.grade, subject=timeslot.order.subject)
        price_obj = Price.objects.get(region=teacher.region, ability=ability, level=teacher.level)
        amount = _timeslot_hours(timeslot) * price_obj.price
        amount = amount * (100 - price_obj.commission_percentage) // 100
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
