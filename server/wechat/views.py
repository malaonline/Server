import logging
import json
import requests
import datetime
import math
import time

# django modules
from django.views.decorators.csrf import csrf_exempt
from django.shortcuts import render, redirect, get_object_or_404
from django.views.generic import View, TemplateView, ListView, DetailView
from django.db.models import Q,Count
from django.conf import settings
from django.http import HttpResponse, JsonResponse, HttpResponseRedirect
from django.utils import timezone
from django.contrib.auth import _get_backends
from django.core.urlresolvers import reverse
from django.utils.decorators import method_decorator

# local modules
from app import models
from app.utils.types import parseInt
from .wxapi import *

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


@method_decorator(csrf_exempt, name='dispatch')
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
        now = timezone.now()
        now_timestamp = int(now.timestamp())
        kwargs['server_timestamp'] = now_timestamp

        # if current_user.parent:
        #     coupons = models.Coupon.objects.filter(parent=current_user.parent,
        #         validated_start__lte=now, expired_at__gt=now, used=False
        #     ).order_by('-amount', 'expired_at')
        #     kwargs['coupon'] = coupons.first()

        nonce_str = make_nonce_str()
        access_token, msg = _get_wx_token()
        jsapi_ticket, msg = _get_wx_jsapi_ticket(access_token)
        cur_url = self.request.build_absolute_uri()
        signature = wx_signature({'noncestr': nonce_str,
                                  'jsapi_ticket': jsapi_ticket,
                                  'timestamp': now_timestamp,
                                  'url': cur_url})
        kwargs['WX_APPID'] = settings.WEIXIN_APPID
        kwargs['WX_APP_SECRET'] = settings.WEIXIN_APP_SECRET
        kwargs['WX_NONCE_STR'] = nonce_str
        kwargs['WX_SIGNATURE'] = signature
        return super(CourseChoosingView, self).get_context_data(**kwargs)

    def post(self, request, teacher_id=None):
        action = request.POST.get('action')
        if action == 'confirm':
            return self.confirm_order(request, teacher_id)
        if action == 'verify':
            return self.verify_order(request)
        return HttpResponse("Not supported request.", status=403)

    def confirm_order(self, request, teacher_id):
        wx_openid = 'wx1n934hnfidhf934hkjd'
        # get request params
        teacher_id = request.POST.get('teacher')
        school_id = request.POST.get('school')
        grade_id = request.POST.get('grade')
        subject_id = request.POST.get('subject')
        coupon_id = request.POST.get('coupon')
        hours = parseInt(request.POST.get('hours'))
        weekly_time_slot_ids = request.POST.get('weekly_time_slots').split('+')
        if not hours or not weekly_time_slot_ids:
            return JsonResponse({'ok': False, 'msg': '时间选择参数错误', 'code': 1})

        # check params and get ref obj
        # TODO: 通过 wx_openid 获得家长
        # profile = get_object_or_404(models.Profile, wx_openid=wx_openid)
        # parent = get_object_or_404(models.Parent, user=profile.user)
        parent =models.Parent.objects.get(user__username='parent7')
        teacher = get_object_or_404(models.Teacher, pk=teacher_id)
        school = get_object_or_404(models.School, pk=school_id)
        grade = get_object_or_404(models.Grade, pk=grade_id)
        subject = teacher.subject() # 老师只有一个科目
        coupon = coupon_id and get_object_or_404(models.Coupon, pk=coupon_id) or None
        weekly_time_slots = [get_object_or_404(models.WeeklyTimeSlot, pk=w_id) for w_id in weekly_time_slot_ids]

        # create order
        order = models.Order.objects.create(
                parent=parent, teacher=teacher, school=school,
                grade=grade, subject=subject, hours=hours, coupon=coupon)
        order.weekly_time_slots.add(*weekly_time_slots)
        order.save()
        # get wx pay order
        ret_json = wx_pay_unified_order(order, request, wx_openid)
        if not ret_json['ok']:
            return JsonResponse({'ok': False, 'msg': ret_json['msg'], 'code': -500})
        # 构造js-sdk 支付接口参数 appId, timeStamp, nonceStr, package, signType
        data = {}
        data['timestamp'] = int(timezone.now().timestamp())
        data['nonceStr'] = make_nonce_str()
        data['package'] = 'prepay_id={id})'.format(id=ret_json['data']['prepay_id'])
        data['signType'] = 'SHA1'
        data['appId'] = settings.WEIXIN_APPID
        data['paySign'] = wx_signature(data) # 签名, TODO: 微信文档中新版签名怎么怎么着, 待测试
        data['prepay_id'] = ret_json['data']['prepay_id']
        data['order_id'] = order.order_id
        return JsonResponse({'ok': True, 'msg': '', 'code': '', 'data': data})

    def verify_order(self, request):
        # get request params
        prepay_id = request.POST.get('prepay_id')
        order_id = request.POST.get('order_id')
        query_ret = wx_pay_order_query(order_id=order_id)
        if query_ret['ok']:
            trade_state = query_ret['data']['trade_state']
            if trade_state == WX_SUCCESS:
                # 支付成功, 设置订单支付成功, 并且生成课程安排
                charge = models.Charge.objects.get(ch_id=prepay_id)
                charge.paid = True
                charge.time_paid = timezone.now()
                # charge.transaction_no = ''
                charge.save()

                order = charge.order
                order.status = models.Order.PAID
                models.Order.objects.allocate_timeslots(order)
                order.save()
                return JsonResponse({'ok': True, 'msg': '', 'code': 0})
            else:
                if trade_state == WX_PAYERROR:
                    return {'ok': False, 'msg': '支付失败', 'code': 2}
                else:
                    return {'ok': False, 'msg': '未支付', 'code': 3}
        else:
            return {'ok': False, 'msg': query_ret['msg'], 'code': 1}


