#!/bin/bash
# This script is used to cleaning up project folder from build files.
# Invoking this script directly is not recomended, please use mttest.sh clean command instead.

source $MTTEST_PROJECT_ROOT/scripts/functions.sh
source $MTTEST_PROJECT_ROOT/mttest.properties

echo ">  ant clean"
ant clean
