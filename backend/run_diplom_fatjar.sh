#!/bin/bash

THIS=`readlink -f "${BASH_SOURCE[0]}"`
DIR=`dirname "${THIS}"`
pushd $DIR

JAVA_OPTIONS=" -Xms1g -Xmx1g -server"
APP_OPTIONS=""

# Хотелось бы, что бы работало так - APP_OPTIONS="-с conf/applicatiob.conf"
# https://github.com/lightbend/config - используется эта библиотека
# сейчас, похоже, нет поддержки указания файла через параметры вызова.

"${JAVA_HOME}/bin/java" $JAVA_OPTIONS -cp src-acc2/target/scala-2.13/acc2.jar:. com.mcn.diplom.Main $APP_OPTION

popd
