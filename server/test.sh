#! /bin/bash
curl --data "user=pepe&pass=123456789" http://localhost:8000/signup && echo && sleep 0.1
curl --data "user=pepepe&pass=123456789" http://localhost:8000/signup && echo && sleep 0.1
curl --data "user=foo&pass=123456789" http://localhost:8000/signup && echo && sleep 0.1
curl --data "user=foo&pass=123456789" http://localhost:8000/auth && echo && sleep 0.1

TOKEN=$(curl --data "user=foo&pass=123456789" http://localhost:8000/auth 2>&1 |  sed  -n 's/^[^:]*: "\([^"]*\)".*$/\1/p')

echo "token a usar $TOKEN"

curl --data "access_token=$TOKEN&message=primer mensaje a pepe" http://localhost:8000/user/pepe/messages && echo && sleep 0.1
curl --data "access_token=$TOKEN&message=primer mensaje a pepepe" http://localhost:8000/user/pepepe/messages && echo && sleep 0.1
curl --data "access_token=$TOKEN&message=segundo mensaje a pepe" http://localhost:8000/user/pepe/messages && echo && sleep 0.1
curl --data "access_token=$TOKEN&message=tercer mensaje a pepe" http://localhost:8000/user/pepe/messages && echo && sleep 0.1
curl --data "access_token=$TOKEN&message=segundo mensaje a pepepe" http://localhost:8000/user/pepepe/messages && echo && sleep 0.1
curl --data "access_token=$TOKEN&message=cuarto mensaje a pepe" http://localhost:8000/user/pepe/messages && echo

