from django.conf.urls import include, url
from . import views

urlpatterns = [
    url(r'^teachers/$', views.TeachersView.as_view(), name="teachers"),
]