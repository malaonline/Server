#!/usr/bin/env bash
sudo -u $USER dropdb maladb
sudo -u $USER createdb -O malauser maladb
