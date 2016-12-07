#!/bin/bash
# $1 - output fir
# $2 - input jar
# $3 - dependency path

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
MULTI_DEX=""
if [ "$use_proguard" == "false" ]; then
    MULTI_DEX="--multi-dex"
    if [ ! -e $3/android-support-multidex.jar ]; then
        MULTIDEX_JAR_PATH="$sdk_dir/extras/android/support/multidex/library/libs/android-support-multidex.jar"
        if [ ! -e $MULTIDEX_JAR_PATH ]; then
            echo "Error: Install 'Android Support Library' and 'Android Support Repository' via Android SDK Manager before build"
            exit 1
        fi
    fi
fi

echo "> $dx_dir/dx --dex $MULTI_DEX --output=$1 $2 $SUPPORT_V4_JAR_PATH $MULTIDEX_JAR_PATH"
$dx_dir/dx --dex $MULTI_DEX --output=$1 $2 $SUPPORT_V4_JAR_PATH $MULTIDEX_JAR_PATH