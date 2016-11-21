#!/bin/bash

# Exit on error
set -e

TYPE_DEV="dev"
TYPE_TD="td"
TYPE_PROD="prod"
TYPE_PRODD="prodd"

if [ -z ${JENKINS_HOME// } ]
then
    echo ERROR: JENKINS_HOME environment variable is not find
    echo "This script will not work without a JENKINS_HOME"
    exit 1 # terminate and indicate error
else
    echo "JENKINS_HOME watch to ${JENKINS_HOME}"
fi

# Defaults
KEYSTORE_FILE_NAME="$1"
BUILD_TYPE="$2"
KEYSTORE_FOLDER="${JENKINS_HOME}/privates/Hyber-SDK-Android/keystores"

if [ -z ${KEYSTORE_FILE_NAME// } ]
then
    echo ERROR: PROP_FILE_NAME argument is not correct
    echo "This script will not work without a KEYSTORE_FILE_NAME - first argument"
    exit 1 # terminate and indicate error
else
    echo "KEYSTORE_FILE_NAME is ${KEYSTORE_FILE_NAME}"
fi

if [ "$BUILD_TYPE" = "$TYPE_DEV" ] || [ "$BUILD_TYPE" = "$TYPE_TD" ] || [ "$BUILD_TYPE" = "$TYPE_PROD" ] || [ "$BUILD_TYPE" = "$TYPE_PRODD" ]
then
    echo "BUILD_TYPE is $BUILD_TYPE"
else
    echo ERROR: BUILD_TYPE - second argument is not correct, this script can work with dev/td/prod arguments
    exit 1 # terminate and indicate error
fi

if [ -e ${KEYSTORE_FOLDER} ]
then
    echo "${KEYSTORE_FOLDER} is exists"
else
    echo ERROR: ${KEYSTORE_FOLDER} directory is not exists
    exit 1 # terminate and indicate error
fi

if [ -e ${KEYSTORE_FOLDER}/${KEYSTORE_FILE_NAME} ]
then
    echo "Keystore file is ${KEYSTORE_FOLDER}/${KEYSTORE_FILE_NAME}"
else
    echo ERROR: ${KEYSTORE_FILE_NAME} could not be found
    exit 1 # terminate and indicate error
fi

rm -rf ./keystores
unzip ${KEYSTORE_FOLDER}/${KEYSTORE_FILE_NAME} -d ./

# List of all modules
modules=( example )

for module in "${modules[@]}"
do
    echo "Providing debug keystores for ${module}"

    # Provide debug.keystore.properties file if one is exists in ./keystores/${module}/${BUILD_TYPE}
    if [ -f ./keystores/${module}/${BUILD_TYPE}/debug.keystore.properties.${BUILD_TYPE} ]; then
        echo "Providing debug.keystore.properties.${BUILD_TYPE} to ${module}/debug.keystore.properties"
        cp -f ./keystores/${module}/${BUILD_TYPE}/debug.keystore.properties.${BUILD_TYPE} ./${module}/debug.keystore.properties
    fi

    # Provide debug.keystore file if one is exists in ./keystores/${module}/${BUILD_TYPE}
    if [ -f ./keystores/${module}/${BUILD_TYPE}/debug.keystore.${BUILD_TYPE} ]; then
        echo "Providing debug.keystore.${BUILD_TYPE} to ${module}/debug.keystore"
        cp -f ./keystores/${module}/${BUILD_TYPE}/debug.keystore.${BUILD_TYPE} ./${module}/debug.keystore
    fi
done
