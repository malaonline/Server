from django.conf.urls import include, url
from django.views.generic.base import TemplateView
from . import views
from django.contrib.auth.decorators import login_required

LOGIN_URL = "/teacher/login/"

urlpatterns = [
    url(r'^login/$', views.TeacherLogin.as_view(), name="register"),
    url(r'^logout/$', login_required(views.TeacherLogout.as_view(), login_url=LOGIN_URL), name="logout"),
    url(r'^doc/agree/$', TemplateView.as_view(template_name="teacher/doc/policy.html"), name="doc-agree"),
    url(r'^information/complete/$', login_required(views.CompleteInformation.as_view(), login_url=LOGIN_URL), name="complete-information"),
    url(r'^register/progress/$', login_required(views.RegisterProgress.as_view(), login_url=LOGIN_URL), name="register-progress"),
    url(r'^first_page/$', login_required(views.FirstPage.as_view(), login_url=LOGIN_URL), name="first-page"),
    url(r'^$', login_required(views.FirstPage.as_view(), login_url=LOGIN_URL), name="default-page"),
    url(r'^my_school_timetable/(?P<year>[0-9]{4})/(?P<month>[0-9]{2})/$', login_required(views.MySchoolTimetable.as_view(), login_url=LOGIN_URL), name="my-school-timetable"),
    url(r'^my_students/(?P<student_type>[0-9]{1})/(?P<page_offset>[0-9]+)/$', login_required(views.MyStudents.as_view(), login_url=LOGIN_URL), name="my-students"),
    url(r'^my_evaluation/(?P<comment_type>[0-9]+)/(?P<page_offset>[0-9]+)/$', login_required(views.MyEvaluation.as_view(), login_url=LOGIN_URL), name="my-evaluation"),
    url(r'^my_evaluation/(?P<comment_type>[0-9]+)/(?P<page_offset>[0-9]+)/reply/comment/(?P<id>\d+)$', login_required(views.CommentReply.as_view(), login_url=LOGIN_URL), name="reply-comment"),
    url(r'^certificate/$', views.CertificateView.as_view(), name="certificate"),
    url(r'^certificate/id/$', views.CertificateIDView.as_view(), name="certificate-id"),
    url(r'^certificate/academic/$', views.CertificateAcademicView.as_view(), name="certificate-academic"),
    url(r'^certificate/teaching/$', views.CertificateTeachingView.as_view(), name="certificate-teaching"),
    url(r'^certificate/english/$', views.CertificateEnglishView.as_view(), name="certificate-english"),
    url(r'^certificate/others/$', views.CertificateOthersView.as_view(), name="certificate-others"),
    url(r'^verify_sms_code/$', views.VerifySmsCode.as_view(), name="verify-sms-code"),
    url(r'^highscore/$', views.HighscoreView.as_view(), name="highscore"),
    url(r'^basic_doc/$', views.BasicDocument.as_view(), name="basic_doc"),
    url(r'^achievement$', views.AchievementView.as_view(), name="achievement"),
    url(r'^achievement/(?P<action>\w+)$', views.AchievementView.as_view(), name="achievement-add"),
    url(r'^achievement/(?P<action>\w+)/(?P<id>\d+)$', views.AchievementView.as_view(), name="achievement-edit"),
    url(r'^wallet/$', views.WalletView.as_view(), name="wallet"),
    url(r'^wallet/(?P<action>histories)/$', views.WalletView.as_view(), name="wallet-histories"),
    url(r'^wallet/bankcard/add/$', views.WalletBankcardView.as_view(), name="wallet-bankcard-add"),
    url(r'^wallet/bankcard/add/(?P<step>success)/$', views.WalletBankcardView.as_view(), name="wallet-bankcard-add-success"),
]
