## Getting started

```
cd server
pip install -r pip_install.txt
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
