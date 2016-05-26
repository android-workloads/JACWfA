#!/bin/bash

source $MTTEST_PROJECT_ROOT/mttest.properties

adb uninstall com.intel.mttest.android 
adb install -r $MTTEST_PROJECT_ROOT/build/apk/$mttest_workload_name-$mttest_version.apk

adb logcat -c
adb logcat | tee $MTTEST_LOG &
logger_pid=$!
#start on device

RUN_COMMAND="-e autostart true"

[ -n "$MTTEST_SUITE" ] && RUN_COMMAND="$RUN_COMMAND -e -s $MTTEST_SUITE" 
[ -n "$MTTEST_CONFIG" ] && RUN_COMMAND="$RUN_COMMAND -e -c $MTTEST_CONFIG"
[ -n "$MTTEST_VERBOSE" ] && RUN_COMMAND="$RUN_COMMAND -e -v $MTTEST_VERBOSE"
[ -n "$MTTEST_THREADS" ] && RUN_COMMAND="$RUN_COMMAND -e -t $MTTEST_THREADS"
[ -n "$MTTEST_REPEATS" ] && RUN_COMMAND="$RUN_COMMAND -e -n $MTTEST_REPEATS"
[ -n "$DEVICE_DATA_DIR" ] && RUN_COMMAND="$RUN_COMMAND -e -output $DEVICE_DATA_DIR"

FULL_COMMAND="am start -S -n com.intel.mttest.android/com.intel.mttest.android.StarterActivity $RUN_COMMAND"

echo "adb shell $FULL_COMMAND"
adb shell $FULL_COMMAND 

# the results go to logcat, grep Total


while [[ "1" == "1" ]]
do
    sleep 10
    cnt=`cat $MTTEST_LOG | grep "MTTest testing finished." | wc -l`
    [[ "$cnt" == "0" ]] || break;
    echo "end"
done

kill -9 ${logger_pid}
echo "Logger killed | ${cnt} "
adb pull ${DEVICE_DATA_DIR}/results.csv ${MTTEST_RESULT_DIR}/
adb pull ${DEVICE_DATA_DIR}/table.txt ${MTTEST_RESULT_DIR}/

#adb logcat ActivityManager:I "mttest-res":I "mttest-cmd":I  mttest:I *:E | tee $MTTEST_PROJECT_ROOT/logcat.out
