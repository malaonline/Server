#!/bin/sh

ENV=/opt/jenkins/env
SET=/opt/keys-pros

cp -Rf $SET/local_settings.py ./server/

. $ENV/bin/activate

pip install -r pip_install.txt

python manage.py test --noinput --setting=server.custom_settings.ci_settings
