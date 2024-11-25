#!/bin/bash

THIS=`readlink -f "${BASH_SOURCE[0]}"`
DIR=`dirname "${THIS}"`
pushd $DIR

mkdir -p workspace

docker run -it --rm -p 8080:8080 -v $PWD/workspace:/usr/local/structurizr structurizr/lite

popd
