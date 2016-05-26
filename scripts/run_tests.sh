#!/bin/bash
# This script is written to run workloads in desired configuration.
# Invoking this script directly is not recomended, please use mttest.sh run command instead.

MTTEST_RUNNER_CLASS="com.intel.mttest.cmd.MTTestRunner"

MTTEST_ARGS=""
[ -n "$MTTEST_SUITE" ] && MTTEST_ARGS="$MTTEST_ARGS -s $MTTEST_SUITE" 
[ -n "$MTTEST_CONFIG" ] && MTTEST_ARGS="$MTTEST_ARGS -c $MTTEST_CONFIG"
[ -n "$MTTEST_VERBOSE" ] && MTTEST_ARGS="$MTTEST_ARGS -v $MTTEST_VERBOSE"
[ -n "$MTTEST_THREADS" ] && MTTEST_ARGS="$MTTEST_ARGS -t $MTTEST_THREADS"
[ -n "$MTTEST_REPEATS" ] && MTTEST_ARGS="$MTTEST_ARGS -n $MTTEST_REPEATS"

case "$MODE" in
	"Java")
		JAVA_LIBRARY_PATH_32="-Djava.library.path=$MTTEST_PROJECT_ROOT/build/native/linux/libs/x86"
		JAVA_LIBRARY_PATH_64="-Djava.library.path=$MTTEST_PROJECT_ROOT/build/native/linux/libs/x86_64"
		JAVA_CLASSPATH="-cp $MTTEST_PROJECT_ROOT/build/jar/*:$MTTEST_PROJECT_ROOT/dependencies/*:$sdk_dir/platforms/$sdk_version/android.jar"
		MTTEST_RESOURCES="-DmttestGoldensDir=./cfg/goldens -DmttestConfigDir=./cfg/configs -DmttestTestSetDir=./cfg/testsets"

		RUN_RESOURCES="$JAVA_CLASSPATH"
		RUN_COMMAND="$MTTEST_RESOURCES $MTTEST_RUNNER_CLASS $MTTEST_ARGS"
		RUN_PLATFORM="$java_home_tested_32/bin/java -client $JAVA_LIBRARY_PATH_32" 
		FULL_COMMAND="$RUN_PLATFORM $RUN_RESOURCES $RUN_COMMAND"
		[ -n "$MTTEST_RESULT_DIR" ] && FULL_COMMAND="$FULL_COMMAND -output ${MTTEST_RESULT_DIR}/hotspot_client_32"
		echo "mode: 32bit java-vm-client"
		echo $FULL_COMMAND
		$FULL_COMMAND

		RUN_PLATFORM="$java_home_tested_32/bin/java -server $JAVA_LIBRARY_PATH_32 $JAVA_CLASSPATH"
		FULL_COMMAND="$RUN_PLATFORM $RUN_RESOURCES $RUN_COMMAND"
        [ -n "$MTTEST_RESULT_DIR" ] && FULL_COMMAND="$FULL_COMMAND -output ${MTTEST_RESULT_DIR}/hotspot_server_32"
		echo "mode: 32bit java-vm-server"
		echo $FULL_COMMAND
		$FULL_COMMAND
		
		RUN_PLATFORM="$java_home_tested_64/bin/java -client $JAVA_LIBRARY_PATH_64"
		FULL_COMMAND="$RUN_PLATFORM $RUN_RESOURCES $RUN_COMMAND"
        [ -n "$MTTEST_RESULT_DIR" ] && FULL_COMMAND="$FULL_COMMAND -output ${MTTEST_RESULT_DIR}/hotspot_client_64"
		echo "mode: 64bit java-vm-client"
		echo $FULL_COMMAND
		$FULL_COMMAND
		
		RUN_PLATFORM="$java_home_tested_64/bin/java -server $JAVA_LIBRARY_PATH_64"
		FULL_COMMAND="$RUN_PLATFORM $RUN_RESOURCES $RUN_COMMAND"
		[ -n "$MTTEST_RESULT_DIR" ] && FULL_COMMAND="$FULL_COMMAND -output ${MTTEST_RESULT_DIR}/hotspot_server_64"
		echo "mode: 64bit java-vm-server"
		echo $FULL_COMMAND
		$FULL_COMMAND
		;;
	"Host")
		export BUILD_DIR=$host_android_top
		export ANDROID_BUILD_TOP=$BUILD_DIR
		export PATH=$ANDROID_BUILD_TOP/out/host/linux-x86/bin:$PATH
		export ANDROID_HOST_OUT=$ANDROID_BUILD_TOP/out/host/linux-x86
		export ANDROID_ROOT=$ANDROID_HOST_OUT
		export ANDROID_DATA=${MTTEST_RESULT_DIR}/android-data
        
		JAVA_LIBRARY_PATH_32="-Djava.library.path=$MTTEST_PROJECT_ROOT/build/native/linux/libs/x86:$ANDROID_BUILD_TOP/out/host/linux-x86/lib"
		JAVA_LIBRARY_PATH_64="-Djava.library.path=$MTTEST_PROJECT_ROOT/build/native/linux/libs/x86_64:$ANDROID_BUILD_TOP/out/host/linux-x86/lib64"
		JAVA_CLASSPATH="-cp $MTTEST_PROJECT_ROOT/build/apk/$MTTEST_DEVICE_EXEC_NAME -Xbootclasspath:$ANDROID_BUILD_TOP/out/host/linux-x86/framework"
		MTTEST_RESOURCES="-DmttestGoldensDir=./cfg/goldens -DmttestConfigDir=./cfg/configs -DmttestTestSetDir=./cfg/testsets"
		rm -rf $ANDROID_DATA
		mkdir -p $ANDROID_DATA
		
		
		RUN_RESOURCES=" -XXlib:libart.so -Ximage:$ANDROID_HOST_OUT/framework/core.art $JAVA_CLASSPATH"
		RUN_COMMAND="$MTTEST_RESOURCES $MTTEST_RUNNER_CLASS $MTTEST_ARGS"



		RUN_PLATFORM="$ANDROID_HOST_OUT/bin/dalvikvm32 $JAVA_LIBRARY_PATH_32" 
		FULL_COMMAND="$RUN_PLATFORM $RUN_RESOURCES $RUN_COMMAND"
        [ -n "$MTTEST_RESULT_DIR" ] && FULL_COMMAND="$FULL_COMMAND -output ${MTTEST_RESULT_DIR}/art_host_32"
		echo "mode: 32bit art-vm-host"
		echo $FULL_COMMAND
		$FULL_COMMAND
        
        RUN_PLATFORM="$ANDROID_HOST_OUT/bin/dalvikvm64  $JAVA_LIBRARY_PATH_64" 
        FULL_COMMAND="$RUN_PLATFORM $RUN_RESOURCES $RUN_COMMAND"
        [ -n "$MTTEST_RESULT_DIR" ] && FULL_COMMAND="$FULL_COMMAND -output ${MTTEST_RESULT_DIR}/art_host_64"
        echo "mode: 64bit art-vm-host"
        echo $FULL_COMMAND
        $FULL_COMMAND
      
		;;
	"Device")
		JAVA_CLASSPATH="-cp $DEVICE_EXEC_DIR/$MTTEST_DEVICE_EXEC_NAME"
		JAVA_LIBRARY_PATH_32="-Djava.library.path=$DEVICE_EXEC_DIR/libs/x86:/system/lib"
		JAVA_LIBRARY_PATH_64="-Djava.library.path=$DEVICE_EXEC_DIR/libs/x86_64:/system/lib64"
		bash $MTTEST_PROJECT_ROOT/scripts/upload.sh $DEVICE_DATA_DIR $DEVICE_EXEC_DIR
		adb logcat -c

		RUN_RESOURCES="$JAVA_CLASSPATH "
		MTTEST_RESOURCES="-DmttestGoldensDir=$DEVICE_DATA_DIR/goldens -DmttestConfigDir=$DEVICE_DATA_DIR/configs -DmttestTestSetDir=$DEVICE_DATA_DIR/testsets"
		RUN_COMMAND="$MTTEST_RESOURCES $MTTEST_RUNNER_CLASS $MTTEST_ARGS"
		RUN_PLATFORM="dalvikvm32 $JAVA_LIBRARY_PATH_32" 
		FULL_COMMAND="cd $DEVICE_EXEC_DIR; $RUN_PLATFORM $RUN_RESOURCES $RUN_COMMAND"
        [ -n "$DEVICE_DATA_DIR" ] && FULL_COMMAND="$FULL_COMMAND -output ${DEVICE_DATA_DIR}/dalvikvm_32"
		echo "mode: 32bit vm-on-device"
		echo "adb shell $FULL_COMMAND"
		adb shell "$FULL_COMMAND"
		adb pull ${DEVICE_DATA_DIR}/dalvikvm_32 ${MTTEST_RESULT_DIR}/dalvikvm_32
				
		RUN_PLATFORM="dalvikvm64 $JAVA_LIBRARY_PATH_64" 
		FULL_COMMAND="cd $DEVICE_EXEC_DIR; $RUN_PLATFORM $RUN_RESOURCES $RUN_COMMAND"
        [ -n "$DEVICE_DATA_DIR" ] && FULL_COMMAND="$FULL_COMMAND -output ${DEVICE_DATA_DIR}/dalvikvm_64"
		echo "mode: 64bit vm-on-device"
		echo "adb shell $FULL_COMMAND"
		adb shell "$FULL_COMMAND"
        adb pull ${DEVICE_DATA_DIR}/dalvikvm_64 ./${MTTEST_RESULT_DIR}/dalvikvm_64
	;;
	*)
		echo "mttest.error: mode with name '$MODE' is unknown, expected mode: Java, Host, Device."
		exit 0
	;;
esac
