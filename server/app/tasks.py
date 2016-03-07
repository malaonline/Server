from __future__ import absolute_import

from celery import shared_task

from django.conf import settings
import jpush


@shared_task
def add(x, y):
    return x + y


@shared_task
def mul(x, y):
    return x * y


@shared_task
def xsum(numbers):
    return sum(numbers)


@shared_task
def send_push_msg(msg, user_ids=None):
    '''
    user_ids is a list of user_id [1, 2, ...]
    if user_ids is None then send to all
    '''
    app_key = settings.JPUSH_APP_KEY
    master_secret = settings.JPUSH_MASTER_SECRET
    _jpush = jpush.JPush(app_key, master_secret)

    push = _jpush.create_push()
    if user_ids is None:
        push.audience = jpush.all_
        push.notification = jpush.notification(alert=msg)
        push.platform = jpush.all_
        push.send()
    elif len(user_ids) > 1000:
        for i in range(len(user_ids) // 1000 + 1):
            send_push_msg(msg, user_ids[i * 1000: (i + 1) * 1000])
    elif len(user_ids) == 0:
        return
    else:
        push.audience = jpush.audience(
                jpush.alias(*user_ids)
                )
        push.notification = jpush.notification(alert=msg)
        push.platform = jpush.all_
        push.send()
