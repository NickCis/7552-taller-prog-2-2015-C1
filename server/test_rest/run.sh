#! /usr/bin/bash

echo "Running server"
../build/server -l server.log &

SERVER_PID=$!
echo "Server pid: $SERVER_PID"

sleep 2

echo "Running tests"

mocha ./*.js

echo "Killing server..."
kill $SERVER_PID
wait

echo "Server log..."
cat server.log
