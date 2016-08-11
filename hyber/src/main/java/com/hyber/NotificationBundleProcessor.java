package com.hyber;

import android.content.Context;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import rx.Observable;
import rx.Subscriber;

class NotificationBundleProcessor {

    private static Subscriber<? super RemoteMessage> mRemoteMessageSubscriber;

    static Observable<RemoteMessage> remoteMessageObservable = Observable.create(new Observable.OnSubscribe<RemoteMessage>() {
        @Override
        public void call(Subscriber<? super RemoteMessage> subscriber) {
            mRemoteMessageSubscriber = subscriber;
        }
    });

    static void ProcessFromFCMIntentService(Context context, RemoteMessage remoteMessage) {
        if (mRemoteMessageSubscriber != null)
            mRemoteMessageSubscriber.onNext(remoteMessage);

        Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        Process(context, remoteMessage.getNotification(), remoteMessage.getData());
    }

    static void Process(Context context, RemoteMessage.Notification notification, Map<String, String> data) {
        //TODO Process notification and data payload
    }

}
