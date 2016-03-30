import json
import logging
import datetime
import itertools
from collections import OrderedDict

from django.contrib.auth.models import User, Group
from django.shortcuts import get_object_or_404, redirect
from django.views.generic import View, TemplateView
from django.views.decorators.csrf import csrf_exempt
from django.http import HttpResponse, JsonResponse, HttpResponseRedirect
from django.db.models import Q
from django.core import exceptions
from django.utils.decorators import method_decorator
from django.utils import timezone
from django.conf import settings

from rest_framework.authtoken.models import Token
from rest_framework.views import APIView
from rest_framework import serializers, viewsets, permissions, generics, mixins
from rest_framework.exceptions import PermissionDenied
from rest_framework.pagination import PageNumberPagination

from app import models
from app.pingpp import pingpp
from app.utils import random_name
from app.utils.smsUtil import isValidPhone, isValidCode, tpl_send_sms, TPL_STU_PAY_FAIL
from app.utils.algorithm import verify_sig
from app.exception import TimeSlotConflict, OrderStatusIncorrect, RefundError
# from .forms import autoConfirmForm

from .tasks import autoConfirmClasses

logger = logging.getLogger('app')


class LargeResultsSetPagination(PageNumberPagination):
    page_size = 300
    page_size_query_param = 'page_size'
    max_page_size = 1000


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
        order.status = order.PAID
        order.save()
        try:
            models.Order.objects.allocate_timeslots(order)
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

        hours = min(hours, 35)  # Only return first 35 timeslots

        weekly_time_slots = request.GET.get('weekly_time_slots').split()
        weekly_time_slots = [get_object_or_404(models.WeeklyTimeSlot, pk=x)
                             for x in weekly_time_slots]
        data = models.Order.objects.concrete_timeslots(
                hours, weekly_time_slots, teacher)
        data = [(x['start'].timestamp(),
                 x['end'].timestamp()) for x in data]

        return JsonResponse({'data': data})

# TODO: 在生产环境去掉!
class autoConfirmClassesView(View):
    template_name="app/test_auto_confirm_classes.html"

    def get(self, request):
        res = autoConfirmClasses()
        if res:
            res_str = '成功'
        else:
            res_str = '失败'
        return HttpResponse('<h1>'+res_str+'</h1>')


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
                except:
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
            teacher = self.request.user.teacher
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
        fields = ('id', 'name', 'superset', 'admin_level', 'leaf',
                  'weekly_time_slots')


class RegionViewSet(viewsets.ReadOnlyModelViewSet):
    pagination_class = LargeResultsSetPagination
    queryset = models.Region.objects.all()
    serializer_class = RegionSerializer

    def get_queryset(self):
        queryset = self.queryset

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

    def to_representation(self, instance):
        return self.fields['name'].get_attribute(instance)


class SchoolViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.School.objects.all()
    serializer_class = SchoolSerializer

    def get_queryset(self):
        queryset = self.queryset

        region = self.request.query_params.get('region', None) or None
        if region is not None:
            queryset = queryset.filter(region__id=region)

        queryset = queryset.extra(order_by=['-center'])
        return queryset


class SubjectSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Subject
        fields = ('id', 'name')


class SubjectNameSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Subject

    def to_representation(self, instance):
        return self.fields['name'].get_attribute(instance)


class SubjectIdSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Subject

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
    level = LevelNameSerializer()

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
    level = LevelNameSerializer()
    highscore_set = HighscoreSerializer(many=True)
    photo_set = PhotoUrlSerializer(many=True)

    class Meta:
        model = models.Teacher
        fields = ('id', 'avatar', 'gender', 'name', 'degree', 'teaching_age',
                  'level', 'subject', 'grades', 'tags', 'achievement_set',
                  'photo_set', 'highscore_set', 'prices', 'min_price',
                  'max_price')


class TeacherViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.Teacher.objects.filter(published=True)

    def get_queryset(self):
        queryset = self.queryset

        grade = self.request.query_params.get('grade', None) or None
        if grade is not None:
            queryset = queryset.filter(
                    Q(abilities__grade__id=grade) |
                    Q(abilities__grade__subset__id=grade)).distinct()

        subject = self.request.query_params.get('subject', None) or None
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
        fields = ('id', 'name', 'amount', 'expired_at', 'used')

    def get_expired_at(self, obj):
        return int(obj.expired_at.timestamp())


class CouponViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.Coupon.objects.filter()

    def get_queryset(self):
        user = self.request.user
        try:
            queryset = user.parent.coupon_set.all()
        except exceptions.ObjectDoesNotExist:
            raise PermissionDenied(detail='Role incorrect')
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

        data = [(str(day), [OrderedDict([
            ('id', s.id),
            ('start', s.start.strftime('%H:%M')),
            ('end', s.end.strftime('%H:%M')),
            ('available', la_dict[(day, s.start, s.end)])])
            for s in ss])
            for day, ss in slots]

        # weekday = datetime.datetime.today().weekday() + 1
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

    class Meta:
        model = models.TimeSlot
        fields = ('id', 'start', 'end', 'subject', 'school', 'is_passed', 'teacher', 'comment')

    def get_start(self, obj):
        return int(obj.start.timestamp())

    def get_end(self, obj):
        return int(obj.end.timestamp())


class TimeSlotViewSet(viewsets.ReadOnlyModelViewSet, ParentBasedMixin):
    pagination_class = LargeResultsSetPagination
    queryset = models.TimeSlot.objects.all()
    serializer_class = TimeSlotSerializer
    permission_classes = (permissions.IsAuthenticated,)

    def get_queryset(self):
        parent = self.get_parent()
        queryset = models.TimeSlot.objects.filter(
                order__parent=parent, deleted=False).order_by('-end')
        return queryset

    def get_serializer_class(self):
        if self.action == 'list':
            return TimeSlotListSerializer
        else:
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


class OrderSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Order
        fields = ('id', 'teacher', 'parent', 'school', 'grade', 'subject',
                  'coupon', 'hours', 'weekly_time_slots', 'price', 'total',
                  'status', 'order_id', 'to_pay', 'is_timeslot_allocated')
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
        if value is not None:
            if value.used:
                raise serializers.ValidationError('coupon has been used.')
        return value


class OrderViewSet(ParentBasedMixin,
                   mixins.CreateModelMixin,
                   mixins.UpdateModelMixin,
                   mixins.ListModelMixin,
                   mixins.RetrieveModelMixin,
                   viewsets.GenericViewSet):
    queryset = models.Order.objects.all()
    serializer_class = OrderSerializer
    permission_classes = (permissions.IsAuthenticated,)

    def get_queryset(self):
        parent = self.get_parent()
        queryset = models.Order.objects.filter(parent=parent).order_by('id')
        return queryset

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
        ch = pingpp.Charge.create(
                order_no=order.order_id,
                amount=order.to_pay,
                app=dict(id=settings.PINGPP_APP_ID),
                channel=data['channel'],
                currency='cny',
                client_ip='127.0.0.1',
                subject='麻辣老师',
                body='课时费',
        )
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
