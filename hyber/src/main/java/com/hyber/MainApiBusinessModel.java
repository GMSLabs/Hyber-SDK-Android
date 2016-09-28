package com.hyber;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.iid.FirebaseInstanceId;
import com.orhanobut.hawk.Hawk;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

final class MainApiBusinessModel implements IMainApiBusinessModel {

    private static final int UNAUTHORIZED_CODE = 401;
    private static MainApiBusinessModel mInstance;
    private WeakReference<Context> mContextWeakReference;

    private MainApiBusinessModel(@NonNull Context context) {
        this.mContextWeakReference = new WeakReference<>(context);
    }

    static synchronized MainApiBusinessModel getInstance(@NonNull Context context) {
        if (mInstance == null) {
            mInstance = new MainApiBusinessModel(context);
        }
        return mInstance;
    }

    @Override
    public void authorize(@NonNull final Long phone, @NonNull final AuthorizationListener listener) {
        Hyber.mLog(Hyber.LogLevel.DEBUG, "Start user registration.");

        RegisterUserReqModel reqModel = new RegisterUserReqModel(phone,
                OsUtils.getDeviceOs(), OsUtils.getAndroidVersion(),
                OsUtils.getDeviceName(), OsUtils.getModelName(),
                OsUtils.getDeviceFormat(mContextWeakReference.get()));

        HyberRestClient.registerUserObservable(reqModel)
                .subscribe(new Action1<Response<RegisterUserRespModel>>() {
                    @Override
                    public void call(Response<RegisterUserRespModel> response) {
                        if (response.isSuccessful()) {
                            Hyber.mLog(Hyber.LogLevel.DEBUG, "Request for user registration is success.");
                            Realm realm = Realm.getDefaultInstance();

                            clearUserDataIfExists(realm, phone);

                            SessionRespItemModel session = response.body().getSession();
                            if (session != null) {
                                Hyber.mLog(Hyber.LogLevel.DEBUG, "New user session is provided.");
                                User user = new User(String.valueOf(phone), phone);
                                realm.beginTransaction();
                                realm.copyToRealm(user);
                                realm.commitTransaction();
                                Hyber.mLog(Hyber.LogLevel.DEBUG, "New user data saved.");

                                updateUserSession(session.getToken(), session.getRefreshToken(), session.getExpirationDate());
                                Hyber.mLog(Hyber.LogLevel.DEBUG, "User is registered.");
                                listener.onAuthorized();
                            } else {
                                Hyber.mLog(Hyber.LogLevel.ERROR, "User not registered, session data is not provided!");
                                listener.onAuthorizationError(AuthErrorStatus.USER_SESSION_DATA_IS_NOT_PROVIDED);
                            }
                        } else {
                            Hyber.mLog(Hyber.LogLevel.ERROR, "Response for user registration api is unsuccessful!");
                            listener.onAuthorizationError(AuthErrorStatus.USER_SESSION_DATA_IS_NOT_PROVIDED);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Hyber.mLog(Hyber.LogLevel.FATAL, "Error in user registration api request!", throwable);
                        listener.onAuthorizationError(AuthErrorStatus.USER_SESSION_DATA_IS_NOT_PROVIDED);
                    }
                });
    }

    private Observable<Response<RefreshTokenRespModel>> refreshTokenObservable() {
        RefreshTokenReqModel reqModel = new RefreshTokenReqModel(getRefreshToken());

        return HyberRestClient.refreshTokenObservable(reqModel)
                .doOnNext(new Action1<Response<RefreshTokenRespModel>>() {
                    @Override
                    public void call(Response<RefreshTokenRespModel> response) {
                        if (response.isSuccessful()) {
                            Hyber.mLog(Hyber.LogLevel.DEBUG, "Request for refresh user auth token is success.");
                            SessionRespItemModel session = response.body().getSession();
                            updateUserSession(session.getToken(), session.getRefreshToken(), session.getExpirationDate());
                        } else {
                            Hyber.mLog(Hyber.LogLevel.ERROR, "Response for refresh user auth token api is unsuccessful!");
                        }
                    }
                }).doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Hyber.mLog(Hyber.LogLevel.FATAL, "Error in refresh user auth token api request!", throwable);
                    }
                });
    }

    @Override
    public void sendDeviceData(@NonNull final SendDeviceDataListener listener) {
        Hyber.mLog(Hyber.LogLevel.DEBUG, "Start sending user device data.");


        final UpdateUserReqModel reqModel = new UpdateUserReqModel(
                FirebaseInstanceId.getInstance().getToken(),
                OsUtils.getDeviceOs(), OsUtils.getAndroidVersion(),
                OsUtils.getDeviceName(), OsUtils.getModelName(),
                OsUtils.getDeviceFormat(mContextWeakReference.get()));

        HyberRestClient.updateUserObservable(reqModel)
                .flatMap(new Func1<Response<UpdateUserRespModel>, Observable<Response<UpdateUserRespModel>>>() {
                    @Override
                    public Observable<Response<UpdateUserRespModel>> call(Response<UpdateUserRespModel> response) {
                        if (response.code() == UNAUTHORIZED_CODE) {
                            Hyber.mLog(Hyber.LogLevel.DEBUG,
                                    String.format(Locale.getDefault(),
                                            "Response for update user device data api request is unsuccessful with code %d!",
                                            response.code()));
                            removeAuthToken();
                            return refreshTokenObservable()
                                    .flatMap(new Func1<Response<RefreshTokenRespModel>,
                                            Observable<Response<UpdateUserRespModel>>>() {
                                        @Override
                                        public Observable<Response<UpdateUserRespModel>> call(
                                                Response<RefreshTokenRespModel> response) {
                                            Hyber.mLog(Hyber.LogLevel.ERROR, "Continue execute updating user data!");
                                            return HyberRestClient.updateUserObservable(reqModel);
                                        }
                                    });
                        }
                        return Observable.just(response);
                    }
                }).subscribe(new Action1<Response<UpdateUserRespModel>>() {
            @Override
            public void call(Response<UpdateUserRespModel> response) {
                if (response.isSuccessful()) {
                    Hyber.mLog(Hyber.LogLevel.DEBUG, "Request for update user device data is success.");
                    if (response.body().getError() == null) {
                        listener.onSent();
                    } else {
                        Hyber.mLog(Hyber.LogLevel.ERROR,
                                String.format(Locale.getDefault(),
                                        "Response for update user device data api with error!\n%s",
                                        response.body().getError().getDescription()));
                        listener.onSendingError(SendDeviceDataErrorStatus.SENDING_UNSUCCESSFUL);
                    }
                } else {
                    Hyber.mLog(Hyber.LogLevel.ERROR,
                            String.format(Locale.getDefault(),
                                    "Response for update user device data api request is unsuccessful with code %d!",
                                    response.code()));
                    listener.onSendingError(SendDeviceDataErrorStatus.SENDING_UNSUCCESSFUL);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Hyber.mLog(Hyber.LogLevel.FATAL, "Error in update user device data api request!", throwable);
                listener.onSendingError(SendDeviceDataErrorStatus.SENDING_UNSUCCESSFUL);
            }
        });

    }

    @Override
    public void sendBidirectionalAnswer(@NonNull final String messageId, @NonNull String answerText,
                                        @NonNull final SendBidirectionalAnswerListener listener) {
        Hyber.mLog(Hyber.LogLevel.DEBUG, "Start sending bidirectional answer.");

        final BidirectionalAnswerReqModel reqModel = new BidirectionalAnswerReqModel(
                messageId,
                answerText);

        HyberRestClient.sendBidirectionalAnswerObservable(reqModel)
                .subscribe(new Action1<Response<Void>>() {
                    @Override
                    public void call(Response<Void> response) {
                        if (response.isSuccessful()) {
                            Hyber.mLog(Hyber.LogLevel.DEBUG, "Request for sending bidirectional answer is success.");
                            listener.onSent(messageId);
                        } else {
                            Hyber.mLog(Hyber.LogLevel.ERROR,
                                    String.format(Locale.getDefault(),
                                            "Response for sending bidirectional answer api request is unsuccessful with code %d!",
                                            response.code()));
                            listener.onSendingError();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Hyber.mLog(Hyber.LogLevel.FATAL, "Error in sending bidirectional answer api request!", throwable);
                        listener.onSendingError();
                    }
                });
    }

    @Override
    public void sendPushDeliveryReport(@NonNull final String messageId, @NonNull Long receivedAt,
                                       @NonNull final SendPushDeliveryReportListener listener) {
        Hyber.mLog(Hyber.LogLevel.DEBUG, "Start sending push delivery report.");

        final PushDeliveryReportReqModel reqModel = new PushDeliveryReportReqModel(messageId, receivedAt);

        HyberRestClient.sendPushDeliveryReportObservable(reqModel)
                .subscribe(new Action1<Response<Void>>() {
                    @Override
                    public void call(Response<Void> response) {
                        if (response.isSuccessful()) {
                            Hyber.mLog(Hyber.LogLevel.DEBUG, "Request for sending push delivery report is success.");
                            listener.onSuccess(messageId);
                        } else {
                            Hyber.mLog(Hyber.LogLevel.ERROR,
                                    String.format(Locale.getDefault(),
                                            "Response for sending push delivery report api request is unsuccessful with code %d!",
                                            response.code()));
                            listener.onFailure();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Hyber.mLog(Hyber.LogLevel.FATAL, "Error in sending push delivery report api request!", throwable);
                        listener.onFailure();
                    }
                });
    }

    @Override
    public void getMessageHistory(@NonNull final Long startDate, @NonNull final MessageHistoryListener listener) {
        Hyber.mLog(Hyber.LogLevel.DEBUG, "Start downloading message history.");

        final MessageHistoryReqModel reqModel = new MessageHistoryReqModel(startDate);

        HyberRestClient.getMessageHistoryObservable(reqModel)
                .subscribe(new Action1<Response<MessageHistoryRespEnvelope>>() {
                    @Override
                    public void call(Response<MessageHistoryRespEnvelope> response) {
                        if (response.isSuccessful()) {
                            Hyber.mLog(Hyber.LogLevel.DEBUG, "Request for downloading message history is success.");
                            Hyber.mLog(Hyber.LogLevel.DEBUG, String.format(Locale.getDefault(), "Downloaded %d messages.",
                                    response.body().getMessages().size()));
                            if (response.body().getMessages() != null && response.body().getMessages().size() > 0) {
                                Hyber.mLog(Hyber.LogLevel.DEBUG, String.format(Locale.getDefault(),
                                        "LimitDays %d\nLimitMessages %d\nTimeLastMessage %d"
                                                + "\nFirst MessageId %s DrTime %d\nLast MessageId %s DrTime %d",
                                        response.body().getLimitDays(),
                                        response.body().getLimitMessages(),
                                        response.body().getTimeLastMessage(),
                                        response.body().getMessages().get(0).getId(),
                                        response.body().getMessages().get(0).getTime(),
                                        response.body().getMessages().get(response.body().getMessages().size() - 1).getId(),
                                        response.body().getMessages().get(response.body().getMessages().size() - 1).getTime()));
                            }
                            listener.onSuccess(startDate, response.body());
                        } else {
                            Hyber.mLog(Hyber.LogLevel.ERROR,
                                    String.format(Locale.getDefault(),
                                            "Response for downloading message history api request is unsuccessful with code %d!",
                                            response.code()));
                            listener.onFailure();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Hyber.mLog(Hyber.LogLevel.FATAL, "Error in downloading message history api request!", throwable);
                        listener.onFailure();
                    }
                });
    }

    private void clearUserDataIfExists(@NonNull Realm realm, @NonNull Long phone) {
        Hyber.mLog(Hyber.LogLevel.DEBUG, "User data cleaning.");
        cleanUserSession();
        RealmResults<User> users = realm.where(User.class)
                .equalTo(User.PHONE, phone)
                .findAll();

        for (User user : users) {
            RealmResults<Message> messages = realm.where(Message.class)
                    .equalTo(Message.ORDER, user.getId())
                    .findAll();
            realm.beginTransaction();
            messages.deleteAllFromRealm();
            user.deleteFromRealm();
            realm.commitTransaction();
        }
        Hyber.mLog(Hyber.LogLevel.DEBUG, "User data is cleaned.");
    }

    private void updateUserSession(@Nullable String token, @Nullable String refreshToken, @Nullable Date expirationDate) {
        Hyber.mLog(Hyber.LogLevel.DEBUG, "User new session data updating.");
        Hawk.Chain chain = Hawk.chain();
        if (token != null) {
            chain.put(Tweakables.HAWK_HYBER_AUTH_TOKEN, token);
        }
        if (refreshToken != null) {
            chain.put(Tweakables.HAWK_HYBER_REFRESH_TOKEN, refreshToken);
        }
        if (expirationDate != null) {
            chain.put(Tweakables.HAWK_HYBER_TOKEN_EXP_DATE, expirationDate);
        }
        chain.commit();
        Hyber.mLog(Hyber.LogLevel.DEBUG, "User session data is updated.");
    }

    private void updateSentPushToken(@NonNull String pushToken) {
        Hyber.mLog(Hyber.LogLevel.DEBUG, "Sent push token updating.");
        Hawk.Chain chain = Hawk.chain();
        chain.put(Tweakables.HAWK_HYBER_SENT_PUSH_TOKEN, pushToken);
        chain.commit();
        Hyber.mLog(Hyber.LogLevel.DEBUG, "Sent push token is updated.");
    }

    private void cleanUserSession() {
        Hyber.mLog(Hyber.LogLevel.DEBUG, "User session data cleaning.");
        Hawk.remove(
                Tweakables.HAWK_HYBER_SENT_PUSH_TOKEN,
                Tweakables.HAWK_HYBER_AUTH_TOKEN,
                Tweakables.HAWK_HYBER_REFRESH_TOKEN,
                Tweakables.HAWK_HYBER_TOKEN_EXP_DATE
        );
        Hyber.mLog(Hyber.LogLevel.DEBUG, "User session data is cleaned.");
    }

    private void removeAuthToken() {
        Hyber.mLog(Hyber.LogLevel.DEBUG, "User auth token removing");
        Hawk.remove(
                Tweakables.HAWK_HYBER_AUTH_TOKEN
        );
        Hyber.mLog(Hyber.LogLevel.DEBUG, "User auth token is removed");
    }

    private boolean isAuthTokenExists() {
        return Hawk.contains(Tweakables.HAWK_HYBER_AUTH_TOKEN);
    }

    private boolean isRefreshTokenExists() {
        return Hawk.contains(Tweakables.HAWK_HYBER_REFRESH_TOKEN);
    }

    private String getRefreshToken() {
        return Hawk.get(Tweakables.HAWK_HYBER_REFRESH_TOKEN);
    }

    interface AuthorizationListener {
        void onAuthorized();

        void onAuthorizationError(AuthErrorStatus status);
    }

    interface SendDeviceDataListener {
        void onSent();

        void onSendingError(SendDeviceDataErrorStatus status);
    }

    interface SendBidirectionalAnswerListener {
        void onSent(@NonNull String messageId);

        void onSendingError();
    }

    interface MessageHistoryListener {
        void onSuccess(@NonNull Long startDate, @NonNull MessageHistoryRespEnvelope envelope);

        void onFailure();
    }

    interface SendPushDeliveryReportListener {
        void onSuccess(@NonNull String messageId);

        void onFailure();
    }
}
