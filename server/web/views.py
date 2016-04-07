from django.shortcuts import render
from django.views.generic import View


class PatriarchIndex(View):
    def get(self, request):
        return render(request, 'web/patriarch_index.html')


class TeacherIndex(View):
    def get(self, request):
        return render(request, 'web/teacher_index.html')
