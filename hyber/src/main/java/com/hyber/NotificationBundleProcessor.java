package com.hyber;

import android.content.Context;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Date;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

class NotificationBundleProcessor {

    private static Subscriber<? super HyberMessageModel> mRemoteMessageSubscriber;

    static Observable<HyberMessageModel> remoteMessageObservable = Observable.create(new Observable.OnSubscribe<HyberMessageModel>() {
        @Override
        public void call(Subscriber<? super HyberMessageModel> subscriber) {
            mRemoteMessageSubscriber = subscriber;
        }
    });

    static void ProcessFromFCMIntentService(Context context, RemoteMessage remoteMessage) {

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

    private static void Process(Context context, RemoteMessage.Notification notification, Map<String, String> data) {
        //TODO Process notification and data payload

        if (data != null && !data.isEmpty()) {
            String message = data.get("message");
            if (message != null) {
                try {
                    HyberMessageModel messageModel = new Gson().fromJson(message, HyberMessageModel.class);

                    if (mRemoteMessageSubscriber != null)
                        mRemoteMessageSubscriber.onNext(messageModel);

                    Message receivedMessage =
                            new Message(messageModel.getId(), "push", new Date(), messageModel.getAlpha(), messageModel.getText(), "");
                    receivedMessage.setAsNewReceived();
                    if (messageModel.getOptions() != null) {
                        receivedMessage.setOptions(
                                messageModel.getOptions().getImageUrl(),
                                messageModel.getOptions().getActionUrl(),
                                messageModel.getOptions().getCaptionText(),
                                messageModel.getOptions().getBidirectionalUrl());
                    }

                    MessageBusinessModel.newInstance().saveMessage(receivedMessage)
                            .subscribe(new Action1<Message>() {
                                @Override
                                public void call(Message message) {
                                    Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "message " + message.getId() + " is saved");
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    Hyber.Log(Hyber.LOG_LEVEL.WARN, throwable.getLocalizedMessage());
                                }
                            });
                } catch (Exception e) {
                    Hyber.Log(Hyber.LOG_LEVEL.WARN, e.getLocalizedMessage());
                }
            }
        }
    }

}
