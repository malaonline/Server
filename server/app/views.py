import json
import logging
import datetime
import itertools
from collections import OrderedDict
import requests

from django.contrib.auth.models import User, Group
from django.shortcuts import get_object_or_404
from django.views.generic import View
from django.views.decorators.csrf import csrf_exempt
from django.http import HttpResponse, JsonResponse
from django.db.models import Q, F, Min, Max
from django.core import exceptions
from django.utils.decorators import method_decorator
from django.utils import timezone
from django.conf import settings

from rest_framework.authtoken.models import Token
from rest_framework.views import APIView
from rest_framework import serializers, viewsets, permissions, generics, mixins
from rest_framework.exceptions import PermissionDenied
from rest_framework.pagination import PageNumberPagination
from rest_framework import status
from rest_framework.response import Response

from app import models
from app.pingpp import pingpp
from app.utils import random_name
from app.utils import random_pad_token
from app.utils.smsUtil import isValidPhone, isValidCode, tpl_send_sms, \
        TPL_STU_PAY_FAIL
from app.utils.algorithm import verify_sig, verify_sha1_sig, sign_sha1
import app.utils.klx_api as klx
from app.exception import TimeSlotConflict, OrderStatusIncorrect, RefundError,\
        KuailexueDataError, KuailexueServerError
# from .forms import autoConfirmForm

from .tasks import autoConfirmClasses, registerKuaiLeXueUserByOrder

logger = logging.getLogger('app')


class LargeResultsSetPagination(PageNumberPagination):
    page_size = 300
    page_size_query_param = 'page_size'
    max_page_size = 5000


class HugeResultsSetPagination(PageNumberPagination):
    page_size = 50000
    page_size_query_param = 'page_size'
    max_page_size = None


class PolicySerializer(serializers.ModelSerializer):
    updated_at = serializers.SerializerMethodField()

    class Meta:
        model = models.StaticContent
        fields = ('content', 'updated_at',)

    def get_updated_at(self, obj):
        return int(obj.updated_at.timestamp())


class Policy(generics.RetrieveAPIView):
    queryset = models.StaticContent.objects.all()
    serializer_class = PolicySerializer

    def get_object(self):
        obj = get_object_or_404(models.StaticContent, name='policy')
        return obj


class ChargeSucceeded(View):
    @method_decorator(csrf_exempt)
    def dispatch(self, request, *args, **kwargs):
        return super(ChargeSucceeded, self).dispatch(request, *args, **kwargs)

    def post(self, request):
        body = request.body
        if not settings.TESTING:
            sig = request.META.get(
                    'HTTP_X_PINGPLUSPLUS_SIGNATURE').encode('utf-8')
            pub_key = settings.PINGPP_PUB_KEY
            if not verify_sig(body, sig, pub_key):
                raise PermissionDenied()

        data = json.loads(body.decode('utf-8'))
        logger.info(data)

        if data['type'] != 'charge.succeeded':
            raise PermissionDenied()

        obj = data['data']['object']
        charge = models.Charge.objects.get(ch_id=obj['id'])

        assert obj['paid']
        charge.paid = obj['paid']
        charge.time_paid = timezone.make_aware(
                datetime.datetime.fromtimestamp(obj['time_paid']))
        charge.transaction_no = obj['transaction_no']
        charge.save()

        order = charge.order
        order_charge_available = True
        # 如果订单已经取消, 包括超时自动取消, 则走退款流程
        if order.status == models.Order.CANCELED:
            order_charge_available = False
        # 一对一不允许对下架老师下单, 双师直播下架老师可能是助教, 允许下单
        elif not order.is_live() and not order.is_teacher_published():
            order_charge_available = False
        order.status = order.PAID
        order.paid_at = timezone.now()
        order.save()
        if not order_charge_available:
            try:
                models.Order.objects.refund(
                    order, '订单已取消，自动退款', order.parent.user)
                # 再次修改订单状态为已取消
                order.cancel()
                return JsonResponse({'ok': 1})
            except OrderStatusIncorrect as e:
                logger.error(e)
                raise e
            except RefundError as e:
                logger.error(e)
                raise e
        try:
            models.Order.objects.allocate_timeslots(order)
            # 把学生和老师注册到快乐学
            registerKuaiLeXueUserByOrder.apply_async((order.id,), retry=True, retry_policy={
                'max_retries': 3,
                'interval_start': 10,
                'interval_step': 20,
                'interval_max': 30,
            })
            return JsonResponse({'ok': 1})
        except TimeSlotConflict:
            logger.info('timeslot conflict, do refund')
            # 短信通知家长失败信息
            try:
                phone = order.parent.user.profile.phone
                tpl_send_sms(phone, TPL_STU_PAY_FAIL)
            except Exception as ex:
                logger.error(ex)
            try:
                models.Order.objects.refund(
                        order, '课程被抢占，自动退款', order.parent.user)
                return JsonResponse({'ok': 1})
            except OrderStatusIncorrect as e:
                logger.error(e)
                raise e
            except RefundError as e:
                logger.error(e)
                raise e


class ConcreteTimeSlots(View):
    def get(self, request):
        hours = int(request.GET.get('hours'))
        teacher = get_object_or_404(
                models.Teacher, pk=request.GET.get('teacher'))
        assert hours % 2 == 0
        assert hours > 0

        hours = min(hours, 200)  # Only return first 100 timeslots

        weekly_time_slots = request.GET.get('weekly_time_slots').split()
        weekly_time_slots = [get_object_or_404(models.WeeklyTimeSlot, pk=x)
                             for x in weekly_time_slots]
        data = models.Order.objects.concrete_timeslots(
                hours, weekly_time_slots, teacher)
        data.sort(key=lambda x: x['start'])
        data = [(int(x['start'].timestamp()),
                 int(x['end'].timestamp())) for x in data]

        return JsonResponse({'data': data})


class Sms(View):

    @method_decorator(csrf_exempt)
    def dispatch(self, request, *args, **kwargs):
        return super(Sms, self).dispatch(request, *args, **kwargs)

    # @method_decorator(csrf_exempt) # here it doesn't work
    def post(self, request):
        if request.META.get('CONTENT_TYPE', '').startswith('application/json'):
            try:
                jsonData = json.loads(request.body.decode())
            except ValueError:
                return HttpResponse(status=400)
            action = jsonData.get('action')
            phone = jsonData.get('phone')
            code = jsonData.get('code')
        else:
            action = request.POST.get('action')
            phone = request.POST.get('phone')
            code = request.POST.get('code')
        if action == 'send':
            if not phone:
                return JsonResponse({'sent': False,
                                     'reason': 'phone is required'})
            if not isValidPhone(phone):
                return JsonResponse({'sent': False,
                                     'reason': 'phone is wrong'})
            # try:
            # generate code
            generate, result = models.Checkcode.generate(phone)
            if generate is True:
                return JsonResponse({"sent": True})
            else:
                return JsonResponse(
                        {"sent": False, "result": "%s" % (result, )})
        if action == 'verify':
            if not phone or not code:
                return JsonResponse({'verified': False,
                                     'reason': 'params error'})
            if not isValidPhone(phone):
                return JsonResponse({'sent': False,
                                     'reason': 'phone is wrong'})
            if not isValidCode(code):
                return JsonResponse({'sent': False, 'reason': 'code is wrong'})
            try:
                is_valid, err_no = models.Checkcode.verify(phone, code)
                if not is_valid:
                    return JsonResponse({
                        'verified': False,
                        'reason': err_no == 3 and 'Retry too much times' or
                        'SMS not match or is expired'})
                # find User
                is_found = False
                try:
                    profile = models.Profile.objects.get(phone=phone)
                    is_found = True
                except Exception as e:
                    pass
                if not is_found:
                    username = random_name()
                    new_user = User.objects.create_user(username)
                    new_user.save()
                    profile = models.Profile.objects.create(user=new_user,
                                                            phone=phone)
                    profile.save()
                # 把用户添加到'家长'Group
                group = Group.objects.get(name='家长')
                profile.user.groups.add(group)
                profile.user.save()
                # 家长角色: 创建parent
                parent, created = models.Parent.objects.get_or_create(
                        user=profile.user)
                first_login = not parent.student_name
                # login(request, profile.user)
                token, created = Token.objects.get_or_create(user=profile.user)
                # 强制刷新已经存在的 token
                if not created:
                    token.delete()
                    token, created = Token.objects.get_or_create(
                            user=profile.user)
                return JsonResponse({
                    'verified': True,
                    'first_login': first_login, 'token': token.key,
                    'parent_id': parent.id, 'user_id': parent.user.id,
                    'profile_id': parent.user.profile.id})

            except Exception as err:
                logger.error(err)
                return JsonResponse({'verified': False, 'reason': 'Unknown'})
        return HttpResponse("Not supported request.", status=403)


class ProfileSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = models.Profile
        fields = ('id', 'gender', 'avatar',)


class ProfileBasedMixin(object):
    def get_profile(self):
        try:
            profile = self.request.user.profile
        except exceptions.ObjectDoesNotExist:
            raise PermissionDenied(detail='Role incorrect')
        return profile


class ProfileViewSet(ProfileBasedMixin,
                     mixins.UpdateModelMixin,
                     viewsets.ReadOnlyModelViewSet):
    queryset = models.Profile.objects.all()
    serializer_class = ProfileSerializer

    def update(self, request, *args, **kwargs):
        if not self.request.user.is_superuser and (
                self.get_profile() != self.get_object()):
            return HttpResponse(status=403)
        try:
            self.request.user.teacher
            return HttpResponse(status=409)
        except (AttributeError, exceptions.ObjectDoesNotExist):
            # This is right
            pass

        response = super(ProfileViewSet, self).update(request, *args, **kwargs)
        if response.status_code == 200:
            response.data = {"done": "true"}
        return response


class UserSerializer(serializers.HyperlinkedModelSerializer):
    profile = ProfileSerializer()

    class Meta:
        model = models.User
        fields = ('id', 'username', 'email', 'is_staff', 'profile')


class UserViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.User.objects.all()
    serializer_class = UserSerializer


class RegionSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = models.Region
        fields = ('id', 'name',)
        # fields = ('id', 'name', 'superset', 'admin_level', 'leaf',
        #           'weekly_time_slots')


class RegionViewSet(viewsets.ReadOnlyModelViewSet):
    pagination_class = LargeResultsSetPagination
    queryset = models.Region.objects.all()
    serializer_class = RegionSerializer

    def get_queryset(self):
        queryset = self.queryset

        all = self.request.query_params.get('all')
        if all != 'true':
            queryset = queryset.filter(opened=True)

        action = self.request.query_params.get('action', '') or ''
        if action == 'sub-regions':
            sid = self.request.query_params.get('sid', '') or ''
            if sid:
                queryset = queryset.filter(superset_id=sid)
            else:
                queryset = queryset.filter(superset_id__isnull=True)

        return queryset


class SchoolSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.School
        fields = ('id', 'name', 'address', 'thumbnail', 'region', 'center',
                  'longitude', 'latitude',)


class SchoolNameSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.School
        fields = ('name',)

    def to_representation(self, instance):
        return self.fields['name'].get_attribute(instance)


class SchoolViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.School.objects.filter(opened=True)
    serializer_class = SchoolSerializer

    def get_queryset(self):
        queryset = self.queryset

        region = self.request.query_params.get('region')
        if region is not None:
            queryset = queryset.filter(region__id=region)

        teacher_id = self.request.query_params.get('teacher')
        if teacher_id is not None:
            queryset = queryset.filter(teacher__id=teacher_id)

        queryset = queryset.extra(order_by=['-center', 'name'])
        return queryset


class SubjectSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Subject
        fields = ('id', 'name')


class SubjectNameSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Subject
        fields = ('name',)

    def to_representation(self, instance):
        return self.fields['name'].get_attribute(instance)


class SubjectIdSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Subject
        fields = ('id',)

    def to_representation(self, instance):
        return self.fields['id'].get_attribute(instance)


class SubjectViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.Subject.objects.all()
    serializer_class = SubjectSerializer


class TagSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = models.Tag
        fields = ('id', 'name')


class TagNameSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Tag
        fields = ('name',)

    def to_representation(self, instance):
        return self.fields['name'].get_attribute(instance)


class TagViewSet(viewsets.ReadOnlyModelViewSet):
    pagination_class = LargeResultsSetPagination
    queryset = models.Tag.objects.all()
    serializer_class = TagSerializer


class GradeSerializer(serializers.ModelSerializer):
    subjects = SubjectIdSerializer(many=True)

    class Meta:
        model = models.Grade
        fields = ('id', 'name', 'subset', 'subjects')


GradeSerializer._declared_fields['subset'] = GradeSerializer(many=True)


class GradeSimpleSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Grade
        fields = ('id', 'name')


class GradeNameSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Grade
        fields = ('name',)

    def to_representation(self, instance):
        return self.fields['name'].get_attribute(instance)


class GradeViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.Grade.objects.all().filter(superset=None)
    serializer_class = GradeSerializer


class PriceSerializer(serializers.ModelSerializer):
    grade = GradeSimpleSerializer()

    class Meta:
        model = models.Price
        fields = ('grade', 'price')


class PriceViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.Price.objects.all()
    serializer_class = PriceSerializer


class LevelSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = models.Level
        fields = ('id', 'name')


class LevelNameSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = models.Level
        fields = ('name',)

    def to_representation(self, instance):
        return self.fields['name'].get_attribute(instance)


class LevelViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.Level.objects.all()
    serializer_class = LevelSerializer


class HighscoreSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = models.Highscore
        fields = ('name', 'increased_scores', 'school_name',
                  'admitted_to')


class HighscoreViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.Highscore.objects.all()
    serializer_class = HighscoreSerializer


class PhotoUrlSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Photo
        fields = ('img',)

    def to_representation(self, instance):
        return self.fields['img'].get_attribute(instance).url


class AchievementSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Achievement
        fields = ('title', 'img')


class AchievementViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.Achievement.objects.all()
    serializer_class = AchievementSerializer


class TeacherListSerializer(serializers.ModelSerializer):
    avatar = serializers.ImageField()
    tags = TagNameSerializer(many=True)
    subject = SubjectNameSerializer()

    class Meta:
        model = models.Teacher
        fields = ('id', 'avatar', 'gender', 'name', 'level', 'min_price',
                  'max_price', 'subject', 'grades_shortname', 'tags')


class TeacherShortSerializer(serializers.ModelSerializer):
    avatar = serializers.ImageField()

    class Meta:
        model = models.Teacher
        fields = ('id', 'avatar', 'name',)


class TeacherSerializer(serializers.ModelSerializer):
    prices = PriceSerializer(many=True)
    avatar = serializers.ImageField()
    tags = TagNameSerializer(many=True)
    achievement_set = AchievementSerializer(many=True)
    grades = GradeNameSerializer(many=True)
    subject = SubjectNameSerializer()
    highscore_set = HighscoreSerializer(many=True)
    photo_set = PhotoUrlSerializer(many=True)
    # Create a custom method field
    favorite = serializers.SerializerMethodField('_favorite')

    # Use this method for the custom field
    def _favorite(self, obj):
        teacher = obj
        try:
            parent = self.context['request'].user.parent
            if teacher.favorite_set.all().filter(parent=parent).count() > 0:
                return True
        except (AttributeError, exceptions.ObjectDoesNotExist):
            pass
        return False

    class Meta:
        model = models.Teacher
        fields = ('id', 'avatar', 'gender', 'name', 'degree', 'teaching_age',
                  'level', 'subject', 'grades', 'tags', 'achievement_set',
                  'photo_set', 'highscore_set', 'prices', 'min_price',
                  'max_price', 'published', 'favorite',)


class TeacherNameSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Teacher
        fields = ('name',)

    def to_representation(self, instance):
        return self.fields['name'].get_attribute(instance)


class TeacherViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.Teacher.objects.all()

    def get_queryset(self):
        queryset = self.queryset
        if self.action == 'list':
            queryset = queryset.filter(published=True)
            region_id = None
            default_region = models.Region.objects.filter(
                opened=True, name__contains='郑州')
            if default_region.count() == 1:
                region_id = default_region.first().id
            region = self.request.query_params.get('region') or region_id
            if region is not None:
                queryset = queryset.filter(region__id=region)
            school_id = self.request.query_params.get('school', 0)
            if school_id:
                queryset = queryset.filter(schools__id=school_id)

        grade = self.request.query_params.get('grade')
        if grade is not None:
            queryset = queryset.filter(
                    Q(abilities__grade__id=grade) |
                    Q(abilities__grade__subset__id=grade)).distinct()

        subject = self.request.query_params.get('subject')
        if subject is not None:
            queryset = queryset.filter(
                    abilities__subject__id__contains=subject)

        tags = self.request.query_params.get('tags', '').split()
        tags = list(map(int, filter(lambda x: x, tags)))
        if tags:
            queryset = queryset.filter(tags__id__in=tags)

        return queryset

    def get_serializer_class(self):
        if self.action == 'list':
            return TeacherListSerializer
        else:
            return TeacherSerializer


class MemberserviceSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = models.Memberservice
        fields = ('name', 'detail',)


class MemberserviceViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.Memberservice.objects.all()
    serializer_class = MemberserviceSerializer


class CouponSerializer(serializers.ModelSerializer):
    expired_at = serializers.SerializerMethodField()

    class Meta:
        model = models.Coupon
        fields = ('id', 'name', 'amount', 'expired_at',
                  'mini_total_price', 'used')

    def get_expired_at(self, obj):
        return int(obj.expired_at.timestamp())


class CouponViewSet(viewsets.ReadOnlyModelViewSet):
    pagination_class = HugeResultsSetPagination
    queryset = models.Coupon.objects.filter()

    def get_queryset(self):
        only_valid = self.request.query_params.get('only_valid', '')
        only_valid = only_valid == 'true'
        user = self.request.user
        try:
            queryset = user.parent.coupon_set.all()
        except exceptions.ObjectDoesNotExist:
            raise PermissionDenied(detail='Role incorrect')

        now = timezone.now()
        out_time = models.Coupon.OUT_OF_DATE_TIME
        if only_valid:
            # 选课页面的奖学金列表
            # 自上而下, 金额大到小 => 时间临近的, 是否符合条件客户端判断
            queryset = queryset.filter(
                expired_at__gt=now,
                used=False,
            ).order_by('-amount', 'expired_at')
        else:
            # 我的页面的奖学金列表, 过期太久的不取
            # 自上而下, 时间临近的 => 金额大到小
            queryset = queryset.filter(
                expired_at__gt=now - out_time,
            ).extra(
                # 该表达式换数据库后类型能要重写
                select={'date_diff': 'abs(extract(epoch from (now()-expired_at)))'}
            ).order_by('date_diff', '-amount')

        # 奖学金列表排序
        if self.action == 'list':
            return sorted(queryset, key=lambda x: x.sort_key())

        return queryset
    serializer_class = CouponSerializer
    permission_classes = (permissions.IsAuthenticated,)


class WeeklyTimeSlotSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = models.WeeklyTimeSlot
        fields = ('id', 'weekday', 'start', 'end',)


class WeeklyTimeSlotViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.WeeklyTimeSlot.objects.all()
    serializer_class = WeeklyTimeSlotSerializer


class ParentBasedMixin(object):
    def get_parent(self):
        try:
            parent = self.request.user.parent
        except (AttributeError, exceptions.ObjectDoesNotExist):
            raise PermissionDenied(detail='Role incorrect')
        return parent


class TeacherWeeklyTimeSlot(ParentBasedMixin, APIView):
    queryset = models.TimeSlot.objects.all()

    def get(self, request, teacher_id):
        parent = self.get_parent()
        school_id = request.GET.get('school_id')
        school = get_object_or_404(models.School, pk=school_id)
        teacher = get_object_or_404(models.Teacher, pk=teacher_id)

        la_dict = teacher.longterm_available_dict(school, parent)

        region = school.region
        weekly_time_slots = list(region.weekly_time_slots.all())
        slots = itertools.groupby(weekly_time_slots, key=lambda x: x.weekday)

        # 获取所有保留的课程
        date = timezone.now() - models.TimeSlot.RENEW_TIME
        reserved_time_slots = models.TimeSlot.objects.filter(
            order__teacher=teacher,
            order__parent=parent,
            start__gte=date,
            order__school=school,
            deleted=False)

        # 转换为周课程表形式
        reserved = [
            (t.start.astimezone().isoweekday(),
             t.start.astimezone().time(),
             t.end.astimezone().time())
            for t in reserved_time_slots]

        # 获取该老师被占用时段, 结束时间由小到大, 所有学校占用都取出来
        occupied_time_slots = models.TimeSlot.objects.filter(
            order__teacher=teacher,
            start__gte=date,
            deleted=False).order_by('end')

        # weekly_time_slot 对应的最后被占用时间
        occupied = {
            (t.start.astimezone().isoweekday(),
             t.start.astimezone().time(),
             t.end.astimezone().time()): t.end.astimezone().timestamp()
            for t in occupied_time_slots
        }

        # 增加 reserved 标记'已买'状态
        # 增加 last_occupied_end 标记最后被占用时间
        data = [(str(day), [OrderedDict([
            ('id', s.id),
            ('start', s.start.strftime('%H:%M')),
            ('end', s.end.strftime('%H:%M')),
            ('available', la_dict[(day, s.start, s.end)]),
            ('reserved', (s.weekday, s.start, s.end) in reserved),
            ('last_occupied_end', occupied.get((s.weekday, s.start, s.end)))])
            for s in ss])
            for day, ss in slots]

        data = OrderedDict(sorted(data, key=lambda x: int(x[0])))

        return JsonResponse(data)


class SubjectRecord(ParentBasedMixin, APIView):
    queryset = models.Order.objects.all()

    def get(self, request, subject_id):
        subject = get_object_or_404(models.Subject, pk=subject_id)
        parent = self.get_parent()
        order_count = models.Order.objects.filter(
                parent=parent, subject=subject,
                status=models.Order.PAID).count()
        ans = {'evaluated': order_count > 0}
        return JsonResponse(ans)


class CommentSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Comment
        fields = ('id', 'timeslot', 'score', 'content',)

    def validate_timeslot(self, value):
        parent = self._context['request'].user.parent
        if value.order.parent != parent:
            raise serializers.ValidationError(
                    'order not belongs to the current user.')
        return value

    def validate_score(self, value):
        value = int(value)
        if value not in range(1, 6):
            raise serializers.ValidationError('score not in range.')
        return value

    def create(self, validated_data):
        timeslot = validated_data.pop('timeslot')
        instance = super(CommentSerializer, self).create(validated_data)
        timeslot.comment = instance
        timeslot.save()
        return instance


class CommentViewSet(ParentBasedMixin,
                     mixins.CreateModelMixin,
                     mixins.ListModelMixin,
                     mixins.RetrieveModelMixin,
                     viewsets.GenericViewSet):
    queryset = models.Comment.objects.all()
    serializer_class = CommentSerializer
    permission_classes = (permissions.IsAuthenticated,)

    def get_queryset(self):
        parent = self.get_parent()
        queryset = models.Comment.objects.filter(
                timeslot__order__parent=parent).order_by('id')
        return queryset


class LecturerShortSerializer(serializers.ModelSerializer):
    avatar = serializers.ImageField()

    class Meta:
        model = models.Lecturer
        fields = ('id', 'avatar', 'name',)


class TimeSlotListSerializer(serializers.ModelSerializer):
    subject = SubjectNameSerializer()
    end = serializers.SerializerMethodField()

    class Meta:
        model = models.TimeSlot
        fields = ('id', 'end', 'subject', 'is_passed', 'is_commented')

    def get_end(self, obj):
        return int(obj.end.timestamp())


