node {
  stage ('Preparation') {
    // Get some code from a GitHub repository
    checkout scm
  }

  stage ('Update Android SDK') {
    //IMPORTANT call android-update-sdk NOT parallel in first time
    sh "android-update-sdk \
          --components=platform-tools \
          --accept-licenses=android-sdk-license-.+"

    parallel(
      tools: {
        sh "android-update-sdk \
              --components=tools,build-tools-24.0.2 \
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

  stage ('Build') {
    sh "chmod +x ./build.sh"
    sh "./build.sh"
  }
}
