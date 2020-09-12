#!/bin/sh

INTERVAL=3
MAX_ATTEMPT=5

stop_app () {
  name=$1
  bin=$2
  pid=$(ps -ef|grep $bin|grep -v grep|grep -v kill|awk '{print $2}')
  if [ -n "$pid" ]; then
    echo "$name is running on process $pid, stopping it now"
    kill -15 $pid

    count=0
    while [ -n "$pid" ] && [ $count -lt $MAX_ATTEMPT ]; do
      sleep $INTERVAL
      count=$((count+1))
      echo "Checking if $name is successfully stopped, attemp #$count"
      pid=$(ps -ef|grep $bin|grep -v grep|grep -v kill|awk '{print $2}')
    done

    if [ -n "$pid" ]; then
      echo "$name is still running on process $pid, killing it now"
      kill -9 $pid
    else
      echo "$name is now down"
    fi
  else
    echo "$name is not running"
  fi
}

stop_app FitNesse fitnesse-${fitnesse.version}.jar