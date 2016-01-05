#!/bin/sh

DES=/opt/jenkins/mala/server
ENV=/opt/jenkins/env
SET=/opt/keys-pros

mkdir -p $DES
rsync -r --delete * $DES/
cp -Rf $SET/local_settings.py $DES/server/

. $ENV/bin/activate

cd $DES
pip install -r pip_install.txt
python manage.py migrate
python manage.py collectstatic --noinput
python manage.py test
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
