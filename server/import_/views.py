import logging
import datetime
import json
from collections import OrderedDict
import xlwt

# django modules
from django.conf import settings
from django.core.files.base import ContentFile
from django.shortcuts import render, redirect, get_object_or_404
from django.http import HttpResponse, JsonResponse
from django.views.decorators.http import require_POST
from django.views.generic import View, TemplateView, ListView
from django.utils.decorators import method_decorator
from django.contrib import auth
from django.db import IntegrityError, transaction
from django.db.models import Q
from django.utils import timezone
from rest_framework.renderers import JSONRenderer
from django.contrib.auth.mixins import AccessMixin

# local modules
from app import models
from app.utils import smsUtil
from app.utils.algorithm import check_id_number
from app.utils.types import parseInt, parse_date, parse_date_next
from app.utils.db import paginate, Pager
from app.utils import excel
from .decorators import mala_staff_required, is_manager
from app.exception import TimeSlotConflict, OrderStatusIncorrect, RefundError
from app.tasks import send_push, Remind, send_sms


logger = logging.getLogger('app')


@mala_staff_required
def index(request):
    return render(request, 'import_/index.html')


def login(request, context={}):
    if is_manager(request.user):
        return redirect('import_:index')
    return render(request, 'import_/login.html', context)


def logout(request):
    auth.logout(request)
    return redirect('import_:login')


class StaffRoleRequiredMixin(AccessMixin):
    def dispatch(self, request, *args, **kwargs):
        url_name = self.request.resolver_match.url_name
        for group in self.request.user.groups.all():
            for staff_permission in group.staffpermission_set.all():
                if staff_permission.allowed_url_name == 'all' \
                        or staff_permission.allowed_url_name == url_name:
                    return super(StaffRoleRequiredMixin, self).dispatch(request, *args, **kwargs)

        return HttpResponse("Not Allowed.", status=403)


class BaseStaffView(StaffRoleRequiredMixin, TemplateView):
    """
    Base view for staff management page views.
    """

    @method_decorator(mala_staff_required)
    def dispatch(self, request, *args, **kwargs):
        return super(BaseStaffView, self).dispatch(request, *args, **kwargs)


class BaseStaffActionView(StaffRoleRequiredMixin, View):
    """
    Base view for staff management action views.
    """

    defaultErrMeg = "操作失败,请稍后重试或联系管理员"

    # @method_decorator(csrf_exempt) # 不加csrf,不允许跨域访问
    @method_decorator(mala_staff_required)
    def dispatch(self, request, *args, **kwargs):
        return super(BaseStaffActionView, self).dispatch(request, *args, **kwargs)
