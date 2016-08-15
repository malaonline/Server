import logging
import json
import requests
import datetime
import math
import time
from urllib.parse import urlencode

# django modules
from django.views.decorators.csrf import csrf_exempt
from django.shortcuts import render, get_object_or_404, redirect
from django.views.generic import View, TemplateView, ListView, DetailView
from django.db.models import Q,Count
from django.conf import settings
from django.http import HttpResponse, JsonResponse, HttpResponseRedirect
from django.utils import timezone
from django.contrib.auth import _get_backends, login
from django.core.urlresolvers import reverse
from django.utils.decorators import method_decorator

# local modules
from app import models
from app.utils import get_server_host
from app.utils.smsUtil import tpl_send_sms, TPL_STU_PAY_FAIL
from app.utils.types import parseInt
from app.exception import TimeSlotConflict, OrderStatusIncorrect, RefundError
from app.tasks import registerKuaiLeXueUserByOrder
from .wxapi import *

logger = logging.getLogger('app')

# Create your views here.


def _get_default_bankend_path():
    for backend, backend_path in _get_backends(return_tuples=True):
        return backend_path


def _get_parent(request):
    parent = None
    if not request.user.is_anonymous():
        try:
            parent = request.user.parent
        except:
            pass
    if not parent:
        # 通过 wx_openid 获得家长
        openid = request.GET.get("openid", None)
        if not openid:
            openid = request.POST.get("openid", None)
        if openid:
            profile = models.Profile.objects.filter(wx_openid=openid).order_by('-id').first()
            try:
                parent = profile and profile.user.parent or None
            except:
                pass
        if parent:
            parent.user.backend = _get_default_bankend_path()
            login(request, parent.user)
    return parent


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


# class TeacherDetailView(DetailView):
#     model = models.Teacher


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

    def get_context_data(self, **kwargs):
        context = super(SchoolsView, self).get_context_data(**kwargs)
        openid = self.request.GET.get("openid", None) or self.request.POST.get("openid", None)
        cur_url = self.request.build_absolute_uri()

        # schools = self.model.objects.all()
        # photosdic = {}
        # for school in schools:
        #     photosdic[school.id] = school.get_photo_url_list()
        # photosdic = json.dumps(photosdic)

        sign_data = _jssdk_sign(cur_url)

        context['WX_OPENID'] = openid
        context['WX_APPID'] = settings.WEIXIN_APPID
        context['WX_NONCE_STR'] = sign_data['noncestr']
        context['WX_SIGNITURE'] = sign_data['signature']
        context['server_timestamp'] = sign_data['timestamp']
        # context['photosdic'] = photosdic

        return context


class SchoolMapView(DetailView):
    model = models.School
    context_object_name = 'school'
    template_name = 'wechat/school/school_map.html'

    def get_context_data(self, **kwargs):
        context = super(SchoolMapView, self).get_context_data(**kwargs)
        context['amap_api_key'] = settings.AMAP_API_KEY
        return context


class SchoolPhotosView(DetailView):
    model = models.School
    context_object_name = 'school'
    template_name = 'wechat/school/school_photo.html'


def _get_auth_redirect_url(request, teacher_id):
    if settings.TESTING:
        return reverse('wechat:phone_page') + '?state='+str(teacher_id)
    checkPhoneURI = get_server_host(request)+reverse('wechat:check_phone')
    params_str = {
        'redirect_uri': checkPhoneURI,
        'response_type': "code",
        'scope': "snsapi_base",
        'state': teacher_id,
        'connect_redirect': "1"
    }
    redirect_url = WX_AUTH_URL + '&' + urlencode(params_str) + '#wechat_redirect'
    return redirect_url


@method_decorator(csrf_exempt, name='dispatch')
class OrderBaseView(View):

    def get_teacher(self, request):
        teacher_id = request.GET.get('teacher_id', -1)
        return get_object_or_404(models.Teacher, pk=teacher_id)

    def get_parent(self, request):
        parent = _get_parent(request)
        if parent is None and settings.TESTING:
            # the below line is only for testing
            parent = models.Parent.objects.get(pk=3)
            parent.user.backend = _get_default_bankend_path()
            login(request, parent.user)
        return parent


