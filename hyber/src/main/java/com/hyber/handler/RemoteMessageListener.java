package com.hyber.handler;

import com.google.firebase.messaging.RemoteMessage;

public interface RemoteMessageListener {

    void onMessageReceived(RemoteMessage message);

}
