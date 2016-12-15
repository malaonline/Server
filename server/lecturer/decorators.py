import logging

from django.contrib.auth import REDIRECT_FIELD_NAME
from django.contrib.auth.decorators import user_passes_test

logger = logging.getLogger('app')


def is_lecturer(u):
    if u.is_active and hasattr(u, 'lecturer'):
        return True
    return False


def mala_lecturer_required(view_func, redirect_field_name=REDIRECT_FIELD_NAME,
                           login_url='lecturer:login'):
    """
    Decorator for views that checks that the user is logged in and is a lecturer,
    displaying the login page if necessary.
    """
    return user_passes_test(
        is_lecturer,
        login_url=login_url,
        redirect_field_name=redirect_field_name
    )(view_func)
