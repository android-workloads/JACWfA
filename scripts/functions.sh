# This file consists of utility functions used in the infrastructure of the project.

function test_availability {
    file=$1
    filepath=$2
    filepath_var_names=$3
    filepath_example=$4
    if [ $# -eq 5 ]; then
        verbose=$5
    else
        verbose=verbose
    fi
    IFS=":" read -ra path <<< "$filepath"
    local file_found=""
    real_filepath=""
    for i in "${path[@]}"; do
        if [ -e "$i/$file" ]; then
            file_found="true"
            real_filepath=$i
            break
        fi
    done
    if [ ! "$file_found" = "true" ]; then
        if [ "$verbose" = "verbose" ]; then
            echo "ERROR: $file is required but not found"
            echo "Tip: add the following line to the file mttest.properties:"
            IFS=":" read -ra vars <<< "$filepath_var_names"
            for i in "${vars[@]}"; do
                echo "    - $i=$filepath_example"
            done
            echo ""
        fi
        status=1
    else
        status=0
    fi
}

function test_java {
    test_availability "bin/java" "$java_home" "java_home" "path/to/java64"
	export java_home=${real_filepath}
}

function test_java_tested_64 {
    test_availability "bin/java" "$java_home_tested_64" "java_home_tested_64" "path/to/java64"
    export java_home_tested_64=${real_filepath}
}

function test_java_tested_32 {
    test_availability "bin/java" "$java_home_tested_32" "java_home_tested_32" "path/to/java32"
    export java_home_tested_32=${real_filepath}
}

function test_dx {
    test_availability "dx" "${sdk_dir}/build-tools/${tools_version}" '${dx_dir}/build-tools/${tools_version}' "path/to/android/build/tools"
    export dx_dir=${real_filepath}
    if [ -z ${ant_opts+x} ]; then
        ant_opts="-Ddx_dir=${real_filepath}"
    else
        ant_opts="${ant_opts} -Ddx_dir=${real_filepath}"
    fi
}

function test_aapt {
    test_availability "aapt" "${sdk_dir}/build-tools/${tools_version}" '${dx_dir}/build-tools/${tools_version}' "path/to/android/build/tools" $1
    export aapt_dir=${real_filepath}
    if [ -z ${ant_opts+x} ]; then
        ant_opts="-Daapt_dir=${real_filepath}"
    else
        ant_opts="${ant_opts} -Daapt_dir=${real_filepath}"
    fi
}

function test_zipalign {
    test_availability "zipalign" "${sdk_dir}/build-tools/${tools_version}" '${dx_dir}/build-tools/${tools_version}' "path/to/android/build/tools"
    export zipalign_dir=${real_filepath}
    if [ -z ${ant_opts+x} ]; then
        ant_opts="-Dzipalign_dir=${real_filepath}"
    else
        ant_opts="${ant_opts} -Dzipalign_dir=${real_filepath}"
    fi
}

function test_ndk {
    test_availability "ndk-build" "$ndk_dir" "dx_dir" "path/to/ndk"
}

test_host_android_top() {
	if [ ! -d "$host_android_top" ]; then
		if [ ! -d "$HOST_ANDROID_TOP" ]; then
			echo "ERROR: Android TOP not found"
			echo "Tips:"
			echo "    1) perform export HOST_ANDROID_TOP=/path/to/top"
			echo "    2) in file mttest.properties set up host_android_top=/path/to/top"
			echo ""
			exit 1
		else
			export host_android_top
		fi
	else
		export host_android_top
	fi
}

cleanup_arguments() {
	export MODE=""
	export CONFFILE=""
	export VERBOSE=""
	export DEVICE_EXEC_DIR=""
	export DEVICE_DATA_DIR=""
	export MTTEST_REPEATS="1"
}

parse_arguments() {
	export MODE=""
	export CONFFILE=""
	export VERBOSE=""

	export DEVICE_EXEC_DIR="$device_exec_folder"
	export DEVICE_DATA_DIR="$device_data_folder"

	while [[ $# > 1 ]]
	do
		key="$1"
		shift

		case $key in
			-m)
				export MODE=$1
				shift
            ;;
            -s)
                export MTTEST_SUITE=$1
                shift
            ;;
            -c)
                export MTTEST_CONFIG=$1
                shift
			;;
			-t)
                export MTTEST_THREADS=$1
                shift
            ;;
            -v)
                export MTTEST_VERBOSE=$1
                shift
            ;;
			-n)
				export MTTEST_REPEATS=$1
				shift
			;;
			-e)
				export DEVICE_EXEC_DIR=$1
				shift
			;;
            -d)
                export DEVICE_DATA_DIR=$1
                shift
            ;;
			*)
				echo "mttest.warning: unknown option: $key"
			;;
		esac
	done
}