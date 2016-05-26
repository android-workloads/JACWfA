#!/bin/bash

source $MTTEST_PROJECT_ROOT/scripts/functions.sh
source $MTTEST_PROJECT_ROOT/mttest.properties

BUILD_TYPE=$1
shift;
export MTTEST_NATIVE_BINARIES_DIR=$MTTEST_PROJECT_ROOT/$1
shift;
command=$1

if [ ! -d $MTTEST_PROJECT_ROOT/src/main/jni ]; then
    echo "INFO: We don't have native libraries to build. Omit this phase"
    exit
fi

cd $MTTEST_PROJECT_ROOT/src/main/jni
case "$BUILD_TYPE" in
	"java")
		make -f Makefile.mk $*
		if [ ! "$command" = "clean" ]; then
			rm -rf $MTTEST_NATIVE_BINARIES_DIR/obj
		fi
	;;
	"android")
		$ndk_dir/ndk-build $*
		if [ ! "$command" = "clean" ]; then
			rm -rf ../obj
			rm -rf $MTTEST_NATIVE_BINARIES_DIR
			mkdir -p $MTTEST_NATIVE_BINARIES_DIR
			mv ../libs/* $MTTEST_NATIVE_BINARIES_DIR
			rm -r ../libs
		fi
	;;
	*)
		echo "mttest.error: invalid option '$BUILD_TYPE', expected either: java, android."
		exit 0
	;;
esac
cd $MTTEST_PROJECT_ROOT
