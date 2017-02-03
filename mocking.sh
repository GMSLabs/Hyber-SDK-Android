#!/bin/bash

# Exit on error
set -e

# List of all modules
modules=(
    ./
    hyber
    example
)

for module in "${modules[@]}"
do
  echo "Mocking files in ${module}"

  # Go to module directory
  cd $module

  # Mock google-services.json file if mocking file is exists
  if [ -f ./google-services.json.dist ]; then
    echo "Mocking google-services.json.dist to google-services.json"
    cp ./google-services.json.dist ./google-services.json
  fi

  # Mock gradle.properties file if mocking file is exists
  if [ -f ./gradle.properties.dist ]; then
    echo "Mocking gradle.properties.dist to gradle.properties"
    cp ./gradle.properties.dist ./gradle.properties
  fi

  # Back to parent directory.
  cd -
done
