import json
# from django.contrib.auth.models import User
from django.shortcuts import get_object_or_404
from django.views.generic import View
# from django.shortcuts import render
from django.http import HttpResponse, JsonResponse
from django.db.models import Q
from rest_framework import serializers, viewsets
import random
import requests
from django.views.decorators.csrf import csrf_exempt
from django.utils.decorators import method_decorator
import datetime
from django.utils import timezone
from django.conf import settings

from app import models


class Policy(View):
    def get(self, request):
        policy = get_object_or_404(models.Policy, pk=1)
        data = dict(result=policy.content,
                    updated_at=int(policy.updated_at.timestamp()))
        return HttpResponse(json.dumps(data), content_type='application/json')

class Sms(View):
    @method_decorator(csrf_exempt)
    def dispatch(self, request, *args, **kwargs):
        return super(Sms, self).dispatch(request, *args, **kwargs)

    def callSendSms(self, phone, msg):
        apikey = settings.YUNPIAN_API_KEY # get apikey by global settings
        params = {'apikey': apikey, 'mobile': phone, 'text': msg}
        print (params)
        url = "http://yunpian.com/v1/sms/send.json"
        headers = {"Accept": "text/plain;charset=utf-8;", "Content-Type":"application/x-www-form-urlencoded;charset=utf-8;"}
        return requests.post(url, headers=headers, data=params)

    def callSendSmsCheckcode(self, phone, checkcode):
        SITE_NAME = '麻辣老师'
        msg = "【"+SITE_NAME+"】您的验证码是"+str(checkcode)
        return self.callSendSms(phone, msg)

    def generateCheckcode(self, phone):
        # 生成，并保存到数据库或缓存，10分钟后过期
        obj, created = models.Checkcode.objects.get_or_create(phone=phone, defaults={'checkcode': random.randrange(1000, 9999)})
        if not created:
            now = timezone.now()
            delta = now - obj.updated_at
            if delta > datetime.timedelta(minutes=10):
                obj.checkcode = random.randrange(1000, 9999)
                obj.save()
        return obj.checkcode

    # @method_decorator(csrf_exempt) # here it doesn't work
    def post(self, request):
        action = request.POST.get('action')
        if action == 'send':
            phone = request.POST.get('phone') # TODO: valid phone NO. && add test phone NO.
            if not phone:
                return JsonResponse({'sent': False, 'reason': 'phone is required'})
            try:
                # generate code
                checkcode = self.generateCheckcode(phone)
                print ('验证码：' + str(checkcode))
                # call send sms api
                r = self.callSendSmsCheckcode(phone, checkcode)
                print (r)
                return JsonResponse({'sent': True})
            except Exception as err:
                print (err)
                return JsonResponse({'sent': False, 'reason': 'Unknown'})
        if action == 'verify':
            return HttpResponse('TODO: please wait')
        return HttpResponse("Not supported request.", status=403)


class RoleSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Role
        fields = ('id', 'name')


class RoleViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.Role.objects.all()
    serializer_class = RoleSerializer


class ProfileSerializer(serializers.HyperlinkedModelSerializer):
    role = RoleSerializer()

    class Meta:
        model = models.Profile
        fields = ('id', 'role', 'gender', 'avatar',)


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


class SchoolSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = models.School
        fields = ('id', 'name', 'address', 'thumbnail', 'region', 'center',
                  'longitude', 'latitude',)


class SchoolViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.School.objects.all()
    serializer_class = SchoolSerializer


class SubjectSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Subject
        fields = ('id', 'name')

class SubjectNameSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Subject

    def to_representation(self, instance):
        return self.fields['name'].get_attribute(instance)


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
    class Meta:
        model = models.Grade
        fields = ('id', 'name', 'subset', 'subjects')


GradeSerializer._declared_fields['subset'] = GradeSerializer(many=True)

class GradeNameSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Grade

    def to_representation(self, instance):
        return self.fields['name'].get_attribute(instance)

class GradeViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.Grade.objects.all().filter(superset=None)
    serializer_class = GradeSerializer

class PriceSerializer(serializers.ModelSerializer):
    grade = GradeNameSerializer()

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
        fields = ('id', 'name', 'increased_scores', 'school_name',
                  'admitted_to')


class HighscoreViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.Highscore.objects.all()
    serializer_class = HighscoreSerializer


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
    grades = GradeNameSerializer(many=True)
    subject = SubjectNameSerializer()

    class Meta:
        model = models.Teacher
        fields = ('id', 'avatar', 'gender', 'name', 'degree', 'min_price',
                  'max_price', 'subject', 'grades', 'tags',)


class TeacherSerializer(serializers.ModelSerializer):
    prices = PriceSerializer(many=True)
    avatar = serializers.ImageField()
    tags = TagNameSerializer(many=True)
    certificate_set = CertificateSerializer(many=True)
    grades = GradeNameSerializer(many=True)
    subject = SubjectNameSerializer()
    level = LevelNameSerializer()

    class Meta:
        model = models.Teacher
        fields = ('id', 'avatar', 'gender', 'name', 'degree', 'teaching_age',
                  'level', 'subject', 'grades', 'tags', 'certificate_set',
                  'highscore_set', 'prices')


class TeacherViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.Teacher.objects.all()

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


class WeeklyTimeSlotSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = models.WeeklyTimeSlot
        fields = ('id', 'weekday', 'start', 'end',)


class WeeklyTimeSlotViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.WeeklyTimeSlot.objects.all()
    serializer_class = WeeklyTimeSlotSerializer

