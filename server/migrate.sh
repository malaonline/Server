#!/bin/sh
set -e

DES=/opt/jenkins/mala/server
ENV=/opt/jenkins/env
SET=/opt/keys-pros

mkdir -p $DES
rsync -r --delete * $DES/
cp -Rf $SET/local_settings.py $DES/server/

. $ENV/bin/activate
pip install -r pip_install.txt

cd $DES

python manage.py migrate --noinput
python manage.py compilestatic
python manage.py collectstatic --noinput
python manage.py mala_all
