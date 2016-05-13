package com.gms_worldwide.hybersdk;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by Andrew Kochura.
 */
public class HyberMessageHelper {

    private static final String TAG = "com.gms_worldwide.hybersdk.HyberMessageHelper";

    private static HyberMessageHelper instance = null;
    private Observable<List<HyberMessageModel>> observableStoredMessages;
    private Subscriber<? super List<HyberMessageModel>> subscriberStoredMessages;
    private PublishSubject<HyberMessageModel> newMessageNotifier;
    private HashMap<MessageType, Boolean> filterMessage;
    private long mDateFrom, mDateTo;
    /**
     * Instantiates a Hyber message helper.
     */
    protected HyberMessageHelper() {

        mDateTo = System.currentTimeMillis();
        mDateFrom = HyberTools.getStartOfDayUtcTime(System.currentTimeMillis());

        filterMessage = new HashMap<>();
        filterMessage.put(MessageType.PUSH, true);
        filterMessage.put(MessageType.VIBER, true);
        filterMessage.put(MessageType.SMS, true);
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static HyberMessageHelper getInstance() {
        if (instance == null) {
            instance = new HyberMessageHelper();
        }
        return instance;
    }

    /**
     * New message.
     *
     * @param messageModel the message model
     */
    void newMessage(@NonNull HyberMessageModel messageModel) {
        if (newMessageNotifier != null)
            newMessageNotifier.onNext(messageModel);
    }

    /**
     * Notify all your subscribers on internal SDK storage
     */
    public void notifyStorageSubscribers() {
        if (subscriberStoredMessages != null && !subscriberStoredMessages.isUnsubscribed()) {
            subscriberStoredMessages.onNext(HyberPlugins.get().getDatabaseHelper()
                    .getMessages(mDateFrom, mDateTo, getMessageTypesForDB()));
        }
    }

    /**
     * Set date for one day filter.
     * Don't set date previously registration user date.
     *
     * @param dateFrom the time un UTC format
     * @param dateTo   the time un UTC format
     */
    public void setDateFilter(long dateFrom, long dateTo) {
        mDateFrom = dateFrom;
        mDateTo = dateTo;
        notifyStorageSubscribers();
    }

    /**
     * Set filter on PUSH messages
     *
     * @param isOn if true, your subscribers relieve messages of this type.
     */
    @Deprecated
    public void setPushFilter(boolean isOn) {
        updateFilter(MessageType.PUSH, isOn);
    }

    /**
     * Set filter on Viber messages
     *
     * @param isOn if true, your subscribers relieve messages of this type.
     */
    @Deprecated
    public void setViberFilter(boolean isOn) {
        updateFilter(MessageType.VIBER, isOn);
    }

    /**
     * Set filter on SMS messages
     *
     * @param isOn if true, your subscribers relieve messages of this type.
     */
    @Deprecated
    public void setSmsFilter(boolean isOn) {
        updateFilter(MessageType.SMS, isOn);
    }

    /**
     * Gets push filter status.
     *
     * @return the push filter status
     */
    @Deprecated
    public boolean getPushFilterStatus() {
        return getFilterStatus(MessageType.PUSH);
    }

    /**
     * Gets viber filter status.
     *
     * @return the viber filter status
     */
    @Deprecated
    public boolean getViberFilterStatus() {
        return getFilterStatus(MessageType.VIBER);
    }

    /**
     * Gets sms filter status.
     *
     * @return the sms filter status
     */
    @Deprecated
    public boolean getSmsFilterStatus() {
        return getFilterStatus(MessageType.SMS);
    }

    public boolean getFilterStatus(MessageType type) {
        if (filterMessage.containsKey(type))
            return filterMessage.get(type);
        return false;
    }

    public void updateFilter(MessageType type, boolean isOn) {
        if (filterMessage.containsKey(type)) {
            filterMessage.remove(type);
        }
        filterMessage.put(type, isOn);
        notifyStorageSubscribers();
    }

    /**
     * This method not delete message,
     * but if apply one to message id, this message doesn't send to your subscribers.
     *
     * @param id message id in database.
     */
    @Deprecated
    public void makeAsDeletedMessage(int id) {
        HyberPlugins.get().getDatabaseHelper().deleteMessage(id);
    }

    /**
     * This method mark message as read
     *
     * @param id message id in database.
     */
    @Deprecated
    public void makeAsReadMessage(int id) {
        HyberPlugins.get().getDatabaseHelper().setReadMessage(id);
    }

    /**
     * This method mark message as unread
     *
     * @param id message id in database.
     */
    @Deprecated
    public void makeAsUnReadMessage(int id) {
        HyberPlugins.get().getDatabaseHelper().setUnReadMessage(id);
    }

    /**
     * Use this method to control which messages have been read by user.
     *
     * @param messageId message id in database.
     * @param status    true if message is read, false to set message us unread
     *                  (false status as default for all incoming messages)
     */
    public void chengeReadMessageStatus(int messageId, boolean status) {
        if (status) HyberPlugins.get().getDatabaseHelper().setReadMessage(messageId);
        else HyberPlugins.get().getDatabaseHelper().setUnReadMessage(messageId);
    }

    /**
     * Use this method to control which messages have been archived by user.
     *
     * @param messageId message id in database.
     * @param status    true if message is deleted, false to undo delete message
     *                  (false status as default for all incoming messages)
     */
    public void chengeDeleteMessageStatus(int messageId, boolean status) {
        if (status) HyberPlugins.get().getDatabaseHelper().deleteMessage(messageId);
        else HyberPlugins.get().getDatabaseHelper().unDeleteMessage(messageId);
    }

    /**
     * This method mark all messages as read
     */
    public void makeAllMessagesAsRead() {
        HyberPlugins.get().getDatabaseHelper().setReadForAllMessages();
    }

    public List<HyberMessageModel> getUnReadMessages() {
        return HyberPlugins.get().getDatabaseHelper().getUnReadMessages();
    }

    /**
     * Delete all messages with 'makeAsDeleted' marker.
     */
    public void clearDeletedMessages() {
        HyberPlugins.get().getDatabaseHelper().clearDeletedMessages();
    }

    /**
     * This for receive all new income messages
     * This method don't coll onComplete signature!!!
     *
     * @return PublishSubject for your subscribers.
     */
    public PublishSubject<HyberMessageModel> getNewMessageNotifier() {
        if (newMessageNotifier == null) {
            newMessageNotifier = PublishSubject.create();
        }
        return newMessageNotifier;
    }

    /**
     * This observable for subscribe on message storage changes.
     * You can manually call update data on this observable,
     * just call notifyStorageSubscribers() from your code.
     * This method don't coll onComplete signature!!!
     *
     * @return Observable for your subscribers.
     */
    public Observable<List<HyberMessageModel>> getStoredMessageObservable() {
        if (observableStoredMessages == null) {
            observableStoredMessages =
                    Observable.create(new Observable.OnSubscribe<List<HyberMessageModel>>() {
                        @Override
                        public void call(Subscriber<? super List<HyberMessageModel>> subscriber) {
                            subscriberStoredMessages = subscriber;
                        }
                    });
        }
        return observableStoredMessages;
    }

    /**
     * This method send request to GMS Worldwide server
     * for check new Viber messages for current user.
     * It's work if your plan include this functionality!
     * If you retrieve Viber messages on previous day,
     * you can't send second request for one day!.
     *
     * @return Observable for your subscribers.
     */
    @Deprecated
    private Observable<List<HyberMessageModel>> getViberMessagesFromCloudObservable(long millsUTC) {
        final long start_of_set_day = HyberTools.getStartOfDayUtcTime(millsUTC);
        if (HyberPlugins.get().getDatabaseHelper().isMessageUpdateForFoolDay(
                start_of_set_day, MessageType.VIBER.getType())) {
            return Observable.create(new Observable.OnSubscribe<List<HyberMessageModel>>() {
                @Override
                public void call(Subscriber<? super List<HyberMessageModel>> subscriber) {
                    subscriber.onCompleted();
                }
            });
        }
        final long phone = HyberPlugins.get().getDatabaseHelper().getCurrentUserPhone();
        long uId = HyberPlugins.get().getDatabaseHelper().getCurrentUserUniqueId();
        return HyberPlugins.get().restClient().getViberMessagesObservable(
                phone, uId, start_of_set_day)
                .filter(new Func1<retrofit2.Response<HyberMessagesEnvelope>, Boolean>() {
                    @Override
                    public Boolean call(retrofit2.Response<HyberMessagesEnvelope> response) {
                        return viberFuncFilter(response.body(), start_of_set_day);
                    }
                })
                .map(new Func1<retrofit2.Response<HyberMessagesEnvelope>, List<HyberMessageModel>>() {
                    @Override
                    public List<HyberMessageModel> call(retrofit2.Response<HyberMessagesEnvelope> response) {
                        setViberMessagesCompleteUpdateDate(start_of_set_day);
                        return viberMessageListSaveToDb(viberEnvelopeToListOfMessages(response.body()));
                    }
                })
                .subscribeOn(Schedulers.newThread());
    }

    /**
     * This method send request to GMS Worldwide server
     * for check new Viber messages for current user.
     * It's work if your plan include this functionality!
     * If you retrieve Viber messages on previous day,
     * you can't send second request for one day!.
     * After receiving a response from the server it will be called notifyStorageSubscribers().
     */
    public void checkViberMessages(long millsUTC) {
        final long start_of_set_day = HyberTools.getStartOfDayUtcTime(millsUTC);
        if (!HyberPlugins.get().getDatabaseHelper().isMessageUpdateForFoolDay(
                start_of_set_day, MessageType.VIBER.getType())) {
            final long phone = HyberPlugins.get().getDatabaseHelper().getCurrentUserPhone();
            long uId = HyberPlugins.get().getDatabaseHelper().getCurrentUserUniqueId();
            HyberPlugins.get().restClient().getViberMessagesObservable(
                    phone, uId, start_of_set_day)
                    .filter(new Func1<retrofit2.Response<HyberMessagesEnvelope>, Boolean>() {
                        @Override
                        public Boolean call(retrofit2.Response<HyberMessagesEnvelope> response) {
                            return viberFuncFilter(response.body(), start_of_set_day);
                        }
                    })
                    .map(new Func1<retrofit2.Response<HyberMessagesEnvelope>, List<HyberMessageModel>>() {
                        @Override
                        public List<HyberMessageModel> call(retrofit2.Response<HyberMessagesEnvelope> response) {
                            setViberMessagesCompleteUpdateDate(start_of_set_day);
                            return viberMessageListSaveToDb(viberEnvelopeToListOfMessages(response.body()));
                        }
                    })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(Schedulers.newThread())
                    .subscribe(new Subscriber<List<HyberMessageModel>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(List<HyberMessageModel> hyberMessageModels) {
                            notifyStorageSubscribers();
                        }
                    });
        }
    }

