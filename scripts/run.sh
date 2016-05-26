#!/bin/bash
# This script is written to run workloads in desired configuration.
# Invoking this script directly is not recomended, please use mttest.sh run command instead.

source $MTTEST_PROJECT_ROOT/scripts/functions.sh
source $MTTEST_PROJECT_ROOT/mttest.properties

errors_count=0

case "$MODE" in
	"Java")
		echo "Your test suit will be launched via Java VM"
		test_java_tested_64
		if [ $status -ne 0 ]; then
			((errors_count += 1))
		fi
		test_java_tested_32
		if [ $status -ne 0 ]; then
			((errors_count += 1))
		fi
	;;
	"Host")
		echo "Your test suit will be launched via ART VM on the host"
		test_host_android_top
	;;
	"Device")
		echo "Your test suit will be launched via VM on the device with serial $ANDROID_SERIAL"
		if [ -z "$ANDROID_SERIAL" ]; then
		    echo "mttest.warning: variable ANDROID_SERIAL is '$ANDROID_SERIAL' and it does not look like a valid device number"
		fi
		if [ -z "$DEVICE_DATA_DIR" ] || [ -z "$DEVICE_EXEC_DIR" ]; then
		    echo "mttest.error: you did not specify a directory where test suit's executables will be"
		fi
	;;
	*)
		echo "mttest.error: mode with name '$MODE' is unknown, expected targers: Java, Host, Device."
		exit 0
	;;
esac

if [ $errors_count -ne 0 ]; then
    echo "There are errors ($errors_count). Aborting"
    exit 1
fi

export MTTEST_RESULT_DIR

#run tests
bash $MTTEST_PROJECT_ROOT/scripts/run_tests.sh | tee $MTTEST_LOG