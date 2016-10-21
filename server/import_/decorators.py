import logging

from django.contrib.auth import REDIRECT_FIELD_NAME
from django.contrib.auth.decorators import user_passes_test
from django.contrib.auth.models import Group
from staff.decorators import is_manager as _is_manager

logger = logging.getLogger('app')

def is_manager(u):
    return _is_manager(u, role='历史数据录入员')

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
