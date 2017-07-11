package com.hyber;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.messaging.RemoteMessage;
import com.hyber.handler.CurrentUserHandler;
import com.hyber.handler.EmptyResult;
import com.hyber.handler.HyberCallback;
import com.hyber.handler.HyberError;
import com.hyber.handler.HyberNotificationListener;
import com.hyber.handler.LogoutUserHandler;
import com.hyber.log.HyberLogger;
import com.hyber.model.Device;
import com.hyber.model.Message;
import com.hyber.model.User;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import rx.Observable;
import rx.functions.Action1;

public final class Hyber {

    /**
     * Tag used on log messages.
     */
    private static final String TAG = "Hyber";
    private static Context mContextReference;
    private static String clientApiKey;
    private static String fingerprint;
    private static Hyber.Builder mInitBuilder;
    private static boolean initDone;
    private static boolean startedRegistration;

    private static boolean foreground;

    private static String lastRegistrationId;

    private static ApiBusinessModel mApiBusinessModel;

    private static Repository repo;
    private static RealmChangeListener<RealmResults<Message>> mMessageChangeListener;
    private static RealmResults<Message> mMessageResults;
    private static HashMap<String, Boolean> drInQueue;

    static long lastAuthorizeTime = 0;

    private static Hyber instance = null;

    private Hyber() {

    }

    private static Hyber getInstance() {
        return instance;
    }

    static String getClientApiKey() {
        return clientApiKey;
    }

    static String getFingerprint() {
        if (fingerprint == null)
            fingerprint = AppFingerprint.keyHash(getAppContext());
        return fingerprint;
    }

    static ApiBusinessModel getApiBusinessModel() {
        if (mApiBusinessModel == null)
            mApiBusinessModel = ApiBusinessModel.getInstance(getAppContext());
        return mApiBusinessModel;
    }

    public static DataSourceController dataSourceController() {
        return DataSourceController.getInstance();
    }

    static Builder getInitBuilder() {
        return mInitBuilder;
    }

    public static Hyber.Builder with(Context context, String clientApiKey) {
        return new Hyber.Builder(context, clientApiKey);
    }

    private static void init(Hyber.Builder inBuilder, String hyberClientApiKey) {
        mInitBuilder = inBuilder;

        Context context = mInitBuilder.getContext();
        mInitBuilder.removeContext(); // Clear to prevent leaks.

        Hyber.init(context, hyberClientApiKey);
    }

    private static void init(Context context, String hyberClientApiKey) {
        DataSourceController.with(context);
        repo = new Repository();
        repo.open();

        NotificationBundleProcessor.getRemoteMessageObservable()
                .subscribe(new Action1<RemoteMessage>() {
                    @Override
                    public void call(RemoteMessage remoteMessage) {
                        if (mInitBuilder.mNotificationListener != null)
                            mInitBuilder.mNotificationListener.onMessageReceived(remoteMessage);
                    }
                });

        // START: Init validation
        if (hyberClientApiKey == null || hyberClientApiKey.isEmpty()) {
            HyberLogger.e(ErrorStatus.SDK_INTEGRATION_ClientApiKeyIsInvalid.toString());
            return;
        }

        switch (Utils.getDeviceType()) {
            //TODO Validate integration params
            case FCM:
                HyberLogger.i("Firebase messaging on board!");
                break;
            case GCM:
                HyberLogger.i("Google cloud messaging on board!");
                break;
            case ADM:
                HyberLogger.i("Amazone messaging on board!");
                break;
            default:
                HyberLogger.i("NON messaging on board!");
        }

        try {
            Class.forName("android.support.v4.view.MenuCompat");
            try {
                Class.forName("android.support.v4.content.WakefulBroadcastReceiver");
                Class.forName("android.support.v4.app.NotificationManagerCompat");
            } catch (ClassNotFoundException e) {
                HyberLogger.e(e, "The included Android Support Library v4 is to old or incomplete."
                        + "\nPlease update your project's android-support-v4.jar to the latest revision.");
            }
        } catch (ClassNotFoundException e) {
            HyberLogger.e(e, "Could not find the Android Support Library v4."
                    + "\nPlease make sure android-support-v4.jar has been correctly added to your project.");
        }

        if (initDone) {
            mContextReference = context.getApplicationContext();
            return;
        }
        // END: Init validation

        boolean contextIsActivity = (context instanceof Activity);
        foreground = contextIsActivity;

        clientApiKey = hyberClientApiKey;
        mContextReference = context.getApplicationContext();

        drInQueue = new HashMap<>();

        mMessageChangeListener = getMessageChangeListener();
        mMessageResults = repo.getAllUnreportedMessages();
        mMessageResults.addChangeListener(mMessageChangeListener);

        initDone = true;

        checkFirebaseToken();
    }