class CourseChoosingView(OrderBaseView):
    template_name = 'wechat/order/course_choosing.html'
    # 确认订单页面
    confirm_page = 'wechat/order/confirm.html'

    def get(self, request):
        step = request.GET.get('step')
        if step == 'confirm_page':
            return self._get_confirm_page(request)
        kwargs = {}
        kwargs['teacher'] = teacher = self.get_teacher(request)
        parent = self.get_parent(request)
        if parent is None:
            redirect_url = _get_auth_redirect_url(request, teacher.id)
            logger.warning(redirect_url)
            return HttpResponseRedirect(redirect_url)
        kwargs['parent'] = parent
        subject = teacher.subject()  # 目前老师只有一个科目
        order_count = models.Order.objects.filter(
                parent=parent, subject=subject,
                status=models.Order.PAID).count()
        first_buy = order_count <= 0  # 对于当前科目来说, 是第一次购买
        kwargs['first_buy'] = first_buy
        kwargs['evaluate_time'] = int(models.TimeSlot.GRACE_TIME.total_seconds())  # 第一次购买某个科目时, 建档需要的时间, 精确到秒
        prices = list(teacher.prices())
        prices.sort(key=lambda x: x.ability.grade_id)
        kwargs['prices'] = prices
        # schools = teacher.schools.all()
        schools = list(models.School.objects.filter(opened=True))
        kwargs['schools'] = schools
        kwargs['daily_time_slots'] = models.WeeklyTimeSlot.DAILY_TIME_SLOTS
        now = timezone.now()
        now_timestamp = int(now.timestamp())
        kwargs['server_timestamp'] = now_timestamp

        url = request.build_absolute_uri()
        sign_data = _jssdk_sign(url)
        kwargs.update(sign_data)
        kwargs['WX_APPID'] = settings.WEIXIN_APPID
        return render(request, self.template_name, kwargs)

    def _get_confirm_page(self, request):
        kwargs = {}
        kwargs['teacher'] = teacher = self.get_teacher(request)
        parent = self.get_parent(request)
        if parent is None:
            redirect_url = _get_auth_redirect_url(request, teacher.id)
            logger.warning(redirect_url)
            return HttpResponseRedirect(redirect_url)
        kwargs['parent'] = parent
        grade_id = request.GET.get('grade_id')
        grade = models.Grade.objects.get(id=grade_id)
        kwargs['grade_subject_name'] = grade.name + teacher.subject().name
        school_id = request.GET.get('school_id')
        school = models.School.objects.get(id=school_id)
        kwargs['school_name'] = school.name

        # wxsdk config
        url = request.build_absolute_uri()
        sign_data = _jssdk_sign(url)
        kwargs.update(sign_data)
        kwargs['WX_APPID'] = settings.WEIXIN_APPID
        return render(request, self.confirm_page, kwargs)

    def post(self, request):
        action = request.POST.get('action')
        if action == 'confirm':
            return self.confirm_order(request)
        if action == 'verify':
            return self.verify_order(request)
        if action == 'schools_dist':
            return self.schools_distance(request)
        return HttpResponse("Not supported request.", status=403)

    def confirm_order(self, request):
        parent = self.get_parent(request)
        if not parent:
            return JsonResponse({'ok': False, 'msg': '您还未登录', 'code': 403})
        if settings.TESTING:
            # the below line is real wx_openid, but not related with ours server
            wx_openid = 'oUpF8uMuAJO_M2pxb1Q9zNjWeS6o'
        else:
            wx_openid = parent.user.profile.wx_openid
        if not wx_openid:
            return JsonResponse({'ok': False, 'msg': '您还未关注公共号', 'code': 403})
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
        teacher = get_object_or_404(models.Teacher, pk=teacher_id)
        school = get_object_or_404(models.School, pk=school_id)
        grade = get_object_or_404(models.Grade, pk=grade_id)
        subject = teacher.subject() # 老师只有一个科目
        coupon = coupon_id and coupon_id != '0' and get_object_or_404(models.Coupon, pk=coupon_id) or None
        weekly_time_slots = [get_object_or_404(models.WeeklyTimeSlot, pk=w_id) for w_id in weekly_time_slot_ids]
        if coupon:
            if coupon.used:
                return JsonResponse({'ok': False, 'msg': '您所选择奖学金已使用, 请重新选择', 'code': 2})
            # 使用期限不满足
            if not coupon.check_date():
                return JsonResponse({'ok': False, 'msg': '您所选择奖学金不在使用有效期内, 请重新选择', 'code': 2})
            # 限制条件不满足
            ability = get_object_or_404(
                models.Ability, grade=grade, subject=subject)
            price = teacher.region.price_set.get(
                ability=ability, level=teacher.level).price
            if hours < coupon.mini_course_count or price * hours < coupon.mini_total_price:
                return JsonResponse({'ok': False, 'msg': '您所选择奖学金不满足使用条件, 请重新选择', 'code': 2})

        periods = [(s.weekday, s.start, s.end) for s in weekly_time_slots]
        if not teacher.is_longterm_available(periods, school, parent):
            return JsonResponse({'ok': False, 'msg': '该老师部分时段已被占用, 请重新选择上课时间', 'code': 3})

        # create order
        order = models.Order.objects.create(
                parent=parent, teacher=teacher, school=school,
                grade=grade, subject=subject, hours=hours, coupon=coupon)
        order.weekly_time_slots.add(*weekly_time_slots)
        order.save()
        order_data = {}
        order_data['order_id'] = order.order_id
        order_data['orders_api_url'] = '/api/v1/orders/%s' % order.id
        if settings.TESTING:
            order_data['TESTING'] = settings.TESTING
            charge = models.Charge()
            charge.order = order
            charge.ch_id = 'ch_%s' % (order.order_id)
            charge.created = timezone.now()
            charge.channel = models.Charge.WX_PUB_MALA
            charge.order_no = order.order_id
            charge.amount = order.to_pay
            charge.save()
            return JsonResponse({'ok': True, 'msg': '', 'code': '', 'data': order_data})
        # get wx pay order
        ret_json = wx_pay_unified_order(order, request, wx_openid)
        if not ret_json['ok']:
            return JsonResponse({'ok': False, 'msg': ret_json['msg'], 'code': -500})
        # 构造js-sdk 支付接口参数 appId, timeStamp, nonceStr, package, signType
        data = {}
        data['timeStamp'] = int(timezone.now().timestamp())
        data['nonceStr'] = make_nonce_str()
        data['package'] = 'prepay_id={id}'.format(id=ret_json['data']['prepay_id'])
        data['signType'] = 'MD5'
        data['appId'] = settings.WEIXIN_APPID
        data['paySign'] = wx_sign_for_pay(data)
        data['prepay_id'] = ret_json['data']['prepay_id']
        data.update(order_data)
        logger.debug(data)
        return JsonResponse({'ok': True, 'msg': '', 'code': '', 'data': data})

    def verify_order(self, request):
        # get request params
        prepay_id = request.POST.get('prepay_id')
        order_id = request.POST.get('order_id')
        if settings.TESTING:
            ret_code = set_order_paid(order_id=order_id)
            if ret_code == 1:
                return JsonResponse({'ok': False, 'msg': FAIL_HINT_MSG, 'code': 4})
            return JsonResponse({'ok': True, 'msg': '', 'code': 0})
        query_ret = wx_pay_order_query(order_id=order_id)
        if query_ret['ok']:
            trade_state = query_ret['data']['trade_state']
            if trade_state == WX_SUCCESS:
                # 支付成功, 设置订单支付成功, 并且生成课程安排
                try:
                    ret_code = set_order_paid(prepay_id=prepay_id, order_id=query_ret['data']['out_trade_no'], open_id=query_ret['data']['openid'])
                    if ret_code == 1:
                        return JsonResponse({'ok': False, 'msg': FAIL_HINT_MSG, 'code': 4})
                except (OrderStatusIncorrect, RefundError):
                    return JsonResponse({'ok': False, 'msg': FAIL_HINT_MSG, 'code': 4})
                except Exception as ex:
                    logger.exception(ex)
                    return JsonResponse({'ok': False, 'msg': '未知异常, 请稍后重试', 'code': 5})
                return JsonResponse({'ok': True, 'msg': '', 'code': 0})
            else:
                if trade_state == WX_PAYERROR:
                    return {'ok': False, 'msg': '支付失败', 'code': 2}
                else:
                    return {'ok': False, 'msg': '未支付', 'code': 3}
        else:
            return {'ok': False, 'msg': query_ret['msg'], 'code': 1}

    def schools_distance(self, request):
        lat = request.POST.get('lat', None)
        lng = request.POST.get('lng', None)
        if lat is None or lat == '' or lng is None or lng == '':
            return JsonResponse({'ok': False})
        lat = float(lat)
        lng = float(lng)
        # schools = teacher.schools.all()
        schools = models.School.objects.filter(opened=True)
        distances = []
        p = {'lat': lat, 'lng': lng}
        for school in schools:
            if school.latitude is None or school.longitude is None:
                distances.append({'id': school.id, 'far': ''})
                continue
            sp = {'lat': school.latitude, 'lng': school.longitude}
            dis = calculateDistance(p, sp)
            distances.append({'id': school.id, 'far': dis})
        return JsonResponse({'ok': True, 'list': distances})


