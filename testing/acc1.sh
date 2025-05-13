#!/bin/bash

# Биллингуем тестовые звонки звонка

##URL=http://0.0.0.0:8022/billing/acc

URL=http://localhost:8084/v1/api/acc

curl -X 'POST' \
  ''$URL'' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "callId": 1234,
  "srcNumber": "74963330003",
  "dstNumber": "79261231111",
  "setupTime": "2025-05-05T01:01:01.000Z",
  "connectTime": "2025-05-05T01:01:02.000Z",
  "disconnectTime": "2025-05-05T01:01:52.000Z",
  "sessionTime": 51,
  "disconnectCause": 16,
  "srcRoute": "vpbx1",
  "dstRoute": "op1"
}'

curl -X 'POST' \
  ''$URL'' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "callId": 3321,
  "srcNumber": "74963330003",
  "dstNumber": "79031233344",
  "setupTime": "2025-05-05T01:01:01.000Z",
  "connectTime": "2025-05-05T01:01:02.000Z",
  "disconnectTime": "2025-05-05T01:01:42.000Z",
  "sessionTime": 41,
  "disconnectCause": 16,
  "srcRoute": "vpbx1",
  "dstRoute": "op2"
}'

curl -X 'POST' \
  ''$URL'' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "callId": 3321,
  "srcNumber": "79241233344",
  "dstNumber": "74951110002",
  "setupTime": "2025-05-05T01:01:01.000Z",
  "connectTime": "2025-05-05T01:01:02.000Z",
  "disconnectTime": "2025-05-05T01:01:32.000Z",
  "sessionTime": 31,
  "disconnectCause": 16,
  "srcRoute": "op1",
  "dstRoute": "vpbx1"
}'

curl -X 'POST' \
  ''$URL'' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "callId": 3321,
  "srcNumber": "79162345666",
  "dstNumber": "74951110002",
  "setupTime": "2025-05-05T01:01:01.000Z",
  "connectTime": "2025-05-05T01:01:02.000Z",
  "disconnectTime": "2025-05-05T01:01:02.000Z",
  "sessionTime": 0,
  "disconnectCause": 31,
  "srcRoute": "op3",
  "dstRoute": "vpbx1"
}'
