#!/bin/bash

DIR=$(dirname "$(readlink -f "$0")")

cd $DIR
mvn clean compile assembly:single
