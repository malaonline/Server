import  logging

# django modules
from django.views.decorators.csrf import csrf_exempt
from django.shortcuts import render, redirect, get_object_or_404
from django.views.generic import View, TemplateView, ListView, DetailView
from django.db.models import Q,Count
from django.utils import timezone
from django.conf import settings
from django.http import HttpResponse, JsonResponse, HttpResponseRedirect
from django.utils import timezone
import json
import requests
import datetime
import urllib

# local modules
from app import models

# Create your views here.


class TeachersView(ListView):
    model = models.Teacher
    context_object_name = 'teacher_list'
    template_name = 'wechat/teacher/teachers.html'

    def get_queryset(self):
        teacher_list = self.model.objects.filter(
            recommended_on_wechat=True
         ).filter(
            published=True
        )
        return teacher_list

class TeacherDetailView(DetailView):
    model = models.Teacher

class SchoolsView(ListView):
    model = models.School
    context_object_name = 'school_list'
    template_name = 'wechat/school/schools.html'

    def get_queryset(self):
        school_list=self.model.objects.annotate(num_photos=Count('schoolphoto'))
        queryset = {}
        queryset['expr_center_list'] = school_list.filter(
            center = True
         )
        queryset['community_center_list'] = school_list.filter(
            center=False
        )
        return queryset

class SchoolDetailView(ListView):
    models = models.School


class CourseChoosingView(TemplateView):
    template_name = 'wechat/order/course_choosing.html'

    def get_context_data(self, teacher_id=None, **kwargs):
        teacher = get_object_or_404(models.Teacher, pk=teacher_id)
        kwargs['teacher'] = teacher
        current_user = self.request.user
        kwargs['current_user'] = current_user
        if not current_user.is_anonymous():
            try:
                parent = models.Parent.objects.get(user=current_user)
            except models.Parent.DoesNotExist:
                parent = None
            kwargs['parent'] = parent
        first_buy = True
        kwargs['first_buy'] = first_buy
        abilities = teacher.abilities.all()
        kwargs['abilities'] = abilities
        prices = teacher.prices()
        kwargs['prices'] = prices
        schools = teacher.schools
        kwargs['schools'] = schools.all()
        kwargs['daily_time_slots'] = models.WeeklyTimeSlot.DAILY_TIME_SLOTS
        # now = timezone.now()
        # kwargs['server_now'] = now

        # if current_user.parent:
        #     coupons = models.Coupon.objects.filter(parent=current_user.parent,
        #         validated_start__lte=now, expired_at__gt=now, used=False
        #     ).order_by('-amount', 'expired_at')
        #     kwargs['coupon'] = coupons.first()

        return super(CourseChoosingView, self).get_context_data(**kwargs)

def get_token_from_weixin():
    wx_url = 'https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential'
    wx_url += '&appid=' + settings.WEIXIN_APPID
    wx_url += '&secret=' + settings.WEIXIN_APP_SECRET

    req = requests.get(wx_url)
    if req.status_code == 200:
        ret = json.loads(req.text)
        if "access_token" in ret:
            models.WeiXinToken.objects.all().delete()
            wxToken = models.WeiXinToken(token=ret['access_token'])
            if "expires_in" in ret:
                wxToken.expires_in = ret['expires_in']
            wxToken.save()
            return {'ok': True, 'token': ret['access_token'], 'code': 0}
        else:
            return {'ok': False, 'msg': '获取微信token出错!', 'code': -1}
    else:
        return {'ok': False, 'msg': '获取微信token出错，请联系管理员!', 'code': -1}

@csrf_exempt
def get_wx_token(request):
    tk = models.WeiXinToken.objects.all().order_by('-id')
    tk = list(tk) and tk[0]
    now = timezone.now()
    retToken = tk.token
    retMsg = None
    expires_date = tk.created_at + datetime.timedelta(seconds=tk.expires_in) + datetime.timedelta(seconds=-20)
    delta = expires_date - now
    if delta.total_seconds() < 0:
        result = get_token_from_weixin()
        if result['ok']:
            retToken = ret['token']
        else:
            retMsg = ret['msg']

    if retMsg:
        return JsonResponse({'ok': False, 'msg': retMsg, 'code': -1})
    else:
        return JsonResponse({'ok': True, 'token': retToken, 'code': 0})

