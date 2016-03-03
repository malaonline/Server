from django.shortcuts import render
from django.views.generic import View

# Create your views here.


class Index(View):
    def get(self, request):
        # 官网的初始页面
        return render(request, 'web/index.html')
