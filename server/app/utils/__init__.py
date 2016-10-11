import uuid
import string
import random


_chars = string.ascii_letters + string.digits

def random_chars(num):
    return ''.join(random.sample(_chars, num))

def random_name():
    return random_chars(9) + '_' + str(uuid.uuid1())[:23].replace('-','')

def random_string():
    return str(uuid.uuid4())

def get_request_ip(request):
    if 'HTTP_X_FORWARDED_FOR' in request.META:
        return request.META['HTTP_X_FORWARDED_FOR']
    else:
        return request.META['REMOTE_ADDR']


def get_server_host(request):
    return request.scheme + '://' + request.get_host()
