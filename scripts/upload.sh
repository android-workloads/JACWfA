#!/bin/bash
# This script uploads all necessary files of the test suit on the target device.

DATA_DIR=$1
EXEC_DIR=$2
echo "START PUSHING DATA_DIR=$DATA_DIR EXEC_DIR=$EXEC_DIR"
if [ -n "$EXEC_DIR" ]; then
    adb shell mkdir -p $EXEC_DIR
	echo "push: $MTTEST_PROJECT_ROOT/build/apk/$MTTEST_DEVICE_EXEC_NAME -> $EXEC_DIR/$MTTEST_DEVICE_EXEC_NAME"
	adb push $MTTEST_PROJECT_ROOT/build/apk/$MTTEST_DEVICE_EXEC_NAME $EXEC_DIR
	adb push $MTTEST_PROJECT_ROOT/build/native/android $EXEC_DIR/libs
fi

#push resources
adb shell mkdir -p $DATA_DIR/
adb shell rm -rf $DATA_DIR/goldens
adb shell rm -rf $DATA_DIR/testsets
adb shell rm -rf $DATA_DIR/configs

adb push $MTTEST_PROJECT_ROOT/assets/goldens $DATA_DIR/goldens

adb push $MTTEST_PROJECT_ROOT/assets/testsets $DATA_DIR/testsets

adb push $MTTEST_PROJECT_ROOT/assets/configs $DATA_DIR/configs
