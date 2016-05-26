#!/bin/bash
# File: mttest.sh
#
# See README.txt for usage details

MTTEST_PROJECT_ROOT=`dirname $0`
INVOCATION_DIR=`pwd`

# The build requires pretty much of memory, tested 2g
export _JAVA_OPTIONS=-Xmx2g

if [ $MTTEST_PROJECT_ROOT != $INVOCATION_DIR ] && [ $MTTEST_PROJECT_ROOT != "." ]; then
	echo "do 'cd $MTTEST_PROJECT_ROOT' first."
	exit 0
else
	MTTEST_PROJECT_ROOT=$INVOCATION_DIR
fi

export MTTEST_PROJECT_ROOT

source $MTTEST_PROJECT_ROOT/scripts/functions.sh

cleanup_arguments
source $MTTEST_PROJECT_ROOT/mttest.properties

export MODE=$default_mode
export CONFFILE=$default_mttestconf

export mttest_version
export sdk_dir
export sdk_version
export MTTEST_DEVICE_EXEC_NAME=${mttest_workload_name}-${mttest_version}.apk

COMMAND=$1
shift


ARGS=( "$@" )
parse_arguments $@


#create result storage
daterun=`date +%d%m%y_%H.%M`
MTTEST_RESULT_DIR="${MTTEST_PROJECT_ROOT}/results/${daterun}_run"
rm -rf $MTTEST_RESULT_DIR
mkdir -p $MTTEST_RESULT_DIR
MTTEST_LOG="${MTTEST_RESULT_DIR}/log.txt"
touch -f $MTTEST_LOG

export MTTEST_RESULT_DIR
export MTTEST_LOG

case $COMMAND in
	build)
		bash -e $MTTEST_PROJECT_ROOT/scripts/build.sh
	;;
	run)
		bash -e $MTTEST_PROJECT_ROOT/scripts/run.sh
	;;
	run-android)
		bash -e $MTTEST_PROJECT_ROOT/scripts/upload_and_run_android.sh
	;;
	build-and-run)
		bash -e $MTTEST_PROJECT_ROOT/scripts/build.sh
		bash -e $MTTEST_PROJECT_ROOT/scripts/run.sh
	;;
	clean)
		bash -e $MTTEST_PROJECT_ROOT/scripts/clean.sh
	;;
	tar-project)
		ant tar
	;;
	# generate debug keystore
	keystore)
		keytool -genkeypair -validity 10000 -dname "CN=NSU, OU=I-lab, O=I-lab, L=Novosibirsk, S=RU" -keystore ./debug.keystore -storepass 123456 -keypass 654321 -alias androidMTTest -keyalg RSA -v
	;;
	*)
		echo "    # mttest.sh manual. Powered by MTTest framework."
		echo "      See README.txt for more details"
		echo ""
                echo "    Build"
                echo "   ./mttest.sh build -m [Java|Host|Device]"
                echo ""    
                echo "   Run from command line examples."
                echo ""
                echo "   All modes support unified parameters:"
                echo "   -s [test1,group2,test3]"
                echo "   Allows to pick testing set. Example '-s masterset:all:html,masterset:all:ai:com.intel.JACW.ai.scArrive'"
                echo "   {masterset:all} by default. It links on ./src/main/mttestconf/testsets/ALL/all.xml"
                echo ""
                echo "   -c [config file name]"
                echo "   Defines duration of testing cycle.  Example [-c medium.xml]."
                echo "   {medium.xml} by default. It links on ./src/main/mttestconf/configs/medium.xml"
                echo ""
                echo "   -t [integer];"
                echo "   Defines number of threads. Example [-t 2]."
                echo "   {-t 1} by default."
                echo ""
                echo "   Run mode Java"
                echo "   Command perform 4 runs on [32 / 64 bit] JVM with [-client / -server] options."
                echo "   ./mttest.sh run -m Java (run the whole workload)"
                echo "   How to specify test."
                echo "   - pass '-s' key, e.g. ./mttest.sh run -m Java -s masterset:all:html"
                echo "   - it's possible to use multiply test definition, -s masterset:all:html,masterset:all:ai:com.intel.JACW.ai.scArrive"
                echo ""
                echo "   Run mode Host"
                echo "   ./mtthet.sh run -m Host"
                echo ""
                echo "   Run mode Device"
                echo "   As Android application"
                echo "   ./mttest.sh run-android -m Device"
                echo "   Direct VM invocation without application interface."
                echo "   ./mttest.sh run -m Device"
                echo ""
		echo "   Utilities:"
		echo "    * clean project"
		echo "   ./mttest.sh clean"
		echo "    * see README for details"
		echo ""
        echo "   Supported test groups:"
        cat ./README.txt |grep "++"
		
	;;
esac
