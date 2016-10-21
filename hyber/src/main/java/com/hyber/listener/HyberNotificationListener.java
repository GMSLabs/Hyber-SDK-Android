package com.hyber.listener;

import com.google.firebase.messaging.RemoteMessage;

public interface HyberNotificationListener {

    void onMessageReceived(RemoteMessage remoteMessage);

}
