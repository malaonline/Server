from django.conf import settings

def gitrev(request):
    return {'rev': settings.GIT_REV,
            'deployed_at': settings.DEPLOYED_AT,}
