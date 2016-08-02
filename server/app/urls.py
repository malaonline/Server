from django.conf.urls import include, url
from django.views.generic import TemplateView

from rest_framework import routers
from rest_framework.authtoken import views as authviews

from . import views

# Routers provide an easy way of automatically determining the URL conf.
router = routers.DefaultRouter(trailing_slash=False)
router.register(r'users', views.UserViewSet)
router.register(r'regions', views.RegionViewSet)
router.register(r'schools', views.SchoolViewSet)
router.register(r'grades', views.GradeViewSet)
router.register(r'subjects', views.SubjectViewSet)
router.register(r'tags', views.TagViewSet)
router.register(r'levels', views.LevelViewSet)
router.register(r'profiles', views.ProfileViewSet)
router.register(r'teachers', views.TeacherViewSet)
router.register(r'memberservices', views.MemberserviceViewSet)
router.register(r'coupons', views.CouponViewSet)
router.register(r'weeklytimeslots', views.WeeklyTimeSlotViewSet)
router.register(r'parents', views.ParentViewSet)
router.register(r'timeslots', views.TimeSlotViewSet)
router.register(r'orders', views.OrderViewSet)
router.register(r'comments', views.CommentViewSet)
router.register(r'favorites', views.FavoriteViewSet)


urlpatterns = [
    # url(r'^$', TemplateView.as_view(template_name='app/index.html')),
    url(r'^api-auth', include(
        'rest_framework.urls', namespace='rest_framework')),
    url(r'^api/v1/teachers/(?P<teacher_id>\d+)/weeklytimeslots',
        views.TeacherWeeklyTimeSlot.as_view(),
        name='teacher_weekly_time_slot'),
    url(r'^api/v1/token-auth', authviews.obtain_auth_token),
    url(r'^api/v1/policy', views.Policy.as_view(), name='policy'),
    url(r'^api/v1/subject/(?P<subject_id>\d+)/record',
        views.SubjectRecord.as_view(), name='subject_record'),
    url(r'^api/v1/sms', views.Sms.as_view(), name='sms'),
    url(r'^api/v1/charge_succeeded', views.ChargeSucceeded.as_view(),
        name='charge_succeeded'),
    url(r'^api/v1/concrete/timeslots', views.ConcreteTimeSlots.as_view(),
        name='concrete_time_slot'),
    url(r'^api/v1/unpaid_count', views.UnpaidCount.as_view(),
        name='unpaid_count'),
    url(r'^api/v1/my_center', views.ParentCenter.as_view(),
        name='my_center'),
    url(r'^test/testAutoConfirmClasses', views.autoConfirmClassesView.as_view(),
        name='autoConfirmClasses'),
    url(r'^api/v1/study_report(\/?)$', views.StudyReportView.as_view(),
        name='study_report_api'),
    url(r'^api/v1/study_report/(?P<subject>\w+)(\/?)$', views.StudyReportView.as_view(),
        name="study_report_subject"),
    # url(r'^api/v1/study_report/(?P<subject>\w+)/(?P<category>\w+)(\/?)$', views.StudyReportView.as_view(),
    #     name="study_report_category"),
    url(r'^api/v1/', include(router.urls)),
]