class TimeSlotSerializer(serializers.ModelSerializer):
    subject = SubjectNameSerializer()
    start = serializers.SerializerMethodField()
    end = serializers.SerializerMethodField()
    teacher = TeacherShortSerializer()
    comment = CommentSerializer()
    school = SchoolNameSerializer()
    grade = serializers.SerializerMethodField()
    lecturer = LecturerShortSerializer()

    class Meta:
        model = models.TimeSlot
        fields = ('id', 'start', 'end', 'subject', 'grade', 'school',
                  'is_passed', 'teacher', 'comment', 'is_expired', 'lecturer',
                  'is_live', 'school_address')

    def get_start(self, obj):
        return int(obj.start.timestamp())

    def get_end(self, obj):
        return int(obj.end.timestamp())

    def get_grade(self, obj):
        if obj.order.is_live():
            return obj.order.live_class.live_course.grade_desc
        return obj.order.grade.name


class TimeSlotViewSet(viewsets.ReadOnlyModelViewSet, ParentBasedMixin):
    pagination_class = HugeResultsSetPagination
    queryset = models.TimeSlot.objects.all()
    serializer_class = TimeSlotSerializer
    permission_classes = (permissions.IsAuthenticated,)

    def get_queryset(self):
        for_review = self.request.query_params.get('for_review', '')
        parent = self.get_parent()
        timeslots = models.TimeSlot.objects.filter(
            order__parent=parent, deleted=False)
        evaluations = []
        # for_review 只获取特定的课程, 不包括测评建档
        if for_review == 'true':
            timeslots = timeslots.filter(end__lt=timezone.now())
        else:
            evaluations = models.Evaluation.objects.filter(
                order__parent=parent, start__isnull=False)

        queryset = [x for x in timeslots] + [y for y in evaluations]
        if self.action == 'list':
            return sorted(queryset, key=lambda x: x.end, reverse=True)
        return queryset

    def get_serializer_class(self):
        # 课程列表与课程详情, 都用详情的序列化接口
        return TimeSlotSerializer


class ParentSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Parent
        fields = ('id', 'student_name', 'student_school_name', )


class ParentViewSet(ParentBasedMixin,
                    mixins.RetrieveModelMixin,
                    mixins.ListModelMixin,
                    mixins.UpdateModelMixin,
                    viewsets.GenericViewSet):
    queryset = models.Parent.objects.all()
    serializer_class = ParentSerializer
    permission_classes = (permissions.IsAuthenticated,)

    def get_queryset(self):
        parent = self.get_parent()
        queryset = models.Parent.objects.filter(id=parent.id)
        return queryset

    def update(self, request, *args, **kwargs):
        response = super(ParentViewSet, self).update(request, *args, **kwargs)
        if response.status_code == 200:
            response.data = {"done": "true"}
        return response

    def partial_update(self, request, *args, **kwargs):
        parent = self.get_object()
        data = request.data
        if 'student_name' in data:
            parent.student_name = data['student_name']
        if 'student_school_name' in data:
            parent.student_school_name = data['student_school_name']

        return super(ParentViewSet, self).partial_update(
                request, *args, **kwargs)


class LiveClassSimpleSerializer(serializers.ModelSerializer):
    lecturer_avatar = serializers.ImageField()
    assistant_avatar = serializers.ImageField()

    class Meta:
        model = models.LiveClass
        fields = ('id', 'course_name', 'lecturer_name', 'lecturer_avatar',
                  'assistant_name', 'assistant_avatar',
                  )


class LiveClassSerializer(serializers.ModelSerializer):
    course_start = serializers.SerializerMethodField()
    course_end = serializers.SerializerMethodField()
    lecturer_avatar = serializers.ImageField()
    assistant_avatar = serializers.ImageField()
    # Create a custom method field
    is_paid = serializers.SerializerMethodField('_is_paid')

    class Meta:
        model = models.LiveClass
        fields = ('id', 'course_name', 'course_start', 'course_end',
                  'course_period', 'course_fee', 'course_lessons',
                  'course_grade', 'course_description', 'room_capacity',
                  'students_count', 'lecturer_name', 'lecturer_title',
                  'lecturer_bio', 'lecturer_avatar', 'assistant_name',
                  'assistant_avatar', 'assistant_phone', 'school_name',
                  'school_address', 'is_paid',
                  )

    def get_course_start(self, obj):
        return int(obj.course_start.timestamp())

    def get_course_end(self, obj):
        return int(obj.course_end.timestamp())

    # Use this method for the custom field
    def _is_paid(self, obj):
        live_class = obj
        try:
            parent = self.context['request'].user.parent
            if models.Order.objects.filter(
                    parent=parent,
                    status=models.Order.PAID,
                    live_class__live_course=live_class.live_course,
            ).count() > 0:
                return True
        except (AttributeError, exceptions.ObjectDoesNotExist):
            pass
        return False


class LiveClassViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.LiveClass.objects.all()
    serializer_class = LiveClassSerializer

    def get_queryset(self):
        queryset = self.queryset
        # 列表页过滤掉已经结束的课程
        if self.action == 'list':
            queryset = queryset.annotate(
                start=Min('live_course__livecoursetimeslot__start'),
                end=Max('live_course__livecoursetimeslot__end'),
            ).filter(end__gt=timezone.now()).order_by('start')

        school_id = self.request.query_params.get('school')
        if school_id:
            queryset = queryset.filter(class_room__school_id=school_id)
        return queryset


class OrderListSerializer(serializers.ModelSerializer):
    grade = GradeNameSerializer()
    subject = SubjectNameSerializer()
    school = SchoolNameSerializer()
    teacher_avatar = serializers.ImageField()
    live_class = LiveClassSimpleSerializer()

    class Meta:
        model = models.Order
        fields = ('id', 'teacher', 'teacher_name', 'teacher_avatar',
                  'school_id', 'school', 'grade', 'subject', 'hours', 'status',
                  'order_id', 'to_pay', 'evaluated', 'is_teacher_published',
                  'live_class', 'is_live', 'school_address')


class OrderRetrieveSerializer(serializers.ModelSerializer):
    grade = GradeNameSerializer()
    subject = SubjectNameSerializer()
    school = SchoolNameSerializer()
    teacher_avatar = serializers.ImageField()
    created_at = serializers.SerializerMethodField()
    paid_at = serializers.SerializerMethodField()
    charge_channel = serializers.CharField()
    live_class = LiveClassSerializer()

    class Meta:
        model = models.Order
        fields = ('id', 'teacher', 'teacher_name', 'teacher_avatar',
                  'school_id', 'school', 'grade', 'subject', 'hours', 'status',
                  'order_id', 'to_pay', 'created_at', 'paid_at',
                  'charge_channel', 'evaluated', 'is_timeslot_allocated',
                  'is_teacher_published', 'timeslots', 'live_class', 'is_live',
                  'school_address')

    def get_created_at(self, obj):
        if obj.created_at:
            return int(obj.created_at.timestamp())
        return None

    def get_paid_at(self, obj):
        if obj.paid_at:
            return int(obj.paid_at.timestamp())
        return None


class OrderSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Order
        fields = ('id', 'teacher', 'parent', 'school', 'grade', 'subject',
                  'coupon', 'hours', 'weekly_time_slots', 'price', 'total',
                  'status', 'order_id', 'to_pay', 'is_timeslot_allocated',
                  'live_class')
        read_only_fields = (
                'parent', 'price', 'total', 'status', 'order_id', 'to_pay')

    def validate_hours(self, value):
        value = int(value)
        if value <= 0:
            raise serializers.ValidationError('hours should be positive.')
        if value % 2 != 0:
            raise serializers.ValidationError('hours should be even.')
        return value

    def validate_coupon(self, value):
        if value is not None and value.used:
            raise serializers.ValidationError('coupon has been used.')
        return value


