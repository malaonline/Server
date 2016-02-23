from django.conf.urls import include, url
from . import views


urlpatterns = [
    url(r'^$', views.index, name="index"),
    url(r'^login/$', views.login, name="login"),
    url(r'^login/auth/$', views.login_auth, name="login_auth"),
    url(r'^logout/$', views.logout, name="logout"),
    url(r'^students/$', views.StudentView.as_view(), name="students"),
    url(r'^students/schedule/manage$', views.StudentScheduleManageView.as_view(), name="student_schedule_manage"),
    url(r'^students/schedule/changelog$', views.StudentScheduleChangelogView.as_view(), name="student_schedule_changelog"),
    url(r'^teachers/$', views.TeacherView.as_view(), name="teachers"),
    url(r'^teachers/unpublished/$', views.TeacherUnpublishedView.as_view(), name="teachers_unpublished"),
    url(r'^teachers/unpublished/(?P<tid>\d+)/edit$', views.TeacherUnpublishedEditView.as_view(), name="teachers_unpublished_edit"),
    url(r'^teachers/published/$', views.TeacherPublishedView.as_view(), name="teachers_published"),
    url(r'^teachers/action/$', views.TeacherActionView.as_view(), name="teachers_action"),
    url(r'^schools/$', views.SchoolsView.as_view(), name="schools"),
    url(r'^school/$', views.SchoolView.as_view(), name='staff_school'),
    url(r'^school/timeslot$', views.SchoolTimeslotView.as_view(), name='school_timeslot'),
    url(r'^orders/review/$', views.OrderReviewView.as_view(), name="orders_review"),
    url(r'^orders/refund/$', views.OrderRefundView.as_view(), name="orders_refund"),
]
