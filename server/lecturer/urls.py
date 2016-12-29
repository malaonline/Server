from django.conf.urls import url
from . import views

urlpatterns = [
    url(r'^$', views.home, name="home"),
    url(r'^index$', views.IndexView.as_view(), name="index"),
    url(r'^login$', views.LoginView.as_view(), name="login"),
    url(r'^logout$', views.logout, name="logout"),
    url(r'^timeslots$', views.TimeslotsView.as_view(), name="timeslots"),
    url(r'^living$', views.LivingView.as_view(), name="living"),
    url(r'^timeslot/(?P<tsid>\d+)/questions$',
        views.LCTimeslotQuestionsView.as_view(), name="timeslot-questions"),
    url(r'^exercise/store$',
        views.ExerciseStore.as_view(), name="exercise-store"),
    url(r'^api/exercise/store$',
        views.ApiExerciseStore.as_view(), name="api-exercise-store"),
]
