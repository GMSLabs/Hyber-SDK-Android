package com.hyber;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.hyber.log.HyberLogger;
import com.hyber.model.Message;
import com.hyber.model.User;

import java.util.Date;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

final class NotificationBundleProcessor {

    private static FlowableEmitter<HyberMessage> mHyberMessageFlowableEmitter;
    private static FlowableEmitter<RemoteMessage> mRemoteMessageFlowableEmitter;

    private static Flowable<RemoteMessage> remoteMessageFlowable =
            Flowable.create(new FlowableOnSubscribe<RemoteMessage>() {
                @Override
                public void subscribe(FlowableEmitter<RemoteMessage> e) {
                    mRemoteMessageFlowableEmitter = e;
                }
            }, BackpressureStrategy.BUFFER);

    private static Flowable<HyberMessage> hyberMessageFlowable =
            Flowable.create(new FlowableOnSubscribe<HyberMessage>() {
                @Override
                public void subscribe(FlowableEmitter<HyberMessage> e) {
                    mHyberMessageFlowableEmitter = e;
                }
            }, BackpressureStrategy.BUFFER);

    private NotificationBundleProcessor() {

    }

    static Flowable<RemoteMessage> getRemoteMessageFlowable() {
        return remoteMessageFlowable;
    }

    static Flowable<HyberMessage> getHyberMessageFlowable() {
        return hyberMessageFlowable;
    }

    static void processMessageFromFCM(RemoteMessage remoteMessage) {

        if (mRemoteMessageFlowableEmitter != null)
            mRemoteMessageFlowableEmitter.onNext(remoteMessage);

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
                    HyberMessage hyberMessage = new Gson().fromJson(messageData, HyberMessage.class);

                    if (mHyberMessageFlowableEmitter != null)
                        mHyberMessageFlowableEmitter.onNext(hyberMessage);

                    Repository repo = new Repository();
                    repo.open();

                    User user = repo.getCurrentUser();
                    if (user == null) {
                        HyberLogger.w("Hyber user is not created");
                        return;
                    }

                    boolean isRead = false;
                    boolean isReported = false;
                    Message message = repo.getMessageById(user, hyberMessage.getMessageId());
                    if (message != null) {
                        isRead = message.getIsRead();
                        isReported = message.getIsReported();
                    }

                    Message.MessageBuilder mb = Message.builder();
                    mb.id(hyberMessage.getMessageId());
                    mb.user(user);
                    mb.partner(hyberMessage.getPartner() != null ? hyberMessage.getPartner() : "push");
                    mb.title(hyberMessage.getTitle());
                    mb.body(hyberMessage.getBody());
                    mb.date(hyberMessage.getTime() != null ? hyberMessage.getTime() : new Date());
                    mb.imageUrl(hyberMessage.getImage() != null ? hyberMessage.getImage().getUrl() : null);
                    mb.buttonUrl(hyberMessage.getButton() != null ? hyberMessage.getButton().getUrl() : null);
                    mb.buttonText(hyberMessage.getButton() != null ? hyberMessage.getButton().getText() : null);
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
