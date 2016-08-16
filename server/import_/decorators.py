import logging

from django.contrib.auth import REDIRECT_FIELD_NAME
from django.contrib.auth.decorators import user_passes_test
from django.contrib.auth.models import Group

logger = logging.getLogger('app')

def is_manager(u):
    if u.is_active:
        if u.is_superuser:
            return True
        try:
            all_group = u.groups.all()
            group = Group.objects.get(name='师资管理员')
            return group in all_group
        except Group.DoesNotExist as ex:
            logger.error("Group DoesNotExist: {0}".format(ex))
        except Exception as err:
            logger.error(err)
    return False

def mala_staff_required(view_func, redirect_field_name=REDIRECT_FIELD_NAME, login_url='import_:login'):
    """
    Decorator for views that checks that the user is logged in and is a staff
    member, displaying the login page if necessary.
    """
    return user_passes_test(
        is_manager,
        login_url=login_url,
        redirect_field_name=redirect_field_name
    )(view_func)
