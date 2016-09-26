node {

  def gradleOptions='-Dorg.gradle.jvmargs="-Xmx1024m -XX:+HeapDumpOnOutOfMemoryError"'

  env.ANDROID_HOME="${pwd()}/android-sdk"
  env.GRADLE_OPTS="${gradleOptions}"

  stage ('Preparation') {
    // Get some code from a GitHub repository
    checkout scm

    sh "chmod +x ./mocking.sh"
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

  stage ('Build Hyber SDK') {
    sh "./gradlew hyber:clean hyber:assembleRelease"
  }

  stage ('Test example app') {
    sh "./gradlew example:testDevDebugUnitTest"
  }

  stage ('Build example app') {
    sh "./gradlew example:clean example:assembleDevDebug"
  }

  stage ('Publication Hyber DEV to Fabric') {
    sh "printenv"

    if (env.BRANCH_NAME == 'master-2.0') {
      env.FABRIC_GROUP="Hyber DEV"
      env.FABRIC_NOTES="${env.BRANCH_NAME}"

      sh "echo ${env.BRANCH_NAME} is branch for crashlytics upload distribution Dev build"
      // sh "./gradlew example:fabricGenerateResourcesDevDebug example:crashlyticsUploadDistributionDevDebug"
    } else {
      sh "echo ${env.BRANCH_NAME} is not branch for crashlytics upload distribution Dev build"
    }
  }

  stage ('Clean-up') {
    deleteDir()
  }
}
