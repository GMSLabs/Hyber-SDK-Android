package com.hyber;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class Hyber {

    /**
     * Tag used on log messages.
     */
    static final String TAG = "Hyber";

    public static String sdkType = "native";
    public static final String sdkVersion = "000001";

    private static LOG_LEVEL visualLogLevel = LOG_LEVEL.NONE;
    private static LOG_LEVEL logCatLevel = LOG_LEVEL.WARN;

    private static OSUtils.DeviceType deviceType;
    private static OSUtils osUtils;

    static Context appContext;
    static String clientApiKey;
    static String installationID, fingerprint;

    static Hyber.Builder mInitBuilder;
    static boolean initDone;
    private static boolean startedRegistration;

    private static boolean foreground;

    private static String lastRegistrationId;
    private static boolean registerForPushFired;

    private static MessageBusinessModel messageBusinessModel;

    private static RealmChangeListener<RealmResults<Message>> mMessageChangeListener;
    private static RealmResults<Message> mMessageResults;
    private static HashMap<String, Boolean> drInQueue;

    public interface NotificationListener {
        void onMessageReceived(HyberMessageModel hyberMessageModel);
    }

    public interface UserRegistrationHandler {
        void onSuccess();
        void onFailure(String message);
    }

    public interface DeviceUpdateHandler {
        void onSuccess();
        void onFailure(String message);
    }

    public interface MessageHistoryHandler {
        void onSuccess(@NonNull Long recommendedNextTime);
        void onFailure(String message);
    }

    public interface PushDeliveryReportHandler {
        void onSuccess(@NonNull String messageId);
        void onFailure(@NonNull String messageId, String message);
    }

    public static class Builder {
        Context mContext;
        boolean mPromptLocation;
        boolean mDisableGmsMissingPrompt;

        private Builder() {
        }

        private Builder(Context context) {
            mContext = context;
        }

        public Builder setAutoPromptLocation(boolean enable) {
            mPromptLocation = enable;
            return this;
        }

        public Builder disableGmsMissingPrompt(boolean disable) {
            mDisableGmsMissingPrompt = disable;
            return this;
        }

        public void init() {
            Hyber.init(this);
        }
    }

    public static Hyber.Builder startInit(Context context) {
        return new Hyber.Builder(context);
    }

    private static void init(Hyber.Builder inBuilder) {
        mInitBuilder = inBuilder;

        Context context = mInitBuilder.mContext;
        mInitBuilder.mContext = null; // Clear to prevent leaks.

        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            Hyber.init(context, bundle.getString("hyber_client_api_key"));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void init(Context context, String hyberClientApiKey) {
        HyberDataSourceController.with(context);

        if (mInitBuilder == null)
            mInitBuilder = new Hyber.Builder();

        osUtils = new OSUtils();

        deviceType = osUtils.getDeviceType();

        installationID = Installation.id(context);
        fingerprint = Fingerprint.keyHash(context);

        // START: Init validation
        try {
            //noinspection ResultOfMethodCallIgnored
            UUID.fromString(hyberClientApiKey);
        } catch (Throwable t) {
            Log(LOG_LEVEL.FATAL, "Hyber ClientId format is invalid.\nExample: 'b2f7f966-d8cc-11e4-bed1-df8f05be55ba'\n", t);
            return;
        }

        if ("b2f7f966-d8cc-11e4-bed1-df8f05be55ba".equals(hyberClientApiKey))
            Log(LOG_LEVEL.WARN, "Hyber Example ClientID detected, please update to your client's id found on Hyber.com");

        if (deviceType == OSUtils.DeviceType.FCM) {
            //TODO Validate integration params
        }

        try {
            Class.forName("android.support.v4.view.MenuCompat");
            try {
                Class.forName("android.support.v4.content.WakefulBroadcastReceiver");
                Class.forName("android.support.v4.app.NotificationManagerCompat");
            } catch (ClassNotFoundException e) {
                Log(LOG_LEVEL.FATAL, "The included Android Support Library v4 is to old or incomplete. Please update your project's android-support-v4.jar to the latest revision.", e);
            }
        } catch (ClassNotFoundException e) {
            Log(LOG_LEVEL.FATAL, "Could not find the Android Support Library v4. Please make sure android-support-v4.jar has been correctly added to your project.", e);
        }

        if (initDone) {
            if (context != null)
                appContext = context.getApplicationContext();

            return;
        }

        // END: Init validation
        boolean contextIsActivity = (context instanceof Activity);

        foreground = contextIsActivity;
        clientApiKey = hyberClientApiKey;
        appContext = context.getApplicationContext();

        messageBusinessModel = MessageBusinessModel.getInstance();

        drInQueue = new HashMap<>();

        mMessageChangeListener = new RealmChangeListener<RealmResults<Message>>() {
            @Override
            public void onChange(RealmResults<Message> elements) {

                for (Message message : elements) {
                    if (!drInQueue.containsKey(message.getId())) {
                        drInQueue.put(message.getId(), false);
                    }
                }

                List<String> messageIds = new ArrayList<>();
                for (Map.Entry<String, Boolean> entry : drInQueue.entrySet()) {
                    if (!entry.getValue()) {
                        drInQueue.put(entry.getKey(), true);
                        messageIds.add(entry.getKey());
                    }
                }

                Observable.from(messageIds)
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String messageId) {
                                Hyber.Log(LOG_LEVEL.DEBUG, "Message " + messageId + " is changed");
                                Realm realm = Realm.getDefaultInstance();
                                Message receivedMessage =
                                        realm.where(Message.class)
                                                .equalTo(Message.ID, messageId)
                                                .findFirst();

                                if (receivedMessage != null) {
                                    Hyber.Log(LOG_LEVEL.DEBUG, "Sending push delivery report with message id " + messageId);
                                    sendPushDeliveryReport(receivedMessage.getId(), receivedMessage.getReceivedAt().getTime(), new PushDeliveryReportHandler() {
                                        @Override
                                        public void onSuccess(@NonNull String messageId) {
                                            String s = "Push delivery report onSuccess" +
                                                    "\nWith message id " + messageId;
                                            Realm realm = Realm.getDefaultInstance();
                                            Log(LOG_LEVEL.INFO, s);
                                            realm.beginTransaction();
                                            Message rm = realm.where(Message.class)
                                                    .equalTo(Message.ID, messageId)
                                                    .findFirst();
                                            if (rm != null) {
                                                rm.setReportedComplete();
                                                Log(LOG_LEVEL.INFO, String.format(Locale.getDefault(),
                                                        "Message %s set delivery report status is %s",
                                                        rm.getId(), rm.isReported()));
                                            } else {
                                                Log(LOG_LEVEL.WARN, String.format(Locale.getDefault(),
                                                        "Message %s local not found", messageId));
                                            }
                                            drInQueue.remove(messageId);
                                            realm.commitTransaction();
                                            realm.close();
                                        }

                                        @Override
                                        public void onFailure(@NonNull String messageId, String message) {
                                            String s = "Push delivery report onFailure" +
                                                    "\n" + message;
                                            Log(LOG_LEVEL.WARN, s);
                                            drInQueue.remove(messageId);
                                        }
                                    });
                                } else {
                                    drInQueue.put(messageId, false);
                                }
                                mMessageChangeListener.onChange(realm.where(Message.class)
                                        .equalTo(Message.IS_REPORTED, false)
                                        .findAllSorted(Message.RECEIVED_AT, Sort.DESCENDING));
                                realm.close();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Log(LOG_LEVEL.ERROR, throwable.getLocalizedMessage());
                                drInQueue = new HashMap<>();
                            }
                        });
            }
        };
        Realm realm = Realm.getDefaultInstance();
        mMessageResults = realm.where(Message.class)
                .equalTo(Message.IS_REPORTED, false)
                .findAllSorted(Message.RECEIVED_AT, Sort.DESCENDING);
        realm.close();
        mMessageResults.addChangeListener(mMessageChangeListener);

        initDone = true;
    }

    public static void setLogLevel(LOG_LEVEL inLogCatLevel, LOG_LEVEL inVisualLogLevel) {
        logCatLevel = inLogCatLevel;
        visualLogLevel = inVisualLogLevel;
    }

    public static void setLogLevel(int inLogCatLevel, int inVisualLogLevel) {
        setLogLevel(getLogLevel(inLogCatLevel), getLogLevel(inVisualLogLevel));
    }

    private static Hyber.LOG_LEVEL getLogLevel(int level) {
        switch (level) {
            case 0:
                return Hyber.LOG_LEVEL.NONE;
            case 1:
                return Hyber.LOG_LEVEL.FATAL;
            case 2:
                return Hyber.LOG_LEVEL.ERROR;
            case 3:
                return Hyber.LOG_LEVEL.WARN;
            case 4:
                return Hyber.LOG_LEVEL.INFO;
            case 5:
                return Hyber.LOG_LEVEL.DEBUG;
            case 6:
                return Hyber.LOG_LEVEL.VERBOSE;
        }

        if (level < 0)
            return Hyber.LOG_LEVEL.NONE;
        return Hyber.LOG_LEVEL.VERBOSE;
    }

    private static boolean atLogLevel(LOG_LEVEL level) {
        return level.compareTo(visualLogLevel) < 1 || level.compareTo(logCatLevel) < 1;
    }

    static void Log(LOG_LEVEL level, String message) {
        Log(level, message, null);
    }

    static void Log(final LOG_LEVEL level, String message, Throwable throwable) {
        if (level.compareTo(logCatLevel) < 1) {
            if (level == LOG_LEVEL.VERBOSE)
                Log.v(TAG, message, throwable);
            else if (level == LOG_LEVEL.DEBUG)
                Log.d(TAG, message, throwable);
            else if (level == LOG_LEVEL.INFO)
                Log.i(TAG, message, throwable);
            else if (level == LOG_LEVEL.WARN)
                Log.w(TAG, message, throwable);
            else if (level == LOG_LEVEL.ERROR || level == LOG_LEVEL.FATAL)
                Log.e(TAG, message, throwable);
        }
    }

    static void onAppFocus() {
        foreground = true;

        try {
            startRegistrationOrOnSession();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public static void userRegistration(@NonNull Long phone, final UserRegistrationHandler handler) {
        HyberRestClient.registerDevice(phone,
                osUtils.getDeviceOs(), osUtils.getAndroidVersion(),
                osUtils.getDeviceName(), osUtils.getModelName(),
                osUtils.getDeviceFormat(appContext),
                new HyberRestClient.UserRegisterHandler() {
                    @Override
                    public void onSuccess() {
                        handler.onSuccess();
                    }

                    @Override
                    public void onFailure(int statusCode, @Nullable String response, @Nullable Throwable throwable) {
                        String err = String.format(Locale.US, "statusCode %d, response %s, error %s", statusCode, response,
                                throwable != null ? throwable.getCause().getLocalizedMessage() : "");
                        Hyber.Log(LOG_LEVEL.ERROR, err, throwable);
                        handler.onFailure(err);
                    }

                    @Override
                    public void onThrowable(@Nullable Throwable throwable) {
                        String err = "";

                        if (throwable != null) {
                            if (throwable.getCause() != null) {
                                err = String.format(Locale.US, "error %s",
                                        throwable.getCause().getLocalizedMessage());
                            } else {
                                err = String.format(Locale.US, "error %s",
                                        throwable.getLocalizedMessage());
                            }
                        }

                        Hyber.Log(LOG_LEVEL.ERROR, err, throwable);
                        handler.onFailure(err);
                    }
                });
    }

    public static void deviceUpdate(final DeviceUpdateHandler handler) {
        HyberRestClient.updateDevice(
                osUtils.getDeviceOs(), osUtils.getAndroidVersion(),
                osUtils.getDeviceName(), osUtils.getModelName(),
                osUtils.getDeviceFormat(appContext),
                new HyberRestClient.DeviceUpdateHandler() {
                    @Override
                    public void onSuccess() {
                        handler.onSuccess();
                    }

                    @Override
                    public void onFailure(int statusCode, @Nullable String response, @Nullable Throwable throwable) {
                        String err = String.format(Locale.US, "statusCode %d, response %s, error %s", statusCode, response,
                                throwable != null ? throwable.getCause().getLocalizedMessage() : "");
                        Hyber.Log(LOG_LEVEL.ERROR, err, throwable);
                        handler.onFailure(err);
                    }

                    @Override
                    public void onThrowable(@Nullable Throwable throwable) {
                        String err = "";

                        if (throwable != null) {
                            if (throwable.getCause() != null) {
                                err = String.format(Locale.US, "error %s",
                                        throwable.getCause().getLocalizedMessage());
                            } else {
                                err = String.format(Locale.US, "error %s",
                                        throwable.getLocalizedMessage());
                            }
                        }

                        Hyber.Log(LOG_LEVEL.ERROR, err, throwable);
                        handler.onFailure(err);
                    }
                });
    }

    public static void getMessageHistory(@NonNull Long startDate, final MessageHistoryHandler handler) {
        HyberRestClient.getMessageHistory(startDate,
                new HyberRestClient.MessageHistoryHandler() {
                    @Override
                    public void onSuccess(@NonNull final Long startDate, @NonNull final MessageHistoryRespEnvelope envelope) {

                        if (!envelope.getMessages().isEmpty()) {
                            Realm realm = Realm.getDefaultInstance();

                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    Message message;

                                    for (MessageRespModel respModel : envelope.getMessages()) {
                                        message = respModel.toRealmMessageHistory();
                                        if (message != null) {
                                            realm.copyToRealmOrUpdate(message);
                                        }
                                    }

                                    //TODO Need for upgrade algorithm of message history page paging
                                    if (envelope.getMessages().size() < envelope.getLimitMessages()) {
                                        handler.onSuccess(startDate + (TimeUnit.DAYS.toMillis(envelope.getLimitDays())));
                                    } else if (envelope.getMessages().size() >= envelope.getLimitMessages()) {
                                        final Message mh =
                                                realm.where(Message.class)
                                                        .findAllSorted(Message.TIME, Sort.DESCENDING)
                                                        .first();
                                        handler.onSuccess(mh.getTime().getTime());
                                    }
                                }
                            });

                        }
                    }

                    @Override
                    public void onFailure(int statusCode, @Nullable String response, @Nullable Throwable throwable) {
                        String err = String.format(Locale.US, "statusCode %d, response %s, error %s", statusCode, response,
                                throwable != null ? throwable.getCause().getLocalizedMessage() : "");
                        Hyber.Log(LOG_LEVEL.ERROR, err, throwable);
                        handler.onFailure(err);
                    }

                    @Override
                    public void onThrowable(@Nullable Throwable throwable) {
                        String err = "";

                        if (throwable != null) {
                            if (throwable.getCause() != null) {
                                err = String.format(Locale.US, "error %s",
                                        throwable.getCause().getLocalizedMessage());
                            } else {
                                err = String.format(Locale.US, "error %s",
                                        throwable.getLocalizedMessage());
                            }
                        }

                        Hyber.Log(LOG_LEVEL.ERROR, err, throwable);
                        handler.onFailure(err);
                    }
                });
    }

    public static void sendPushDeliveryReport(@NonNull String messageId, @NonNull Long receivedAt, final PushDeliveryReportHandler handler) {
        HyberRestClient.sendPushDeliveryReport(messageId, receivedAt,
                new HyberRestClient.PushDeliveryReportHandler() {
                    @Override
                    public void onSuccess(@NonNull String messageId) {
                        handler.onSuccess(messageId);
                    }

                    @Override
                    public void onFailure(@NonNull String messageId, int statusCode, @Nullable String response, @Nullable Throwable throwable) {
                        String err = String.format(Locale.US, "statusCode %d, response %s, error %s", statusCode, response,
                                throwable != null ? throwable.getCause().getLocalizedMessage() : "");
                        Hyber.Log(LOG_LEVEL.ERROR, err, throwable);
                        handler.onFailure(messageId, err);
                    }

                    @Override
                    public void onThrowable(@NonNull String messageId, @Nullable Throwable throwable) {
                        String err = "";

                        if (throwable != null) {
                            if (throwable.getCause() != null) {
                                err = String.format(Locale.US, "error %s",
                                        throwable.getCause().getLocalizedMessage());
                            } else {
                                err = String.format(Locale.US, "error %s",
                                        throwable.getLocalizedMessage());
                            }
                        }

                        Hyber.Log(LOG_LEVEL.ERROR, err, throwable);
                        handler.onFailure(messageId, err);
                    }
                });
    }

    private static void startRegistrationOrOnSession() throws Throwable {
        if (startedRegistration)
            return;

        startedRegistration = true;

        PushRegistrator pushRegistrator;

        switch (deviceType) {
            case FCM:
                pushRegistrator = new PushRegistrarFCM();
                break;
            default:
                throw new Throwable("Firebase classes not found");
        }

        pushRegistrator.registerForPush(appContext, new PushRegistrator.RegisteredHandler() {
            @Override
            public void complete(String id) {
                lastRegistrationId = id;
                registerForPushFired = true;
                updateDeviceData();
            }
        });
    }

    public static void notificationListener(final NotificationListener listener) {
        NotificationBundleProcessor.remoteMessageObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<HyberMessageModel>() {
                    @Override
                    public void call(HyberMessageModel hyberMessageModel) {
                        listener.onMessageReceived(hyberMessageModel);
                    }
                });
    }

    private static void updateDeviceData() {
        Log(LOG_LEVEL.DEBUG, "updateDeviceData: registerForPushFired:" + registerForPushFired);

        if (!registerForPushFired)
            return;
    }

    static SharedPreferences getHyberPreferences(Context context) {
        return context.getSharedPreferences(Hyber.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    static MessageBusinessModel getMessageBusinessModel() {
        return messageBusinessModel;
    }

    static void saveMessage(Message message) {
        getMessageBusinessModel().saveMessage(message)
                .subscribe(new Action1<Message>() {
                    @Override
                    public void call(Message message) {

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log(LOG_LEVEL.WARN, throwable.getLocalizedMessage());
                    }
                });
    }

    static void runOnUiThread(Runnable action) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(action);
    }

    public enum LOG_LEVEL {
        NONE, FATAL, ERROR, WARN, INFO, DEBUG, VERBOSE
    }

}
