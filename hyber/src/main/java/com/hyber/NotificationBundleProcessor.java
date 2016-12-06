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
                    MessageRespModel messageModel = new Gson().fromJson(messageData, MessageRespModel.class);

                    Repository repo = new Repository();
                    repo.open();

                    User user = repo.getCurrentUser();
                    if (user == null) {
                        HyberLogger.w("Hyber user is not created");
                        return;
                    }

                    boolean isRead = false;
                    boolean isReported = false;
                    Message message = repo.getMessageById(user, messageModel.getMessageId());
                    if (message != null) {
                        isRead = message.getIsRead();
                        isReported = message.getIsReported();
                    }

                    Message.MessageBuilder mb = Message.builder();
                    mb.id(messageModel.getMessageId());
                    mb.user(user);
                    mb.partner(messageModel.getPartner() != null ? messageModel.getPartner() : "push");
                    mb.title(messageModel.getTitle());
                    mb.body(messageModel.getBody());
                    mb.date(messageModel.getTime() != null ? messageModel.getTime() : new Date());
                    mb.imageUrl(messageModel.getImage() != null ? messageModel.getImage().getUrl() : null);
                    mb.buttonUrl(messageModel.getButton() != null ? messageModel.getButton().getUrl() : null);
                    mb.buttonText(messageModel.getButton() != null ? messageModel.getButton().getText() : null);
                    mb.isRead(isRead);
                    mb.isReported(isReported);

                    repo.saveMessageOrUpdate(mb.build());
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
