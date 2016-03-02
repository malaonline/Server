from ..settings import *

# Jenkins单元测试专用的settings

# 关闭SMS短信发送功能
FAKE_SMS_SERVER = True

# 用sqlite替代postgresql
DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.sqlite3',
        'NAME': os.path.join(BASE_DIR, 'db.sqlite3'),
    }
}