class CouponListView(OrderBaseView):
    template_name = 'wechat/order/coupon_list.html'

    def get(self, request):
        kwargs = {}
        kwargs['teacher'] = teacher = self.get_teacher(request)
        parent = self.get_parent(request)
        if parent is None:
            redirect_url = _get_auth_redirect_url(request, teacher.id)
            logger.warning(redirect_url)
            return HttpResponseRedirect(redirect_url)
        kwargs['parent'] = parent

        now = timezone.now()
        coupons = models.Coupon.objects.filter(parent=parent, expired_at__gt=now, used=False
                                        ).order_by('used', '-amount', 'expired_at')

        kwargs['coupons'] = sorted(coupons, key=lambda x: x.sort_key())
        pre_chosen_coupon = None
        # for coupon in coupons:
        #     if coupon.usable and coupon.mini_course_count==0:
        #         pre_chosen_coupon = coupon
        #         break
        # pre_chosen_coupon = pre_chosen_coupon or coupons.first()
        kwargs['pre_chosen_coupon'] = pre_chosen_coupon
        return render(request, self.template_name, kwargs)


class EvaluateListView(OrderBaseView):
    """
    测评建档服务列表
    """
    template_name = 'wechat/order/evaluate_list.html'

    def get(self, request):
        kwargs = {}
        kwargs['teacher'] = teacher = self.get_teacher(request)
        parent = self.get_parent(request)
        if parent is None:
            redirect_url = _get_auth_redirect_url(request, teacher.id)
            logger.warning(redirect_url)
            return HttpResponseRedirect(redirect_url)
        kwargs['parent'] = parent
        return render(request, self.template_name, kwargs)


