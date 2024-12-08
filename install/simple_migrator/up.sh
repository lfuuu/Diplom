#!/bin/bash

source ./getauth.sh

export DATABASE_URL="postgresql://$ANTIFRAUD_USER:$ANTIFRAUD_PASSWORD@$ANTIFRAUD_HOST/$ANTIFRAUD_DB"

echo $DATABASE_URL

echo "CREATE TABLE IF NOT EXISTS public.migrations (filename text, CONSTRAINT migrations_pkey PRIMARY KEY (filename));" | psql $DATABASE_URL
./migrate.sh up