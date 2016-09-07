from ..settings import *

# Jenkins单元测试专用的settings
DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.postgresql_psycopg2',
        'NAME': 'maladb',
        'USER': 'postgres',
        'PASSWORD': '',
        'HOST': '127.0.0.1',
        'PORT': '5432',
        'TEST': {
            'NAME': 'test_%s' % subprocess.check_output(
                ['git', 'rev-parse', '--short', 'HEAD']).decode().strip(),
        }
    }
}
CELERY_RESULT_BACKEND = 'redis://localhost:6379/0'
BROKER_URL = 'redis://localhost:6379/0'

# 关闭SMS短信发送功能
FAKE_SMS_SERVER = True

# 用sqlite替代postgresql
# DATABASES = {
#     'default': {
#         'ENGINE': 'django.db.backends.sqlite3',
#         'NAME': os.path.join(BASE_DIR, 'db.sqlite3'),
#     }
# }

# 把上传路径改为本地
STATICFILES_STORAGE = 'django.contrib.staticfiles.storage.StaticFilesStorage'
COMPRESS_URL = '/static/'

STATIC_ROOT = '/tmp/var/www/static/'
MEDIA_ROOT = '/tmp/var/www/upload/'

JPUSH_APP_KEY = '5d4d4dc079a022deee259fb1'
JPUSH_MASTER_SECRET = 'ef010e142da0aa8c4be23eaf'
