from __future__ import absolute_import

from celery import shared_task

from django.conf import settings
# from django.utils import timezone
# from django.db.models import F
import jpush

from .models import TimeSlot, TimeSlotAttendance, Order
import logging

logger = logging.getLogger('app')


@shared_task
def autoConfirmClasses():
    operateTargets = TimeSlot.should_auto_confirmed_objects.all()
    logger.debug("target amount:%d" %(len(operateTargets)))
    for timeslot in operateTargets:
        timeslot.confirm()
        logger.debug("The Timeslot ends at %s ,was been set the attendance to %s" %(timeslot.start, timeslot.attendance))
    return True


@shared_task
def send_push(msg, user_ids=None, extras=None):
    '''
    user_ids is a list of user_id [1, 2, ...]
    if user_ids is None then send to all
    '''
    app_key = settings.JPUSH_APP_KEY
    master_secret = settings.JPUSH_MASTER_SECRET
    _jpush = jpush.JPush(app_key, master_secret)

    push = _jpush.create_push()

    ios_msg = jpush.ios(alert=msg, extras=extras)
    android_msg = jpush.android(alert=msg, extras=extras)
    push.notification = jpush.notification(
            alert=msg, android=android_msg, ios=ios_msg)
    push.platform = jpush.all_

    if user_ids is None:
        push.audience = jpush.all_
        return str(push.send())
    elif len(user_ids) > 1000:
        ans = []
        for i in range(len(user_ids) // 1000 + 1):
            ret = send_push(msg, user_ids[i * 1000: (i + 1) * 1000])
            ans.append(ret)
        return ans
    elif len(user_ids) == 0:
        return ''
    else:
        push.audience = jpush.audience(
                jpush.alias(*user_ids)
                )
        return str(push.send())


@shared_task
def autoCancelOrders():
    operateTargets = Order.objects.should_auto_canceled_objects()
    logger.debug("target amount:%d" %(len(operateTargets)))
    for order in operateTargets:
        order.status = Order.CANCELED
        order.save()
        logger.debug("The Order created at %s which order_id is %d, was been canceled automatically" %(order.created_at, order.order_id))
    return True