def _jssdk_sign(url):
    now = timezone.now()
    now_timestamp = int(now.timestamp())
    nonce_str = make_nonce_str()
    access_token, msg = _get_wx_token()
    jsapi_ticket, msg = _get_wx_jsapi_ticket(access_token)
    data = {'noncestr': nonce_str,
            'jsapi_ticket': jsapi_ticket,
            'timestamp': now_timestamp,
            'url': url}
    signature = wx_signature(data)
    return {'noncestr': nonce_str,
            'timestamp': now_timestamp,
            'signature': signature}


def set_order_paid(prepay_id=None, order_id=None, open_id=None):
    """
    支付成功, 设置订单支付成功, 并且生成课程安排
    有两个地方调用:
        1, 刚支付完, verify order 主动去微信查询订单状态, 当支付成功时调用
        2, 接受微信支付结果异步通知中, 当支付成功时调用
    return:
        0, all ok
        1, 分配上课时间失败
        -1, ignore it
    """
    logger.debug('wx_pub_pay try to set_order_paid, order_no: '+str(order_id)+', prepay_id: '+str(prepay_id)+', open_id: '+str(open_id))
    charge = None
    if prepay_id:
        charge = models.Charge.objects.get(ch_id=prepay_id)
    elif order_id:
        charge = models.Charge.objects.get(order__order_id=order_id)
    # if charge.paid:
    #     return # 已经处理过了, 直接返回
    charge.paid = True
    charge.time_paid = timezone.now()
    # charge.transaction_no = ''
    charge.save()

    order = charge.order
    if not order_id:
        order_id = order.order_id

    if order.status == models.Order.PAID:
        return -1 # 已经处理过了, 直接返回
    order.status = models.Order.PAID
    order.paid_at = timezone.now()
    order.save()

    logger.debug('wx_pub_pay set_order_paid, allocate_timeslots order_no: '+str(order_id))
    try:
        models.Order.objects.allocate_timeslots(order)
        # 微信通知用户购课成功信息
        send_pay_info_to_user(open_id, order_id)
        # 把学生和老师注册到快乐学
        registerKuaiLeXueUserByOrder.apply_async((order.id,), retry=True, retry_policy={
            'max_retries': 3,
            'interval_start': 10,
            'interval_step': 20,
            'interval_max': 30,
        })
        return 0
    except TimeSlotConflict:
        logger.warning('timeslot conflict, do refund, order_id: '+str(order_id))
        # 微信通知用户失败信息
        send_pay_fail_to_user(open_id, order_id)
        # 短信通知家长
        try:
            phone = order.parent.user.profile.phone
            tpl_send_sms(phone, TPL_STU_PAY_FAIL)
        except Exception as ex:
            logger.error(ex)
        # 退款事宜操作
        try:
            models.Order.objects.refund(
                    order, '课程被抢占，自动退款', order.parent.user)
        except OrderStatusIncorrect as e:
            logger.exception(e)
            raise e
        except RefundError as e:
            logger.exception(e)
            raise e
        return 1 # 没有其他错误, 返回分配上课时间失败


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
def wx_pay_notify_view(request):
    """
    接受微信支付结果异步通知view
    """
    req_json = resolve_wx_pay_notify(request)
    if not req_json['ok']:
        return HttpResponse(wx_dict2xml({'return_code': WX_FAIL, 'return_msg': ''}))
    data = req_json['data']
    openid = data['openid']
    wx_order_id = data['transaction_id']
    order_id = data['out_trade_no']
    try:
        set_order_paid(order_id=order_id, open_id=openid)
    except:
        pass # 该view为异步调用, 忽略错误
    return HttpResponse(wx_dict2xml({'return_code': WX_SUCCESS, 'return_msg': ''}))


