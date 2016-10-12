package com.hyber;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.orhanobut.hawk.Hawk;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

final class HyberApiBusinessModel implements IHyberApiBusinessModel {

    private static final String TAG = "HyberApiBusinessModel";

    private static final int UNAUTHORIZED_CODE = 401;
    private static HyberApiBusinessModel mInstance;
    private WeakReference<Context> mContextWeakReference;

    private HyberApiBusinessModel(@NonNull Context context) {
        this.mContextWeakReference = new WeakReference<>(context);
    }

    static synchronized HyberApiBusinessModel getInstance(@NonNull Context context) {
        if (mInstance == null) {
            mInstance = new HyberApiBusinessModel(context);
        }
        return mInstance;
    }

    @Override
    public void authorize(@NonNull final Long phone, @NonNull final AuthorizationListener listener) {
        HyberLogger.i("Start user registration.");

        RegisterUserReqModel reqModel = new RegisterUserReqModel(phone,
                OsUtils.getDeviceOs(), OsUtils.getAndroidVersion(),
                OsUtils.getDeviceName(), OsUtils.getModelName(),
                OsUtils.getDeviceFormat(mContextWeakReference.get()));

        HyberRestClient.registerUserObservable(reqModel)
                .subscribe(new Action1<Response<RegisterUserRespModel>>() {
                    @Override
                    public void call(Response<RegisterUserRespModel> response) {
                        if (response.isSuccessful()) {
                            HyberLogger.i("Request for user registration is success.");

                            Realm realm = Realm.getDefaultInstance();

                            clearUserDataIfExists(realm, phone);

                            SessionRespItemModel session = response.body().getSession();
                            if (session != null) {
                                HyberLogger.i("User is registered.");
                                HyberLogger.i("User session is provided.");
                                User user = new User(String.valueOf(phone), phone);
                                realm.beginTransaction();
                                realm.copyToRealm(user);
                                realm.commitTransaction();
                                HyberLogger.i("User data saved.");
                                updateUserSession(session.getToken(), session.getRefreshToken(), session.getExpirationDate());
                                listener.onSuccess();
                            } else {
                                if (response.body().getError() != null) {
                                    HyberLogger.w(HyberStatus.byCode(response.body().getError().getCode()));
                                } else {
                                    HyberLogger.tag(TAG);
                                    HyberLogger.wtf("User not registered, session data is not provided!");
                                }
                                listener.onFailure(AuthErrorStatus.USER_SESSION_DATA_IS_NOT_PROVIDED);
                            }
                        } else {
                            responseIsUnsuccessful(response);
                            listener.onFailure(AuthErrorStatus.USER_SESSION_DATA_IS_NOT_PROVIDED);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        HyberLogger.e(throwable, "Error in user registration api request!");
                        listener.onFailure(AuthErrorStatus.USER_SESSION_DATA_IS_NOT_PROVIDED);
                    }
                });
    }

    private void responseIsUnsuccessful(Response response) {
        if (response.code() == 404) {
            HyberLogger.e(HyberStatus.SDK_API_404Error, "url: %s\nresponse code: %d - %s",
                    response.raw().request().url().toString(), response.code(), response.message());
            return;
        } else {
            try {
                BaseResponse errorResp = new Gson().fromJson(response.errorBody().string(), BaseResponse.class);
                if (errorResp != null && errorResp.getError() != null
                        && errorResp.getError().getCode() != null) {
                    HyberLogger.e(HyberStatus.byCode(errorResp.getError().getCode()), "url: %s\nresponse code: %d - %s",
                            response.raw().request().url().toString(), response.code(), response.message());
                    return;
                }
            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
            }
        }

        HyberLogger.e(HyberStatus.SDK_API_ResponseIsUnsuccessful, "url: %s\nresponse code: %d - %s",
                response.raw().request().url().toString(), response.code(), response.message());
    }

    private <T> Observable<Response<T>> tokenActualProcessorObservable(final Observable<Response<T>> currObservable,
                                                                       final Response<T> currResponse) {
        String errorBody = null;
        try {
            if (currResponse.isSuccessful()) {
                return Observable.just(currResponse);
            } else {
                errorBody = currResponse.errorBody().string();
                BaseResponse errorResp = new Gson().fromJson(errorBody, BaseResponse.class);
                if (errorResp != null && errorResp.getError() != null
                        && errorResp.getError().getCode() != null
                        && errorResp.getError().getDescription() != null
                        && errorResp.getError().getCode().intValue()
                        != HyberStatus.SDK_API_mobileAuthTokenExpired.getCode()
                        && errorResp.getError().getCode().intValue()
                        != HyberStatus.SDK_API_notCorrectAuthorizationDataOrTokenExpired.getCode()) {
                    return Observable.just((Response<T>) Response.error(ResponseBody.create(null, errorBody),
                            currResponse.raw()));
                }
            }
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
            if (errorBody == null) {
                return Observable.just(currResponse);
            } else {
                return Observable.just((Response<T>) Response.error(ResponseBody.create(null, errorBody),
                        currResponse.raw()));
            }
        }

        HyberLogger.i("User auth token expired.\nStart refreshing auth token.");
        removeAuthToken();

        RefreshTokenReqModel reqModel = new RefreshTokenReqModel(getRefreshToken());

        final String finalErrorBody = errorBody;
        return HyberRestClient.refreshTokenObservable(reqModel)
                .flatMap(new Func1<Response<RefreshTokenRespModel>, Observable<Response<T>>>() {
                    @Override
                    public Observable<Response<T>> call(Response<RefreshTokenRespModel> response) {
                        if (response.isSuccessful()) {
                            HyberLogger.i("Request for refresh user auth token is success.");
                            SessionRespItemModel session = response.body().getSession();
                            updateUserSession(session.getToken(), session.getRefreshToken(), session.getExpirationDate());
                            HyberLogger.i("Continue execute current observable!");
                            return currObservable;
                        } else {
                            responseIsUnsuccessful(response);
                            if (finalErrorBody == null) {
                                return Observable.just(currResponse);
                            } else {
                                return Observable.just((Response<T>) Response.error(ResponseBody.create(null, finalErrorBody),
                                        currResponse.raw()));
                            }
                        }
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        HyberLogger.e("Error in refresh user auth token api request!", throwable);
                    }
                });
    }

    @Override
    public void sendDeviceData(@NonNull final SendDeviceDataListener listener) {
        HyberLogger.i("Start sending user device data.");

        if (!Hawk.contains(Tweakables.HAWK_HYBER_AUTH_TOKEN)) {
            listener.onFailure();
            return;
        }

        final UpdateUserReqModel reqModel = new UpdateUserReqModel(
                FirebaseInstanceId.getInstance().getToken(),
                OsUtils.getDeviceOs(), OsUtils.getAndroidVersion(),
                OsUtils.getDeviceName(), OsUtils.getModelName(),
                OsUtils.getDeviceFormat(mContextWeakReference.get()));

        HyberRestClient.updateUserObservable(reqModel)
                .flatMap(new Func1<Response<UpdateUserRespModel>, Observable<Response<UpdateUserRespModel>>>() {
                    @Override
                    public Observable<Response<UpdateUserRespModel>> call(Response<UpdateUserRespModel> response) {
                        return tokenActualProcessorObservable(
                                HyberRestClient.updateUserObservable(reqModel), response);
                    }
                })
                .subscribe(new Action1<Response<UpdateUserRespModel>>() {
                    @Override
                    public void call(Response<UpdateUserRespModel> response) {
                        if (response.isSuccessful()) {
                            HyberLogger.i("Request for update user device data is success.");
                            if (response.body().getError() == null) {
                                listener.onSuccess();
                            } else {
                                HyberLogger.i(HyberStatus.byCode(response.body().getError().getCode()),
                                        "Response for update user device data api with error!");
                                listener.onFailure();
                            }
                        } else {
                            responseIsUnsuccessful(response);
                            listener.onFailure();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        HyberLogger.e(throwable, "Error in update user device data api request!");
                        listener.onFailure();
                    }
                });
    }

    @Override
    public void sendBidirectionalAnswer(@NonNull final String messageId, @NonNull String answerText,
                                        @NonNull final SendBidirectionalAnswerListener listener) {
        HyberLogger.i("Start sending bidirectional answer.");

        if (!Hawk.contains(Tweakables.HAWK_HYBER_AUTH_TOKEN)) {
            listener.onFailure();
            return;
        }

        final BidirectionalAnswerReqModel reqModel = new BidirectionalAnswerReqModel(
                messageId,
                answerText);

        HyberRestClient.sendBidirectionalAnswerObservable(reqModel)
                .flatMap(new Func1<Response<Void>, Observable<Response<Void>>>() {
                    @Override
                    public Observable<Response<Void>> call(Response<Void> response) {
                        return tokenActualProcessorObservable(
                                HyberRestClient.sendBidirectionalAnswerObservable(reqModel), response);
                    }
                })
                .subscribe(new Action1<Response<Void>>() {
                    @Override
                    public void call(Response<Void> response) {
                        if (response.isSuccessful()) {
                            HyberLogger.i("Request for sending bidirectional answer is success.");
                            listener.onSuccess(messageId);
                        } else {
                            responseIsUnsuccessful(response);
                            listener.onFailure();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        HyberLogger.e(throwable, "Error in sending bidirectional answer api request!");
                        listener.onFailure();
                    }
                });
    }

    @Override
    public void sendPushDeliveryReport(@NonNull final String messageId, @NonNull Long receivedAt,
                                       @NonNull final SendPushDeliveryReportListener listener) {
        HyberLogger.i("Start sending push delivery report.");

        if (!Hawk.contains(Tweakables.HAWK_HYBER_AUTH_TOKEN)) {
            listener.onFailure();
            return;
        }

        final PushDeliveryReportReqModel reqModel = new PushDeliveryReportReqModel(messageId, receivedAt);

        HyberRestClient.sendPushDeliveryReportObservable(reqModel)
                .flatMap(new Func1<Response<Void>, Observable<Response<Void>>>() {
                    @Override
                    public Observable<Response<Void>> call(Response<Void> response) {
                        return tokenActualProcessorObservable(
                                HyberRestClient.sendPushDeliveryReportObservable(reqModel), response);
                    }
                })
                .subscribe(new Action1<Response<Void>>() {
                    @Override
                    public void call(Response<Void> response) {
                        if (response.isSuccessful()) {
                            HyberLogger.i("Request for sending push delivery report is success.");
                            listener.onSuccess(messageId);
                        } else {
                            responseIsUnsuccessful(response);
                            listener.onFailure();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        HyberLogger.e(throwable, "Error in sending push delivery report api request!");
                        listener.onFailure();
                    }
                });
    }

    @Override
    public void getMessageHistory(@NonNull final Long startDate, @NonNull final MessageHistoryListener listener) {
        HyberLogger.i("Start downloading message history.");

        if (!Hawk.contains(Tweakables.HAWK_HYBER_AUTH_TOKEN)) {
            listener.onFailure();
            return;
        }

        final MessageHistoryReqModel reqModel = new MessageHistoryReqModel(startDate);

        HyberRestClient.getMessageHistoryObservable(reqModel)
                .flatMap(new Func1<Response<MessageHistoryRespEnvelope>, Observable<Response<MessageHistoryRespEnvelope>>>() {
                    @Override
                    public Observable<Response<MessageHistoryRespEnvelope>> call(Response<MessageHistoryRespEnvelope> response) {
                        return tokenActualProcessorObservable(
                                HyberRestClient.getMessageHistoryObservable(reqModel), response);
                    }
                })
                .subscribe(new Action1<Response<MessageHistoryRespEnvelope>>() {
                    @Override
                    public void call(Response<MessageHistoryRespEnvelope> response) {
                        if (response.isSuccessful()) {
                            HyberLogger.i("Request for downloading message history is success.");
                            HyberLogger.i("Downloaded %d messages.", response.body().getMessages().size());

                            if (response.body().getMessages() != null && response.body().getMessages().size() > 0) {
                                HyberLogger.d("LimitDays %d\nLimitMessages %d\nTimeLastMessage %d"
                                                + "\nFirst MessageId %s DrTime %d\nLast MessageId %s DrTime %d",
                                        response.body().getLimitDays(),
                                        response.body().getLimitMessages(),
                                        response.body().getTimeLastMessage(),
                                        response.body().getMessages().get(0).getId(),
                                        response.body().getMessages().get(0).getTime(),
                                        response.body().getMessages().get(response.body().getMessages().size() - 1).getId(),
                                        response.body().getMessages().get(response.body().getMessages().size() - 1).getTime());
                            }
                            listener.onSuccess(startDate, response.body());
                        } else {
                            responseIsUnsuccessful(response);
                            listener.onFailure();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        HyberLogger.e(throwable, "Error in downloading message history api request!");
                        listener.onFailure();
                    }
                });
    }

    private void clearUserDataIfExists(@NonNull Realm realm, @NonNull Long phone) {
        HyberLogger.i("User data cleaning.");
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
        HyberLogger.i("User data is cleaned.");
    }

    private void updateUserSession(@Nullable String token, @Nullable String refreshToken, @Nullable Date expirationDate) {
        HyberLogger.i("User new session data updating.");
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
        HyberLogger.i("User session data is updated.");
    }

    private void updateSentPushToken(@NonNull String pushToken) {
        HyberLogger.i("Sent push token updating.");
        Hawk.Chain chain = Hawk.chain();
        chain.put(Tweakables.HAWK_HYBER_SENT_PUSH_TOKEN, pushToken);
        chain.commit();
        HyberLogger.i("Sent push token is updated.");
    }

    private void cleanUserSession() {
        HyberLogger.i("User session data cleaning.");
        Hawk.remove(
                Tweakables.HAWK_HYBER_SENT_PUSH_TOKEN,
                Tweakables.HAWK_HYBER_AUTH_TOKEN,
                Tweakables.HAWK_HYBER_REFRESH_TOKEN,
                Tweakables.HAWK_HYBER_TOKEN_EXP_DATE
        );
        HyberLogger.i("User session data is cleaned.");
    }

    private void removeAuthToken() {
        HyberLogger.i("User auth token removing");
        Hawk.remove(
                Tweakables.HAWK_HYBER_AUTH_TOKEN
        );
        HyberLogger.i("User auth token is removed");
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
        void onSuccess();

        void onFailure(AuthErrorStatus status);
    }

    interface SendDeviceDataListener {
        void onSuccess();

        void onFailure();
    }

    interface SendBidirectionalAnswerListener {
        void onSuccess(@NonNull String messageId);

        void onFailure();
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
