from django.shortcuts import render
from django.views.generic import View
from django.http import HttpResponse
from django.shortcuts import get_object_or_404

from app import models


class PatriarchIndex(View):
    def get(self, request):
        return render(request, 'web/patriarch_index.html')


class TeacherIndex(View):
    def get(self, request):
        return render(request, 'web/teacher_index.html')


class PolicyView(View):
    def get(self, request):
        policy = get_object_or_404(models.StaticContent, name='policy')
        return HttpResponse(policy.content) 
