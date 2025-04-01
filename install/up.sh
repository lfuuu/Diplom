#!/bin/bash

THIS=`readlink -f "${BASH_SOURCE[0]}"`
DIR=`dirname "${THIS}"`
pushd $DIR

HOST_UID=$(id -u)
HOST_GID=$(id -g)

export HOST_UID
export HOST_GID

echo "Ваш UID: $HOST_UID, GID: $HOST_GID"
docker-compose --project-name dev-diplom -f docker-compose.dev.yml up


echo http://0.0.0.0:8022/pgadmin4/ - pgadmin

popd
