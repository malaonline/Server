from django.shortcuts import render
from .decorators import mala_staff_required
from django.http import HttpResponse, JsonResponse

# Create your views here.

@mala_staff_required
def index(request):
    return render(request, 'staff/index.html')

def login(request):
    return HttpResponse('todo login')