class OrderViewSet(ParentBasedMixin,
                   mixins.CreateModelMixin,
                   mixins.UpdateModelMixin,
                   mixins.ListModelMixin,
                   mixins.RetrieveModelMixin,
                   mixins.DestroyModelMixin,
                   viewsets.GenericViewSet):
    queryset = models.Order.objects.all()
    serializer_class = OrderSerializer
    permission_classes = (permissions.IsAuthenticated,)

    def get_queryset(self):
        parent = self.get_parent()
        queryset = self.queryset.filter(parent=parent)
        if self.action == 'list':
            return sorted(queryset, key=lambda x: x.sort_key())
        return queryset

    def validate_create(self, request):
        live_class = request.data.get('live_class', None)
        # Do not validate coupon for live class for now
        if live_class:
            live_class = get_object_or_404(
                models.LiveClass, pk=request.data.get('live_class'))
            if live_class.is_full():
                return -3  # class room is full!
            parent = request.user.parent
            if models.Order.objects.filter(
                    parent=parent,
                    status=models.Order.PAID,
                    live_class__live_course=live_class.live_course,
            ).count() > 0:
                return -4  # this class is paid!
            return 0
        school = get_object_or_404(
            models.School, pk=request.data.get('school'))
        # 奖学金使用校验
        if request.data.get('coupon'):
            coupon = get_object_or_404(
                models.Coupon, pk=request.data.get('coupon'))
            teacher = get_object_or_404(
                models.Teacher, pk=request.data.get('teacher'))
            grade = get_object_or_404(
                models.Grade, pk=request.data.get('grade'))
            hours = request.data.get('hours')
            prices_set = teacher.level.priceconfig_set.filter(
                deleted=False,
                school=school,
                grade=grade,
                min_hours__lte=hours,
            ).order_by('-min_hours')
            price_obj = prices_set.first()
            price = price_obj.price

            # 限制条件不满足
            if hours < coupon.mini_course_count or price * hours < coupon.mini_total_price:
                return -2
            # 使用期限不满足
            if not coupon.check_date():
                return -2

        # 课程占用校验
        weekly_time_slots = request.data.get('weekly_time_slots')
        if weekly_time_slots is None or len(weekly_time_slots) == 0:
            return -1
        weekly_time_slots = [get_object_or_404(models.WeeklyTimeSlot, pk=x)
                             for x in weekly_time_slots]
        periods = [(s.weekday, s.start, s.end) for s in weekly_time_slots]

        teacher = get_object_or_404(
                models.Teacher, pk=request.data.get('teacher'))
        parent = self.get_parent()
        if not teacher.is_longterm_available(periods, school, parent):
            return -1
        return 0

    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        ret_code = self.validate_create(request)
        if ret_code != 0:
            return JsonResponse({'ok': False, 'code': ret_code})
        self.perform_create(serializer)
        headers = self.get_success_headers(serializer.data)
        return Response(serializer.data, status=status.HTTP_201_CREATED,
                        headers=headers)

    def perform_create(self, serializer):
        parent = self.get_parent()
        serializer.save(parent=parent)

    def update(self, request, *args, **kwargs):
        return JsonResponse({'err': 'Method not allowed'})

    def partial_update(self, request, *args, **kwargs):
        order = self.get_object()
        data = request.data
        if data['action'] != 'pay':
            return JsonResponse({'err': 'action not allowed'})

        # 支付前, 再次验证课程是否被占用
        weekly_time_slots = list(order.weekly_time_slots.all())
        periods = [(s.weekday, s.start, s.end) for s in weekly_time_slots]
        school = order.school
        teacher = order.teacher
        parent = self.get_parent()
        if not teacher.is_longterm_available(periods, school, parent):
            return JsonResponse({'ok': False, 'code': -1})
        pp_params = dict(order_no=order.order_id,
                         amount=order.to_pay,
                         app=dict(id=settings.PINGPP_APP_ID),
                         channel=data['channel'],
                         currency='cny',
                         client_ip='127.0.0.1',
                         subject='麻辣老师',
                         body='课时费', )
        prd_id = 'lc%s' % order.live_class_id if order.is_live() \
            else '1to1%s' % order.teacher_id
        if data['channel'] == 'wx_pub_qr':
            pp_params['extra'] = dict(product_id=prd_id)
        ch = pingpp.Charge.create(**pp_params)
        logger.info(ch)

        charge, created = models.Charge.objects.get_or_create(ch_id=ch['id'])
        if created:
            charge.order = order
            charge.created = timezone.make_aware(
                    datetime.datetime.fromtimestamp(ch['created']))
            charge.livemode = ch['livemode']
            charge.app = ch['app']
            assert not ch['paid']
            assert not ch['refunded']
            charge.channel = ch['channel']
            assert ch['order_no'] == order.order_id
            charge.order_no = ch['order_no']
            charge.client_ip = ch['client_ip']
            assert ch['amount'] == order.to_pay
            charge.amount = ch['amount']
            charge.amount_settle = ch['amount_settle']
            assert ch['currency'] == 'cny'
            charge.currency = ch['currency']
            charge.subject = ch['subject']
            charge.body = ch['body']
            charge.extra = json.dumps(ch['extra'], ensure_ascii=False)
            assert ch['time_paid'] is None
            charge.time_paid = ch['time_paid']
            charge.time_expire = timezone.make_aware(
                    datetime.datetime.fromtimestamp(ch['time_expire']))
            assert ch['time_settle'] is None
            charge.time_settle = ch['time_settle']
            assert ch['transaction_no'] is None
            charge.transaction_no = ''
            charge.failure_code = ch['failure_code'] or ''
            charge.failure_msg = ch['failure_msg'] or ''
            charge.credential = json.dumps(
                    ch['credential'], ensure_ascii=False)
            charge.description = ch['description'] or ''
            charge.save()

        return JsonResponse(ch)

    def destroy(self, request, *args, **kwargs):
        order = self.get_object()
        parent = self.get_parent()
        if order.parent == parent and order.status == models.Order.PENDING:
            self.perform_destroy(order)
            return JsonResponse({'ok': True})
        return JsonResponse({'ok': False})

    def perform_destroy(self, order):
        order.cancel()

    def get_serializer_class(self):
        if self.action == 'list':
            return OrderListSerializer
        if self.action == 'retrieve':
            return OrderRetrieveSerializer
        else:
            return OrderSerializer


class UnpaidCount(ParentBasedMixin, APIView):
    queryset = models.Order.objects.all()

    def get(self, request):
        parent = self.get_parent()
        order_count = self.queryset.filter(
                parent=parent, status=models.Order.PENDING).count()
        return JsonResponse({'count': order_count})


class ParentCenter(ParentBasedMixin, APIView):
    queryset = models.Order.objects.all()

    def get(self, request):
        parent = self.get_parent()
        unpaid_num = self.queryset.filter(
                parent=parent, status=models.Order.PENDING).count()
        tocomment_num = models.TimeSlot.objects.filter(
                deleted=False,
                order__parent=parent,
                comment__isnull=True,
                end__lt=timezone.now(),
                end__gte=timezone.now()-models.TimeSlot.COMMENT_DELAY).count()
        # 错题本数据
        exercise_mistakes = models.ExerciseSubmit.objects.filter(
            parent=parent,
            exercise_session__is_active=False,
        ).exclude(question__solution=F('option')).order_by('-updated_at')
        math_mistakes = exercise_mistakes.filter(
            exercise_session__live_course_timeslot__live_course__subject__name='数学'
        )
        english_mistakes = exercise_mistakes.filter(
            exercise_session__live_course_timeslot__live_course__subject__name='英语'
        )
        # 获取提交答案当时上课学校
        order = None
        latest_mistake = exercise_mistakes.first()
        if latest_mistake is not None:
            live_course = latest_mistake.exercise_session.live_course_timeslot.live_course
            updated_at = latest_mistake.updated_at
            timeslot = models.TimeSlot.objects.filter(
                order__parent=parent,
                order__live_class__live_course=live_course,
                start__lte=updated_at,
                end__gte=updated_at,
            ).first()
            if timeslot is not None:
                order = timeslot.order

        school_name = order.school.name if order is not None else ''
        return JsonResponse(
                {
                    'unpaid_num': unpaid_num,
                    'tocomment_num': tocomment_num,
                    'exercise_mistakes': {
                        'school': school_name,
                        'student': parent.student_name,
                        'numbers': {
                            'total': exercise_mistakes.count(),
                            'math': math_mistakes.count(),
                            'english': english_mistakes.count(),
                        }
                    }
                })


