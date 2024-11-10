#!/bin/bash

docker run -it --rm -v $PWD:/usr/local/structurizr structurizr/cli $@

