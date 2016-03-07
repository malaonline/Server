from django.conf.urls import include, url
from . import views

urlpatterns = [
    url(r'^schools/$', views.SchoolsView.as_view(), name="schools"),
    url(r'^schools/detail/(?P<id>[0-9]+)/$', views.SchoolDetailView.as_view(), name="school-detail"),
    url(r'^teachers/$', views.TeachersView.as_view(), name="teachers"),
    url(r'^teachers/detail/(?P<id>[0-9]+)/$', views.TeacherDetailView.as_view(), name="teacher-detail"),
    url(r'^send_template_ms/$', views.send_template_msg, name="send_template_msg"),
    url(r'^order/course_choosing/(?P<teacher_id>[0-9]+)/$', views.CourseChoosingView.as_view(), name="order-course-choosing"),
]
