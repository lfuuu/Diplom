#!/bin/bash

source ./getauth.sh

dropdb -h $ANTIFRAUD_HOST $ANTIFRAUD_DB -U postgres
createdb -h $ANTIFRAUD_HOST -U postgres -O $ANTIFRAUD_USER $ANTIFRAUD_DB
./up.sh

