from django.conf.urls import include, url
from django.views.generic.base import TemplateView
from . import views


urlpatterns = [
    url(r'^$', views.register, name="register"),
    url(r'^doc/agree/$', TemplateView.as_view(template_name="teacher/doc/policy.html"), name="doc-agree"),

]