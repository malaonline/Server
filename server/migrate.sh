#!/bin/sh
set -e

DES=/opt/jenkins/mala
ENV=/opt/jenkins/env
SET=/opt/keys-pros

mkdir -p $DES
rsync -rv --delete .. $DES --exclude=ios --exclude=android
cp -Rf $SET/local_settings.py $DES/server/server/
cp -Rf $SET/mala_kuailexue.pem $DES/server/server/
cp -Rf $SET/mala_kuailexue.pub $DES/server/server/

. $ENV/bin/activate
pip install -r pip_install.txt --upgrade

cd $DES/server

python manage.py migrate --noinput
python manage.py compilestatic
python manage.py collectstatic --noinput
