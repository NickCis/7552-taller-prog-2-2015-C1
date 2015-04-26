#! /bin/bash
echo "Creo usuario Pepe"
curl -s --data "user=pepe&pass=123456789" http://localhost:8000/signup && echo
echo "Creo usuario Foo"
curl -s --data "user=foo&pass=123456789" http://localhost:8000/signup && echo

echo "Conecto a Pepe"
TOKEN_PEPE=$(curl -s --data "user=pepe&pass=123456789" http://localhost:8000/auth 2>&1 |  sed  -n 's/^[^:]*: "\([^"]*\)".*$/\1/p')
echo "Token de Pepe: $TOKEN_PEPE"
echo "Conecto a Foo"
TOKEN_FOO=$(curl -s --data "user=foo&pass=123456789" http://localhost:8000/auth 2>&1 |  sed  -n 's/^[^:]*: "\([^"]*\)".*$/\1/p')
echo "Token de Foo: $TOKEN_FOO"

echo "Foo manda mensajes a pepe"
curl -s --data "access_token=$TOKEN_FOO&message=primer mensaje a pepe" http://localhost:8000/user/pepe/messages && echo
curl -s --data "access_token=$TOKEN_FOO&message=segundo mensaje a pepe" http://localhost:8000/user/pepe/messages && echo
curl -s --data "access_token=$TOKEN_FOO&message=tercer mensaje a pepe" http://localhost:8000/user/pepe/messages && echo
curl -s --data "access_token=$TOKEN_FOO&message=cuarto mensaje a pepe" http://localhost:8000/user/pepe/messages && echo
curl -s --data "access_token=$TOKEN_FOO&message=quinto mensaje a pepepe" http://localhost:8000/user/pepe/messages && echo
curl -s --data "access_token=$TOKEN_FOO&message=sexto mensaje a pepe" http://localhost:8000/user/pepe/messages && echo

echo "Notificaciones de pepe"
curl -s "http://localhost:8000/notification?access_token=$TOKEN_PEPE" && echo