    public static void checkFirebaseToken() {
        try {
            startRegistrationOrOnSession();
        } catch (Throwable throwable) {
            HyberLogger.e(throwable);
        }

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
                                HyberLogger.d("Message %s is changed", messageId);
                                Message receivedMessage = repo.getMessageById(repo.getCurrentUser(), messageId);

                                if (receivedMessage != null) {
                                    HyberLogger.d("Sending push delivery report with message id %s", messageId);
                                    sendPushDeliveryReport(receivedMessage.getId(), receivedMessage.getDate(),
                                            new HyberCallback<String, EmptyResult>() {
                                                @Override
                                                public void onSuccess(@NonNull String messageId) {
                                                    Realm realm = repo.getNewRealmInstance();
                                                    HyberLogger.i("Push delivery report onSuccess\nWith message id %s",
                                                            messageId);
                                                    realm.beginTransaction();
                                                    Message rm = repo.getMessageById(repo.getCurrentUser(), messageId);
                                                    if (rm != null) {
                                                        rm.setReportedStatus(true);
                                                        HyberLogger.i("Message %s set delivery report status is %s",
                                                                rm.getId(), rm.getIsReported());
                                                    } else {
                                                        HyberLogger.w("Message %s local not found", messageId);
                                                    }
                                                    drInQueue.remove(messageId);
                                                    realm.commitTransaction();
                                                    realm.close();
                                                }

                                                @Override
                                                public void onFailure(EmptyResult error) {

                                                }
                                            });
                                } else {
                                    drInQueue.put(messageId, false);
                                }
                                mMessageChangeListener.onChange(repo.getAllUnreportedMessages());
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                HyberLogger.e(throwable);
                                drInQueue = new HashMap<>();
                            }
                        });
            }
        };
    }

    static void onAppFocus() {
        foreground = true;
        checkFirebaseToken();
    }

    public static void userRegistration(@NonNull String phone, @NonNull String password, final HyberCallback<EmptyResult, HyberError> callback) {
        checkInitialized();
        getApiBusinessModel().authorize(phone, password, new ApiBusinessModel.AuthorizationListener() {
            @Override
            public void onSuccess() {
                getApiBusinessModel().sendDeviceData(new ApiBusinessModel.SendDeviceDataListener() {
                    @Override
                    public void onSuccess() {
                        HyberLogger.i("Send device data is success");
                        callback.onSuccess(new EmptyResult());
                    }

                    @Override
                    public void onFailure(@lombok.NonNull HyberError status) {
                        HyberLogger.e("Send device data is failure");
                        callback.onFailure(status);
                    }
                });
            }

            @Override
            public void onFailure(HyberError status) {
                callback.onFailure(status);
            }
        });
    }

    public static void isAuthorized(final HyberCallback<EmptyResult, EmptyResult> callback) {
        checkInitialized();
        boolean isAuthorized = false;
        Repository repo = new Repository();
        repo.open();
        if (repo.getCurrentUser() != null)
            isAuthorized = true;
        repo.close();
        if (isAuthorized) {
            callback.onSuccess(new EmptyResult());
        } else {
            callback.onFailure(new EmptyResult());
        }
    }

    public static void getCurrentUser(final CurrentUserHandler handler) {
        checkInitialized();
        if (repo.getCurrentUser() != null)
            handler.onCurrentUser(repo.getCurrentUser().getId(), repo.getCurrentUser().getPhone());
    }

    public static void logoutCurrentUser(final LogoutUserHandler handler) {
        checkInitialized();
        if (repo.getCurrentUser() != null) {
            repo.clearUserData(repo.getCurrentUser());
            handler.onSuccess();
        } else {
            handler.onFailure();
        }
    }

    public static void sendMessageCallback(@NonNull String messageId, @NonNull String answerText,
                                           final HyberCallback<String, HyberError> callback) {
        checkInitialized();
        getApiBusinessModel().sendMessageCallback(messageId, answerText,
                new ApiBusinessModel.SendMessageCallbackListener() {
                    @Override
                    public void onSuccess(@NonNull String messageId) {
                        callback.onSuccess(messageId);
                    }

                    @Override
                    public void onFailure(@lombok.NonNull HyberError status) {
                        callback.onFailure(status);
                    }
                });
    }

    public static void getAllDevices(final HyberCallback<EmptyResult, HyberError> callback) {
        checkInitialized();
        getApiBusinessModel().getAllDevices(new ApiBusinessModel.AllDevicesListener() {
            @Override
            public void onSuccess() {
                callback.onSuccess(new EmptyResult());
            }

            @Override
            public void onFailure(@lombok.NonNull HyberError status) {
                callback.onFailure(status);
            }
        });
    }

    public static void revokeDevices(List<String> deviceIds, final HyberCallback<EmptyResult, HyberError> callback) {
        checkInitialized();
        getApiBusinessModel().revokeDevices(deviceIds, new ApiBusinessModel.RevokeDevicesListener() {
            @Override
            public void onSuccess() {
                callback.onSuccess(new EmptyResult());
            }

            @Override
            public void onFailure(@lombok.NonNull HyberError status) {
                callback.onFailure(status);
            }
        });
    }

    public static void getMessageHistory(@NonNull Long startDate, final HyberCallback<Long, HyberError> callback) {
        checkInitialized();
        getApiBusinessModel().getMessageHistory(startDate, new ApiBusinessModel.MessageHistoryListener() {
            @Override
            public void onSuccess(@NonNull Long startDate) {
                callback.onSuccess(startDate);
            }

            @Override
            public void onFailure(@lombok.NonNull HyberError status) {
                callback.onFailure(status);
            }
        });
    }

    @Nullable
    public static RealmResults<Message> getAllUserMessages() {
        User user = repo.getCurrentUser();
        if (user == null)
            return null;
        return repo.getMessages(user);
    }

    @Nullable
    public static OrderedRealmCollection<Device> getAllUserDevices() {
        User user = repo.getCurrentUser();
        if (user == null)
            return null;
        return repo.getDevices(user);
    }

    private static void sendPushDeliveryReport(@NonNull final String messageId, @NonNull Date receivedAt,
                                               final HyberCallback<String, EmptyResult> callback) {
        getApiBusinessModel().sendMessageDeliveryReport(messageId, receivedAt,
                new ApiBusinessModel.SendMessageDeliveryReportListener() {
                    @Override
                    public void onSuccess(@NonNull String messageId) {
                        callback.onSuccess(messageId);
                    }

                    @Override
                    public void onFailure() {
                        callback.onFailure(new EmptyResult());
                    }
                });
    }

    private static void startRegistrationOrOnSession() throws Throwable {
        if (startedRegistration)
            return;

        startedRegistration = true;

        PushRegistrator pushRegistrator;

        switch (Utils.getDeviceType()) {
            case FCM:
                pushRegistrator = new PushRegistrarFCM();
                break;
            default:
                throw new Throwable("Firebase classes not found");
        }

        pushRegistrator.registerForPush(mContextReference, new PushRegistrator.RegisteredHandler() {
            @Override
            public void complete(String id) {
                lastRegistrationId = id;
                updateDeviceData();
            }
        });
    }

    static Context getAppContext() {
        return mContextReference;
    }

    static void updateDeviceData() {
        getApiBusinessModel().sendDeviceData(new ApiBusinessModel.SendDeviceDataListener() {
            @Override
            public void onSuccess() {
                HyberLogger.i("Refreshed FCM token sent to Hyber.");
            }

            @Override
            public void onFailure(@lombok.NonNull HyberError status) {
                HyberLogger.d("Refreshed FCM token can not sent to Hyber.\n%s - %s", status.getStatus().name(), status.getMessage());
            }
        });
    }

    public static final class Builder {
        private WeakReference<Context> mWeakContext;
        private String mClientApiKey;
        private HyberNotificationListener mNotificationListener;

        private Builder() {

        }

        private Builder(Context context, String clientApiKey) {
            this.mWeakContext = new WeakReference<>(context);
            this.mClientApiKey = clientApiKey;
        }

        public Builder setNotificationListener(@NonNull final HyberNotificationListener listener) {
            this.mNotificationListener = listener;
            return this;
        }

        public void init() {
            Hyber.init(this, this.mClientApiKey);
        }

        Context getContext() {
            return mWeakContext.get();
        }

        void removeContext() {
            mWeakContext.clear();
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
