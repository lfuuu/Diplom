#!/bin/bash

THIS=`readlink -f "${BASH_SOURCE[0]}"`
DIR=`dirname "${THIS}"`
pushd $DIR

cd ../docs/c4-dsl/plantuml

ls *

#sudo plantuml  -Djava.awt.headless=true -tpng -I*.puml  -o /png

sudo java -Djava.awt.headless=true -DRELATIVE_INCLUDE="." -jar  /usr/share/plantuml/plantuml.jar -tpng *  -o png
popd
