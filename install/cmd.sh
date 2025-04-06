#!/bin/bash

set -e

function build() {
  echo "Build:"
  docker compose  -f docker-compose.dev.yml build \
    --build-arg COMPOSE_PROJECT_NAME=$COMPOSE_PROJECT_NAME \
    --build-arg HOST_GID=$HOST_GID \
    --build-arg HOST_UID=$HOST_UID 
}

function up() {
  echo "Up:"  # --project-name dev-diplom
  docker compose -f docker-compose.dev.yml up
      

  echo http://0.0.0.0:8022/pgadmin4/ - pgadmin
}

function down() {
  echo "Down:"
  docker compose -f docker-compose.dev.yml down
}

function show_help() {
  echo "Использование:"
  echo "------"
  echo ""
  echo "$0 build"
  echo "$0 up"
  echo "$0 down"
  echo "$0 help"
}

function main() {
  ACTION=${1-:"help"}
  shift
  case $ACTION in
    build)
      build $@
      ;;
    up)
      up $@
      ;;
    down)
      down $@
      ;;
    *)
      show_help
      exit 1
  esac
}

THIS=`readlink -f "${BASH_SOURCE[0]}"`
DIR=`dirname "${THIS}"`
pushd $DIR

HOST_UID=$(id -u)
HOST_GID=$(id -g)
COMPOSE_PROJECT_NAME=diplom

export HOST_UID
export HOST_GID
export COMPOSE_PROJECT_NAME

echo "Ваш UID: $HOST_UID, GID: $HOST_GID"

main $@

popd