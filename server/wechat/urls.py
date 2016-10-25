from django.conf.urls import include, url
from django.views.generic import RedirectView
from . import views

urlpatterns = [
    url(r'^$', RedirectView.as_view(url='teachers/'), name="index"),
    url(r'^schools/$', views.SchoolsView.as_view(), name="schools"),
    url(r'^schools/map/(?P<pk>[0-9]+)/$', views.SchoolMapView.as_view(), name='school-map'),
    url(r'^schools/photos/(?P<pk>[0-9]+)/$', views.SchoolPhotosView.as_view(), name="school-photos"),
    url(r'^teachers/$', views.TeachersView.as_view(), name="teachers"),
    # url(r'^teachers/detail/(?P<id>[0-9]+)/$', views.TeacherDetailView.as_view(), name="teacher-detail"),
    url(r'^order/course_choosing/$', views.CourseChoosingView.as_view(), name="order-course-choosing"),
    url(r'^order/coupon/list/$', views.CouponListView.as_view(), name="order-coupon-list"),
    url(r'^order/evaluate/list/$', views.EvaluateListView.as_view(), name="order-evaluate-list"),
    url(r'^teacher/$', views.teacher_view, name="teacher"),
    url(r'^teacher/schools/$', views.getSchoolsWithDistance, name="teacher-schools"),
    url(r'^phone_page/', views.phone_page, name="phone_page"),
    url(r'^add_openid/', views.add_openid, name="add_openid"),
    url(r'^check_phone/', views.check_phone, name="check_phone"),
    url(r'^pay/notify/', views.wx_pay_notify_view, name="wx_pay_notify"),
    url(r'^policy/', views.policy, name="policy"),
    url(r'^report/sample/$', views.ReportSampleView.as_view(), name="report-sample"),
    url(r'^vip/$', views.VipView.as_view(), name="vip"),
    url(r'^register/$', views.RegisterRedirectView.as_view(), name="register"),
]