def _get_wx_jsapi_ticket(access_token):
    jsapi_ticket = _get_wx_jsapi_ticket_from_db()
    msg = None
    if not jsapi_ticket:
        result = wx_get_jsapi_ticket(access_token)
        if result['ok']:
            jsapi_ticket = result['ticket']
        else:
            msg = result['msg']
    return jsapi_ticket, msg


def _get_wx_jsapi_ticket_from_db():
    tk = models.WeiXinToken.objects.filter(token_type=models.WeiXinToken.JSAPI_TICKET).order_by('-id').first()
    if tk and tk.token and (not tk.is_token_expired()):
        return tk.token
    return None


def _get_wx_token():
    token = _get_wx_token_from_db()
    msg = None
    if not token:
        result = wx_get_token()
        if result['ok']:
            token = result['token']
        else:
            msg = result['msg']
    return token, msg


def _get_wx_token_from_db():
    tk = models.WeiXinToken.objects.filter(token_type=models.WeiXinToken.ACCESS_TOKEN).order_by('-id').first()
    if tk and tk.token and (not tk.is_token_expired()):
        return tk.token
    return None


@csrf_exempt
def wx_pay_notify(request):
    """
    接受微信支付结果异步通知view
    """
    # TODO: 接受微信支付结果
    pass


@csrf_exempt
def get_wx_token(request):
    retToken, retMsg = _get_wx_token()

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
        temptype = request.POST.get("temptype", None)

    if temptype == 'payok':
        return template_msg_data_pay_ok(request)
    elif temptype == 'payinfo':
        return template_msg_data_pay_info(request)
    return {}

