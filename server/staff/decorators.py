from django.contrib.auth import REDIRECT_FIELD_NAME
from django.contrib.auth.decorators import user_passes_test
from app import models

def is_manager(u):
    if u.is_active:
        try:
            profile = models.Profile.objects.get(user=u)
            if not profile.role:
                return False
            role = models.Role.objects.get(name='师资管理员')
            return profile.role.id == role.id
        except:
            pass
    return False

def mala_staff_required(view_func, redirect_field_name=REDIRECT_FIELD_NAME, login_url='staff:login'):
    """
    Decorator for views that checks that the user is logged in and is a staff
    member, displaying the login page if necessary.
    """
    return user_passes_test(
        is_manager,
        login_url=login_url,
        redirect_field_name=redirect_field_name
    )(view_func)