## Getting started

```
cd server
(switch into your virtualenv if needed.)
pip install -r pip_install.txt

Recommended way to get started with PostgreSQL(on MAC): http://postgresapp.com
make sure your postgres service in on.

open postgres console by clicking Elephant icon on system bar,
then create user and db on local database server:(you can find PASSWORD here: server/server/settings.py)

create user malauser with password 'PASSWORD';
create database maladb;
GRANT ALL PRIVILEGES on database maladb to malauser;

python manage.py migrate
python manage.py createsuperuser

python manage.py runserver
```

### Celery worker
See Celery Settings [here](#celery).
- celery worker --app=server -l info
- celery -A server beat

- celery multi start taskman --app=server  -c2  --pidfile=taskman.pid --beat
- celery multi restart taskman --app=server  -c2  --pidfile=taskman.pid --beat
- celery multi stop taskman


## Implicit APIs

### TokenAuthentication
View detail documentation [here](http://www.django-rest-framework.org/api-guide/authentication/#tokenauthentication).

Url: `/api/token-auth/`

Method: POST

Parameters:
- `username`
- `password`

Return:
- token

Example:
```
curl http://127.0.0.1:8000/api/token-auth/ -d 'username=user1&password=pass1'
{ 'token' : '9944b09199c62bcf9418ad846dd0e4bbdfc6ee4b' }
```

### Manage Region
Open region or show region info, need authentication staff login.

URL: `/staff/region/${region_id}/`

Method: GET

Parameters:
- `action`: None or in ('open',)

Return: text/html

Example:
```
http://127.0.0.1:8000/staff/region/1/?action=open
```


## Settings/Configurations
See file `server/settings.py`.Please make your own settings in the file `server/local_settings.py`.

### Site
- ``SERVICE_SUPPORT_TEL``: Service Support telephone NO., 'service_hotline' in template.

### Kuailexue API Config
- `KUAILEXUE_API_PRI_KEY`: content of API RSA private key, default `server/server/mala_kuailexue.pem`.
- `KUAILEXUE_API_PUB_KEY`: content of API RSA public key, kuailexue verify sign with it, unit test also use it, default `server/server/mala_kuailexue.pub`.

*Please overwrite them in folder `"/server/server/"` with real keys*

### Celery
- `CELERY_TIMEZONE`: i.e. 'Asia/Shanghai'
- `BROKER_URL`: i.e. 'redis://localhost:6379/0'
- `CELERY_RESULT_BACKEND`: i.e. 'redis://localhost:6379/0'