class StudyReportView(ParentBasedMixin, APIView):
    queryset = models.Parent.objects.all()

    # default params value
    ALL = 'all'
    MATH = 'math'
    SUMMARY = 'summary'

    def get(self, request, subject=None):
        '''
        subject is one subject ID
        '''
        parent = self.get_parent()
        klx_username = klx.klx_reg_student(parent)
        params = klx.klx_build_params({'username': klx_username}, True)
        if subject is None or subject is '' or subject == '/':
            subject = self.ALL

        # to collect all purchased subjects info
        if subject == self.ALL:
            return self.get_subjects_summary(parent, params)

        # to get one certain subject's info
        the_subject = get_object_or_404(models.Subject, id=subject)
        s_name = the_subject.name
        if s_name not in klx.KLX_REPORT_SUBJECTS:
            return HttpResponse(status=404)
        return self.get_one_subject_report(the_subject, parent, params)

    def get_subjects_summary(self, parent, params):
        subjects_list = []
        if settings.TESTING:
            return JsonResponse({'results': subjects_list})
        purchased_subjects = []
        # get subject from order
        ordered_subjects = models.Order.objects.filter(
                parent=parent, status=models.Order.PAID).values(
                        'subject', 'grade', 'created_at').order_by(
                                '-created_at')
        for tmp_order in ordered_subjects:
            # only support math presently
            tmp_subject = models.Subject.objects.get(id=tmp_order['subject'])
            s_name = tmp_subject.name
            if s_name in purchased_subjects:
                continue
            purchased_subjects.append(s_name)
            if s_name in klx.KLX_REPORT_SUBJECTS:
                s_name_en = klx.klx_subject_name(s_name)
                url = klx.KLX_STUDY_URL_FMT.format(subject=s_name_en)
                subject_data = {
                        'subject_id': tmp_subject.id, 'supported': True,
                        'purchased': True, 'grade_id': tmp_order['grade']}
                # 累计答题数、正确答题数
                subject_data.update(self._get_total_nums(url, params))
                subjects_list.append(subject_data)
            else:
                subjects_list.append({
                    'subject_id': tmp_subject.id,
                    'purchased': True,
                    'grade_id': tmp_order['grade'],
                    'supported': False,
                })
        # subjects supported, but user did not purchase
        should_buy_subjects = [b for b in klx.KLX_REPORT_SUBJECTS
                               if b not in purchased_subjects]
        if should_buy_subjects:
            to_buy_subjects = models.Subject.objects.filter(
                    name__in=should_buy_subjects)
            for s in to_buy_subjects:
                subjects_list.append({
                    'subject_id': s.id,
                    'supported': True,
                    'purchased': False,
                })
        settings.DEBUG and logger.debug(json.dumps(subjects_list))
        return JsonResponse({'results': subjects_list})

    def get_one_subject_report(self, the_subject, parent, params):
        s_name = the_subject.name
        s_name_en = klx.klx_subject_name(s_name)
        url = klx.KLX_STUDY_URL_FMT.format(subject=s_name_en)
        ans_data = {'subject_id': the_subject.id}
        if settings.TESTING:
            return JsonResponse(ans_data)
        # query the last order
        last_order = models.Order.objects.filter(
                parent=parent, status=models.Order.PAID,
                subject=the_subject).order_by('-created_at').first()
        if not last_order:
            return HttpResponse(status=404)  # Have not joined the course
        ans_data['grade_id'] = last_order.grade_id
        # 累计答题数、正确答题数
        ans_data.update(self._get_total_nums(url, params))
        # 累计答题次数（即答题次数）及完成率
        ans_data.update(self._get_exercise_total_nums(url, params))

        # 错题知识点分布
        ans_data['error_rates'] = self._get_error_rates(url, params)
        # 按月显示练习量走势
        ans_data['month_trend'] = self._get_month_trend(url, params)
        # 指定学生一级/二级知识点正确率
        ans_data['knowledges_accuracy'] = self._get_knowledges_accuracy(
                url, params)
        # 能力结构分析
        ans_data['abilities'] = self._get_abilities(
            url, params, klx.KLX_MATH_ABILITY_KEYS)
        # 提分点分析(各知识点全部用户平均得分率及指定学生得分率)
        ans_data['score_analyses'] = self._get_score_analyses(url, params)

        settings.DEBUG and logger.debug(json.dumps(ans_data))
        return JsonResponse(ans_data)

    def _get_total_nums(self, url, params):
        '''
        累计答题数、正确答题数
        :param url:
        :param params:
        :return:
        '''
        # logger.debug(url + '/total-item-nums')
        # logger.debug(params)
        resp = requests.get(url + '/total-item-nums', params=params)
        if resp.status_code != 200:
            logger.error('cannot reach kuailexue server, http_status is %s'
                         % (resp.status_code))
            raise KuailexueServerError(
                    'cannot reach kuailexue server, http_status is %s'
                    % (resp.status_code))
        ret_json = json.loads(resp.content.decode('utf-8'))
        if ret_json.get('code') == 0 and ret_json.get('data') is not None:
            ret_nums = ret_json.get('data')
            return {'total_nums': ret_nums.get('total_item_nums', 0),
                    'right_nums': ret_nums.get('total_right_item_nums', 0)}
        else:
            logger.error('get kuailexue wrong data, CODE: %s, MSG: %s'
                         % (ret_json.get('code'), ret_json.get('message')))
            raise KuailexueDataError(
                    'get kuailexue wrong data, CODE: %s, MSG: %s'
                    % (ret_json.get('code'), ret_json.get('message')))

    def _get_exercise_total_nums(self, url, params):
        '''
        累计答题次数（即答题次数）及完成率
        :param url:
        :param params:
        :return:
        '''
        resp = requests.get(url + '/total-exercise-nums', params=params)
        if resp.status_code != 200:
            logger.error('cannot reach kuailexue server, http_status is %s'
                         % (resp.status_code))
            raise KuailexueServerError(
                    'cannot reach kuailexue server, http_status is %s'
                    % (resp.status_code))
        ret_json = json.loads(resp.content.decode('utf-8'))
        if ret_json.get('code') == 0 and ret_json.get('data') is not None:
            ret_nums = ret_json.get('data')
            return {'exercise_total_nums': ret_nums.get(
                        'total_exercise_nums', 0),
                    'exercise_fin_nums': ret_nums.get(
                        'total_finished_exercise_nums', 0)}
        else:
            logger.error('get kuailexue wrong data, CODE: %s, MSG: %s'
                         % (ret_json.get('code'), ret_json.get('message')))
            raise KuailexueDataError(
                    'get kuailexue wrong data, CODE: %s, MSG: %s'
                    % (ret_json.get('code'), ret_json.get('message')))

    def _get_error_rates(self, url, params):
        '''
        错题知识点分布
        :param url:
        :param params:
        :return: list
        '''
        resp = requests.get(url + '/error-knowledge-point', params=params)
        if resp.status_code != 200:
            logger.error('cannot reach kuailexue server, http_status is %s'
                         % (resp.status_code))
            raise KuailexueServerError(
                    'cannot reach kuailexue server, http_status is %s'
                    % (resp.status_code))
        ret_json = json.loads(resp.content.decode('utf-8'))
        if ret_json.get('code') == 0 and ret_json.get('data') is not None:
            ret_list = ret_json.get('data')
            return [{'id': ep.get('tag_id'),
                     'name': ep.get('tag_name'),
                     'rate': ep.get('per')
                     } for ep in ret_list]
        else:
            logger.error('get kuailexue wrong data, CODE: %s, MSG: %s'
                         % (ret_json.get('code'), ret_json.get('message')))
            raise KuailexueDataError(
                    'get kuailexue wrong data, CODE: %s, MSG: %s'
                    % (ret_json.get('code'), ret_json.get('message')))

    def _get_month_trend(self, url, params):
        '''
        按月显示练习量走势
        :param url:
        :param params:
        :return: list
        '''
        resp = requests.get(url + '/items-trend', params=params)
        if resp.status_code != 200:
            logger.error('cannot reach kuailexue server, http_status is %s'
                         % (resp.status_code))
            raise KuailexueServerError(
                    'cannot reach kuailexue server, http_status is %s'
                    % (resp.status_code))
        ret_json = json.loads(resp.content.decode('utf-8'))
        if ret_json.get('code') == 0 and ret_json.get('data') is not None:
            ret_list = ret_json.get('data')
            return [{'total_item': ep.get('total_item', 0),
                     'error_item': ep.get('total_error_item', 0),
                     'year': ep.get('year'),
                     'month': ep.get('month'),
                     'day': ep.get('day')
                     } for ep in ret_list]
        else:
            logger.error('get kuailexue wrong data, CODE: %s, MSG: %s'
                         % (ret_json.get('code'), ret_json.get('message')))
            raise KuailexueDataError(
                    'get kuailexue wrong data, CODE: %s, MSG: %s'
                    % (ret_json.get('code'), ret_json.get('message')))

    def _get_knowledges_accuracy(self, url, params):
        '''
        指定学生一级/二级知识点正确率
        :param url:
        :param params:
        :return: list
        '''
        resp = requests.get(url + '/knowledge-point-accuracy', params=params)
        if resp.status_code != 200:
            logger.error('cannot reach kuailexue server, http_status is %s'
                         % (resp.status_code))
            raise KuailexueServerError(
                    'cannot reach kuailexue server, http_status is %s'
                    % (resp.status_code))
        ret_json = json.loads(resp.content.decode('utf-8'))
        if ret_json.get('code') == 0 and ret_json.get('data') is not None:
            ret_list = ret_json.get('data')
            return [{'id': ep.get('tag_id'),
                     'name': ep.get('tag_name'),
                     'total_item': ep.get('total_item'),
                     'right_item': ep.get('total_right_item')
                     } for ep in ret_list]
        else:
            logger.error('get kuailexue wrong data, CODE: %s, MSG: %s' % (
                ret_json.get('code'), ret_json.get('message')))
            raise KuailexueDataError(
                    'get kuailexue wrong data, CODE: %s, MSG: %s' % (
                        ret_json.get('code'), ret_json.get('message')))

    def _get_abilities(self, url, params, ability_keys):
        '''
        能力结构分析
        :param url:
        :param params:
        :return: list
        '''
        resp = requests.get(url + '/ability-structure', params=params)
        if resp.status_code != 200:
            logger.error('cannot reach kuailexue server, http_status is %s'
                         % (resp.status_code))
            raise KuailexueServerError(
                    'cannot reach kuailexue server, http_status is %s'
                    % (resp.status_code))
        ret_json = json.loads(resp.content.decode('utf-8'))
        if ret_json.get('code') == 0 and ret_json.get('data') is not None:
            ret_obj = ret_json.get('data')
            if not ret_obj:
                return [{'key': ab, 'val': 0} for ab in ability_keys]
            return [{'key': k, 'val': v} for k, v in ret_obj.items()]
        else:
            logger.error('get kuailexue wrong data, CODE: %s, MSG: %s' % (
                ret_json.get('code'), ret_json.get('message')))
            raise KuailexueDataError(
                    'get kuailexue wrong data, CODE: %s, MSG: %s' % (
                        ret_json.get('code'), ret_json.get('message')))

    def _get_score_analyses(self, url, params):
        '''
        各知识点全部用户平均得分率及指定学生得分率
        :param url:
        :param params:
        :return: list
        '''
        resp = requests.get(url + '/my-average-score', params=params)
        if resp.status_code != 200:
            logger.error('cannot reach kuailexue server, http_status is %s'
                         % (resp.status_code))
            raise KuailexueServerError(
                    'cannot reach kuailexue server, http_status is %s'
                    % (resp.status_code))
        ret_json = json.loads(resp.content.decode('utf-8'))
        if ret_json.get('code') == 0 and ret_json.get('data') is not None:
            ret_list = ret_json.get('data')
            return [{'id': ep.get('tag_id'),
                     'name': ep.get('tag_name'),
                     'my_score': ep.get('my_score'),
                     'ave_score': ep.get('ave_score')
                     } for ep in ret_list]
        else:
            logger.error('get kuailexue wrong data, CODE: %s, MSG: %s' % (
                ret_json.get('code'), ret_json.get('message')))
            raise KuailexueDataError(
                    'get kuailexue wrong data, CODE: %s, MSG: %s'
                    % (ret_json.get('code'), ret_json.get('message')))


class FavoriteSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Favorite
        fields = ('teacher',)


class FavoriteViewSet(ParentBasedMixin,
                      mixins.CreateModelMixin,
                      mixins.ListModelMixin,
                      mixins.DestroyModelMixin,
                      viewsets.GenericViewSet):
    queryset = models.Favorite.objects.all()
    serializer_class = FavoriteSerializer
    permission_classes = (permissions.IsAuthenticated,)

    def get_queryset(self):
        parent = self.get_parent()
        queryset = models.Favorite.objects.filter(parent=parent)
        if self.action == 'list':
            # 返回被收藏老师列表
            return [f.teacher for f in queryset]
        if self.action == 'destroy':
            # 取消收藏, 给的是老师 id, queryset 用老师的
            return models.Teacher.objects.all()
        return queryset

    def perform_create(self, serializer):
        parent = self.get_parent()
        teacher = self.request.POST.get('teacher')
        if models.Favorite.objects.filter(teacher=teacher,
                                          parent=parent).count() == 0:
            serializer.save(parent=parent)

    def destroy(self, request, *args, **kwargs):
        # 取消收藏, 给的是老师 id
        teacher = self.get_object()
        parent = self.get_parent()
        models.Favorite.objects.filter(teacher=teacher, parent=parent).delete()
        return JsonResponse({'ok': True})

    def get_serializer_class(self):
        if self.action == 'list':
            # 收藏列表用老师列表序列化接口
            return TeacherListSerializer
        # 新增、删除, 用收藏序列化接口
        return FavoriteSerializer


class TeacherSchoolPrices(View):
    def get(self, request, teacher_id, school_id):
        school = get_object_or_404(models.School, pk=school_id)
        teacher = get_object_or_404(models.Teacher, pk=teacher_id)
        # no need grade, this will return all grades prices
        prices = models.PriceConfig.objects.filter(
            deleted=False,
            school=school,
            grade__in=teacher.grades(),
            level=teacher.level
        ).order_by('grade_id')
        prices = list(prices)
        step_prices = itertools.groupby(prices, key=lambda x: x.grade)

        data = [
            {'grade': grade.id,
             'grade_name': grade.name,
             'prices': [OrderedDict(
                 [('min_hours', config.min_hours),
                  ('max_hours', config.max_hours),
                  ('price', config.price)])
                        for config in
                        sorted(configs, key=lambda x: x.min_hours)]}
            for grade, configs in step_prices]

        return JsonResponse({
            'count': len(data),
            'previous': None,
            'next': None,
            'results': data
        })


class PadLogin(View):
    @method_decorator(csrf_exempt)
    def dispatch(self, request, *args, **kwargs):
        return super(PadLogin, self).dispatch(request, *args, **kwargs)

    def get(self, request):
        return HttpResponse("Not supported request.", status=403)

    def post(self, request):
        if request.META.get('CONTENT_TYPE', '').startswith('application/json'):
            try:
                jsonData = json.loads(request.body.decode())
            except ValueError:
                return HttpResponse(status=400)
            phone = jsonData.get('phone', '')
        else:
            phone = request.POST.get('phone', '')
        phone = phone.strip()

        parent = models.Parent.objects.filter(
            user__profile__phone=phone).first()
        if parent is None:
            return JsonResponse({'code': -1, 'msg': '当前账号未注册，请查证'})

        now = timezone.now()
        # 需确认课程已经购买
        timeslot = models.TimeSlot.objects.filter(
            deleted=False,
            order__parent=parent,
            order__status=models.Order.PAID,
            order__live_class__isnull=False,
            start__lte=now,
            end__gte=now,
        ).first()
        if timeslot is None:
            return JsonResponse({'code': -2, 'msg': '您好，暂无有效课程'})

        current_lesson = timeslot
        live_class = current_lesson.order.live_class
        school = live_class.class_room.school
        live_course = live_class.live_course
        lc_timeslot = models.LiveCourseTimeSlot.objects.filter(
            live_course=live_course,
            start__lte=now,
            end__gte=now,
        ).order_by("start").first()
        if lc_timeslot is None:
            return JsonResponse({'code': -2, 'msg': '您好，暂无有效课程'})
        parent.pad_token = random_pad_token(parent.user.profile.phone)
        parent.save()
        return JsonResponse({
            'code': 0,
            'msg': '登录成功',
            'data': {
                'token': parent.pad_token,
                'live_class': {
                    'id': live_class.id,
                    'assistant': live_class.assistant.name,
                    'class_room': live_class.class_room.name,
                    'live_course': {
                        'id': live_course.id,
                        'course_no': live_course.course_no,
                        'name': live_course.name,
                        'grade': live_course.grade_desc,
                        'subject': live_course.subject.name,
                        'lecturer': live_course.lecturer.name,
                    },
                },
                'phone': parent.user.profile.phone,
                'name': parent.student_name,
                'school': school.name,
                'school_id': school.id,
                'lc_timeslot_id': lc_timeslot.id,
            },
        })


