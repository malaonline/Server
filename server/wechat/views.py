import  logging

# django modules
from django.shortcuts import render, redirect, get_object_or_404
from django.views.generic import View, TemplateView, ListView
from django.db.models import Q
from django.utils import timezone


# local modules
from app import models
# Create your views here.


class TeachersView(ListView):
    model = models.Coupon
    template_name = 'wechat/teacher/teachers.html'