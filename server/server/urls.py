"""server URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/1.8/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  url(r'^$', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  url(r'^$', Home.as_view(), name='home')
Including another URLconf
    1. Add an import:  from blog import urls as blog_urls
    2. Add a URL to urlpatterns:  url(r'^blog/', include(blog_urls))
"""
import django
import os
from django.conf.urls import include, url
from django.contrib import admin
from django.conf import settings

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

urlpatterns = [
    url(r'^admin/', include(admin.site.urls)),
    url(r'^', include('app.urls')),
    url(r'^', include('django.contrib.auth.urls')),
    url(r'^teacher/', include('teacher.urls', namespace='teacher')),
    url(r'^staff/', include('staff.urls', namespace='staff')),
    url(r'^wechat/', include('wechat.urls', namespace='wechat')),
    url(r'^upload/(?P<path>.*)$', django.views.static.serve,
        {'document_root': settings.MEDIA_ROOT}),
    url(r'^api/v1/(?P<path>.*(\.json|\.yaml))$', django.views.static.serve,
        {'document_root': os.path.join(BASE_DIR, "app", "api")}),
    url(r'^', include('web.urls', namespace='web')),
    url(r'^favicon\.ico$', django.views.generic.base.RedirectView.as_view(
        url=settings.STATIC_URL + 'common/icons/favicon.ico', permanent=True)),
]
