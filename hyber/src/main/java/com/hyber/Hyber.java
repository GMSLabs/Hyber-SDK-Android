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

import com.hyber.handler.BidirectionalAnswerHandler;
import com.hyber.handler.DeviceUpdateHandler;
import com.hyber.handler.MessageHistoryHandler;
import com.hyber.handler.UserRegistrationHandler;
import com.hyber.listener.DeliveryReportListener;
import com.hyber.listener.HyberErrorListener;
import com.hyber.listener.HyberNotificationListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.functions.Action1;

public final class Hyber {

    /**
     * Tag used on log messages.
     */
    private static final String TAG = "Hyber";
    private static WeakReference<Context> appWeakContext;
    private static String clientApiKey;
    private static String installationID;
    private static String fingerprint;
    private static boolean isBidirectionalAvailable = false;
    private static Hyber.Builder mInitBuilder;
    private static boolean initDone;
    private static LogLevel visualLogLevel = LogLevel.NONE;
    private static LogLevel logCatLevel = LogLevel.WARN;
    private static boolean startedRegistration;

    private static boolean foreground;

    private static String lastRegistrationId;
    private static boolean registerForPushFired;

    private static MainApiBusinessModel mMainApiBusinessModel;
    private static MessageBusinessModel mMessageBusinessModel;

    private static RealmChangeListener<RealmResults<Message>> mMessageChangeListener;
    private static RealmResults<Message> mMessageResults;
    private static HashMap<String, Boolean> drInQueue;

    private static Hyber instance = null;

    private Hyber() {

    }

    private static Hyber getInstance() {
        return instance;
    }

    static String getClientApiKey() {
        return clientApiKey;
    }

    static String getInstallationID() {
        return installationID;
    }

    static String getFingerprint() {
        return fingerprint;
    }

    static Builder getInitBuilder() {
        return mInitBuilder;
    }

    public static Hyber.Builder with(Context context) {
        return new Hyber.Builder(context);
    }

