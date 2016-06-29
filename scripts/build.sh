#!/bin/bash
# This script is needed to build the testsuit in desired configuration.
# Invoking this script directly is not recomended, please use mttest.sh build command instead.

source $MTTEST_PROJECT_ROOT/scripts/functions.sh
source $MTTEST_PROJECT_ROOT/mttest.properties

errors_count=0

test_java
if [ $status -ne 0 ]; then
    exit 1
fi

case "$MODE" in
	"Java")
		test_aapt not_verbose
		if [ $status -ne 0 ]; then
			echo "WARNING: $file is not found"
			echo "         R.java is not going to be updated"
			echo "Tip: add the following line to the file mttest.properties:"
			echo "    - sdk_dir=...
			echo "    - tools_version=...
			echo "aapt, dx should be available by ${sdk_dir}/build-tools/${tools_version}/"
			echo ""
		fi
	;;
	"Host" | "Device")
		test_dx
			if [ $status -ne 0 ]; then
				((errors_count += 1))
			fi
		test_aapt
			if [ $status -ne 0 ]; then
				((errors_count += 1))
			fi
		test_ndk
			if [ $status -ne 0 ]; then
				((errors_count += 1))
			fi
		test_zipalign
			if [ $status -ne 0 ]; then
				((errors_count += 1))
			fi
	;;
	"Product")
		#TODO test everything
		test_aapt not_verbose
		if [ $status -ne 0 ]; then
			echo "WARNING: $file is not found"
			echo "         R.java is not going to be updated"
			echo "Tip: add the following line to the file mttest.properties:"
			echo "    - sdk_dir=...
			echo "    - tools_version=...
			echo "aapt, dx should be available by ${sdk_dir}/build-tools/${tools_version}/"
			echo ""
		fi
		test_dx
			if [ $status -ne 0 ]; then
				((errors_count += 1))
			fi
		test_aapt
			if [ $status -ne 0 ]; then
				((errors_count += 1))
			fi
		test_ndk
			if [ $status -ne 0 ]; then
				((errors_count += 1))
			fi
		test_zipalign
			if [ $status -ne 0 ]; then
				((errors_count += 1))
			fi
	;;
	*)
		echo "ERROR: mode with name '$MODE' is unknown, expected targers: Java, Host, Device."
		exit 1
	;;
esac

if [ $errors_count -ne 0 ]; then
    echo "There are errors ($errors_count). Aborting"
    exit 1
fi

ant $ant_opts build$MODE