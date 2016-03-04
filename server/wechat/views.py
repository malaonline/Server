import  logging

# django modules
from django.shortcuts import render, redirect, get_object_or_404
from django.views.generic import View, TemplateView, ListView, DetailView
from django.db.models import Q,Count
from django.utils import timezone


# local modules
from app import models
# Create your views here.


class TeachersView(ListView):
    model = models.Teacher
    context_object_name = 'teacher_list'
    template_name = 'wechat/teacher/teachers.html'

    def get_queryset(self):
        teacher_list = self.model.objects.filter(
            recommended_on_wechat=True
         ).filter(
            published=True
        )
        return teacher_list

class TeacherDetailView(DetailView):
    model = models.Teacher

class SchoolsView(ListView):
    model = models.School
    context_object_name = 'school_list'
    template_name = 'wechat/school/schools.html'

    def get_queryset(self):
        school_list=self.model.objects.annotate(num_photos=Count('schoolphoto'))
        queryset = {}
        queryset['expr_center_list'] = school_list.filter(
            center = True
         )
        queryset['community_center_list'] = school_list.filter(
            center=False
        )
        return queryset

class SchoolDetailView(ListView):
    models = models.School


class CourseChoosingView(TemplateView):
    template_name = 'wechat/order/course_choosing.html'

    def get_context_data(self, teacher_id=None, **kwargs):
        teacher = get_object_or_404(models.Teacher, pk=teacher_id)
        kwargs['teacher'] = teacher
        current_user = self.request.user
        kwargs['current_user'] = current_user
        if not current_user.is_anonymous():
            try:
                parent = models.Parent.objects.get(user=current_user)
            except models.Parent.DoesNotExist:
                parent = None
            kwargs['parent'] = parent
        first_buy = True
        kwargs['first_buy'] = first_buy
        abilities = teacher.abilities.all()
        kwargs['abilities'] = abilities
        prices = teacher.prices()
        kwargs['prices'] = prices
        schools = teacher.schools
        kwargs['schools'] = schools.all()
        kwargs['daily_time_slots'] = models.WeeklyTimeSlot.DAILY_TIME_SLOTS
        # now = timezone.now()
        # kwargs['server_now'] = now

        # if current_user.parent:
        #     coupons = models.Coupon.objects.filter(parent=current_user.parent,
        #         validated_start__lte=now, expired_at__gt=now, used=False
        #     ).order_by('-amount', 'expired_at')
        #     kwargs['coupon'] = coupons.first()

        return super(CourseChoosingView, self).get_context_data(**kwargs)

