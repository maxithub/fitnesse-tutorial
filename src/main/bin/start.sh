#!/bin/sh

APP_HOME="$(cd "$(dirname "$1")/.."; pwd)"
APP_BIN=fitnesse-${fitnesse.version}.jar
LOG_PATH=$APP_HOME/fitnesse.log
PORT=${fitnesse.port}
INTERVAL=3
MAX_ATTEMPT=5
FIT_ROOT=fitnesseroot
FIT_HOME="$(cd "$APP_HOME/../"; mkdir -p $FIT_ROOT; cd $FIT_ROOT; pwd)"

pid=$(ps -ef|grep $APP_BIN|grep -v grep|grep -v kill|awk '{print $2}')
if [ -n "$pid" ]; then
  echo "FitNesse is already running on process $pid"
else
  echo "Starting FitNesse on port $PORT"
  nohup $JAVA_HOME/bin/java -cp $(printf %s: $APP_HOME/lib/*.jar) fitnesseMain.FitNesseMain -p $PORT -d $FIT_HOME >> $LOG_PATH 2>&1 &

  ret=-1
  count=0
  while [ $ret -ne 0 ] && [ $count -lt $MAX_ATTEMPT ]; do
    sleep $INTERVAL
    count=$((count+1))
    echo "Checking if FitNesse is successfully started on port $PORT, attempt #$count"
    curl http://localhost:$PORT > /dev/null 2>&1
    ret=$?
  done

  if [ $ret -ne 0 ]; then
    echo "Failed to start FitNesse on port $PORT"
    exit 1
  fi
  echo "FitNesse is now successfully running on $PORT"
fi

exit 0