class PadStatus(View):
    @method_decorator(csrf_exempt)
    def dispatch(self, request, *args, **kwargs):
        return super(PadStatus, self).dispatch(request, *args, **kwargs)

    def get(self, request):
        return HttpResponse("Not supported request.", status=403)

    def post(self, request):
        if request.META.get('CONTENT_TYPE', '').startswith('application/json'):
            try:
                jsonData = json.loads(request.body.decode())
            except ValueError:
                return HttpResponse(status=400)
            lc_timeslot = jsonData.get('lc_timeslot', '0')
        else:
            lc_timeslot = request.POST.get('lc_timeslot', '0')
        token = request.META.get('HTTP_PAD_TOKEN', '').strip()
        lc_timeslot_id = int(lc_timeslot)

        parent = models.Parent.objects.filter(pad_token=token).first()
        if parent is None:
            return JsonResponse({'code': -1, 'msg': '您好，当前账号已在别处登录'})

        now = timezone.now()
        lc_timeslot = models.LiveCourseTimeSlot.objects.filter(
            pk=lc_timeslot_id).first()
        # 判断课程是否有效存在
        if lc_timeslot is None:
            return JsonResponse({'code': -2, 'msg': '您好，下课自动登出'})
        # 上次登录时的课程是否在有效时间内
        if lc_timeslot.start > now or lc_timeslot.end < now:
            return JsonResponse({'code': -2, 'msg': '您好，下课自动登出'})
        # 需确认课程已经购买
        timeslot = models.TimeSlot.objects.filter(
            deleted=False,
            order__parent=parent,
            order__status=models.Order.PAID,
            order__live_class__live_course=lc_timeslot.live_course,
            start__lte=now,
            end__gte=now,
        ).first()

        if timeslot is None:
            return JsonResponse({'code': -2, 'msg': '您好，下课自动登出'})

        exercise_session = models.ExerciseSession.objects.filter(
            is_active=True,
            live_course_timeslot=lc_timeslot,
        ).order_by('-created_at').first()

        if exercise_session is not None:
            return JsonResponse({
                'code': 0,
                'msg': '成功',
                'type': 1,
                'data': {
                    'question_group': exercise_session.question_group.id,
                    'exercise_session': exercise_session.id
                }
            })

        return JsonResponse({
            'code': 0,
            'msg': '成功',
            'type': 1,
            'data': {}
        })


class PadQuestion(View):
    @method_decorator(csrf_exempt)
    def dispatch(self, request, *args, **kwargs):
        return super(PadQuestion, self).dispatch(request, *args, **kwargs)

    def get(self, request):
        return HttpResponse("Not supported request.", status=403)

    def post(self, request):
        if request.META.get('CONTENT_TYPE', '').startswith('application/json'):
            try:
                jsonData = json.loads(request.body.decode())
            except ValueError:
                return HttpResponse(status=400)
            question_group = jsonData.get('question_group', '0')
        else:
            question_group = request.POST.get('question_group', '0')
        token = request.META.get('HTTP_PAD_TOKEN', '').strip()
        question_group_id = int(question_group)

        parent = models.Parent.objects.filter(pad_token=token).first()
        if parent is None:
            return JsonResponse({'code': -1, 'msg': '您好，当前账号已在别处登录'})

        question_group = models.QuestionGroup.objects.filter(
            deleted=False,
            pk=question_group_id,
        ).first()
        if question_group is None:
            return JsonResponse({'code': -3, 'msg': '题组不存在'})

        questions = question_group.questions.filter(deleted=False)
        return JsonResponse({
            'code': 0,
            'msg': '成功',
            'data': {
                'id': question_group.id,
                'title': question_group.title,
                'questions': [
                    {
                        'id': q.id,
                        'title': q.title,
                        'options': [
                            {
                                'id': o.id,
                                'text': o.text,
                            } for o in
                            models.QuestionOption.objects.filter(question=q)
                        ]
                    } for q in questions
                ]
            }
        })


class PadSubmit(View):
    @method_decorator(csrf_exempt)
    def dispatch(self, request, *args, **kwargs):
        return super(PadSubmit, self).dispatch(request, *args, **kwargs)

    def get(self, request):
        return HttpResponse("Not supported request.", status=403)

    def post(self, request):
        try:
            jsonData = json.loads(request.body.decode())
        except ValueError:
            return HttpResponse(status=400)
        token = request.META.get('HTTP_PAD_TOKEN', '').strip()

        parent = models.Parent.objects.filter(pad_token=token).first()
        if parent is None:
            return JsonResponse({'code': -1, 'msg': '您好，当前账号已在别处登录'})

        exercise_session_id = jsonData.get('exercise_session', 0)
        exercise_session = get_object_or_404(
            models.ExerciseSession, pk=exercise_session_id)
        if not exercise_session.is_active:
            return JsonResponse({'code': -3, 'msg': '提交失败，答题已结束'})

        answers = jsonData.get('answers', [])
        submits = []
        for an in answers:
            submit = {}
            question_id = an.get('question')
            option_id = an.get('option')
            submit['question'] = get_object_or_404(
                models.Question, pk=question_id)
            submit['option'] = get_object_or_404(
                models.QuestionOption, pk=option_id)
            submits.append(submit)
        for submit in submits:
            exercise_submit, created = \
                models.ExerciseSubmit.objects.get_or_create(
                    exercise_session=exercise_session,
                    parent=parent,
                    question=submit['question'],
                    defaults={'option': submit['option']}
                )
            exercise_submit.option = submit['option']
            exercise_submit.save()

        return JsonResponse({'code': 0, 'msg': '成功'})


class QuestionOptionSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.QuestionOption
        fields = ('id', 'text')


class QuestionSerializer(serializers.ModelSerializer):
    options = QuestionOptionSerializer(many=True)

    class Meta:
        model = models.Question
        fields = ('id', 'title', 'options', 'solution', 'explanation')


class QuestionGroupSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.QuestionGroup
        fields = ('id', 'title', 'description')


class ExerciseSubmitSerializer(serializers.ModelSerializer):
    question = QuestionSerializer()
    submit_option = serializers.SerializerMethodField()
    updated_at = serializers.SerializerMethodField()
    question_group = QuestionGroupSerializer()

    class Meta:
        model = models.ExerciseSubmit
        fields = (
            'id',
            'question_group',
            'question',
            'submit_option',
            'updated_at',
        )

    def get_updated_at(self, obj):
        if obj.updated_at:
            return int(obj.updated_at.timestamp())
        return None

    def get_submit_option(self, obj):
        return obj.option.id


class ExerciseSubmitViewSet(ParentBasedMixin, viewsets.ReadOnlyModelViewSet):
    # 从 ExerciseSubmit 提交结果中获取
    queryset = models.ExerciseSubmit.objects.filter(
        exercise_session__is_active=False
    ).exclude(question__solution=F('option'))
    serializer_class = ExerciseSubmitSerializer

    def get_queryset(self):
        if settings.ENV_TYPE == 'debug' or settings.ENV_TYPE == 'dev':
            queryset = self.queryset
            try:
                parent = self.request.user.parent
            except (AttributeError, exceptions.ObjectDoesNotExist):
                parent = None
            if parent is not None:
                queryset = queryset.filter(parent=parent)
        else:
            parent = self.get_parent()
            queryset = self.queryset.filter(parent=parent)

        subject_id = self.request.query_params.get('subject', None)
        if subject_id is not None:
            subject = get_object_or_404(models.Subject, pk=subject_id)
            queryset = queryset.filter(
                exercise_session__live_course_timeslot__live_course__subject=subject
            )

        return queryset.order_by('-updated_at')
