from django.conf.urls import url
from . import views
from django.views.generic import TemplateView

urlpatterns = [
    url(r'^$', views.PatriarchIndex.as_view(), name="patriarch-index"),
    url(r'^web/teacher', views.TeacherIndex.as_view(), name='teacher-index'),
    url(r'^policy', views.PolicyView.as_view(), name='policy'),
]
