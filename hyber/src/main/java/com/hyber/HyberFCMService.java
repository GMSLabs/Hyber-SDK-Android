package com.hyber;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class HyberFCMService extends FirebaseMessagingService {

    private static final String TAG = "HyberFCMService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        NotificationBundleProcessor.processMessageFromFCM(remoteMessage);

    }
    // [END receive_message]

}
