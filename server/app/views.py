import re
import time
import json
import random
import requests
import datetime
import itertools
from collections import OrderedDict

from segmenttree import SegmentTree

from django.conf import settings
from django.contrib.auth.models import User, Group
from django.contrib.auth import authenticate, login
from django.shortcuts import get_object_or_404
from django.views.generic import View
from django.views.decorators.csrf import csrf_exempt
from django.http import HttpResponse, JsonResponse
from django.db.models import Q
from django.utils.decorators import method_decorator
from django.utils.translation import ugettext_lazy as _
from django.utils import timezone

from rest_framework.authtoken.models import Token
from rest_framework import serializers, viewsets, mixins
from rest_framework.viewsets import ModelViewSet
from rest_framework import serializers, viewsets, permissions
from rest_framework.exceptions import PermissionDenied

from app import models
from .utils.smsUtil import sendCheckcode

class Policy(View):
    def get(self, request):
        policy = get_object_or_404(models.Policy, pk=1)
        data = dict(result=policy.content,
                    updated_at=int(policy.updated_at.timestamp()))
        return HttpResponse(json.dumps(data, ensure_ascii=False))

class TeacherWeeklyTimeSlot(View):
    def get(self, request, teacher_id):
        renew_time = datetime.timedelta(hours=2)
        traffic_time = 60 # 1 hour
        utcoffset = datetime.timedelta(hours=8)

        school_id = request.GET.get('school_id')
        school = get_object_or_404(models.School, pk=school_id)
        teacher = get_object_or_404(models.Teacher, pk=teacher_id)
        region = school.region
        weekly_time_slots = list(region.weekly_time_slots.all())
        slots = itertools.groupby(weekly_time_slots, key=lambda x: x.weekday)

        date = timezone.now() - renew_time
        occupied = models.TimeSlot.objects.filter(
                order__teacher__id=teacher_id, start__gte=date, deleted=False)

        segtree = SegmentTree(0, 7 * 24 * 60 - 1)
        for occ in occupied:
            cur_school = occ.order.school
            occ.start = timezone.localtime(occ.start)
            occ.end = timezone.localtime(occ.end)
            start = occ.start.weekday() * 24 * 60 + occ.start.hour * 60 + occ.start.minute
            end = occ.end.weekday() * 24 * 60 + occ.end.hour * 60 + occ.end.minute - 1
            if cur_school.id != school.id:
                start, end = start - traffic_time, end + traffic_time
            segtree.add(start, end)

        data = [(str(day), [OrderedDict([('start', s.start.strftime('%H:%M')),
            ('end', s.end.strftime('%H:%M')),
            ('available', segtree.query_len(
                (day - 1) * 24 * 60 + s.start.hour * 60 + s.start.minute,
                (day - 1) * 24 * 60 + s.end.hour * 60 + s.end.minute - 1
                ) == 0)]) for s in ss]) for day, ss in slots]

        weekday = datetime.datetime.today().weekday() + 1
        data = OrderedDict(sorted(data, key=lambda x: (int(x[0]) + 7 - weekday) % 7))

        return JsonResponse(data)