    private static void init(Hyber.Builder inBuilder) {
        mInitBuilder = inBuilder;

        Context context = mInitBuilder.getContext();
        mInitBuilder.removeContext(); // Clear to prevent leaks.

        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            Hyber.init(context, bundle.getString("hyber_client_api_key"));
        } catch (PackageManager.NameNotFoundException e) {
            HyberErrorProcessor.getInstance().onError(new HyberError(e));
        }
    }

    private static void init(Context context, String hyberClientApiKey) {
        HyberErrorProcessor.getInstance().getHyberErrorObservable()
                .subscribe(new Action1<HyberError>() {
                    @Override
                    public void call(HyberError error) {
                        if (mInitBuilder.mErrorListener != null)
                            mInitBuilder.mErrorListener.onError(error);

                        mLog(LogLevel.ERROR, String.format(Locale.getDefault(),
                                "Hyber error code %d\n%s", error.getCode(), error.getDescription()));
                    }
                });

        HyberDataSourceController.with(context);

        NotificationBundleProcessor.getRemoteMessageObservable()
                .subscribe(new Action1<HyberMessageModel>() {
                    @Override
                    public void call(HyberMessageModel hyberMessageModel) {
                        if (mInitBuilder.mNotificationListener != null)
                            mInitBuilder.mNotificationListener.onMessageReceived(hyberMessageModel);

                        mLog(LogLevel.INFO, String.format(Locale.getDefault(),
                                "New Hyber message:\nID: %s\nAlpha name: %s\nMessage: %s",
                                hyberMessageModel.getId(), hyberMessageModel.getAlpha(), hyberMessageModel.getText()));
                    }
                });

        installationID = Installation.id(context);
        fingerprint = Fingerprint.keyHash(context);

        isBidirectionalAvailable = /*TODO Add Bidirectional support controller*/ true;

        // START: Init validation
        if (hyberClientApiKey == null || hyberClientApiKey.isEmpty()) {
            mLog(LogLevel.FATAL, "Hyber Client Api Key format is invalid.");
            HyberErrorProcessor.getInstance().onError(new HyberError(HyberErrorStatus.INTERNAL_ChuckNorrisException));
            return;
        }

        switch (OsUtils.getDeviceType()) {
            //TODO Validate integration params
            case FCM:
                mLog(LogLevel.INFO, "Firebase messaging on board!");
                break;
            case GCM:
                mLog(LogLevel.INFO, "Google cloud messaging on board!");
                break;
            case ADM:
                mLog(LogLevel.INFO, "Amazone messaging on board!");
                break;
            default:
                mLog(LogLevel.INFO, "NON messaging on board!");
        }

        try {
            Class.forName("android.support.v4.view.MenuCompat");
            try {
                Class.forName("android.support.v4.content.WakefulBroadcastReceiver");
                Class.forName("android.support.v4.app.NotificationManagerCompat");
            } catch (ClassNotFoundException e) {
                mLog(LogLevel.FATAL,
                        "The included Android Support Library v4 is to old or incomplete."
                                + "\nPlease update your project's android-support-v4.jar to the latest revision.", e);
            }
        } catch (ClassNotFoundException e) {
            mLog(LogLevel.FATAL,
                    "Could not find the Android Support Library v4."
                            + "\nPlease make sure android-support-v4.jar has been correctly added to your project.", e);
        }

        if (initDone) {
            if (context != null)
                appWeakContext = new WeakReference<>(context.getApplicationContext());

            return;
        }
        // END: Init validation

        boolean contextIsActivity = (context instanceof Activity);
        foreground = contextIsActivity;

        clientApiKey = hyberClientApiKey;
        appWeakContext = new WeakReference<>(context.getApplicationContext());

        mMainApiBusinessModel = MainApiBusinessModel.getInstance(context);
        mMessageBusinessModel = MessageBusinessModel.getInstance();

        drInQueue = new HashMap<>();

        mMessageChangeListener = getMessageChangeListener();

        Realm realm = Realm.getDefaultInstance();
        mMessageResults = realm.where(Message.class)
                .equalTo(Message.IS_REPORTED, false)
                .findAllSorted(Message.RECEIVED_AT, Sort.DESCENDING);
        realm.close();
        mMessageResults.addChangeListener(mMessageChangeListener);

        initDone = true;
    }

    private static RealmChangeListener<RealmResults<Message>> getMessageChangeListener() {
        return new RealmChangeListener<RealmResults<Message>>() {
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
                                Hyber.mLog(LogLevel.DEBUG, "Message " + messageId + " is changed");
                                Realm realm = Realm.getDefaultInstance();
                                Message receivedMessage =
                                        realm.where(Message.class)
                                                .equalTo(Message.ID, messageId)
                                                .findFirst();

                                if (receivedMessage != null) {
                                    Hyber.mLog(LogLevel.DEBUG, "Sending push delivery report with message id " + messageId);
                                    sendPushDeliveryReport(receivedMessage.getId(), receivedMessage.getReceivedAt().getTime(),
                                            new DeliveryReportListener() {
                                                @Override
                                                public void onDeliveryReportSent(@NonNull String messageId) {
                                                    String s = String.format(Locale.getDefault(),
                                                            "Push delivery report onSuccess\nWith message id %s", messageId);
                                                    Realm realm = Realm.getDefaultInstance();
                                                    mLog(LogLevel.INFO, s);
                                                    realm.beginTransaction();
                                                    Message rm = realm.where(Message.class)
                                                            .equalTo(Message.ID, messageId)
                                                            .findFirst();
                                                    if (rm != null) {
                                                        rm.setReportedComplete();
                                                        mLog(LogLevel.INFO, String.format(Locale.getDefault(),
                                                                "Message %s set delivery report status is %s",
                                                                rm.getId(), rm.isReported()));
                                                    } else {
                                                        mLog(LogLevel.WARN, String.format(Locale.getDefault(),
                                                                "Message %s local not found", messageId));
                                                    }
                                                    drInQueue.remove(messageId);
                                                    realm.commitTransaction();
                                                    realm.close();
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
                                mLog(LogLevel.ERROR, throwable.getLocalizedMessage());
                                drInQueue = new HashMap<>();
                            }
                        });
            }
        };
    }

    public static boolean isBidirectionalAvailable() {
        checkInitialized();
        return isBidirectionalAvailable;
    }

    public static void setLogLevel(LogLevel inLogCatLevel, LogLevel inVisualLogLevel) {
        logCatLevel = inLogCatLevel;
        visualLogLevel = inVisualLogLevel;
    }

    private static boolean atLogLevel(LogLevel level) {
        return level.compareTo(visualLogLevel) < 1 || level.compareTo(logCatLevel) < 1;
    }

    static void mLog(LogLevel level, String message) {
        mLog(level, message, null);
    }

    static void mLog(final LogLevel level, String message, Throwable throwable) {
        if (level.compareTo(logCatLevel) < 1) {
            if (level == LogLevel.VERBOSE)
                Log.v(TAG, message, throwable);
            else if (level == LogLevel.DEBUG)
                Log.d(TAG, message, throwable);
            else if (level == LogLevel.INFO)
                Log.i(TAG, message, throwable);
            else if (level == LogLevel.WARN)
                Log.w(TAG, message, throwable);
            else if (level == LogLevel.ERROR || level == LogLevel.FATAL)
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
        checkInitialized();
        mMainApiBusinessModel.authorize(phone, new MainApiBusinessModel.AuthorizationListener() {
            @Override
            public void onAuthorized() {
                handler.onSuccess();
            }

            @Override
            public void onAuthorizationError(AuthErrorStatus status) {
                handler.onFailure(status.getDescription());
            }
        });
    }

    public static void deviceUpdate(final DeviceUpdateHandler handler) {
        checkInitialized();
        mMainApiBusinessModel.sendDeviceData(new MainApiBusinessModel.SendDeviceDataListener() {
            @Override
            public void onSent() {
                handler.onSuccess();
            }

            @Override
            public void onSendingError(SendDeviceDataErrorStatus status) {
                handler.onFailure(status.getDescription());
            }
        });
    }

    public static void sendBidirectionalAnswer(@NonNull String messageId, @NonNull String answerText,
                                               final BidirectionalAnswerHandler handler) {
        checkInitialized();
        mMainApiBusinessModel.sendBidirectionalAnswer(messageId, answerText,
                new MainApiBusinessModel.SendBidirectionalAnswerListener() {
                    @Override
                    public void onSent(@NonNull String messageId) {
                        handler.onSuccess();
                    }

                    @Override
                    public void onSendingError() {
                        handler.onFailure(/*TODO*/ "TODO");
                    }
                });
    }

    public static void getMessageHistory(@NonNull Long startDate, final MessageHistoryHandler handler) {
        checkInitialized();
        mMainApiBusinessModel.getMessageHistory(startDate, new MainApiBusinessModel.MessageHistoryListener() {
            @Override
            public void onSuccess(@NonNull final Long startDate, @NonNull final MessageHistoryRespEnvelope envelope) {
                Realm realm = null;
                if (!envelope.getMessages().isEmpty()) {
                    realm = Realm.getDefaultInstance();
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
                        }
                    });
                }

                handler.onSuccess(envelope.getTimeLastMessage());
            }

            @Override
            public void onFailure() {
                handler.onFailure(/*TODO*/ "TODO");
            }
        });
    }

    private static void sendPushDeliveryReport(@NonNull final String messageId, @NonNull Long receivedAt,
                                               final DeliveryReportListener handler) {
        mMainApiBusinessModel.sendPushDeliveryReport(messageId, receivedAt,
                new MainApiBusinessModel.SendPushDeliveryReportListener() {
                    @Override
                    public void onSuccess(@NonNull String messageId) {
                        handler.onDeliveryReportSent(messageId);
                    }

                    @Override
                    public void onFailure() {
                        HyberErrorProcessor.getInstance().onError(new HyberError());
                    }
                });
    }

    private static void startRegistrationOrOnSession() throws Throwable {
        if (startedRegistration)
            return;

        startedRegistration = true;

        PushRegistrator pushRegistrator;

        switch (OsUtils.getDeviceType()) {
            case FCM:
                pushRegistrator = new PushRegistrarFCM();
                break;
            default:
                throw new Throwable("Firebase classes not found");
        }

        pushRegistrator.registerForPush(appWeakContext.get(), new PushRegistrator.RegisteredHandler() {
            @Override
            public void complete(String id) {
                lastRegistrationId = id;
                registerForPushFired = true;
                updateDeviceData();
            }
        });
    }

    static Context getAppContext() {
        return appWeakContext.get();
    }

    private static void updateDeviceData() {
        mLog(LogLevel.DEBUG, "updateDeviceData: registerForPushFired:" + registerForPushFired);

        if (!registerForPushFired)
            return;
    }

    static SharedPreferences getHyberPreferences(Context context) {
        return context.getSharedPreferences(Hyber.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    static MessageBusinessModel getmMessageBusinessModel() {
        return mMessageBusinessModel;
    }

    static void saveMessage(Message message) {
        getmMessageBusinessModel().saveMessage(message)
                .subscribe(new Action1<Message>() {
                    @Override
                    public void call(Message message) {

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mLog(LogLevel.WARN, throwable.getLocalizedMessage());
                    }
                });
    }

    static void runOnUiThread(Runnable action) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(action);
    }

    public enum LogLevel {
        NONE, FATAL, ERROR, WARN, INFO, DEBUG, VERBOSE
    }

    public interface PushTokenUpdateHandler {
        void onSuccess();

        void onFailure(String message);
    }

    public static final class Builder {
        private WeakReference<Context> mWeakContext;
        private boolean mPromptLocation;
        private boolean mDisableGmsMissingPrompt;
        private HyberErrorListener mErrorListener;
        private HyberNotificationListener mNotificationListener;

        private Builder() {

        }

        private Builder(Context context) {
            this.mWeakContext = new WeakReference<>(context);
        }

        public Builder setLogLevel(LogLevel level) {
            logCatLevel = level;
            return this;
        }

        public Builder setErrorListener(@NonNull final HyberErrorListener listener) {
            this.mErrorListener = listener;
            return this;
        }

        public Builder setNotificationListener(@NonNull final HyberNotificationListener listener) {
            this.mNotificationListener = listener;
            return this;
        }

        public Builder setAutoPromptLocation(boolean enable) {
            this.mPromptLocation = enable;
            return this;
        }

        public Builder disableGmsMissingPrompt(boolean disable) {
            this.mDisableGmsMissingPrompt = disable;
            return this;
        }

        public void init() {
            Hyber.init(this);
        }

        Context getContext() {
            return mWeakContext.get();
        }

        void removeContext() {
            mWeakContext.clear();
        }

        boolean isPromptLocation() {
            return mPromptLocation;
        }

        boolean isDisableGmsMissingPrompt() {
            return mDisableGmsMissingPrompt;
        }

        @Nullable
        public HyberErrorListener getErrorListener() {
            return mErrorListener;
        }

        @Nullable
        public HyberNotificationListener getNotificationListener() {
            return mNotificationListener;
        }

    }

    private static void checkInitialized() {
        if (!initDone) {
            throw new IllegalStateException(
                    "Hyber must be initialized by calling Hyber.with(Context).init() prior to calling Hyber methods");
        }
    }

}
