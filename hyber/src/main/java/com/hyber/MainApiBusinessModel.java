package com.hyber;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.iid.FirebaseInstanceId;
import com.orhanobut.hawk.Hawk;

import java.lang.ref.WeakReference;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Response;
import rx.functions.Action1;

class MainApiBusinessModel implements IAuthorizationModel {

    private static MainApiBusinessModel mInstance;
    private WeakReference<Context> mContextWeakReference;

    static synchronized MainApiBusinessModel getInstance(@NonNull Context context) {
        if (mInstance == null) {
            mInstance = new MainApiBusinessModel(context);
        }
        return mInstance;
    }

    private MainApiBusinessModel(@NonNull Context context) {
        this.mContextWeakReference = new WeakReference<>(context);
    }

    @Override
    public void authorize(@NonNull final Long phone, @NonNull final AuthorizationListener listener) {
        Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "Start user registration.");

        RegisterUserReqModel reqModel = new RegisterUserReqModel(phone,
                OsUtils.getDeviceOs(), OsUtils.getAndroidVersion(),
                OsUtils.getDeviceName(), OsUtils.getModelName(),
                OsUtils.getDeviceFormat(mContextWeakReference.get()));

        HyberRestClient.registerUserObservable(reqModel)
                .subscribe(new Action1<Response<RegisterUserRespModel>>() {
                    @Override
                    public void call(Response<RegisterUserRespModel> response) {
                        if (response.isSuccessful()) {
                            Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "Request for user registration is success.");
                            Realm realm = Realm.getDefaultInstance();

                            clearUserDataIfExists(realm, phone);

                            SessionRespItemModel session = response.body().getSession();
                            if (session != null) {
                                Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "New user session is provided.");
                                HyberUser user = new HyberUser(String.valueOf(phone), phone);
                                realm.beginTransaction();
                                realm.copyToRealm(user);
                                realm.commitTransaction();
                                Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "New user data saved.");

                                updateUserSession(session.getToken(), session.getRefreshToken(), session.getExpirationDate());
                                Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "User is registered.");
                                listener.onAuthorized();
                            } else {
                                Hyber.Log(Hyber.LOG_LEVEL.ERROR, "User not registered, session data is not provided!");
                                listener.onAuthorizationError(AuthErrorStatus.USER_SESSION_DATA_IS_NOT_PROVIDED);
                            }
                        } else {
                            Hyber.Log(Hyber.LOG_LEVEL.ERROR, "Response for user registration api is unsuccessful!");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Hyber.Log(Hyber.LOG_LEVEL.FATAL, "Error in user registration api request!", throwable);
                    }
                });
    }

    @Override
    public void sendPushToken(@NonNull final SendPushTokenListener listener) {
        Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "Start sending user push token.");

        final String pushToken = FirebaseInstanceId.getInstance().getToken();
        if (pushToken == null) {
            return;
        }

        UpdateUserReqModel reqModel = new UpdateUserReqModel(pushToken);
        HyberRestClient.updateUserObservable(reqModel)
                .subscribe(new Action1<Response<UpdateUserRespModel>>() {
                    @Override
                    public void call(Response<UpdateUserRespModel> response) {
                        if (response.isSuccessful()) {
                            Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "Request for update user push token is success.");
                            if (response.body().getError() == null) {
                                updateSentPushToken(pushToken);
                                listener.onSent();
                            } else {
                                Hyber.Log(Hyber.LOG_LEVEL.ERROR, "Response for update user push token api with error!\n" +
                                        response.body().getError().getDescription());
                                listener.onSendingError(SendPushTokenErrorStatus.SENDING_UNSUCCESSFUL);
                            }
                        } else {
                            Hyber.Log(Hyber.LOG_LEVEL.ERROR, "Response for update user push token api request is unsuccessful!");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Hyber.Log(Hyber.LOG_LEVEL.FATAL, "Error in update user push token api request!", throwable);
                    }
                });
    }

    @Override
    public void sendDeviceData(@NonNull final SendDeviceDataListener listener) {
        Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "Start sending user device data.");

        UpdateUserReqModel reqModel = new UpdateUserReqModel(
                OsUtils.getDeviceOs(), OsUtils.getAndroidVersion(),
                OsUtils.getDeviceName(), OsUtils.getModelName(),
                OsUtils.getDeviceFormat(mContextWeakReference.get()));

        HyberRestClient.updateUserObservable(reqModel)
                .subscribe(new Action1<Response<UpdateUserRespModel>>() {
                    @Override
                    public void call(Response<UpdateUserRespModel> response) {
                        if (response.isSuccessful()) {
                            Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "Request for update user device data is success.");
                            if (response.body().getError() == null) {
                                listener.onSent();
                            } else {
                                Hyber.Log(Hyber.LOG_LEVEL.ERROR, "Response for update user device data api with error!\n" +
                                        response.body().getError().getDescription());
                                listener.onSendingError(SendDeviceDataErrorStatus.SENDING_UNSUCCESSFUL);
                            }
                        } else {
                            Hyber.Log(Hyber.LOG_LEVEL.ERROR, "Response for update user device data api request is unsuccessful!");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Hyber.Log(Hyber.LOG_LEVEL.FATAL, "Error in update user device data api request!", throwable);
                    }
                });
    }

    private void clearUserDataIfExists(@NonNull Realm realm, @NonNull Long phone) {
        Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "User data start cleaning.");
        cleanUserSession();
        RealmResults<HyberUser> users = realm.where(HyberUser.class)
                .equalTo(HyberUser.PHONE, phone)
                .findAll();

        for (HyberUser user : users) {
            RealmResults<Message> messages = realm.where(Message.class)
                    .equalTo(Message.ORDER, user.getId())
                    .findAll();
            realm.beginTransaction();
            messages.deleteAllFromRealm();
            user.deleteFromRealm();
            realm.commitTransaction();
        }
        Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "User data is cleaned.");
    }

    private void updateUserSession(@Nullable String token, @Nullable String refreshToken, @Nullable Date expirationDate) {
        Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "User new session data start updating.");
        Hawk.Chain chain = Hawk.chain();
        if (token != null) {
            chain.put(Tweakables.HAWK_HyberAuthToken, token);
        }
        if (refreshToken != null) {
            chain.put(Tweakables.HAWK_HyberRefreshToken, refreshToken);
        }
        if (expirationDate != null) {
            chain.put(Tweakables.HAWK_HyberTokenExpDate, expirationDate);
        }
        chain.commit();
        Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "User session data is updated.");
    }

    private void updateSentPushToken(@NonNull String pushToken) {
        Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "Sent push token start updating.");
        Hawk.Chain chain = Hawk.chain();
        chain.put(Tweakables.HAWK_HyberSentPushToken, pushToken);
        chain.commit();
        Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "Sent push token is updated.");
    }

    private void cleanUserSession() {
        Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "User session data start cleaning.");
        Hawk.remove(
                Tweakables.HAWK_HyberSentPushToken,
                Tweakables.HAWK_HyberAuthToken,
                Tweakables.HAWK_HyberRefreshToken,
                Tweakables.HAWK_HyberTokenExpDate
        );
        Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "User session data is cleaned.");
    }

    interface AuthorizationListener {
        void onAuthorized();

        void onAuthorizationError(AuthErrorStatus status);
    }

    interface SendPushTokenListener {
        void onSent();

        void onSendingError(SendPushTokenErrorStatus status);
    }

    interface SendDeviceDataListener {
        void onSent();

        void onSendingError(SendDeviceDataErrorStatus status);
    }

}
