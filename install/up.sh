#!/bin/bash

THIS=`readlink -f "${BASH_SOURCE[0]}"`
DIR=`dirname "${THIS}"`
pushd $DIR

unset DOCKER_HOST

docker-compose --project-name dev-telbill -f docker-compose.yml up


echo http://0.0.0.0:8022/pgadmin4/ - pgadmin

popd
