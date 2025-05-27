#!/bin/bash

URL=http://localhost:8084/v1/api/doAuth

curl -X 'POST' \
  ''$URL'' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "srcNumber": "74952220001",
  "dstNumber": "79263747216",
  "srcRoute": "vpbx1"
}'

echo "\n"

curl -X 'POST' \
  ''$URL'' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "srcNumber": "74963330003",
  "dstNumber": "79263747216",
  "srcRoute": "vpbx1"
}'

echo "\n"

curl -X 'POST' \
  'http://localhost:8084/v1/api/doAuth' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "srcNumber": "74963330003",
  "dstNumber": "74963333333",
  "srcRoute": "vpbx1"
}'

echo "\n"

curl -X 'POST' \
  'http://localhost:8084/v1/api/doAuth' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "srcNumber": "74963330003",
  "dstNumber": "78883333333",
  "srcRoute": "vpbx1"
}'

echo "\n"

curl -X 'POST' \
  'http://localhost:8084/v1/api/doAuth' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "srcNumber": "74963330003",
  "dstNumber": "74983333333",
  "srcRoute": "vpbx1"
}'

echo "\n"

curl -X 'POST' \
  'http://localhost:8084/v1/api/doAuth' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "srcNumber": "74963330003",
  "dstNumber": "74983333333",
  "srcRoute": "vpbx1"
}'