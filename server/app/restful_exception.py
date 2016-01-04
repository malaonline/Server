from rest_framework.exceptions import APIException
from rest_framework import status
from django.utils.translation import ugettext_lazy as _
from rest_framework import exceptions
from rest_framework.compat import set_rollback
from rest_framework.response import Response
from django.core.exceptions import PermissionDenied
from django.utils import six
from django.http import Http404
import json


class AlreadyCreated(APIException):
    status_code = status.HTTP_201_CREATED
    default_detail = _('Object already created.')


def exception_handler(exc, context):
    """
    Returns the response that should be used for any given exception.

    By default we handle the REST framework `APIException`, and also
    Django's built-in `Http404` and `PermissionDenied` exceptions.

    Any unhandled exceptions may return `None`, which will cause a 500 error
    to be raised.
    """
    if isinstance(exc, exceptions.APIException):
        headers = {}
        if getattr(exc, 'auth_header', None):
            headers['WWW-Authenticate'] = exc.auth_header
        if getattr(exc, 'wait', None):
            headers['Retry-After'] = '%d' % exc.wait

        if isinstance(exc.detail, (list, dict)):
            data = exc.detail
        else:
            # print(exc.detail)
            # print(type(exc.detail))
            data = json.loads(exc.detail)
            # if isinstance(exc.detail, dict):
            #     data = exc.detail
            # else:
            #     data = {'detail': exc.detail}

        set_rollback()
        return Response(data, status=exc.status_code, headers=headers)

    elif isinstance(exc, Http404):
        msg = _('Not found.')
        data = {'detail': six.text_type(msg)}

        set_rollback()
        return Response(data, status=status.HTTP_404_NOT_FOUND)

    elif isinstance(exc, PermissionDenied):
        msg = _('Permission denied.')
        data = {'detail': six.text_type(msg)}

        set_rollback()
        return Response(data, status=status.HTTP_403_FORBIDDEN)

    # Note: Unhandled exceptions will raise a 500 error.
    return None
