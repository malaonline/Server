from django.conf import settings

def gitrev(request):
    return {'rev': settings.GIT_REV,
            'last_updated_at': settings.GIT_DATE,
            'deployed_at': settings.DEPLOYED_AT,}
