from django.conf.urls import include, url
from . import views


urlpatterns = [
    url(r'^$', views.index, name="index"),

    url(r'^login$', views.LoginView.as_view(), name="login"),
    url(r'^logout$', views.logout, name="logout"),

    url(r'^teachers$', views.TeacherView.as_view(), name="teachers"),
    url(r'^parents$', views.ParentView.as_view(), name="parents"),
    url(r'^orders$', views.OrderView.as_view(), name="orders"),
]
