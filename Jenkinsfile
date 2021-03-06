node {

  def gradleOptions='-Dorg.gradle.jvmargs="-Xmx1024m -XX:+HeapDumpOnOutOfMemoryError"'

  env.ANDROID_HOME="${pwd()}/android-sdk"
  env.GRADLE_OPTS="${gradleOptions}"

  stage ('Preparation') {
    // Get some code from a GitHub repository
    checkout scm

    sh "git config --global user.name JenkinsBrainCI"
    sh "git config --global user.email jenkinsbrainci@gmail.com"

    sh "./mocking.sh"
  }

  stage ('Providing Android SDK') {
    sh "android-sdk-producer android-sdk_r24.4.1-linux.tgz"
  }

  stage ('Update Android SDK') {
    parallel(
      tools: {
        sh "android-update-sdk \
              --components=platform-tools,tools,build-tools-25.0.0 \
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

  stage ('Test hyber') {
    sh "./gradlew hyber:checkstyle hyber:testReleaseUnitTest"
  }

  stage ('Test example') {
    sh "./gradlew example:checkstyle example:testDevDebugUnitTest"
  }

  stage ('Publication Hyber DEV to Fabric') {
    if (env.BRANCH_NAME == 'master') {
      sh "printenv"
      sh "./provide_properties.sh properties.zip dev"
      sh "./provide_keystore.sh keystores.zip dev"

      env.FABRIC_GROUP='hyber-android,hyber-developers,hyber-testers'
      env.FABRIC_NOTES='This is DEV's build from branch ' + env.BRANCH_NAME
      env.FABRIC_DESCRIPTION='This build powered by Jenkins CI'

      sh "echo FABRIC_GROUP = ${FABRIC_GROUP}"
      sh "echo FABRIC_NOTES = ${FABRIC_NOTES}"

      sh "echo ${env.BRANCH_NAME} is branch for crashlytics upload distribution DEV's build"
      sh "./gradlew hyber:clean example:clean example:assembleDevDebug"
      sh "./gradlew example:fabricGenerateResourcesDevDebug example:crashlyticsUploadDistributionDevDebug"
    } else {
      sh "echo ${env.BRANCH_NAME} is not branch for crashlytics upload distribution DEV's build"
    }
  }

  stage ('Publication Hyber QA to Fabric') {
    if (env.BRANCH_NAME == 'master') {
      sh "printenv"
      sh "./provide_properties.sh properties.zip qa"
      sh "./provide_keystore.sh keystores.zip qa"

      env.FABRIC_GROUP='hyber-android,hyber-developers,hyber-testers'
      env.FABRIC_NOTES='This is QA's build from branch ' + env.BRANCH_NAME
      env.FABRIC_DESCRIPTION='This build powered by Jenkins CI'

      sh "echo FABRIC_GROUP = ${FABRIC_GROUP}"
      sh "echo FABRIC_NOTES = ${FABRIC_NOTES}"

      sh "echo ${env.BRANCH_NAME} is branch for crashlytics upload distribution QA's build"
      sh "./gradlew hyber:clean example:clean example:assembleQaDebug"
      sh "./gradlew example:fabricGenerateResourcesQaDebug example:crashlyticsUploadDistributionQaDebug"
    } else {
      sh "echo ${env.BRANCH_NAME} is not branch for crashlytics upload distribution QA's build"
    }
  }

  stage ('Publication Hyber FQA to Fabric') {
    if (env.BRANCH_NAME == 'master') {
      sh "printenv"
      sh "./provide_properties.sh properties.zip fqa"
      sh "./provide_keystore.sh keystores.zip fqa"

      env.FABRIC_GROUP='hyber-android,hyber-developers,hyber-testers'
      env.FABRIC_NOTES='This is FQA's build from branch ' + env.BRANCH_NAME
      env.FABRIC_DESCRIPTION='This build powered by Jenkins CI'

      sh "echo FABRIC_GROUP = ${FABRIC_GROUP}"
      sh "echo FABRIC_NOTES = ${FABRIC_NOTES}"

      sh "echo ${env.BRANCH_NAME} is branch for crashlytics upload distribution FQA's build"
      sh "./gradlew hyber:clean example:clean example:assembleFqaDebug"
      sh "./gradlew example:fabricGenerateResourcesFqaDebug example:crashlyticsUploadDistributionFqaDebug"
    } else {
      sh "echo ${env.BRANCH_NAME} is not branch for crashlytics upload distribution FQA's build"
    }
  }

  stage ('Publication Hyber TD to Fabric') {
    if (env.BRANCH_NAME == 'master') {
      sh "printenv"
      sh "./provide_properties.sh properties.zip td"
      sh "./provide_keystore.sh keystores.zip td"

      env.FABRIC_GROUP='hyber-android,hyber-testers'
      env.FABRIC_NOTES='This is TD's build from branch ' + env.BRANCH_NAME
      env.FABRIC_DESCRIPTION='This build powered by Jenkins CI'

      sh "echo FABRIC_GROUP = ${FABRIC_GROUP}"
      sh "echo FABRIC_NOTES = ${FABRIC_NOTES}"

      sh "echo ${env.BRANCH_NAME} is branch for crashlytics upload distribution TD's build"
      sh "./gradlew hyber:clean example:clean example:assembleTdDebug"
      sh "./gradlew example:fabricGenerateResourcesTdDebug example:crashlyticsUploadDistributionTdDebug"
    } else {
      sh "echo ${env.BRANCH_NAME} is not branch for crashlytics upload distribution TD's build"
    }
  }

  stage ('Publication Hyber PROD to Fabric') {
    if (env.BRANCH_NAME == 'master') {
      sh "printenv"
      sh "./provide_properties.sh properties.zip prod"
      sh "./provide_keystore.sh keystores.zip prod"

      env.FABRIC_GROUP='hyber-android,hyber-td,hyber-managers'
      env.FABRIC_NOTES='This is PROD's build from branch ' + env.BRANCH_NAME
      env.FABRIC_DESCRIPTION='This build powered by Jenkins CI'

      sh "echo FABRIC_GROUP = ${FABRIC_GROUP}"
      sh "echo FABRIC_NOTES = ${FABRIC_NOTES}"

      sh "echo ${env.BRANCH_NAME} is branch for crashlytics upload distribution PROD's build"
      sh "./gradlew hyber:clean example:clean example:assembleProdDebug"
      sh "./gradlew example:fabricGenerateResourcesProdDebug example:crashlyticsUploadDistributionProdDebug"
    } else {
      sh "echo ${env.BRANCH_NAME} is not branch for crashlytics upload distribution PROD's build"
    }
  }

  stage ('Clean-up') {
    deleteDir()
  }
}