def _try_send_wx_tpl_msg(tpl_id, openid, data, times=1):
    if not openid:
        return False
    while (times > 0):
        try:
            access_token, msg = _get_wx_token()
            ret_json = wx_send_tpl_msg(access_token, tpl_id, openid, data)
            if 'msgid' in ret_json:
                return ret_json['msgid']
            errcode = ret_json.get('errcode')
            if errcode == 40001: # access_token失效, 重新获取
                wx_get_token()
        except Exception as ex:
            logger.error(ex)
        times -= 1
    return False


def send_pay_info_to_user(openid, order_no):
    """
    给微信用户发送购课成功信息
    """
    order = models.Order.objects.get(order_id=order_no)
    data = {
        "first": {
            "value": "感谢您购买麻辣老师课程！"
        },
        "keyword1": {
            "value": order.grade.name + order.subject.name
        },
        "keyword2": {
            "value": order.teacher.name
        },
        "keyword3": {
            "value": '课时费'
        },
        "keyword4": {
            "value": order.parent.student_name or order.parent.user.profile.mask_phone()
        },
        "keyword5": {
            "value": "%.2f元"%(order.to_pay/100)
        },
        "remark": {
            "value": '有任何疑问请拨打客服电话'+settings.SERVICE_SUPPORT_TEL
        }
    }
    tpl_id = settings.WECHAT_PAY_INFO_TEMPLATE
    _try_send_wx_tpl_msg(tpl_id, openid, data, 3)


FAIL_HINT_MSG = '您好，该老师该时段课程已被抢购，您可重新选择课时进行购买。' \
                '我们将在24小时内为您退款。退款事宜请联系客服：%s' % (settings.SERVICE_SUPPORT_TEL,)

