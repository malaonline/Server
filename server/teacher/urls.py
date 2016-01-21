from django.conf.urls import include, url
from django.views.generic.base import TemplateView
from . import views
from django.contrib.auth.decorators import login_required

LOGIN_URL = "/teacher/login"

urlpatterns = [
    url(r'^login/$', views.register, name="register"),
    url(r'^logout/$', views.teacher_logout, name="logout"),
    url(r'^doc/agree/$', TemplateView.as_view(template_name="teacher/doc/policy.html"), name="doc-agree"),
    # url(r'^information/complete/$', views.complete_information, name="complete-information"),
    url(r'^information/complete/$', login_required(views.CompleteInformation.as_view(), login_url=LOGIN_URL), name="complete-information"),
    # url(r'^register/progress/$', views.CompleteInformation.as_view(), name="register-progress"),
    url(r'^register/progress/$', views.register_progress, name="register-progress"),
    url(r'^first_page/$', views.first_page, name="first-page"),
    url(r'^certificate/$', views.CertificateView.as_view(), name="certificate"),
    url(r'^certificate/id/$', views.CertificateIDView.as_view(), name="certificate-id"),
    url(r'^certificate/academic/$', views.CertificateAcademicView.as_view(), name="certificate-academic"),
    url(r'^certificate/teaching/$', views.CertificateTeachingView.as_view(), name="certificate-teaching"),
    url(r'^certificate/english/$', views.CertificateEnglishView.as_view(), name="certificate-english"),
    url(r'^certificate/others/$', views.CertificateOthersView.as_view(), name="certificate-others"),
    url(r'^verify_sms_code/$', views.verify_sms_code, name="verify-sms-code"),
]
