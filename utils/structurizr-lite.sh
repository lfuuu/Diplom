#!/bin/bash

THIS=`readlink -f "${BASH_SOURCE[0]}"`
DIR=`dirname "${THIS}"`
pushd $DIR

cd ../docs/c4-dsl

unset DOCKER_HOST

docker run -it --rm -p 8044:8080 -v $PWD:/usr/local/structurizr structurizr/lite

popd
