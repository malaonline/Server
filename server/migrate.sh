#!/bin/sh
set -e

DES=/opt/jenkins/mala
ENV=/opt/jenkins/env
SET=/opt/keys-pros

mkdir -p $DES
rsync -rv --delete .. $DES --exclude=ios --exclude=android
cp -Rf $SET/local_settings.py $DES/server/

. $ENV/bin/activate
pip install -r pip_install.txt --upgrade

cd $DES

python manage.py migrate --noinput
python manage.py compilestatic
python manage.py collectstatic --noinput
python manage.py mala_all
