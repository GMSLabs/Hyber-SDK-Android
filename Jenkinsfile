node {

  def gradleOptions='-Dorg.gradle.jvmargs="-Xmx1024m -XX:+HeapDumpOnOutOfMemoryError"'

  env.ANDROID_HOME="${pwd()}/android-sdk"
  env.GRADLE_OPTS="${gradleOptions}"

  stage ('Preparation') {
    // Get some code from a GitHub repository
    checkout scm

    sh "chmod +x ./mocking.sh"
    sh "chmod +x ./provide_properties.sh"
    sh "chmod +x ./provide_keystore.sh"

    sh "./mocking.sh"
  }

  stage ('Providing Android SDK') {
    sh "android-sdk-producer android-sdk_r24.4.1-linux.tgz"
  }

  stage ('Update Android SDK') {
    parallel(
      tools: {
        sh "android-update-sdk \
              --components=platform-tools,tools,build-tools-24.0.2 \
              --accept-licenses=android-sdk-license-.+"
      },
      android: {
        sh "android-update-sdk \
              --components=android-24 \
              --accept-licenses=android-sdk-license-.+"
      },
      extraandroid: {
        sh "android-update-sdk \
              --components=extra-android-support,extra-android-m2repository \
              --accept-licenses=android-sdk-license-.+"
      },
      extragoogle: {
        sh "android-update-sdk \
              --components=extra-google-google_play_services,extra-google-m2repository \
              --accept-licenses=android-sdk-license-.+"
      }
    )
  }

  stage ('Test Hyber SDK') {
    sh "./gradlew hyber:testReleaseUnitTest"
  }

  stage ('Test example app') {
    sh "./gradlew example:testDevDebugUnitTest"
  }

  stage ('Publication Hyber DEV to Fabric') {
    if (env.BRANCH_NAME == 'master-2.0') {
      sh "printenv"
      sh "./provide_properties.sh properties.zip dev"
      sh "./provide_keystore.sh keystores.zip dev"

      env.FABRIC_GROUP='hyber-developers,hyber-testers'
      env.FABRIC_NOTES='This is developers build from branch ' + env.BRANCH_NAME
      env.FABRIC_DESCRIPTION='This build powered by Jenkins CI'

      sh "echo FABRIC_GROUP = ${FABRIC_GROUP}"
      sh "echo FABRIC_NOTES = ${FABRIC_NOTES}"

      sh "echo ${env.BRANCH_NAME} is branch for crashlytics upload distribution Dev build"
      sh "./gradlew hyber:clean example:clean example:assembleDevDebug"
      sh "./gradlew example:fabricGenerateResourcesDevDebug example:crashlyticsUploadDistributionDevDebug"
    } else {
      sh "echo ${env.BRANCH_NAME} is not branch for crashlytics upload distribution Dev build"
    }
  }

  stage ('Publication Hyber TD to Fabric') {
    if (env.BRANCH_NAME == 'master-2.0') {
      sh "printenv"
      sh "./provide_properties.sh properties.zip td"
      sh "./provide_keystore.sh keystores.zip td"

      env.FABRIC_GROUP='hyber-testers'
      env.FABRIC_NOTES='This is testers build from branch ' + env.BRANCH_NAME
      env.FABRIC_DESCRIPTION='This build powered by Jenkins CI'

      sh "echo FABRIC_GROUP = ${FABRIC_GROUP}"
      sh "echo FABRIC_NOTES = ${FABRIC_NOTES}"

      sh "echo ${env.BRANCH_NAME} is branch for crashlytics upload distribution Td build"
      sh "./gradlew hyber:clean example:clean example:assembleTdDebug"
      sh "./gradlew example:fabricGenerateResourcesTdDebug example:crashlyticsUploadDistributionTdDebug"
    } else {
      sh "echo ${env.BRANCH_NAME} is not branch for crashlytics upload distribution Td build"
    }
  }

  stage ('Publication Hyber PROD to Fabric') {
    if (env.BRANCH_NAME == 'master-2.0') {
      sh "printenv"
      sh "./provide_properties.sh properties.zip prod"
      sh "./provide_keystore.sh keystores.zip prod"

      env.FABRIC_GROUP='hyber-td,hyber-managers'
      env.FABRIC_NOTES='This is prodaction build from branch ' + env.BRANCH_NAME
      env.FABRIC_DESCRIPTION='This build powered by Jenkins CI'

      sh "echo FABRIC_GROUP = ${FABRIC_GROUP}"
      sh "echo FABRIC_NOTES = ${FABRIC_NOTES}"

      sh "echo ${env.BRANCH_NAME} is branch for crashlytics upload distribution Prod build"
      sh "./gradlew hyber:clean example:clean example:assembleProdDebug"
      sh "./gradlew example:fabricGenerateResourcesProdDebug example:crashlyticsUploadDistributionProdDebug"
    } else {
      sh "echo ${env.BRANCH_NAME} is not branch for crashlytics upload distribution Prod build"
    }
  }

  stage ('Clean-up') {
    deleteDir()
  }
}