class Sms(View):
    expired_time = 10    # 10 minutes
    resend_span = 1      # 1 minute
    max_verify_times = 3
    @method_decorator(csrf_exempt)
    def dispatch(self, request, *args, **kwargs):
        return super(Sms, self).dispatch(request, *args, **kwargs)

    def isValidPhone(self, phone):
        return re.match(r'^((((\+86)|(86))?(1)\d{10})|000\d+)$', phone)

    def isTestPhone(self, phone):
        return re.match(r'^000\d+$', phone)

    def isValidCode(self, code):
        return re.match(r'^\d+$', code)

    def generateCheckcode(self, phone):
        # 生成，并保存到数据库或缓存，10分钟后过期
        is_test = self.isTestPhone(phone)
        obj, created = models.Checkcode.objects.get_or_create(phone=phone, defaults={'checkcode': "1111"})
        # obj, created = models.Checkcode.objects.get_or_create(phone=phone, defaults={'checkcode': is_test and '1111' or random.randrange(1000, 9999)})
        if not created:
            now = timezone.now()
            delta = now - obj.updated_at
            if delta > datetime.timedelta(minutes=self.expired_time):
                # expired, make new one
                obj.checkcode = "1111"
                # obj.checkcode = is_test and '1111' or random.randrange(1000, 9999)
                obj.updated_at = now
                obj.verify_times = 0
                obj.resend_at = now
                obj.save()
            else:
                resend_at = obj.resend_at and obj.resend_at or obj.updated_at
                delta = now - resend_at
                if delta < datetime.timedelta(minutes=self.resend_span):
                    # resend too much times
                    return False
                obj.resend_at = now
                obj.save()
        return obj.checkcode

    def verifyCheckcode(self, phone, code):
        # return is_valid, err_no
        try:
            obj = models.Checkcode.objects.get(phone=phone)
            delta = timezone.now() - obj.updated_at
            if delta > datetime.timedelta(minutes=self.expired_time):
                return False, 2
            if obj.verify_times >= self.max_verify_times: # meybe someone attack
                return False, 3
            is_valid = code == obj.checkcode
            if is_valid:
                obj.delete()
            else:
                obj.verify_times += 1;
                obj.save()
            return is_valid, 0
        except:
            return False, 1

    # @method_decorator(csrf_exempt) # here it doesn't work
    def post(self, request):
        action = request.POST.get('action')
        if action == 'send':
            phone = request.POST.get('phone')
            if not phone:
                return JsonResponse({'sent': False, 'reason': 'phone is required'})
            if not self.isValidPhone(phone):
                return JsonResponse({'sent': False, 'reason': 'phone is wrong'})
            try:
                # generate code
                checkcode = self.generateCheckcode(phone)
                if not checkcode:
                    return JsonResponse({'sent': False, 'reason': 'resend too much times'})
                print ('验证码：' + str(checkcode))
                if not self.isTestPhone(phone):
                    # call send sms api
                    r = sendCheckcode(phone, checkcode)
                    print (r)
                return JsonResponse({'sent': True})
            except Exception as err:
                print (err)
                return JsonResponse({'sent': False, 'reason': 'Unknown'})
        if action == 'verify':
            phone = request.POST.get('phone')
            code = request.POST.get('code')
            if not phone or not code:
                return JsonResponse({'verified': False, 'reason': 'params error'})
            if not self.isValidPhone(phone):
                return JsonResponse({'sent': False, 'reason': 'phone is wrong'})
            if not self.isValidCode(code):
                return JsonResponse({'sent': False, 'reason': 'code is wrong'})
            try:
                is_valid, err_no = self.verifyCheckcode(phone, code)
                if not is_valid:
                    return JsonResponse({'verified': False, 'reason': err_no == 3 and 'Retry too much times' or 'SMS not match or is expired'})
                # find User
                is_found = False
                try:
                    profile = models.Profile.objects.get(phone=phone)
                    is_found = True
                except:
                    pass
                if not is_found:
                    username = ''.join(random.sample('AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789', 10))
                    new_user = User.objects.create_user(username)
                    new_user.save()
                    profile = models.Profile.objects.create(user=new_user, phone=phone)
                    profile.save()
                # 把用户添加到'家长'Group
                group = Group.objects.get(name='家长')
                profile.user.groups.add(group)
                profile.user.save()
                # 家长角色: 创建parent
                parent, created = models.Parent.objects.get_or_create(user=profile.user)
                first_login = not parent.student_name
                # login(request, profile.user)
                token, created = Token.objects.get_or_create(user=profile.user)
                return JsonResponse({'verified': True,
                    'first_login': first_login, 'token': token.key,
                    'parent_id': parent.id})

            except Exception as err:
                print (err)
                return JsonResponse({'verified': False, 'reason': 'Unknown'})
        return HttpResponse("Not supported request.", status=403)


