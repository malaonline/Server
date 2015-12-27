#!/bin/sh

. /opt/jenkins/env/bin/activate
cp -f /opt/keys-pros/local_settings.py server/
pip install -r pip_install.txt
python manage.py migrate
if [ -n "`ps aux | grep gunicorn | grep server.wsgi| awk '{ print $2 }'`" ]
then
    ps aux | grep gunicorn | grep server.wsgi| awk '{ print $2 }' | xargs kill -HUP
else
    gunicorn server.wsgi:application --bind 127.0.0.1:8001 &
fi