@csrf_exempt
def send_template_msg(request):
    tk = get_wx_token(request)
    if tk.status_code == 200:
        content = json.loads(tk.content.decode())
        token = content['token']

        tmpmsg_url = 'https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=' + token

        ct = getContentData(request)
        ct['access_token'] = token
        json_template = json.dumps(ct)

        req = requests.post(tmpmsg_url, data=json.dumps(ct))
        retText = json.loads(req.text)

        msgid = None
        if 'msgid' in retText:
            msgid = retText['msgid']

        return JsonResponse({'ok': True, 'msgid': msgid, 'code': 0})
    else:
        return JsonResponse({'ok': False, 'msg': '获取token错误', 'code': -1})

def getContentData(request):
    temptype = request.GET.get("temptype", None)
    if not temptype:
        openid = request.POST.get("temptype", None)

    if temptype == 'payok':
        return template_msg_data_pay_ok(request)
    elif temptype == 'payinfo':
        return template_msg_data_pay_info(request)
    return {}

# 报名缴费成功
def template_msg_data_pay_ok(request):
    tempId = 'n5xWnCpnRDouQUTMdWtlNF90n54xbNoAj6ZKMOBh0yY'

    toUser = request.GET.get("toUser", None)
    if not toUser:
        openid = request.POST.get("toUser", None)

    first = request.GET.get("first", None)
    if not first:
        openid = request.POST.get("first", None)

    kw1 = request.GET.get("kw1", None)
    if not kw1:
        openid = request.POST.get("kw1", None)

    kw2 = request.GET.get("kw2", None)
    if not kw2:
        openid = request.POST.get("kw2", None)

    kw3 = request.GET.get("kw3", None)
    if not kw3:
        openid = request.POST.get("kw3", None)

    kw4 = request.GET.get("kw4", None)
    if not kw4:
        openid = request.POST.get("kw4", None)

    kw5 = request.GET.get("kw5", None)
    if not kw5:
        openid = request.POST.get("kw5", None)

    remark = request.GET.get("remark", None)
    if not remark:
        openid = request.POST.get("remark", None)

    return {
        "access_token": None,
        "touser": toUser,
        "template_id": tempId,
        "data": {
            "first": {
                "value": first
            },
            "keyword1": {
                "value": kw1
            },
            "keyword2": {
                "value": kw2
            },
            "keyword3": {
                "value": kw3
            },
            "keyword4": {
                "value": kw4
            },
            "keyword5": {
                "value": kw5
            },
            "remark": {
                "value": remark
            }
        }
    }

# 支付提醒：支付提醒，支付失败
def template_msg_data_pay_info(request):
    tempId = 'RMpNzoIW1Gken7qh4L2YbM_g3rU_KTZF7i-VLGA_Vnk'

    toUser = request.GET.get("toUser", None)
    if not toUser:
        openid = request.POST.get("toUser", None)

    first = request.GET.get("first", None)
    if not first:
        openid = request.POST.get("first", None)

    kw1 = request.GET.get("kw1", None)
    if not kw1:
        openid = request.POST.get("kw1", None)

    kw2 = request.GET.get("kw2", None)
    if not kw2:
        openid = request.POST.get("kw2", None)

    kw3 = request.GET.get("kw3", None)
    if not kw3:
        openid = request.POST.get("kw3", None)

    kw4 = request.GET.get("kw4", None)
    if not kw4:
        openid = request.POST.get("kw4", None)

    kw5 = request.GET.get("kw5", None)
    if not kw5:
        openid = request.POST.get("kw5", None)

    remark = request.GET.get("remark", None)
    if not remark:
        openid = request.POST.get("remark", None)

    return {
        "access_token": None,
        "touser": toUser,
        "template_id": tempId,
        "data": {
            "first": {
                "value": first
            },
            "keyword1": {
                "value": kw1
            },
            "keyword2": {
                "value": kw2
            },
            "keyword3": {
                "value": kw3
            },
            "keyword4": {
                "value": kw4
            },
            "keyword5": {
                "value": kw5
            },
            "remark": {
                "value": remark
            }
        }
    }
