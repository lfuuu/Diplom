#!/bin/bash

THIS=`readlink -f "${BASH_SOURCE[0]}"`
DIR=`dirname "${THIS}"`
pushd $DIR

cd ../docs/c4-dsl

unset DOCKER_HOST

sudo rm plantuml/* -rf

docker run -it --rm -v $PWD:/usr/local/structurizr structurizr/cli export -workspace workspace.dsl  -format plantuml/structurizr -output plantuml

#docker run -it --rm -v $PWD:/usr/local/structurizr structurizr/cli export -workspace workspace.dsl  -format plantuml/c4plantuml -output plantuml

sudo chown -R $USER plantuml

popd
