package com.hyber;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.hyber.log.HyberLogger;
import com.hyber.model.Message;
import com.hyber.model.User;

import java.util.Date;

import rx.Observable;
import rx.Subscriber;

final class NotificationBundleProcessor {

    private static Subscriber<? super RemoteMessage> mRemoteMessageSubscriber;

    private static Observable<RemoteMessage> remoteMessageObservable =
            Observable.create(new Observable.OnSubscribe<RemoteMessage>() {
                @Override
                public void call(Subscriber<? super RemoteMessage> subscriber) {
                    mRemoteMessageSubscriber = subscriber;
                }
            });

    private NotificationBundleProcessor() {

    }

    static Observable<RemoteMessage> getRemoteMessageObservable() {
        return remoteMessageObservable;
    }

    static void processMessageFromFCM(RemoteMessage remoteMessage) {

        if (mRemoteMessageSubscriber != null)
            mRemoteMessageSubscriber.onNext(remoteMessage);

        HyberLogger.i("From: %s", remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            HyberLogger.i("Message data payload: %s", remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            HyberLogger.i("Message Notification Body: %s", remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData() != null && !remoteMessage.getData().isEmpty()) {
            String messageData = remoteMessage.getData().get("message");
            if (messageData != null) {
                try {
                    HyberMessageModel messageModel = new Gson().fromJson(messageData, HyberMessageModel.class);

                    Repository repo = new Repository();
                    repo.open();

                    User user = repo.getCurrentUser();
                    if (user == null) {
                        HyberLogger.w("Hyber user is not created");
                        return;
                    }

                    boolean isRead = false;
                    boolean isReported = false;
                    Message message = repo.getMessageById(user, messageModel.getId());
                    if (message != null) {
                        isRead = message.isRead();
                        isReported = message.isReported();
                    }

                    Message hyberMessage = new Message(
                            messageModel.getId(),
                            user,
                            "push",
                            messageModel.getAlpha(),
                            messageModel.getText(),
                            new Date(),
                            messageModel.getOptions() == null ? null : messageModel.getOptions().getImageUrl(),
                            messageModel.getOptions() == null ? null : messageModel.getOptions().getActionUrl(),
                            messageModel.getOptions() == null ? null : messageModel.getOptions().getCaptionText(),
                            isRead, isReported);

                    repo.saveMessageOrUpdate(hyberMessage);

                    repo.close();
                } catch (Exception e) {
                    HyberLogger.e(e);
                }
            } else {
                HyberLogger.w("Message object not exist in RemoteMessage data payload");
            }
        }
    }
}
