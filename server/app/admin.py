from django.contrib import admin
from django.utils.html import format_html_join
from django.utils.safestring import mark_safe
from django.core import urlresolvers

from .models import *


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

    def timeslots(self, instance):
        return format_html_join(
                '\n',
                '<li><a href="{}">{}</a></li>',
                ((urlresolvers.reverse(
                    'admin:app_timeslot_change', args=(ts.id,)), ts)
                    for ts in instance.timeslot_set.all()),
                ) or ''


admin.site.register(Region, RegionAdmin)
admin.site.register(School)
admin.site.register(Grade)
admin.site.register(Tag)
admin.site.register(Subject)
admin.site.register(Level)
admin.site.register(Price)
admin.site.register(Profile)
admin.site.register(Teacher)
admin.site.register(Highscore)
admin.site.register(Photo)
admin.site.register(Ability)
admin.site.register(Certificate)
admin.site.register(InterviewRecord)
admin.site.register(Account)
admin.site.register(BankCard)
admin.site.register(BankCodeInfo)
admin.site.register(AccountHistory)
admin.site.register(Feedback)
admin.site.register(Memberservice)
admin.site.register(Parent, ParentAdmin)
admin.site.register(Coupon)
admin.site.register(WeeklyTimeSlot)
admin.site.register(Order, OrderAdmin)
admin.site.register(TimeSlot)
admin.site.register(Comment)
admin.site.register(Message)