def send_pay_fail_to_user(openid, order_no):
    """
    给微信用户发送购课失败信息
    """
    order = models.Order.objects.get(order_id=order_no)
    data = {
        "first": {
            "value": "您好，该老师该时段课程已被抢购，您可重新选择课时进行购买。"
        },
        "keyword1": {
            "value": order.grade.name + order.subject.name
        },
        "keyword2": {
            "value": order.order_id
        },
        "remark": {
            "value": '我们将在24小时内为您退款。退款事宜请联系客服：'+settings.SERVICE_SUPPORT_TEL
        }
    }
    tpl_id = settings.WECHAT_PAY_FAIL_TEMPLATE
    _try_send_wx_tpl_msg(tpl_id, openid, data, 3)


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
    parent = _get_parent(request)
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

    teacher_grade_ids = [grade.id for grade in teacher.grades()]
    grades_all = models.Grade.objects.all()
    _heap = {}
    grades_tree = []
    for grade in grades_all:
        if not grade.superset_id:
            _temp = {'id':grade.id, 'name':grade.name, 'children':[]}
            _heap[grade.id] = _temp
        else:
            _temp = _heap[grade.superset_id]
            _temp['children'].append({'id':grade.id, 'name':grade.name})
    # 过滤该老师的
    for _, _grade in _heap.items():
        _children = _grade['children']
        _exists = []
        for _child in _children:
            if _child['id'] in teacher_grade_ids:
                _exists.append(_child)
        if len(_exists) > 0:
            _name = _grade['name']
            _grade['children'] = _exists
            if _name == '小学':
                _grade['key'] = 'elementary'
            if _name == '初中':
                _grade['key'] = 'middle'
            if _name == '高中':
                _grade['key'] = 'high'
            grades_tree.append(_grade)
    grades_tree.sort(key=lambda x: x['id'])

    schools = models.School.objects.filter(opened=True)

    cur_url = request.build_absolute_uri()
    sign_data = _jssdk_sign(cur_url)

    context = {
        "server_timestamp": sign_data['timestamp'],
        "WX_APPID": settings.WEIXIN_APPID,
        "WX_NONCE_STR": sign_data['noncestr'],
        "WX_SIGNATURE": sign_data['signature'],
        "openid": openid,
        "gender": gender,
        "tags": list(teacher.tags.all()),
        "achievements": achievements,
        "memberService": list(memberService),
        "subjects": models.Subject.objects.all,
        "grades_tree": grades_tree,
        "teacher_grade_ids": teacher_grade_ids,
        "teacher": teacher,
        "schools": schools,
        "hasLogin": parent and True or False,
        "isFavorite": parent and models.Favorite.isFavorite(parent, teacher) or False,
        "isTesting": settings.TESTING
    }

    return render(request, template_name, context)


