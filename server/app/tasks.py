from __future__ import absolute_import

import datetime
import logging

from django.conf import settings
from django.utils import timezone
# from django.utils import timezone
# from django.db.models import F
import jpush

from celery import shared_task
from .models import TimeSlot, TimeSlotAttendance,\
    Order, Teacher, Coupon, Evaluation, School
from .utils.klx_api import klx_reg_student, klx_reg_teacher, klx_relation
from .utils.smsUtil import tpl_send_sms

logger = logging.getLogger('tasks')


class Remind:
    # 通知和提醒类型
    COURSE_CHANGED = "1"
    ORDER_REFUNDED = "2"
    COURSE_CONFIRMED = "3"
    COURSE_REMIND = "4"
    COUPON_WILL_EXPIRED = "5"
    EVALUATION_SCHEDULED = "6"
    # 标题
    titles = {
        COURSE_CHANGED: '课程变动',
        ORDER_REFUNDED: '退费成功',
        COURSE_CONFIRMED: '完课评价',
        COURSE_REMIND: '课前通知',
        COUPON_WILL_EXPIRED: '奖学金即将到期',
        EVALUATION_SCHEDULED: '测评建档',
    }

    @staticmethod
    def title(remind_type):
        return Remind.titles.get(remind_type)


@shared_task
def autoConfirmClasses():
    operateTargets = TimeSlot.should_auto_confirmed_objects.all()
    logger.debug("[autoConfirmClasses] target amount:%d" %(len(operateTargets)))
    user_ids = []
    for timeslot in operateTargets:
        timeslot.confirm()
        logger.debug("[autoConfirmClasses] The Timeslot ends at %s ,was been set the attendance to %s" %(timeslot.end, timeslot.attendance))
        user_ids.append(timeslot.order.parent.user_id)
    # JPush 通知
    extras = {
        "type": Remind.COURSE_CONFIRMED,  # 完课评价
        "code": None
    }
    send_push.delay(
        "您有课程已完成, 去评价>>",
        title=Remind.title(Remind.COURSE_CONFIRMED),
        user_ids=user_ids,
        extras=extras
    )
    return True


@shared_task
def autoNotifyComment():
    operateTargets = TimeSlot.auto_notify_comment_objects.all()
    logger.debug("[autoNotifyComment] target amount:%d" % (len(operateTargets)))
    # JPush 通知
    extras = {
        "type": Remind.COURSE_CONFIRMED,  # 完课评价
        "code": None
    }
    for timeslot in operateTargets:
        logger.debug("[autoNotifyComment] The Timeslot ends at %s" % (timeslot.end))
        teacher_name = timeslot.lecturer.name if timeslot.is_live() else timeslot.teacher.name
        send_push.delay(
            "%s 老师的课上完了, 觉得怎么样呢, 去评价发表你的看法吧>>" % teacher_name,
            title=Remind.title(Remind.COURSE_CONFIRMED),
            user_ids=[timeslot.order.parent.user_id],
            extras=extras
        )
    return True


@shared_task
def autoRemindClasses():
    # JPush 通知
    extras = {
        "type": Remind.COURSE_REMIND,  # 课前通知
        "code": None
    }
    remind_time = timezone.now() + TimeSlot.REMIND_TIME
    timeslots = TimeSlot.objects.filter(
        deleted=False,
        start__lt=remind_time,
        reminded=False
    )
    evaluations = Evaluation.objects.filter(
        status=Evaluation.SCHEDULED,
        start__lt=remind_time,
        reminded=False
    )
    targets = [x for x in timeslots] + [y for y in evaluations]

    for target in targets:
        user_ids = [target.order.parent.user_id]
        msg = "您在%s-%s有一节%s课，记得准时参加哦>>" % (
            target.start.astimezone().time().strftime("%H:%M"),
            target.end.astimezone().time().strftime("%H:%M"),
            target.subject.name
        )
        send_push.delay(
            msg,
            title=Remind.title(Remind.COURSE_REMIND),
            user_ids=user_ids,
            extras=extras
        )
        # 标记为已推送
        target.reminded = True
        target.save()


@shared_task
def autoRemindCoupons():
    # JPush 通知
    extras = {
        "type": Remind.COUPON_WILL_EXPIRED,  # 奖学金即将到期
        "code": None
    }
    remind_time = timezone.now() + Coupon.REMIND_TIME
    targets = Coupon.objects.filter(
        used=False,
        expired_at__gt=timezone.now(),
        expired_at__lt=remind_time,
        reminded=False
    )
    for coupon in targets:
        user_ids = [coupon.parent.user_id]
        msg = "您有一张%d元的奖学金券即将到期，快去使用吧>>" % (
            coupon.amount_yuan
        )
        send_push.delay(
            msg,
            title=Remind.title(Remind.COUPON_WILL_EXPIRED),
            user_ids=user_ids,
            extras=extras
        )
        # 标记为已推送
        coupon.reminded = True
        coupon.save()


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
    logger.debug("[autoCancelOrders] estimated target amount:%d" %(len(operateTargets)))
    count = 0
    for order in operateTargets:
        if Order.objects.filter(pk=order.id, status=Order.PENDING).update(status=Order.CANCELED):
            order.cancel()
            count += 1
            logger.debug("[autoCancelOrders] The Order created at %s which order_id is %s, was been canceled automatically" %(order.created_at, order.order_id))
    if count > 0:
        logger.debug("[autoCancelOrders] effected target amount:%d" % count)
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
    logger.debug("[registerKuaiLeXueUserByOrder] order id: %s" % oid)
    order = Order.objects.get(pk=oid)
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


@shared_task(bind=True, default_retry_delay=32, max_retries=4)
def send_sms(self, phone, tpl_id, params=None, times=1):
    if params is None:
        params = {}
    logger.debug("[send_sms] to "+str(phone)+', '+str(tpl_id)+': '+str(params))
    try:
        tpl_send_sms(phone, tpl_id, params)
    except Exception as exc:
        # request.retries is an integer starting at 0
        if self.request.retries + 1 < times:
            raise self.retry(exc=exc)
        logger.error(exc)
        raise exc


@shared_task
def autoCreateSchoolIncomeRecord():
    all_schools = School.objects.filter(opened=True)
    logger.debug("[autoCreateSchoolIncomeRecord] all schools: %d" % len(all_schools))

    now = timezone.localtime(timezone.now())
    yesterday = now - datetime.timedelta(hours=now.hour + 1)
    ok_count = 0
    logger.debug('[autoCreateSchoolIncomeRecord] end time: %s' % yesterday)
    for school in all_schools:
        created = school.create_income_record(yesterday)
        if created:
            ok_count += 1
            logger.debug("[autoCreateSchoolIncomeRecord] %s" % school.name)
    logger.debug("[autoCreateSchoolIncomeRecord] %d schools end." % ok_count)
    return True