    private List<HyberMessageModel> viberEnvelopeToListOfMessages(HyberMessagesEnvelope envelope) {
        List<HyberMessageModel> messageModels = envelope.getMessages();
        for (int i = 0; i < messageModels.size(); i++) {
            messageModels.get(i).setType(MessageType.VIBER.getType());
        }
        return messageModels;
    }

    private List<HyberMessageModel> viberMessageListSaveToDb(List<HyberMessageModel> messageList) {
        return HyberPlugins.get().getDatabaseHelper()
                .updateMessagesOfDay(messageList);
    }

    private List<Integer> getMessageTypesForDB() {
        List<Integer> integerTypes = new ArrayList<>();
        if (filterMessage.get(MessageType.PUSH) != null && filterMessage.get(MessageType.PUSH))
            integerTypes.add(MessageType.PUSH.getType());
        if (filterMessage.get(MessageType.VIBER) != null && filterMessage.get(MessageType.VIBER))
            integerTypes.add(MessageType.VIBER.getType());
        if (filterMessage.get(MessageType.SMS) != null && filterMessage.get(MessageType.SMS))
            integerTypes.add(MessageType.SMS.getType());
        return integerTypes;
    }

    private boolean viberFuncFilter(HyberMessagesEnvelope envelope, long queryTime) {
        if (!envelope.isStatus() ||
                envelope.getMessages() == null)
            return false;
        if (envelope.getMessages().size() == 0) {
            if (queryTime < HyberTools.getStartOfDayUtcTime(System.currentTimeMillis()))
                setViberMessagesCompleteUpdateDate(queryTime);
            return false;
        }
        return envelope.getMessages().size() > 0;
    }

    private void setViberMessagesCompleteUpdateDate(long millsUTC) {
        HyberPlugins.get().getDatabaseHelper()
                .addMessageUpdateInfo(millsUTC, MessageType.VIBER.getType(),
                        (HyberTools.getEndOfDayUtcTime(millsUTC) < System.currentTimeMillis()));
    }

    public enum MessageType {
        /**
         * Push message type.
         */
        PUSH(HyberConstants.PUSH_TYPE),
        /**
         * Viber message type.
         */
        VIBER(HyberConstants.VIBER_TYPE),
        /**
         * Sms message type.
         */
        SMS(HyberConstants.SMS_TYPE);

        /**
         * The Type.
         */
        int type;

        MessageType(int t) {
            type = t;
        }

        /**
         * Gets type.
         *
         * @return the type
         */
        int getType() {
            return type;
        }
    }
}
