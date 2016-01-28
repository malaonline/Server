#!/bin/sh

ENV=/opt/jenkins/env
SET=/opt/keys-pros

cp -Rf $SET/local_settings.py ./server/

. $ENV/bin/activate
pip install -r pip_install.txt --upgrade

python manage.py test --noinput
