from django.contrib import admin
from django import forms
from django.utils.html import format_html_join
from django.utils.safestring import mark_safe
from django.core import urlresolvers

import app.models as m


class RegionAdmin(admin.ModelAdmin):
    search_fields = ['name']


class ParentAdmin(admin.ModelAdmin):
    readonly_fields = ('recent_orders', )
    search_fields = ['students__name', 'user__profile__phone']

    def recent_orders(self, instance):
        return format_html_join(
                '\n',
                '<li><a href="{}">{}</a></li>',
                ((urlresolvers.reverse(
                    'admin:app_order_change', args=(order.id,)), order)
                    for order in instance.recent_orders()),
                ) or mark_safe('No orders')


class OrderAdmin(admin.ModelAdmin):
    readonly_fields = ('timeslots', )
    search_fields = ['teacher__name', 'parent__student_name']
    list_filter = ['subject', 'paid_at', 'status', 'refund_status']

    def timeslots(self, instance):
        return format_html_join(
                '\n',
                '<li><a href="{}">{}</a></li>',
                ((urlresolvers.reverse(
                    'admin:app_timeslot_change', args=(ts.id,)), ts)
                    for ts in instance.timeslot_set.filter(deleted=False)),
                ) or ''


class TimeSlotAdmin(admin.ModelAdmin):
    raw_id_fields = (
            'order', 'confirmed_by', 'transferred_from', 'last_updated_by',
            'comment', 'complaint', 'attendance')
    search_fields = ['order__teacher__name', 'order__parent__student_name']
    list_display = ['__str__', 'deleted', 'suspended', 'reminded']
    list_filter = ['start']


class ProfileAdmin(admin.ModelAdmin):
    search_fields = ['phone']
    list_filter = ['gender']


class StudentAdmin(admin.ModelAdmin):
    search_fields = ['name', 'school_name']


class CheckcodeAdmin(admin.ModelAdmin):
    search_fields = ['phone']
    list_filter = ['updated_at']


class TeacherAdmin(admin.ModelAdmin):
    search_fields = ('name', 'phone')
    list_display = ('__str__', 'region', 'level', 'status',
                    'published', 'is_assistant', 'recommended_on_wechat')
    list_filter = ('status', 'degree',)


class LecturerAdmin(admin.ModelAdmin):
    search_fields = ('name', 'phone', 'user__username')
    list_display = ('__str__', 'user', 'subject')


class LiveCourseAdmin(admin.ModelAdmin):
    search_fields = ['name', 'course_no', 'grade_desc', 'lecturer__name']
    list_filter = ['subject']


class LiveClassAdmin(admin.ModelAdmin):
    search_fields = ['live_course__name', 'live_course__lecturer__name',
                     'assistant__name']
    list_filter = ['class_room', 'created_at']


class LiveCourseTimeSlotAdmin(admin.ModelAdmin):
    search_fields = ['live_course__name']
    list_filter = ['start']


class PriceConfigAdmin(admin.ModelAdmin):
    list_filter = ['school']


class SchoolIncomeRecordAdmin(admin.ModelAdmin):
    search_fields = ('school_account__school__name',
                     'school_account__account_name__name')
    list_display = ('__str__', 'status')
    list_filter = ('type',)


class SchoolMasterAdmin(admin.ModelAdmin):
    search_fields = ('name', 'user__username', 'school__name')


class StaffPermissionAdmin(admin.ModelAdmin):
    search_fields = ['allowed_url_name', 'groups__name']
    list_display = ('__str__', 'group_names')

    def group_names(self, instance):
        return ', '.join([g.name for g in instance.groups.all()])


class QuestionForm(forms.ModelForm):
    def __init__(self, *args, **kwargs):
        super(QuestionForm, self).__init__(*args, **kwargs)
        self.fields['solution'].queryset = m.QuestionOption.objects.filter(question=self.instance)


class QuestionAdmin(admin.ModelAdmin):
    form = QuestionForm
    list_display = ('__str__', 'question_options', 'solution')

    def question_options(self, instance):
        return ' , '.join([o.text for o in instance.questionoption_set.all()])


class QuestionOptionAdmin(admin.ModelAdmin):
    list_display = ('__str__', 'question')


admin.site.register(m.Region, RegionAdmin)
admin.site.register(m.School)
admin.site.register(m.Grade)
admin.site.register(m.Tag)
admin.site.register(m.Subject)
admin.site.register(m.Level)
admin.site.register(m.Price)
admin.site.register(m.Profile, ProfileAdmin)
admin.site.register(m.Teacher, TeacherAdmin)
admin.site.register(m.Highscore)
admin.site.register(m.Photo)
admin.site.register(m.Ability)
admin.site.register(m.Certificate)
admin.site.register(m.InterviewRecord)
admin.site.register(m.Account)
admin.site.register(m.BankCard)
admin.site.register(m.BankCodeInfo)
admin.site.register(m.AccountHistory)
admin.site.register(m.Feedback)
admin.site.register(m.Memberservice)
admin.site.register(m.Student, StudentAdmin)
admin.site.register(m.Parent, ParentAdmin)
admin.site.register(m.Coupon)
admin.site.register(m.WeeklyTimeSlot)
admin.site.register(m.Order, OrderAdmin)
admin.site.register(m.OrderRefundRecord)
admin.site.register(m.Charge)
admin.site.register(m.TimeSlot, TimeSlotAdmin)
admin.site.register(m.TimeSlotAttendance)
admin.site.register(m.Comment)
admin.site.register(m.Message)
admin.site.register(m.Checkcode, CheckcodeAdmin)
admin.site.register(m.LevelRecord)
admin.site.register(m.Config)
admin.site.register(m.WeiXinToken)
admin.site.register(m.Evaluation)
admin.site.register(m.Letter)
admin.site.register(m.SchoolMaster, SchoolMasterAdmin)
admin.site.register(m.SchoolAccount)
admin.site.register(m.PriceConfig, PriceConfigAdmin)
admin.site.register(m.SchoolIncomeRecord, SchoolIncomeRecordAdmin)
admin.site.register(m.StaffPermission, StaffPermissionAdmin)
admin.site.register(m.ClassRoom)
admin.site.register(m.Lecturer, LecturerAdmin)
admin.site.register(m.LiveCourseWeeklyTimeSlot)
admin.site.register(m.LiveCourse, LiveCourseAdmin)
admin.site.register(m.LiveClass, LiveClassAdmin)
admin.site.register(m.LiveCourseTimeSlot, LiveCourseTimeSlotAdmin)
admin.site.register(m.QuestionOption, QuestionOptionAdmin)
admin.site.register(m.Question, QuestionAdmin)
admin.site.register(m.QuestionGroup)