@csrf_exempt
def getSchoolsWithDistance(request):
    lat = request.POST.get("lat", None)
    lng = request.POST.get("lng", None)

    point = None
    if lat is not None and lng is not None:
        try:
            point = {
                'lat': float(lat),
                'lng': float(lng)
            }
        except:
            pass

    if not point:
        JsonResponse({'ok': False, 'msg': 'no lat,lng', 'code': -1})
    schools = models.School.objects.filter(opened=True)
    ret = []
    for school in schools:
        pointB = None
        sc = {
            'name': school.name,
            'img': school.get_thumbnail,
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

  return math.acos(math.sin(toRadians * pointA["lat"]) * math.sin(toRadians * pointB["lat"]) + math.cos(toRadians * pointA["lat"]) * math.cos(
      toRadians * pointB["lat"]) * math.cos(toRadians * pointB["lng"] - toRadians * pointA["lng"])) * R;


@csrf_exempt
def phone_page(request):
    template_name = 'wechat/parent/reg_phone.html'
    teacherId = request.GET.get('state', None) # 注册, 报名, 收藏

    openid = request.GET.get("openid", None)
    if not openid:
        openid = request.POST.get("openid", None)

    nextpage = _get_reg_next_page(teacherId, openid)
    parent = _get_parent(request)
    if parent:
        return HttpResponseRedirect(nextpage)

    if not openid and settings.TESTING:
        # the below line is real wx_openid, but not related with ours server
        openid = 'oUpF8uMuAJO_M2pxb1Q9zNjWeS6o'

    context = {
        "openid": openid,
        "teacherId": teacherId,
        "nextpage": nextpage
    }
    return render(request, template_name, context)


@csrf_exempt
def add_openid(request):
    phone = request.POST.get("phone", None)
    code = request.POST.get("code", None)
    openid = request.POST.get("openid", None)
    stu_name = request.POST.get("name", None)
    if not stu_name:
        return JsonResponse({'result': False, 'code': -4})
    if not openid or openid == 'None':
        return JsonResponse({
            "result": False,
            "code": -1
        })

    CheckCode = models.Checkcode
    if not CheckCode.verify(phone, code)[0]:
        # 验证失败
        return JsonResponse({
            "result": False,
            "code": -2
        })

    Profile = models.Profile
    Parent = models.Parent
    try:
        profile = Profile.objects.get(phone=phone)
        if profile.wx_openid and profile.wx_openid != openid:
            return JsonResponse({
                "result": False,
                "code": -3
            })
        profile.wx_openid = openid
        profile.save()
        user = profile.user
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

    parent.student_name = stu_name
    parent.save()
    parent.user.backend = _get_default_bankend_path()
    login(request, parent.user)

    return JsonResponse({
        "result": True
    })


def _get_reg_next_page(state, openid):
    '''
    微信注册后的下一个页面
    '''
    if not state or state == 'ONLY_REGISTER':
        return reverse('wechat:register')+'?step=success'
    if state.startswith('FAVORITE_'):
        id = state[len('FAVORITE_'):]
        return reverse('wechat:teacher')+'?teacherid='+str(id)
    return reverse('wechat:order-course-choosing')+'?teacher_id='+str(state)+'&openid='+str(openid)


@csrf_exempt
def check_phone(request):
    get_openid_url = 'https://api.weixin.qq.com/sns/oauth2/access_token?grant_type=authorization_code'
    wx_code = request.GET.get('code', None)
    teacherId = request.GET.get('state', None) # 注册, 报名, 收藏

    if wx_code is None or wx_code == 'None' or wx_code == '':
        return HttpResponse(json.dumps({
            "msg": '请关注麻辣老师公众号, 通过公众号访问！',
            "code": -1
        }, ensure_ascii=False))

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
            logger.debug(ret)
    else:
        logger.debug(req.status_code)

    nextpage = _get_reg_next_page(teacherId, openid)

    if openid:
        profiles = models.Profile.objects.filter(wx_openid=openid).order_by('-id')
        lastOne = list(profiles) and profiles[0]
        if lastOne:
            return HttpResponseRedirect(nextpage)

    context = {
        "openid": openid,
        "teacherId": teacherId,
        "nextpage": nextpage
    }
    sign_data = _jssdk_sign(request.build_absolute_uri())
    context.update(sign_data)
    context['WX_APPID'] = settings.WEIXIN_APPID
    return render(request, 'wechat/parent/reg_phone.html', context)


@csrf_exempt
def policy(request):
    content = ''
    try:
        wxpolicy = models.StaticContent.objects.get(name='wxpolicy')
        content = wxpolicy.content
    except models.StaticContent.DoesNotExist:
        logger.debug('models.StaticContent.DoesNotExist')
    return JsonResponse({
        "result": True,
        "policy": json.dumps(content)
    })


class ReportSampleView(View):
    template_name = 'wechat/report/sample.html'

    def get(self, request, *args, **kwargs):
        return render(request, self.template_name, kwargs)


class VipView(View):
    template_name = 'wechat/vip/privileges.html'

    def get(self, request, *args, **kwargs):
        return render(request, self.template_name, kwargs)


class RegisterRedirectView(View):

    def get(self, request):
        step = request.GET.get('step')
        if step == 'success':
            context = {}
            sign_data = _jssdk_sign(request.build_absolute_uri())
            context.update(sign_data)
            context['WX_APPID'] = settings.WEIXIN_APPID
            return render(request, 'wechat/parent/reg_success.html', context)
        reg_url = _get_auth_redirect_url(request, 'ONLY_REGISTER')
        return redirect(reg_url)
