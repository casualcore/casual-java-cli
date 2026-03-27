#!/usr/bin/env bash

script_dir=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
jar="wiremock-standalone.jar"
port=8080
pid=""
keepAlive=""
wiremockStart=""
wiremockStop=""

help()
{
   echo ""
   echo "Usage: $0"
   echo -e "\t-k Keep Wiremock alive between tests"
   echo -e "\t-w Start Wiremock"
   echo -e "\t-s Stop Wiremock"
   echo -e "\t-p Port for Wiremock"
   exit 1
}

function is_running()
{
  response=$(curl -s -w "\n%{http_code}" "http://localhost:$port/__admin/health")
  http_code=$(tail -n1 <<< "$response")
  echo "$http_code"
}

function shutdown()
{
  if ps -p $1 > /dev/null
  then
     kill $1
     echo "Stopped Wiremock on pid: $1"
  fi
}

while getopts "kwsp:" opt
do
   case "$opt" in
      k ) keepAlive="true" ;;
      w ) wiremockStart="true" ;;
      s ) wiremockStop="true" ;;
      p ) port="$OPTARG" ;;
      ? ) help ;;
   esac
done

# check if wiremock already running
if [ $(is_running) == "200" ]; then
  pid=$(ps -ef | grep -v "grep" | grep "$jar" | awk '{print $2}')
  echo "Wiremock already started, pid: $pid, port: $port"
else
  exec java -jar "$script_dir/unittest/$jar" --port $port --disable-banner & disown
  pid=$!
  while [ $(is_running) != "200" ]; do
    echo "Waiting for wiremock, pid: $pid, port: $port"
    sleep 1
  done
fi

# Only start wiremock
if [ ! -z "$wiremockStart" ]; then
  exit 0
fi

# Stop if running
if [ ! -z "$wiremockStop" ]; then
  shutdown "$pid"
  exit 0
fi

WIREMOCK_PORT=$port cmake --build . --preset conan-release --target casual-java-cli-unittest
WIREMOCK_PORT=$port cmake --build . --preset conan-release --target test "$@"

if [ -z "$keepAlive" ]; then
  shutdown "$pid"
fi