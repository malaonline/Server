import json
from django.contrib.auth.models import User
from django.shortcuts import get_object_or_404
from django.views.generic import View
from django.shortcuts import render
from django.http import HttpResponse, JsonResponse
from django.db.models import Q
from rest_framework import serializers, viewsets
import random
import urllib
import httplib2
from django.views.decorators.csrf import csrf_exempt

from app import models

class Policy(View):
    def get(self, request):
        policy = get_object_or_404(models.Policy, pk=1)
        data = dict(result=policy.content,
                updated_at=int(policy.updated_at.timestamp()))
        return HttpResponse(json.dumps(data), content_type='application/json')

# client 提交post 到 django出现403错误
@csrf_exempt
def Sms(request):
    def callSendSms(phone, msg):
        apikey = 'test_key' # TODO: get apikey by global settings
        params = {'apikey': apikey, 'mobile': phone, 'text': msg}
        print (params)
        url = "http://yunpian.com/v1/sms/send.json"
        headers = {"Accept": "text/plain;charset=utf-8;", "Content-Type":"application/x-www-form-urlencoded;charset=utf-8;"}
        httpClient = httplib2.Http()
        return httpClient.request(url, "POST", headers=headers, body=urllib.parse.urlencode(params))

    def callSendSmsCheckcode(phone, checkCode):
        SITE_NAME = '麻辣老师'
        msg = "【"+SITE_NAME+"】您的验证码是"+str(checkCode)
        return callSendSms(phone, msg)

    def generateCheckcode(phone):
        # TODO: 生成，并保存到数据库或缓存，10分钟后过期
        return random.randrange(1000, 9999)


    if request.method != 'POST':
        return HttpResponse('Must use POST method', status=403)

    action = request.POST.get('action')
    if action == 'send':
        phone = request.POST.get('phone')
        if not phone:
            return JsonResponse({'sent': False, 'reason': 'phone is required'})
        # generate code
        checkCode = generateCheckcode(phone)
        print ('验证码：' + str(checkCode))
        # call send sms api
        resp, content = callSendSmsCheckcode(phone, checkCode)
        print (resp)
        print ( '-' * 20 )
        print (content)
        return JsonResponse({'sent': True})
    if action == 'verify':
        return HttpResponse('TODO: please wait')
    return HttpResponse("Not supported request.", status=403)

class PriceSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Price
        fields = ('grade', 'price')

class PriceViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.Price.objects.all()
    serializer_class = PriceSerializer

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

class SubjectViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.Subject.objects.all()
    serializer_class = SubjectSerializer

class TagSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = models.Tag
        fields = ('id', 'name')

class TagViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.Tag.objects.all()
    serializer_class = TagSerializer

class GradeSerializer(serializers.ModelSerializer):
    class Meta:
        model = models.Grade
        fields = ('id', 'name', 'subset', 'subjects')

GradeSerializer._declared_fields['subset'] = GradeSerializer(many=True)

class GradeViewSet(viewsets.ReadOnlyModelViewSet):
    queryset = models.Grade.objects.all().filter(superset=None)
    serializer_class = GradeSerializer

class LevelSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = models.Level
        fields = ('id', 'name')

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

class TeacherListSerializer(serializers.ModelSerializer):
    avatar = serializers.ImageField()
    class Meta:
        model = models.Teacher
        fields = ('id', 'avatar', 'gender', 'name', 'degree', 'min_price',
                'max_price', 'subject', 'grades', 'tags',)

class TeacherSerializer(serializers.ModelSerializer):
    prices = PriceSerializer(many=True)
    avatar = serializers.ImageField()
    class Meta:
        model = models.Teacher
        fields = ('id', 'avatar', 'gender', 'name', 'degree', 'teaching_age',
                'level', 'subject', 'grades', 'tags', 'highscore_set', 'prices')

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

