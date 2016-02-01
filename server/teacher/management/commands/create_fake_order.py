from django.core.management.base import BaseCommand
from django.contrib.auth.models import User, Group
# import django.contrib.auth.models.DoesNotExist
from django.contrib.auth.hashers import make_password
from django.utils.timezone import make_aware

import datetime

from app.models import Teacher, Order, School, Region, Profile, Parent, Grade, Subject, TimeSlot


class Command(BaseCommand):
    help = "Add fake order to exists teacher"

    def fake_school(self):
        """
        获得一个伪造的学校
        :return: 伪造的学校
        """
        school_name = "逗比中学"
        school_address = "逗比路"
        try:
            school = School.objects.get(name=school_name)
        except School.DoesNotExist:
            school = School(name=school_name,
                            address=school_address, region=Region.objects.get(name="其他"),
                            center=True, longitude=0, latitude=0, opened=False)
            school.save()
        return school

    def fake_parent(self):
        """
        获得伪造的家长
        :return:
        """
        parent_user_name = "fake_parent_username"

        parent_name = "张三的家长"
        parent_password = "fake parent's password"
        parent_salt = "fake parent's salt"
        parent_email = "fake-parent@email.com"
        student_name = "张三"
        student_school_name = "张三的学校"

        try:
            parent_user = User.objects.get(username=parent_user_name)
        except User.DoesNotExist:
            parent_user = User(username=parent_user_name)
            parent_user.password = make_password(parent_password, parent_salt)
            parent_user.email = parent_email
            parent_user.save()
            parent_group = Group.objects.get(name="家长")
            parent_user.groups.add(parent_group)
            parent_group.save()
        try:
            parent_profile = Profile.objects.get(user=parent_user)
        except Profile.DoesNotExist:
            parent_profile = Profile(user=parent_user, phone="fake_phone_number")
            parent_profile.save()
        try:
            parent = Parent.objects.get(user=parent_user)
        except Parent.DoesNotExist:
            parent = Parent(user=parent_user, student_name=student_name, student_school_name=student_school_name)
            parent.save()
        return parent

    def add_time_slot(self, order, start, end):
        one_time_slot = TimeSlot(order=order, start=start, end=end)
        one_time_slot.save()

    def handle(self, *args, **options):
        for one_teacher in Teacher.objects.all():
            if len(Order.objects.filter(teacher=one_teacher).all()) == 0:
                school = self.fake_school()
                parent = self.fake_parent()
                order = Order(parent=parent, teacher=one_teacher, school=school, grade=Grade.objects.get(name="一年级"),
                              subject=Subject.objects.get(name="数学"), coupon=None, price=200, hours=50, total=100,
                              paid_at=make_aware(datetime.datetime.now()), status=Order.PAID)
                order.save()

                self.add_time_slot(order, make_aware(datetime.datetime(2016, 2, 2, 9, 0, 0)),
                                   make_aware(datetime.datetime(2016, 2, 2, 11, 0, 0)))
                self.add_time_slot(order, make_aware(datetime.datetime(2016, 2, 1, 8, 0, 0)),
                                   make_aware(datetime.datetime(2016, 2, 1, 9, 0, 0)))
                self.add_time_slot(order, make_aware(datetime.datetime(2016, 2, 1, 10, 0, 0)),
                                   make_aware(datetime.datetime(2016, 2, 1, 13, 0, 0)))
                self.add_time_slot(order, make_aware(datetime.datetime(2016, 2, 1, 15, 0, 0)),
                                   make_aware(datetime.datetime(2016, 2, 1, 17, 0, 0)))
                self.add_time_slot(order, make_aware(datetime.datetime(2016, 1, 1, 8, 0, 0)),
                                   make_aware(datetime.datetime(2016, 1, 1, 10, 0, 0)))
                self.add_time_slot(order, make_aware(datetime.datetime(2015, 12, 30, 15, 0, 0)),
                                   make_aware(datetime.datetime(2015, 12, 30, 17, 0, 0)))
                self.add_time_slot(order, make_aware(datetime.datetime(2015, 12, 20, 11, 0, 0)),
                                   make_aware(datetime.datetime(2015, 12, 20, 12, 0, 0)))
