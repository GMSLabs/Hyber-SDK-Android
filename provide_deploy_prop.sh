#!/bin/bash

# Exit on error
set -e

TYPE_DEV="dev"
TYPE_TD="td"
TYPE_PROD="prod"

if [ -z ${JENKINS_HOME// } ]
then
    echo ERROR: JENKINS_HOME environment variable is not find
    echo "This script will not work without a JENKINS_HOME"
    exit 1 # terminate and indicate error
else
    echo "JENKINS_HOME watch to ${JENKINS_HOME}"
fi

# Defaults
PROP_FILE_NAME="$1"
BUILD_TYPE="$2"
PROP_FOLDER="${JENKINS_HOME}/properties"

if [ -z ${PROP_FILE_NAME// } ]
then
    echo ERROR: PROP_FILE_NAME argument is not correct
    echo "This script will not work without a PROP_FILE_NAME - first argument"
    exit 1 # terminate and indicate error
else
    echo "JENKINS_HOME watch to ${JENKINS_HOME}"
fi

if [ "$BUILD_TYPE" = "$TYPE_DEV" ] || [ "$BUILD_TYPE" = "$TYPE_TD" ] || [ "$BUILD_TYPE" = "$TYPE_PROD" ]
then
    echo "BUILD_TYPE is $BUILD_TYPE"
else
    echo ERROR: BUILD_TYPE - second argument is not correct, this script can work with dev/td/prod arguments
    exit 1 # terminate and indicate error
fi

if [ -e ${PROP_FOLDER} ]
then
    echo "${PROP_FOLDER} is exists"
else
    echo ERROR: ${PROP_FOLDER} directory is not exists
    exit 1 # terminate and indicate error
fi

if [ -e ${PROP_FOLDER}/${PROP_FILE_NAME} ]
then
    echo "Properties file is ${PROP_FOLDER}/${PROP_FILE_NAME}"
else
    echo ERROR: ${PROP_FILE_NAME} could not be found
    exit 1 # terminate and indicate error
fi

unzip ${PROP_FOLDER}/${PROP_FILE_NAME} -d ./

# List of all modules
modules=( hyber example )

for module in "${modules[@]}"
do
    echo "Providing properties for ${module}"

    # Provide google-services.json file if one is exists in ./Hyber-SDK-Android-propreties/${module}
    if [ -f ./Hyber-SDK-Android-propreties/${module}/google-services.json ]; then
        echo "Providing google-services.json to ${module}/google-services.json"
        cp -R ./Hyber-SDK-Android-propreties/${module}/google-services.json ./${module}/google-services.json
    fi

    # Provide fabric.properties file if one is exists in ./Hyber-SDK-Android-propreties/${module}
    if [ -f ./Hyber-SDK-Android-propreties/${module}/fabric.properties ]; then
        echo "Providing fabric.properties to ${module}/fabric.properties"
        cp -R ./Hyber-SDK-Android-propreties/${module}/fabric.properties ./${module}/fabric.properties
    fi

    # Provide gradle.properties.${BUILD_TYPE} file if one is exists in ./Hyber-SDK-Android-propreties/${module}
    if [ -f ./Hyber-SDK-Android-propreties/${module}/gradle.properties.${BUILD_TYPE} ]; then
        echo "Providing gradle.properties.${BUILD_TYPE} to ${module}/gradle.properties"
        cp -R ./Hyber-SDK-Android-propreties/${module}/gradle.properties.${BUILD_TYPE} ./${module}/gradle.properties
    fi
done
