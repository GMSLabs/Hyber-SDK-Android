#!/bin/bash
#
# Deploy a apk to Fabric Beta, SDK build if version has changed to maven branch and javadoc to gp branch.
#
# Adapted from https://coderwall.com/p/9b_lfq and
# http://benlimmer.com/2013/12/26/automatically-publish-javadoc-to-gh-pages-with-travis-ci/

SLUG="Incuube/Hyber-SDK-Android"
JDK="oraclejdk8"
BRANCH="master-2.0"

FABRIC_GROUP=""
FABRIC_NOTES=""
FABRIC_DESCRIPTION=""

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
  openssl aes-256-cbc -K $encrypted_d1ceb7cf5733_key -iv $encrypted_d1ceb7cf5733_iv -in properties.zip.enc -out properties.zip -d
  echo "Decrypt keystores"
  openssl aes-256-cbc -K $encrypted_d1ceb7cf5733_key -iv $encrypted_d1ceb7cf5733_iv -in keystores.zip.enc -out keystores.zip -d

  echo "Provide DEV properties"
  ./provide_properties.sh properties.zip dev
  echo "Provide DEV keystores"
  ./provide_keystore.sh keystores.zip dev
  echo "Initialize Fabric params for DEV"
  FABRIC_GROUP='hyber-android,hyber-developers,hyber-testers'
  FABRIC_NOTES="$TRAVIS_COMMIT_MESSAGE"
  FABRIC_DESCRIPTION="Build from commit $TRAVIS_COMMIT"
  echo "Build DEV snapshot..."
  ./gradlew clean example:assembleDevDebug
  echo "Deploy DEV snapshot..."
  ./gradlew example:fabricGenerateResourcesDevDebug example:crashlyticsUploadDistributionDevDebug
  echo "DEV snapshot is deployed!"

  echo "Provide TD properties"
  ./provide_properties.sh properties.zip td
  echo "Provide TD keystores"
  ./provide_keystore.sh keystores.zip td
  echo "Initialize Fabric params for TD"
  FABRIC_GROUP='hyber-android,hyber-developers,hyber-testers,hyber-td'
  FABRIC_NOTES="$TRAVIS_COMMIT_MESSAGE"
  FABRIC_DESCRIPTION="Build from commit $TRAVIS_COMMIT"
  echo "Build TD snapshot..."
  ./gradlew clean example:assembleTdDebug
  echo "Deploy TD snapshot..."
  ./gradlew example:fabricGenerateResourcesTdDebug example:crashlyticsUploadDistributionTdDebug
  echo "TD snapshot is deployed!"

  echo "Provide PROD properties"
  ./provide_properties.sh properties.zip prod
  echo "Provide PROD keystores"
  ./provide_keystore.sh keystores.zip prod
  echo "Initialize Fabric params for PROD"
  FABRIC_GROUP='hyber-android,hyber-developers,hyber-testers,hyber-td,hyber-managers'
  FABRIC_NOTES="$TRAVIS_COMMIT_MESSAGE"
  FABRIC_DESCRIPTION="Build from commit $TRAVIS_COMMIT"
  echo "Build PROD snapshot..."
  ./gradlew clean example:assembleProdDebug
  echo "Deploy PROD snapshot..."
  ./gradlew example:fabricGenerateResourcesProdDebug example:crashlyticsUploadDistributionProdDebug
  echo "PROD snapshot is deployed!"

fi
