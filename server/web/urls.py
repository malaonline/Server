from django.conf.urls import url
from . import views
from django.views.generic import TemplateView

urlpatterns = [
    url(r'^$', views.Index.as_view(), name="index"), #家长页
    url(r'^teacher/', TemplateView.as_view(template_name="web/teacher_index.html"),name='teacher-index'),
]