# 报名缴费成功
def template_msg_data_pay_ok(request):
    tempId = settings.WECHAT_PAY_OK_TEMPLATE

    toUser = request.GET.get("toUser", None)
    if not toUser:
        toUser = request.POST.get("toUser", None)

    first = request.GET.get("first", None)
    if not first:
        openid = request.POST.get("first", None)

    kw1 = request.GET.get("kw1", None)
    if not kw1:
        openid = request.POST.get("kw1", None)

    kw2 = request.GET.get("kw2", None)
    if not kw2:
        kw2 = request.POST.get("kw2", None)

    kw3 = request.GET.get("kw3", None)
    if not kw3:
        kw3 = request.POST.get("kw3", None)

    kw4 = request.GET.get("kw4", None)
    if not kw4:
        kw4 = request.POST.get("kw4", None)

    kw5 = request.GET.get("kw5", None)
    if not kw5:
        kw5 = request.POST.get("kw5", None)

    remark = request.GET.get("remark", None)
    if not remark:
        remark = request.POST.get("remark", None)

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
    tempId = settings.WECHAT_PAY_INFO_TEMPLATE

    toUser = request.GET.get("toUser", None)
    if not toUser:
        toUser = request.POST.get("toUser", None)

    first = request.GET.get("first", None)
    if not first:
        first = request.POST.get("first", None)

    kw1 = request.GET.get("kw1", None)
    if not kw1:
        kw1 = request.POST.get("kw1", None)

    kw2 = request.GET.get("kw2", None)
    if not kw2:
        kw2 = request.POST.get("kw2", None)

    kw3 = request.GET.get("kw3", None)
    if not kw3:
        kw3 = request.POST.get("kw3", None)

    kw4 = request.GET.get("kw4", None)
    if not kw4:
        kw4 = request.POST.get("kw4", None)

    kw5 = request.GET.get("kw5", None)
    if not kw5:
        kw5 = request.POST.get("kw5", None)

    remark = request.GET.get("remark", None)
    if not remark:
        remark = request.POST.get("remark", None)

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


@csrf_exempt
def teacher_view(request):
    template_name = 'wechat/teacher/teacher.html'
    openid = request.GET.get("openid", None)

    if not openid:
        openid = request.POST.get("openid", None)

    teacherid = request.GET.get("teacherid", None)
    if not teacherid:
        teacherid = request.POST.get("teacherid", None)

    teacher = None
    gender = None
    try:
        teacher = models.Teacher.objects.get(id=teacherid)
        profile = models.Profile.objects.get(user=teacher.user)

        gender_dict = {"f": "女", "m": "男", "u": ""}
        gender = gender_dict.get(profile.gender, "")
    except models.Teacher.DoesNotExist:
        return JsonResponse({'error': 'teacher not exist', 'code': -1})
    except models.Profile.DoesNotExist:
        return JsonResponse({'error': 'teacher profile not exist', 'code': -1})

    memberService = models.Memberservice.objects.all()
    achievements = models.Achievement.objects.filter(teacher=teacher).order_by('id')

    grades_all = models.Grade.objects.all()
    _heap = {}
    grades_tree = []
    for grade in grades_all:
        if not grade.superset_id:
            _temp = {'id':grade.id, 'name':grade.name, 'children':[]}
            _heap[grade.id] = _temp
            grades_tree.append(_temp)
        else:
            _temp = _heap[grade.superset_id]
            _temp['children'].append({'id':grade.id, 'name':grade.name})

    now_timestamp = int(time.time())

    nonce_str = make_nonce_str()
    access_token, msg = _get_wx_token()
    jsapi_ticket, msg = _get_wx_jsapi_ticket(access_token)
    cur_url = request.build_absolute_uri()

    signature = wx_signature({'noncestr': nonce_str,
                            'jsapi_ticket': jsapi_ticket,
                            'timestamp': now_timestamp,
                            'url': cur_url})

    context = {
        "server_timestamp": now_timestamp,
        "WX_APPID": settings.WEIXIN_APPID,
        "WX_APP_SECRET": settings.WEIXIN_APP_SECRET,
        "WX_NONCE_STR": nonce_str,
        "WX_SIGNATURE": signature,
        "openid": openid,
        "gender": gender,
        "tags": list(teacher.tags.all()),
        "achievements": achievements,
        "memberService": list(memberService),
        "subjects": models.Subject.objects.all,
        "grades_tree": grades_tree,
        "teacher_grade_ids": [grade.id for grade in teacher.grades()],
        "teacher": teacher
    }

    return render(request, template_name, context)

