#!/bin/sh

DES=/opt/jenkins/mala/server
ENV=/opt/jenkins/env
SET=/opt/keys-pros

. $ENV/bin/activate
cd $DES

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

if [ -n "`ps aux | grep celery | grep python | awk '{ print $2 }'`" ]
then
    echo 'Restarting celery...'
    celery multi restart 1 --pidfile=/var/run/celery/%n.pid
    echo 'Restarted.'
else
    echo 'Starting celery...'
    celery multi start 1 -A server -l info -c4 --pidfile=/var/run/celery/%n.pid
    echo 'Started.'
fi
