#!/bin/bash
#
# Deploy a apk to Fabric Beta, SDK build if version has changed to maven branch and javadoc to gp branch.
#
# Adapted from https://coderwall.com/p/9b_lfq and
# http://benlimmer.com/2013/12/26/automatically-publish-javadoc-to-gh-pages-with-travis-ci/

SLUG="Incuube/Hyber-SDK-Android"
JDK="oraclejdk8"
BRANCH="master"

export FABRIC_GROUP=""
export FABRIC_NOTES=""
export FABRIC_DESCRIPTION=""

set -e

if [ "$TRAVIS_REPO_SLUG" != "$SLUG" ]; then
  echo "Skipping snapshot deployment: wrong repository. Expected '$SLUG' but was '$TRAVIS_REPO_SLUG'."
elif [ "$TRAVIS_JDK_VERSION" != "$JDK" ]; then
  echo "Skipping snapshot deployment: wrong JDK. Expected '$JDK' but was '$TRAVIS_JDK_VERSION'."
elif [ "$TRAVIS_PULL_REQUEST" != "false" ]; then
  echo "Skipping snapshot deployment: was pull request."
elif [ "$TRAVIS_BRANCH" != "$BRANCH" ]; then
  echo "Skipping snapshot deployment: wrong branch. Expected '$BRANCH' but was '$TRAVIS_BRANCH'."
else
  echo "Decrypt properties"
  openssl aes-256-cbc -d -K ${encrypted_openssl_key} -iv ${encrypted_openssl_iv} -in properties.zip.enc -out properties.zip
  echo "Decrypt keystores"
  openssl aes-256-cbc -d -K ${encrypted_openssl_key} -iv ${encrypted_openssl_iv} -in keystores.zip.enc -out keystores.zip

  echo "Unzip properties"
  unzip -P ${encrypted_zip_password} properties.zip
  echo "Unzip keystores"
  unzip -P ${encrypted_zip_password} keystores.zip

  echo "Provide DEV's properties"
  ./provide_properties.sh dev
  echo "Provide DEV's keystores"
  ./provide_keystore.sh dev
  echo "Initialize Fabric params for DEV's"
  export FABRIC_GROUP='hyber-android,hyber-developers,hyber-testers'
  export FABRIC_NOTES="$TRAVIS_COMMIT_MESSAGE"
  export FABRIC_DESCRIPTION="Build for DEV's from commit $TRAVIS_COMMIT"
  echo "Build DEV's snapshot..."
  ./gradlew clean example:assembleDevDebug
  echo "Deploy DEV's snapshot..."
  ./gradlew example:fabricGenerateResourcesDevDebug example:crashlyticsUploadDistributionDevDebug
  echo "DEV's snapshot is deployed!"

  echo "Provide QA's properties"
  ./provide_properties.sh qa
  echo "Provide QA's keystores"
  ./provide_keystore.sh qa
  echo "Initialize Fabric params for QA's"
  export FABRIC_GROUP='hyber-android,hyber-developers,hyber-testers'
  export FABRIC_NOTES="$TRAVIS_COMMIT_MESSAGE"
  export FABRIC_DESCRIPTION="Build for QA's from commit $TRAVIS_COMMIT"
  echo "Build QA's snapshot..."
  ./gradlew clean example:assembleQaDebug
  echo "Deploy QA's snapshot..."
  ./gradlew example:fabricGenerateResourcesQaDebug example:crashlyticsUploadDistributionQaDebug
  echo "QA's snapshot is deployed!"

  echo "Provide FQA's properties"
  ./provide_properties.sh fqa
  echo "Provide FQA's keystores"
  ./provide_keystore.sh fqa
  echo "Initialize Fabric params for FQA's"
  export FABRIC_GROUP='hyber-android,hyber-developers,hyber-testers'
  export FABRIC_NOTES="$TRAVIS_COMMIT_MESSAGE"
  export FABRIC_DESCRIPTION="Build for FQA's from commit $TRAVIS_COMMIT"
  echo "Build FQA's snapshot..."
  ./gradlew clean example:assembleFqaDebug
  echo "Deploy FQA's snapshot..."
  ./gradlew example:fabricGenerateResourcesFqaDebug example:crashlyticsUploadDistributionFqaDebug
  echo "FQA's snapshot is deployed!"

  echo "Provide TD's properties"
  ./provide_properties.sh td
  echo "Provide TD's keystores"
  ./provide_keystore.sh td
  echo "Initialize Fabric params for TD's"
  export FABRIC_GROUP='hyber-android,hyber-developers,hyber-testers,hyber-td'
  export FABRIC_NOTES="$TRAVIS_COMMIT_MESSAGE"
  export FABRIC_DESCRIPTION="Build for TD's from commit $TRAVIS_COMMIT"
  echo "Build TD's snapshot..."
  ./gradlew clean example:assembleTdDebug
  echo "Deploy TD's snapshot..."
  ./gradlew example:fabricGenerateResourcesTdDebug example:crashlyticsUploadDistributionTdDebug
  echo "TD's snapshot is deployed!"

  echo "Provide PROD's properties"
  ./provide_properties.sh prod
  echo "Provide PROD's keystores"
  ./provide_keystore.sh prod
  echo "Initialize Fabric params for PROD's"
  export FABRIC_GROUP='hyber-android,hyber-developers,hyber-testers,hyber-td,hyber-managers'
  export FABRIC_NOTES="$TRAVIS_COMMIT_MESSAGE"
  export FABRIC_DESCRIPTION="Build for PROD's from commit $TRAVIS_COMMIT"
  echo "Build PROD's snapshot..."
  ./gradlew clean example:assembleProdDebug
  echo "Deploy PROD's snapshot..."
  ./gradlew example:fabricGenerateResourcesProdDebug example:crashlyticsUploadDistributionProdDebug
  echo "PROD's snapshot is deployed!"
fi