@csrf_exempt
def getSchoolsWithDistance(request):
    lat = request.POST.get("lat", None)
    lng = request.POST.get("lng", None)

    point = None
    if lat is not None and lng is not None:
        point = {
            'lat': float(lat),
            'lng': float(lng)
        }

    if not point:
        JsonResponse({'ok': False, 'msg': 'no lat,lng', 'code': -1})
    schools = models.School.objects.all()
    ret = []
    for school in schools:
        pointB = None
        sc = {
            'name': school.name,
            'img': school.get_thumbnail(),
            'address': school.address,
            'region': school.region.name
        }
        if school.latitude is not None and school.longitude is not None:
            pointB = {
                'lat': school.latitude,
                'lng': school.longitude
            }
            dis = calculateDistance(point, pointB)
            sc['dis'] = dis
        ret.append(sc)
    ret = sorted(ret, key = lambda school: school['dis'] if 'dis' in school else 63710000)
    for sc in ret:
        if 'dis' in sc and sc['dis'] is not None:
            sc['dis'] = sc['dis']/1000
    return JsonResponse({'ok': True, 'schools': ret, 'code': 0})

def calculateDistance(pointA, pointB):
  R = 6371000; #metres
  toRadians = math.pi/180;

  return math.acos(math.sin(toRadians * pointA["lat"]) * math.sin(toRadians * pointB["lat"]) + math.cos(toRadians * pointA["lat"]) * math.cos(toRadians * pointB["lat"]) * math.cos(toRadians * pointB["lng"] - toRadians * pointA["lng"])) * R;

@csrf_exempt
def phone_page(request):
    template_name = 'wechat/parent/reg_phone.html'

    openid = request.GET.get("openid", None)
    if not openid:
        openid = request.POST.get("openid", None)
    context = {
        "openid": openid
    }
    return render(request, template_name, context)

@csrf_exempt
def add_openid(request):
    phone = request.POST.get("phone", None)
    code = request.POST.get("code", None)
    openid = request.POST.get("openid", None)
    if not openid:
        return JsonResponse({
            "result": False,
            "code": -1
        })
    Profile = models.Profile
    CheckCode = models.Checkcode
    Parent = models.Parent
    try:
        profile = Profile.objects.get(phone=phone)
        profile.wx_openid = openid
        profile.save()
        user = profile.user
        for backend, backend_path in _get_backends(return_tuples=True):
            user.backend = backend_path
            break
        parent = Parent.objects.get(user=user)
    except Profile.DoesNotExist:
        # new user
        user = Parent.new_parent()
        parent = user.parent
        profile = parent.user.profile
        profile.phone = phone
        profile.wx_openid = openid
        profile.save()
    except Parent.DoesNotExist:
        parent = Parent(user=user)
        parent.save()
    if CheckCode.verify(phone, code)[0]:
        return JsonResponse({
            "result": True
        })
    else:
        # 验证失败
        return JsonResponse({
            "result": False
        })

@csrf_exempt
def check_phone(request):
    get_openid_url = 'https://api.weixin.qq.com/sns/oauth2/access_token?grant_type=authorization_code'
    wx_code = request.GET.get('code', None)
    teacherId = request.GET.get('state', None)

    get_openid_url += '&appid=' + settings.WEIXIN_APPID
    get_openid_url += '&secret=' + settings.WEIXIN_APP_SECRET
    get_openid_url += '&code=' + wx_code
    req = requests.get(get_openid_url)
    ret = None
    openid = None
    if req.status_code == 200:
        ret = json.loads(req.text)
        if "openid" in ret:
            openid = ret["openid"]
        if "errcode" in ret:
            pass
    if openid:
        profiles = models.Profile.objects.filter(wx_openid=openid).order_by('-id')
        lastOne = list(profiles) and profiles[0]
        if lastOne:
            return HttpResponseRedirect(reverse('wechat:order-course-choosing', kwargs={"teacher_id": teacherId})+'?openid='+openid)

    context = {
        "openid": openid,
        "teacherId": teacherId,
        "nextpage": reverse('wechat:order-course-choosing', kwargs={"teacher_id": teacherId})
    }
    return render(request, 'wechat/parent/reg_phone.html', context)
