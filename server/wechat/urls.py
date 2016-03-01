from django.conf.urls import include, url
from . import views

urlpatterns = [
    url(r'^teachers/$', views.TeachersView.as_view(), name="teachers"),
    url(r'^teachers/detail/(?P<id>[0-9]+)/$', views.TeacherDetailView.as_view(), name="teacher-detail"),
]