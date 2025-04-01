#!/bin/bash

THIS=`readlink -f "${BASH_SOURCE[0]}"`
DIR=`dirname "${THIS}"`
pushd $DIR

echo "Ваш UID: $(id -u), GID: $(id -g)"

unset DOCKER_HOST

docker-compose --project-name dev-diplom -f docker-compose.dev.yml build

popd
