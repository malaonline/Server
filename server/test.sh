#!/bin/sh
set -e

ENV=/opt/jenkins/env
SET=/opt/keys-pros

cp -Rf $SET/local_settings.py ./server/
cp -Rf $SET/mala_kuailexue.pem ./server/
cp -Rf $SET/mala_kuailexue.pub ./server/

. $ENV/bin/activate

pip install -r pip_install.txt

python manage.py test --noinput --setting=server.custom_settings.ci_settings -v 2
