from django.conf.urls import include, url
from django.views.generic.base import TemplateView
from . import views


urlpatterns = [
    url(r'^login/$', views.register, name="register"),
    url(r'^doc/agree/$', TemplateView.as_view(template_name="teacher/doc/policy.html"), name="doc-agree"),
    url(r'^information/complete/$', views.complete_information, name="complete-information"),
    url(r'^register/progress/$', views.register_progress, name="register-progress"),
    url(r'^certificate/$', views.CertificateView.as_view(), name="certificate"),
    url(r'^certificate/id/$', views.CertificateIDView.as_view(), name="certificate-id"),
]
