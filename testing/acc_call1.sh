#!/bin/bash


URL=http://localhost:8084/v1/api/testCall

curl -X 'POST' \
  ''$URL'' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "cdr": {
    "id": 0,
    "callId": 0,
    "srcNumber": "74951059999",
    "dstNumber": "74951359999",
    "setupTime": "2025-05-16T06:29:07.641Z",
    "connectTime": "2025-05-16T06:29:07.641Z",
    "disconnectTime": "2025-05-16T06:29:07.641Z",
    "sessionTime": 60,
    "disconnectCause": 0,
    "srcRoute": "op1",
    "dstRoute": "vpbx1"
  },
  "orig": true
}'
