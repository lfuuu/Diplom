#!/bin/bash

THIS=`readlink -f "${BASH_SOURCE[0]}"`
DIR=`dirname "${THIS}"`
pushd $DIR

cd ../docs/c4-dsl

unset DOCKER_HOST

docker run -it --rm -v $PWD:/usr/local/structurizr structurizr/cli push --workspace workspace.dsl -url http://10.252.0.23:8080/api -id 1 -key 04496cf0-5f7c-4307-ac10-5d14d2bba789 -secret 7291cb9d-1d99-4f83-a2d6-0308e396bf60

popd
