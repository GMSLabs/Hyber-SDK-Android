# Hyber SDK for Android
[![Release][release-svg]][release-link]
[![Build Status][ci-build-status-svg]][ci-build-status]
[![Codecov Status][codecov-svg]][codecov]

A library that gives you access to the powerful Hyber cloud platform from your Android app.
For more information about Hyber and its features, see [hyber.im][hyber.im].
***

### Introduction
First of all you need to configure [Google Services Plugin][google-services-plugin] and [Firebase Cloud Messaging][firebase-cloud-messaging]

After that you need to initialize the Hyber inside onCreate method in your application class.
```
    buildConfigField 'String', 'HYBER_CLIENT_API_KEY', hyber_client_api_key
```

And after that you need to write next line to app level build.gradle file inside build config block
```java
    Hyber.with(this, BuildConfig.HYBER_CLIENT_API_KEY)
        .setNotificationListener(...)
        .init();
```
***

### Download
#### GRADLE
Add next repository to project level build.gradle:
```groovy
    maven { url 'https://raw.github.com/Incuube/Hyber-SDK-Android/maven/' }
```

Then add next dependencies to app level build.gradle:
```groovy
    compile 'com.hyber:hyber-messaging:2.2.0'
```

*Hyber SDK requires at minimum Java 7 or Android 4.1.*
***

### User control
#### User authorization management
```java
    //User registration
    Hyber.userRegistration(phone, password, new HyberCallback<EmptyResult, HyberError>() {
        @Override
        public void onSuccess() { /*User session is active*/ }
        @Override
        public void onFailure(HyberError error) { /*Something went wrong*/ }
    });

    //User logout
    Hyber.logoutCurrentUser(new LogoutUserHandler() {
        @Override
        public void onSuccess() { /*User is logout*/ }
        @Override
        public void onFailure(HyberError error) { /*Something went wrong*/ }
    });

    //Check user auth status
    Hyber.isAuthorized(new HyberCallback<EmptyResult, EmptyResult>() {
        @Override
        public void onSuccess(EmptyResult result) { /*User is authorized*/ }
        @Override
        public void onFailure(EmptyResult error) { /*User is not authorized*/ }
    });

    //Get current user info
    Hyber.getCurrentUser(new CurrentUserHandler() {
        @Override
        public void onCurrentUser(String id, String phone) { /*Return current user id and phone*/ }
    });
```
#### User's messages management
```java
    //Get access to stored user's messages and add change listeners
    Hyber.getAllUserMessages();

    //Get all user's messages from server and save it to local storage
    Hyber.getMessageHistory(historyStartTime, new HyberCallback<Long, HyberError>() {
        @Override
        public void onSuccess(@NonNull Long recommendedNextTime) { /*User's message list loaded and saved successfuly*/ }
        @Override
        public void onFailure(HyberError error) { /*Something went wrong*/ }
    });

    //Send answer for message
    Hyber.sendMessageCallback(messageId, answer, new HyberCallback<String, HyberError>() {
        @Override
        public void onSuccess(String result) { /*Answer is sent*/ }
        @Override
        public void onFailure(HyberError error) { /*Something went wrong*/ }
    });
```
#### User's devices management
```java
    //Get access to stored user's devices and add change listeners
    Hyber.getAllUserDevices();

    //Get all user's devices from server and save it to local storage
    Hyber.getAllDevices(new HyberCallback<EmptyResult, HyberError>() {
        @Override
        public void onSuccess(EmptyResult result) { /*User's device list loaded and saved successfuly*/ }
        @Override
        public void onFailure(HyberError error) { /*Something went wrong*/ }
    });

    //Revoke devices from user's authorized device list
    Hyber.revokeDevices(deviceIds, new HyberCallback<EmptyResult, HyberError>() {
        @Override
        public void onSuccess(EmptyResult result) { /*Devices is revoked*/ }
        @Override
        public void onFailure(HyberError error) { /*Something went wrong*/ }
    });
```
***

### PROGUARD
If you are using Proguard in your project add the following lines to your configuration:
```proguard

```

### Logging
For manipulation with logs from library you can create a subclass ```HyberLogger.Tree```.
Example:
```java
private class CrashReportingTree extends HyberLogger.Tree {
    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (priority <= Log.WARN) return; else {/*Do something with error*/}
    }
}
```

[release-svg]: http://github-release-version.herokuapp.com/github/Incuube/Hyber-SDK-Android/release.svg?style=flat
[release-link]: https://github.com/Incuube/Hyber-SDK-Android/releases/latest

[ci-build-status-svg]: https://travis-ci.org/Incuube/Hyber-SDK-Android.svg?branch=master-2.0
[ci-build-status]: https://travis-ci.org/Incuube/Hyber-SDK-Android

[codecov-svg]: https://codecov.io/gh/Incuube/Hyber-SDK-Android/branch/master/graph/badge.svg
[codecov]: https://codecov.io/gh/Incuube/Hyber-SDK-Android

[hyber.im]: https://hyber.im/
[google-services-plugin]: https://developers.google.com/android/guides/google-services-plugin
[firebase-cloud-messaging]: https://firebase.google.com/docs/cloud-messaging/android/client
