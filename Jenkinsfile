node {

  env.ANDROID_HOME="${pwd()}/android-sdk"

  def gradleOptions='-Dorg.gradle.jvmargs="-Xmx2048m -XX:+HeapDumpOnOutOfMemoryError"'

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
      extra: {
        sh "android-update-sdk \
              --components=extra-android-support,extra-android-m2repository,extra-google-google_play_services,extra-google-m2repository \
              --accept-licenses=android-sdk-license-.+"
      }
    )
  }

  stage ('Test Hyber SDK') {
    sh "GRADLE_OPTS=${gradleOptions} ./gradlew hyber:testReleaseUnitTest"
  }

  stage ('Build Hyber SDK') {
    sh "GRADLE_OPTS=${gradleOptions} ./gradlew hyber:clean hyber:assembleRelease"
  }

  stage ('Test example app') {
    sh "GRADLE_OPTS=${gradleOptions} ./gradlew example:test"
  }

  stage ('Build example app') {
    sh "GRADLE_OPTS=${gradleOptions} ./gradlew example:assembleDebug"
  }

  stage ('Clean-up') {
    deleteDir()
  }
}
