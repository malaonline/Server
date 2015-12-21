#!/bin/sh
cur_date=`date "+%Y-%m-%d"`
cur_datetime=`date "+%Y-%m-%d_%H:%M:%S"`
ROOT_DIR=`dirname $0`
# echo "ROOT_DIR: $ROOT_DIR"

###############################################################################
# usage: the.sh -m -f [LOG_FILE] [MAIL_LIST]
#   -m 指定是否发邮件，后面跟着邮件地址列表。默认会发给自己`whoami`@`hostname`
#   -f [LOG_FILE] 指定日志文件
#      若指定-m而没有指定$LOG_FILE，默认为$ROOT_DIR/logs/nightly-release.$cur_datetime.log
#
###############################################################################
SEND_MAIL=false
MAIL_LIST=""
LOG_FILE=""
while getopts ":mf:" Option
do
  case $Option in
    m) SEND_MAIL=true;;
    f) LOG_FILE="$OPTARG";;
    *) echo "Unsupported option $Option";;
  esac
done
shift $(($OPTIND - 1))
MAIL_LIST=$*
if [ "$SEND_MAIL" = "true" -a "$LOG_FILE" = "" ]; then
  LOG_FILE="$ROOT_DIR/logs/nightly-release.$cur_datetime.log"
fi

# echo "LOG_FILE: "$LOG_FILE
# echo $SEND_MAIL
# echo $MAIL_LIST
# exit

if [ "$LOG_FILE" != "" ]; then
  exec >> $LOG_FILE 2>&1
fi
echo "nightly-release start @$cur_datetime"

# 1. git update
echo
echo "git update"
echo
git pull
if [ $? != 0 ]; then
  echo "---------------"
  echo "try git pull again"
  git pull
fi
echo
echo "git update end"

# 2. gradle build
BUILD_OK=true
echo
echo "build start"
echo
./gradlew clean build
if [ $? != 0 ]; then
  echo "---------------"
  echo "try build again"
  ./gradlew clean build
  if [ $? != 0 ]; then
    echo "---------------------"
    echo "try build again again"
    ./gradlew clean build
    if [ $? != 0 ]; then
      BUILD_OK=false
      echo "nightly-release build error @$cur_datetime" # | mail -s "build error" liumengjun@liumengjunsMini.lan liumengjun@malalaoshi.com
      if [ "$SEND_MAIL" = "true" ]; then
        mail -s "build error" `whoami`@`hostname` $MAIL_LIST < $LOG_FILE
      fi
      exit
    fi
  fi
fi
echo
echo "build finish"

# 3. deploy to NAS
echo
echo "deploy to NAS make sure dest folder exists"
# release_dir="/Volumes/home/release/nightly/"
# mkdir $(dirname $release_dir)
# mkdir $release_dir
# dest_dir=$release_dir$cur_date
# mkdir $dest_dir
## use ssh copy
NAS_HOST=172.16.0.10
USER=root
REMOTE_DIR=/volume1/homes/mlonline/release/nightly/$(date "+%F-%H%M%S")
ssh $USER@$NAS_HOST mkdir -p $REMOTE_DIR

echo
echo "deploy to NAS start"
# cp app/build/outputs/apk/*.apk "$dest_dir"
## use scp
scp app/build/outputs/apk/*.apk $USER@$NAS_HOST:$REMOTE_DIR
echo "deploy to NAS finish"

# finish
echo
echo "nightly-release finish"
