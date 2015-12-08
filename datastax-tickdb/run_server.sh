#!/bin/bash
clear
echo "Running Server"

cd datastax-tickdb
mvn jetty:run -Djetty.port=7001
