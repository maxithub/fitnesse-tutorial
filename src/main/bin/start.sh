#!/bin/sh

BASEDIR=$(dirname "$0")
APP_HOME=$BASEDIR/..
APP_BIN=fitnesse.jar
LOG_PATH=$APP_HOME/fitnesse.log
FIT_HOME=$APP_HOME/../fitnesseroot
PORT=8003
INTERVAL=3
MAX_ATTEMPT=5

mkdir -p $FIT_HOME

pid=`ps -ef|grep $APP_BIN|grep -v grep|grep -v kill|awk '${print $2}'`
if [ -n "$pid" ]; then
  echo "FitNesse is already running on process $pid"
else
  echo "Starting FitNesse on port $PORT"
  nohup $JAVA_HOME/bin/java -jar $BASEDIR/$APP_BIN -p $PORT -d $FIT_HOME >> $LOG_PATH 2>&1 &

  ret=-1
  count=0
  while [ $ret -ne 0 ] && [ $count -lt $MAX_ATTEMPT ]; do
    sleep $INTERVAL
    let count=count+1
    echo "Checking if FitNesse is successfully started on port $PORT, attempt #$count"
    echo > /dev/tcp/localhost/$PORT
    ret=$?
  done

  if [ $ret -ne 0 ]; then
    echo "Failed to start FitNesse on port $PORT"
    exit 1
  fi
  echo "FitNesse is now successfully running on $PORT"
fi

exit 0