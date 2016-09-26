from __future__ import absolute_import

import os
from datetime import timedelta

from celery import Celery
from celery.schedules import crontab

# set the default Django settings module for the 'celery' program.
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'server.settings')

from django.conf import settings  # noqa
import logging

logger = logging.getLogger('tasks')

celery_app = Celery('server')

# Using a string here means the worker will not have to
# pickle the object when using Windows.
celery_app.config_from_object('django.conf:settings')
celery_app.autodiscover_tasks(lambda: settings.INSTALLED_APPS)

celery_app.conf.update(
    CELERY_ROUTES={
        "proj.tasks.add": {"queue": "hipri"},  # 把add任务放入hipri队列
        # 需要执行时指定队列 add.apply_async((2, 2), queue='hipri')
    },
    CELERYBEAT_SCHEDULE={
        # "confirm-classes":{
        #     "task":"app.tasks.autoConfirmClasses",
        #     "schedule":timedelta(
        #         seconds=15),
        #     },
        "remind-classes": {
            "task": "app.tasks.autoRemindClasses",
            "schedule": timedelta(
                seconds=15),
        },
        "remind-coupons": {
            "task": "app.tasks.autoRemindCoupons",
            "schedule": timedelta(
                seconds=15),
        },
        "cancel-orders": {
            "task": "app.tasks.autoCancelOrders",
            "schedule": timedelta(
                seconds=15),
        },
        "add-teaching-age": {
            "task": "app.tasks.autoAddTeacherTeachingAge",
            "schedule": crontab(hour=0, minute=30),
        },
        "school-income-records": {
            "task": "app.tasks.autoCreateSchoolIncomeRecord",
            "schedule": crontab(minute=6, hour=23, day_of_week=1),
        },
    },
)

if __name__ == "__main__":
    celery_app.start()


@celery_app.task(bind=True)
def debug_task(self):
    logger.debug('Request: {0!r}'.format(self.request))
