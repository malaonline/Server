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


admin.site.register(m.Region, RegionAdmin)
admin.site.register(m.School)
admin.site.register(m.Grade)
admin.site.register(m.Tag)
admin.site.register(m.Subject)
admin.site.register(m.Level)
admin.site.register(m.Price)
admin.site.register(m.Profile)
admin.site.register(m.Teacher)
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
admin.site.register(m.Student)
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
admin.site.register(m.Checkcode)
admin.site.register(m.LevelRecord)
admin.site.register(m.Config)
admin.site.register(m.WeiXinToken)
admin.site.register(m.Evaluation)
admin.site.register(m.Letter)
admin.site.register(m.SchoolMaster)
admin.site.register(m.SchoolAccount)
admin.site.register(m.PriceConfig)
admin.site.register(m.SchoolIncomeRecord)
admin.site.register(m.StaffPermission)
admin.site.register(m.ClassRoom)
