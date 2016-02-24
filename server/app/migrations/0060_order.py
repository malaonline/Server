import random

from django.db import migrations

from app.utils.algorithm import orderid


def create_order(apps, parent, teacher, school, grade, subject, hours, coupon):
    Order = apps.get_model('app', 'Order')

    ability = grade.ability_set.filter(subject=subject)[0]

    price = teacher.region.price_set.get(
            ability=ability, level=teacher.level).price

    discount_amount = coupon.amount if coupon is not None else 0

    total = price * hours - discount_amount

    order_id = orderid()

    order = Order.objects.create(
            parent=parent, teacher=teacher, school=school, grade=grade,
            subject=subject, price=price, hours=hours,
            total=total, coupon=coupon, order_id=order_id)

    order.save()
    return order


def add_item(apps, schema_editor):
    Parent = apps.get_model('app', 'Parent')
    Teacher = apps.get_model('app', 'Teacher')
    School = apps.get_model('app', 'School')
    Grade = apps.get_model('app', 'Grade')
    WeeklyTimeSlot = apps.get_model('app', 'WeeklyTimeSlot')

    parents = Parent.objects.all()
    teachers = list(Teacher.objects.filter(user__username__istartswith="test"))
    schools = list(School.objects.all())
    grades = list(Grade.objects.filter(leaf=True))
    weeklytimeslots = list(WeeklyTimeSlot.objects.all())

    for i, parent in enumerate(parents):
        if i % 2 == 0:
            teacher = random.choice(teachers)
            teachers.remove(teacher)
            school = random.choice(schools)
            grade = random.choice(grades)
            subject = random.choice(
                    [x.subject for x in grade.ability_set.all()])
            coupon = None
            if i % 3 == 0:
                coupons = list(parent.coupon_set.filter(used=False))
                if coupons:
                    coupon = random.choice(coupons)

            order = create_order(
                    apps=apps, parent=parent, teacher=teacher, school=school,
                    grade=grade, subject=subject,
                    hours=2 * random.randint(1, 10), coupon=coupon)
            if i % 4 == 0:
                order.status = 'p'
                order.save()
            slots = set()
            for j in range(3):
                slots.add(random.choice(weeklytimeslots))
            for slot in slots:
                order.weekly_time_slots.add(slot)


class Migration(migrations.Migration):
    dependencies = [
        ('app', '0059_auto_20160130_2118'),
    ]

    operations = [
        migrations.RunPython(add_item),
    ]
