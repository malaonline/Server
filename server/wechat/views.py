import logging
import json
import requests
import datetime
import math

# django modules
from django.views.decorators.csrf import csrf_exempt
from django.shortcuts import render, redirect, get_object_or_404
from django.views.generic import View, TemplateView, ListView, DetailView
from django.db.models import Q,Count
from django.conf import settings
from django.http import HttpResponse, JsonResponse, HttpResponseRedirect
from django.utils import timezone
from django.views.decorators.csrf import csrf_exempt
from django.contrib.auth import _get_backends
from django.http import HttpResponse, JsonResponse, HttpResponseRedirect
import requests
import json
from django.conf import settings

# local modules
from app import models
from app.utils import random_string
from .wxapi import wx_signature, get_token_from_weixin, get_wx_jsapi_ticket_from_weixin

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
        now = timezone.now()
        now_timestamp = int(now.timestamp())
        kwargs['server_timestamp'] = now_timestamp

        # if current_user.parent:
        #     coupons = models.Coupon.objects.filter(parent=current_user.parent,
        #         validated_start__lte=now, expired_at__gt=now, used=False
        #     ).order_by('-amount', 'expired_at')
        #     kwargs['coupon'] = coupons.first()

        nonce_str = random_string().replace('-','')
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


def _get_wx_jsapi_ticket(access_token):
    jsapi_ticket = _get_wx_jsapi_ticket_from_db()
    msg = None
    if not jsapi_ticket:
        result = get_wx_jsapi_ticket_from_weixin(access_token)
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
        result = get_token_from_weixin()
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
        openid = request.POST.get("temptype", None)

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
    tempId = settings.WECHAT_PAY_INFO_TEMPLATE

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


@csrf_exempt
def teacher_view(request):
    template_name = 'wechat/teacher/teacher.html'
    openid = request.GET.get("openid", None)

    if not openid:
        openid = request.POST.get("openid", None)

    teacherid = request.GET.get("teacherid", None)
    if not teacherid:
        teacherid = request.POST.get("teacherid", None)

    teacher = []
    gender = None
    try:
        teacher = models.Teacher.objects.get(id=teacherid)
        profile = models.Profile.objects.get(user=teacher.user)

        gender_dict = {"f": "女", "m": "男", "u": ""}
        gender = gender_dict.get(profile.gender, "")
    except:
        pass

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

    now = timezone.now()
    now_timestamp = int(now.timestamp())

    nonce_str = random_string().replace('-','')
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
def check_phone(request):
    phone = request.POST.get("phone", None)
    code = request.POST.get("code", None)
    Profile = models.Profile
    CheckCode = models.Checkcode
    Parent = models.Parent
    new_user = True
    try:
        profile = Profile.objects.get(phone=phone)
        user = profile.user
        for backend, backend_path in _get_backends(return_tuples=True):
            user.backend = backend_path
            break
        parent = Parent.objects.get(user=user)
        new_user = False
    except Profile.DoesNotExist:
        # new user
        user = Parent.new_parent()
        parent = user.parent
        profile = parent.user.profile
        profile.phone = phone
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
def phone_page(request):
    template_name = 'wechat/parent/reg_phone.html'
    context = {
        "zl": "aaa"
    }
    return render(request, template_name, context)

@csrf_exempt
<<<<<<< 9f0a4eaca47d77aebb583489fbd03d77f007252b
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
    new_user = True
    try:
        profile = Profile.objects.get(phone=phone)
        user = profile.user
        for backend, backend_path in _get_backends(return_tuples=True):
            user.backend = backend_path
            break
        parent = Parent.objects.get(user=user)
        new_user = False
    except Profile.DoesNotExist:
        # new user
        user = Parent.new_parent()
        parent = user.parent
        profile = parent.user.profile
        profile.phone = phone
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
=======
def check_phone(request):
    get_openid_url = 'https://api.weixin.qq.com/sns/oauth2/access_token?grant_type=authorization_code'
    wx_code = request.GET.get('code', None)
    if not wx_code:
        wx_code = request.POST.get('code', None)
    nextpage = request.GET.get('nextpage', None)
    if not nextpage:
        nextpage = request.POST.get('nextpage', None)
    print('.......in check phone.....')
    get_openid_url += '&appid=' + settings.WEIXIN_APPID
    get_openid_url += '&secret=' + settings.WEIXIN_APP_SECRET
    get_openid_url += '&code=' + '001d11713c28f9def0ab3a3ab80ddb8M'
    req = requests.get(get_openid_url)
    print(req.status_code)
    ret = None
    openid = None
    if req.status_code == 200:
        ret = json.loads(req.text)
        if "openid" in ret:
            openid = ret["openid"]
        if "errcode" in ret:
            pass
    if openid:
        print("得到了openid")
        profiles = models.Profile.objects.filter(wx_openid=openid).order_by('-id')
        print(profiles)
        lastOne = list(profiles) and profiles[0]
        print(lastOne)
        print('end...')
        if lastOne:
            return HttpResponseRedirect(nextpage)
        else:
            return HttpResponseRedirect("/wechat/phone_page/?openid="+openid)
    print(ret)
    print(".....................end.......")
    return HttpResponseRedirect("/wechat/phone_page/")
>>>>>>> WEC-8:back file
