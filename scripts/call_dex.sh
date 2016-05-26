#!/bin/bash

cat $MTTEST_PROJECT_ROOT/mttest.properties
source $MTTEST_PROJECT_ROOT/mttest.properties

SUPPORT_V4_JAR_PATH=""
if [ ! -e $3/android-support-v4.jar ]; then
    SUPPORT_V4_JAR_PATH="$sdk_dir/extras/android/support/v4/android-support-v4.jar"
    if [ ! -e $SUPPORT_V4_JAR_PATH ]; then
        echo "Error: Install 'Android Support Library' and 'Android Support Repository' via Android SDK Manager before build"
        exit 1
    fi
fi

MULTIDEX_JAR_PATH=""
if [ ! -e $3/android-support-multidex.jar ]; then
    MULTIDEX_JAR_PATH="$sdk_dir/extras/android/support/multidex/library/libs/android-support-multidex.jar"
    if [ ! -e $MULTIDEX_JAR_PATH ]; then
        echo "Error: Install 'Android Support Library' and 'Android Support Repository' via Android SDK Manager before build"
        exit 1
    fi
fi

echo "> $dx_dir/dx --dex --multi-dex --output=$1 $2/*.jar $3/*.jar $sdk_dir/extras/android/support/v4/android-support-v4.jar $MULTIDEX_JAR_PATH"
$dx_dir/dx --dex --multi-dex --output=$1 $2/*.jar $3/*.jar $SUPPORT_V4_JAR_PATH $MULTIDEX_JAR_PATH