class ProfileSerializer(serializers.HyperlinkedModelSerializer):
    # role = RoleSerializer()

    class Meta:
        model = models.Profile
        fields = ('id', 'gender', 'avatar',)


class ProfileViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.Profile.objects.all()
    serializer_class = ProfileSerializer


# Serializers define the API representation.
class UserSerializer(serializers.HyperlinkedModelSerializer):
    profile = ProfileSerializer()

    class Meta:
        model = models.User
        fields = ('id', 'username', 'email', 'is_staff', 'profile')


# ViewSets define the view behavior.
class UserViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.User.objects.all()
    serializer_class = UserSerializer


class RegionSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = models.Region
        fields = ('id', 'name', 'superset', 'admin_level', 'leaf', 'weekly_time_slots')


class RegionViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.Region.objects.all()
    serializer_class = RegionSerializer


class SchoolSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.School
        fields = ('id', 'name', 'address', 'thumbnail', 'region', 'center',
                  'longitude', 'latitude',)


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

class CertificateSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Certificate
    def to_representation(self, instance):
        return self.fields['name'].get_attribute(instance)

class CertificateViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.Certificate.objects.all()
    serializer_class = CertificateSerializer

class TeacherListSerializer(serializers.ModelSerializer):
    avatar = serializers.ImageField()
    tags = TagNameSerializer(many=True)
    subject = SubjectNameSerializer()

    class Meta:
        model = models.Teacher
        fields = ('id', 'avatar', 'gender', 'name', 'degree', 'min_price',
                  'max_price', 'subject', 'grades_shortname', 'tags')


class TeacherSerializer(serializers.ModelSerializer):
    prices = PriceSerializer(many=True)
    avatar = serializers.ImageField()
    tags = TagNameSerializer(many=True)
    certificate_set = CertificateSerializer(many=True)
    grades = GradeNameSerializer(many=True)
    subject = SubjectNameSerializer()
    level = LevelNameSerializer()
    highscore_set = HighscoreSerializer(many=True)
    photo_set = PhotoUrlSerializer(many=True)

    class Meta:
        model = models.Teacher
        fields = ('id', 'avatar', 'gender', 'name', 'degree', 'teaching_age',
                  'level', 'subject', 'grades', 'tags', 'certificate_set',
                  'photo_set', 'highscore_set', 'prices')


class TeacherViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.Teacher.objects.filter(public=True)

    def get_queryset(self):
        queryset = self.queryset

        grade = self.request.query_params.get('grade', None) or None
        if grade is not None:
            queryset = queryset.filter(Q(ability__grade__id__contains=grade) |
                    Q(ability__grade__subset__id__contains=grade)).distinct()

        subject = self.request.query_params.get('subject', None) or None
        if subject is not None:
            queryset = queryset.filter(ability__subject__id__contains=subject)

        tags = self.request.query_params.get('tags', '').split()
        tags = list(map(int, filter(lambda x:x, tags)))
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
        fields = ('name', 'amount', 'expired_at', 'used')

    def get_expired_at(self, obj):
        return int(obj.expired_at.timestamp())


class CouponViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.Coupon.objects.filter()
    def get_queryset(self):
        user = self.request.user;
        try:
            queryset = user.parent.coupon_set.all()
        except:
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


class ParentViewSetSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = models.Parent
        fields = ('student_name', )

    def is_valid(self, raise_exception=False):
        super().is_valid(raise_exception=raise_exception)

class ParentViewSet(ModelViewSet):
    queryset = models.Parent.objects.all()
    serializer_class = ParentViewSetSerializer

    def update(self, request, *args, **kwargs):
        response = super().update(request, *args, **kwargs)
        if response.status_code == 200:
            response.data = {"done": "true"}
        return response
