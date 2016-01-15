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
python manage.py build_groups_and_permissions
python manage.py add_groups_to_sample_users
python manage.py test
