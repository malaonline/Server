from django.core.management.base import BaseCommand
from django.contrib.auth.models import User, Group
from django.contrib.auth.hashers import make_password
from django.utils.timezone import make_aware

import datetime
from dateutil.relativedelta import relativedelta

from app.models import Teacher, Order, School, Region, Profile, Parent, Grade, Subject, TimeSlot, WeeklyTimeSlot


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

    def fake_parent(self, student_name="张三"):
        """
        获得伪造的家长
        :param student_name: 学生姓名
        :return:
        """
        parent_user_name = "{student_name}_fpu".format(student_name=student_name)

        parent_name = "{student_name}的家长".format(student_name=student_name)
        parent_password = "fake parent's password"
        parent_salt = "fake parent's salt"
        parent_email = "fake-parent@email.com"
        student_school_name = "{student_name}的学校".format(student_name=student_name)

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

    def build_time_array(self, today: datetime.datetime, offset: int, duration_list: list):
        def build_date(given_date: datetime.datetime, special_time):
            return datetime.datetime(given_date.year, given_date.month, given_date.day,
                                     special_time[0], special_time[1], special_time[2])

        time_array = []
        for index, one_duration in enumerate(duration_list):
            new_day = today + relativedelta(days=+(offset + index))
            start_time = build_date(new_day, one_duration[0])
            end_time = build_date(new_day, one_duration[1])
            time_array.append([start_time, end_time])
        return time_array

    def handle(self, *args, **options):
        today = make_aware(datetime.datetime.now())
        # offset = 0
        # duration_list = [[[9, 0, 0], [11, 0, 0]], [[8, 0, 0], [9, 0, 0]], [[10, 0, 0], [13, 0, 0]],
        #                  [[15, 0, 0], [17, 0, 0]],
        #                  [[8, 0, 0], [10, 0, 0]], [[15, 0, 0], [17, 0, 0]], [[11, 0, 0], [12, 0, 0]]]*2
        duration_list = []
        for legal_time_slot in WeeklyTimeSlot.DAILY_TIME_SLOTS:
            start = legal_time_slot["start"]
            end = legal_time_slot["end"]
            duration_list.append([[start.hour, start.minute, start.second], [end.hour, end.minute, end.second]])
        # today + relativedelta(day=offset+1)
        for one_teacher in Teacher.objects.all():
            if len(Order.objects.filter(teacher=one_teacher).all()) == 0:
                if not one_teacher.abilities.all():
                    print("{name} has no ability.".format(name=one_teacher.name))
                    continue
                subject = one_teacher.abilities.all()[0].subject
                grade = one_teacher.abilities.all()[0].grade
                # 创建一个普通学生
                for student_id in range(-25, 25):
                    student_name = "普通正在上课学生{id}".format(id=student_id)

                    school = self.fake_school()
                    parent = self.fake_parent(student_name)
                    order = Order(parent=parent, teacher=one_teacher, school=school,
                                  grade=Grade.objects.get(name=grade),
                                  subject=Subject.objects.get(name=subject), coupon=None, price=200, hours=50,
                                  total=100, paid_at=make_aware(datetime.datetime.now()), status=Order.PAID)
                    order.save()
                    for start_time, end_time in self.build_time_array(today, student_id, duration_list):
                        self.add_time_slot(order, make_aware(start_time), make_aware(end_time))
                # 创建一个有退费情况的学生
                school = self.fake_school()
                parent = self.fake_parent("李四(退费学生)")

                order = Order(parent=parent, teacher=one_teacher, school=school,
                              grade=Grade.objects.get(name=grade),
                              subject=Subject.objects.get(name=subject), coupon=None, price=200, hours=50,
                              total=100,
                              paid_at=make_aware(datetime.datetime.now()), status=Order.REFUND)
                order.save()
                for start_time, end_time in self.build_time_array(today, 0, duration_list):
                    self.add_time_slot(order, make_aware(start_time), make_aware(end_time))
