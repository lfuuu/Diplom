#!/bin/bash

THIS=`readlink -f "${BASH_SOURCE[0]}"`
DIR=`dirname "${THIS}"`
pushd $DIR

pg_dump -U postgres -d nispd -h db -f cont_postgresql/init.sql

popd