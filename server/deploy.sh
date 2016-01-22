#!/bin/sh

DES=/opt/jenkins/mala/server
ENV=/opt/jenkins/env

. $ENV/bin/activate

cd $DES

python manage.py migrate
python manage.py compilestatic
python manage.py collectstatic --noinput
python manage.py mala_all
if [ -n "`ps aux | grep gunicorn | grep server.wsgi| awk '{ print $2 }'`" ]
then
    echo 'Restarting gunicorn...'
    ps aux | grep gunicorn | grep server.wsgi| awk '{ print $2 }' | xargs kill -HUP
    echo 'Restarted.'
else
    echo 'Starting gunicorn...'
    gunicorn server.wsgi:application --bind 127.0.0.1:8001 &
    echo 'Started.'
fi
