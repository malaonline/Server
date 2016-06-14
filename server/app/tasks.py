from __future__ import absolute_import

import datetime
import logging

from django.conf import settings
from django.utils import timezone
# from django.utils import timezone
# from django.db.models import F
import jpush

from celery import shared_task
from .models import TimeSlot, TimeSlotAttendance, Order, Teacher
from .utils.klx_api import klx_reg_student, klx_reg_teacher, klx_relation

logger = logging.getLogger('app')


@shared_task
def autoConfirmClasses():
    operateTargets = TimeSlot.should_auto_confirmed_objects.all()
    logger.debug("target amount:%d" %(len(operateTargets)))
    user_ids = []
    for timeslot in operateTargets:
        timeslot.confirm()
        logger.debug("The Timeslot ends at %s ,was been set the attendance to %s" %(timeslot.start, timeslot.attendance))
        user_ids.append(timeslot.order.parent.user_id)
    # JPush 通知
    extras = {
        "type": "3",  # 完课评价
        "code": None
    }
    send_push.delay("您有课程已完成, 去评价>>", title="完课评价", user_ids=user_ids, extras=extras)
    return True


@shared_task
def autoRemindClasses():
    # JPush 通知
    extras = {
        "type": "4",  # 课前通知
        "code": None
    }
    remind_time = timezone.now() + TimeSlot.REMIND_TIME
    targets = TimeSlot.objects.filter(deleted=False, start__lt=remind_time, reminded=False)
    for timeslot in targets:
        user_ids = [timeslot.order.parent.user_id]
        msg = "您在%s-%s有一节%s课，记得准时参加哦>>" % (
            timeslot.start.astimezone().time().strftime("%H:%M"),
            timeslot.end.astimezone().time().strftime("%H:%M"),
            timeslot.subject.name
        )
        send_push.delay(msg, title="课前通知", user_ids=user_ids, extras=extras)
        # 标记为已推送
        timeslot.reminded = True
        timeslot.save()


@shared_task
def send_push(msg, user_ids=None, extras=None, title=None):
    '''
    user_ids is a list of user_id [1, 2, ...]
    if user_ids is None then send to all
    '''
    app_key = settings.JPUSH_APP_KEY
    master_secret = settings.JPUSH_MASTER_SECRET
    _jpush = jpush.JPush(app_key, master_secret)

    push = _jpush.create_push()

    ios_msg = jpush.ios(alert=msg, extras=extras)
    android_msg = jpush.android(alert=msg, extras=extras, title=title)
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
    logger.debug("estimated target amount:%d" %(len(operateTargets)))
    count = 0
    for order in operateTargets:
        if Order.objects.filter(pk=order.id, status=Order.PENDING).update(status=Order.CANCELED):
            order.cancel()
            count += 1
            logger.debug("The Order created at %s which order_id is %s, was been canceled automatically" %(order.created_at, order.order_id))
    logger.debug("effected target amount:%d" % (len(operateTargets)))
    return True


@shared_task
def autoAddTeacherTeachingAge():
    cpmStartDate = timezone.now()
    try:
        cpmStartDate = cpmStartDate.replace(year=int(cpmStartDate.strftime("%Y"))-1, hour=0, minute=0, second=0, microsecond=0)
    except:
        cpmStartDate -= datetime.timedelta(days=1)
        cpmStartDate = cpmStartDate.replace(year=int(cpmStartDate.strftime("%Y"))-1, hour=0, minute=0, second=0, microsecond=0)

    # cpmStartDate = cpmStartDate.replace(year=2015, month=12, day=31, hour=0, minute=0, second=0, microsecond=0)

    cpmEndDate = cpmStartDate.replace(hour=23, minute=59, second=59, microsecond=999999)

    tempAll = Teacher.objects.filter(user__date_joined__gte=cpmStartDate, user__date_joined__lte=cpmEndDate)
    for teacher in tempAll:
        teacher.teaching_age += 1
        teacher.save()

    return True;


@shared_task
def registerKuaiLeXueUserByOrder(oid):
    '''
    注册快乐学用户, 订单付款后, 把学生和老师注册到快乐学, 并关联师生关系
    :param oid: models.Order.id
    :return: True or not
    '''
    order = Order.objects.get(oid)
    parent = order.parent
    teacher = order.teacher
    klx_stu_name = klx_reg_student(parent)
    if not klx_stu_name: # just try again
        klx_stu_name = klx_reg_student(parent)
    if not klx_stu_name:
        return False
    klx_tea_name = klx_reg_teacher(teacher)
    if not klx_tea_name: # just try again
        klx_tea_name = klx_reg_teacher(teacher)
    if not klx_tea_name:
        return False
    ok = klx_relation(klx_tea_name, klx_stu_name)
    if not ok: # just try again
        ok = klx_relation(klx_tea_name, klx_stu_name)
    return ok
