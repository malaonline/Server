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

## TokenAuthentication

View detail documentation [here](http://www.django-rest-framework.org/api-guide/authentication/#tokenauthentication).

Url: `/api/token-auth/`

Method: POST

Parameters:

- username
- password


Return:

- token


###Example:


```
curl http://127.0.0.1:8000/api/token-auth/ -d 'username=user1&password=pass1'
```

```
{ 'token' : '9944b09199c62bcf9418ad846dd0e4bbdfc6ee4b' }
```


### Celery worker
- celery worker --app=server -l info
- celery -A server beat

- celery multi start taskman --app=server  -c2  --pidfile=taskman.pid --beat
- celery multi restart taskman --app=server  -c2  --pidfile=taskman.pid --beat
- celery multi stop taskman


### Kuailexue API Config
- `mala_kuailexue.pem`:  RSA private key
- `mala_kuailexue.pub`:  RSA public key, kuailexue verify sign with it, unit test also use it.    

*** Please overwrite them in folder "/server/server/" with real keys ***

