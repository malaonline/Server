from ..settings import *

# 单元测试短一点,5个就够了
SAMPLE_DATA_LENGTH = 2
UNITTEST = True

STATICFILES_STORAGE = 'django.contrib.staticfiles.storage.StaticFilesStorage'
COMPRESS_URL = '/static/'

STATIC_ROOT = '/tmp/var/www/static/'
MEDIA_ROOT = '/tmp/var/www/upload/'

