# DB settings for Travis CI
DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.postgresql_psycopg2',
        'NAME': 'maladb',
        'USER': 'postgres',
        'PASSWORD': '',
        'HOST': '127.0.0.1',
        'PORT': '5432',
        'TEST': {
            'NAME': 'test_mala',
        }
    }
}

# 关闭SMS短信发送功能
FAKE_SMS_SERVER = True

COMPRESS_URL = '/static/'

STATIC_ROOT = '/tmp/var/www/static/'
MEDIA_ROOT = '/tmp/var/www/upload/'

JPUSH_APP_KEY = '5d4d4dc079a022deee259fb1'
JPUSH_MASTER_SECRET = 'ef010e142da0aa8c4be23eaf'
