#!/bin/bash
# This script shrinks the jar specified using proguard

PROGUARD_PROPS_FILE=$1
IN_JAR=$2
OUT_JAR=$3
LIBRARY_JAR=$4

source mttest.properties

if [ ! -e "${sdk_dir}/tools/proguard/lib/proguard.jar" ]; then
    echo "Error: can't find Proguard in Android SDK directory. Did you install it?"
    exit 1
fi

case "$use_proguard" in
    "true")
        echo "java -jar ${sdk_dir}/tools/proguard/lib/proguard.jar -injars ${IN_JAR} -outjars ${OUT_JAR} -libraryjars ${LIBRARY_JAR} -include ${PROGUARD_PROPS_FILE}"
        java -jar "${sdk_dir}/tools/proguard/lib/proguard.jar" \
            -injars "${IN_JAR}" \
            -outjars "${OUT_JAR}" \
            -libraryjars "${LIBRARY_JAR}" \
            -include "${PROGUARD_PROPS_FILE}"
    ;;
    "false")
        cp ${IN_JAR} ${OUT_JAR}
    ;;
    *)
        echo "Error: use_proguard property has invalid value ($use_proguard). Available values are: true, false"
        exit 1
    ;;
esac
