package com.hyber;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.hyber.handler.HyberError;
import com.hyber.log.HyberLogger;
import com.hyber.model.Device;
import com.hyber.model.Message;
import com.hyber.model.User;
import com.hyber.security.HashGeneratorUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import retrofit2.Response;
import rx.functions.Action1;

final class ApiBusinessModel implements IApiBusinessModel {

    private static final String TAG = "ApiBusinessModel";
    static final String X_HYBER_CLIENT_API_KEY = "X-Hyber-Client-API-Key";
    static final String X_HYBER_APP_FINGERPRINT = "X-Hyber-App-Fingerprint";
    static final String X_HYBER_SESSION_ID = "X-Hyber-Session-Id";
    static final String X_HYBER_AUTH_TOKEN = "X-Hyber-Auth-Token";
    static final String X_HYBER_TIMESTAMP = "X-Hyber-Timestamp";

    private static ApiBusinessModel mInstance;
    private Context mContextReference;

    private ApiBusinessModel(@NonNull Context context) {
        this.mContextReference = context;
    }

    static synchronized ApiBusinessModel getInstance(@NonNull Context context) {
        if (mInstance == null) {
            mInstance = new ApiBusinessModel(context);
        }
        return mInstance;
    }

    private HyberError responseIsUnsuccessful(Response response) {
        try {
            String contentType = response.headers().get("Content-Type");
            if (contentType != null && contentType.contains("application/json")) {
                BaseResponse errorResp = new Gson().fromJson(response.errorBody().string(), BaseResponse.class);
                if (errorResp != null && errorResp.getError() != null && errorResp.getError().getCode() != null) {
                    ErrorStatus es = ErrorStatus.byCode(errorResp.getError().getCode());
                    HyberLogger.d("Url: %s\nResponse code: %d\nResponse error body: %s\nHyber error: %d ==> %s",
                            response.raw().request().url().toString(), response.code(), response.errorBody(),
                            es.getCode(), es.getDescription());
                    if (es.getCode() >= ErrorStatus.mobileIncorrectPhoneOrPassword.getCode()
                            && es.getCode() <= ErrorStatus.mobileIncorrectAccessToken.getCode()) {
                        return new HyberError(HyberError.HyberErrorStatus.UNAUTHORIZED);
                    } else if (es.getCode() >= ErrorStatus.mobileIncorrectHeadersFormat.getCode()
                            && es.getCode() <= ErrorStatus.mobileIncorrectIosBundleId.getCode()) {
                        return new HyberError(HyberError.HyberErrorStatus.SDK_CONFIGURED_INCORRECTLY);
                    }
                } else {
                    HyberLogger.e("Url: %s\nResponse code: %d\nResponse in json format: %s",
                            response.raw().request().url().toString(), response.code(), response.errorBody().string());
                }
            } else if (contentType != null && contentType.contains("text/html")) {
                HyberLogger.e("Url: %s\nResponse code: %d\nResponse in html format: %s",
                        response.raw().request().url().toString(), response.code(), Html.fromHtml(response.errorBody().string()));
            } else {
                HyberLogger.e("Url: %s\nResponse code: %d\nResponse in undefined format: %s",
                        response.raw().request().url().toString(), response.code(), response.errorBody().string());
            }
        } catch (IOException e) {
            HyberLogger.e(e, "Url: %s\nResponse code: %d\nResponse error body: %s",
                    response.raw().request().url().toString(), response.code(), response.errorBody());
        }
        return new HyberError(HyberError.HyberErrorStatus.API_ERROR);
    }

    private Map<String, String> generateAuthorizedHeaders() {
        Map<String, String> headers = new HashMap<>();
        Repository repo = new Repository();
        repo.open();
        User user = repo.getCurrentUser();
        long timestamp = System.currentTimeMillis();
        try {
            headers.put(X_HYBER_SESSION_ID, user.getSessionId());
        } catch (Exception e) {
            HyberLogger.e(e);
        }
        try {
            headers.put(X_HYBER_AUTH_TOKEN, HashGeneratorUtils.generateSHA256(user.getAuthToken() + ":" + timestamp));
        } catch (Exception e) {
            HyberLogger.e(e);
        }
        headers.put(X_HYBER_TIMESTAMP, String.valueOf(timestamp));
        repo.close();
        return headers;
    }

    @Override
    public void authorize(@NonNull final String phone, @NonNull final String password, @NonNull final AuthorizationListener listener) {
        HyberLogger.i("Start user registration.");
        if ((System.currentTimeMillis() - Hyber.lastAuthorizeTime) < TimeUnit.SECONDS.toMillis(1)) {
            listener.onFailure(new HyberError(HyberError.HyberErrorStatus.TOO_MANY_REQUESTS, "Authorize request has limit to 1 req/sec"));
            return;
        } else {
            Hyber.lastAuthorizeTime = System.currentTimeMillis();
        }

        final String sessionId = Utils.getRandomUuid();

        DeviceRegistrationReqModel reqModel = new DeviceRegistrationReqModel(phone, password,
                Utils.getOsType(), Utils.getOsVersion(),
                Utils.getDeviceType(mContextReference),
                Utils.getFullDeviceName(), HyberSdkVersion.BUILD);

        Map<String, String> headers = new HashMap<>();
        headers.put(X_HYBER_CLIENT_API_KEY, Hyber.getClientApiKey());
        headers.put(X_HYBER_APP_FINGERPRINT, Hyber.getFingerprint());
        headers.put(X_HYBER_SESSION_ID, sessionId);

        HyberRestClient.registerUserObservable(headers, reqModel)
                .subscribe(new Action1<Response<DeviceRegistrationRespModel>>() {
                    @Override
                    public void call(Response<DeviceRegistrationRespModel> response) {
                        if (response.isSuccessful()) {
                            HyberLogger.i("Request for user registration is success.");

                            Repository repo = new Repository();
                            repo.open();

                            if (repo.getCurrentUser() != null)
                                repo.clearUserData(repo.getCurrentUser());

                            SessionRespItemModel sessionModel = response.body().getSession();
                            ProfileRespItemModel profileModel = response.body().getProfile();
                            if (sessionModel != null && profileModel != null) {
                                HyberLogger.i("User authToken and profile is provided.");
                                User user = new User(profileModel.getUserId(), profileModel.getUserPhone(), profileModel.getCreatedAt(), sessionModel.getToken(), sessionId);
                                repo.saveNewUser(user);
                                HyberLogger.i("User data saved.");
                                listener.onSuccess();
                            } else {
                                if (response.body().getError() != null) {
                                    HyberLogger.w("%d ==> %s",
                                            ErrorStatus.byCode(response.body().getError().getCode()).getCode(),
                                            ErrorStatus.byCode(response.body().getError().getCode()).getDescription());
                                } else {
                                    HyberLogger.tag(TAG);
                                    HyberLogger.wtf("User not registered, session data is not provided!");
                                }
                                listener.onFailure(new HyberError(HyberError.HyberErrorStatus.UNAUTHORIZED));
                            }

                            repo.close();
                        } else {
                            listener.onFailure(responseIsUnsuccessful(response));
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        HyberError hyberError = new HyberError(HyberError.HyberErrorStatus.INTERNAL_ERROR, "Error in user registration api request!");
                        HyberLogger.e(throwable, hyberError.toString());
                        listener.onFailure(hyberError);
                    }
                });
    }

    @Override
    public void sendDeviceData(@NonNull final SendDeviceDataListener listener) {
        User user;
        String token = FirebaseInstanceId.getInstance().getToken();
        Repository repo = new Repository();
        repo.open();
        user = repo.getCurrentUser();
        if (user == null || token == null) {
            listener.onFailure(new HyberError(HyberError.HyberErrorStatus.UNAUTHORIZED, "User not authorized"));
            repo.close();
            return;
        } else {
            repo.updateFcmToken(user, token);
        }

        HyberLogger.i("Start sending user device data.");

        DeviceUpdateReqModel reqModel = new DeviceUpdateReqModel(user.getFcmToken(),
                Utils.getOsType(), Utils.getOsVersion(),
                Utils.getDeviceType(mContextReference),
                Utils.getFullDeviceName(), HyberSdkVersion.BUILD);

        repo.close();
        HyberRestClient.updateUserObservable(generateAuthorizedHeaders(), reqModel)
                .subscribe(new Action1<Response<DeviceUpdateRespModel>>() {
                    @Override
                    public void call(final Response<DeviceUpdateRespModel> response) {
                        if (response.isSuccessful()) {
                            HyberLogger.i("Request for update user device data is success.");
                            if (response.body().getError() == null) {
                                final Repository repo = new Repository();
                                repo.open();
                                repo.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        User user1 = repo.getCurrentUser();
                                        Device device = repo.getDevices(user1).where().equalTo(Device.IS_CURRENT, true).findFirst();
                                        if (device == null) {
                                            device = new Device(response.body().getDeviceId(), user1,
                                                    Utils.getOsType(), Utils.getOsVersion(),
                                                    Utils.getDeviceType(mContextReference),
                                                    Utils.getFullDeviceName(), HyberSdkVersion.BUILD,
                                                    new Date(), new Date(), true);
                                            realm.copyToRealmOrUpdate(device);
                                        }
                                    }
                                });
                                repo.close();
                                listener.onSuccess();
                            } else {
                                HyberLogger.i("Response for update user device data api with error\n%d ==> %s!",
                                        ErrorStatus.byCode(response.body().getError().getCode()).getCode(),
                                        ErrorStatus.byCode(response.body().getError().getCode()).getDescription());
                                listener.onFailure(new HyberError(HyberError.HyberErrorStatus.API_ERROR,
                                        String.format(Locale.getDefault(),
                                                "Response for update user device data api with error\n%d ==> %s!",
                                                ErrorStatus.byCode(response.body().getError().getCode()).getCode(),
                                                ErrorStatus.byCode(response.body().getError().getCode()).getDescription())));
                            }
                        } else {
                            listener.onFailure(responseIsUnsuccessful(response));
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        HyberLogger.e(throwable, "Error in update user device data api request!");
                        listener.onFailure(new HyberError(HyberError.HyberErrorStatus.INTERNAL_ERROR, "Error in update user device data api request!"));
                    }
                });
    }

    @Override
    public void getAllDevices(@NonNull final AllDevicesListener listener) {
        Repository repo = new Repository();
        repo.open();
        if (repo.getCurrentUser() == null) {
            listener.onFailure(new HyberError(HyberError.HyberErrorStatus.UNAUTHORIZED));
            repo.close();
            return;
        } else {
            repo.close();
        }
        HyberLogger.i("Start downloading all devices.");
        HyberRestClient.getAllDevicesObservable(generateAuthorizedHeaders())
                .subscribe(new Action1<Response<DevicesRespEnvelope>>() {
                    @Override
                    public void call(final Response<DevicesRespEnvelope> response) {
                        if (response.isSuccessful()) {
                            if (!response.body().getDevices().isEmpty()) {
                                HyberLogger.i("Request for downloading all devices is success.");
                                HyberLogger.i("Downloaded %d devices.", response.body().getDevices().size());

                                Repository repo = new Repository();
                                repo.open();
                                final User user = repo.getCurrentUser();
                                if (user != null)
                                    repo.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            realm.where(Device.class)
                                                    .equalTo(Device.USER_ID, user.getId())
                                                    .equalTo(Device.IS_CURRENT, false)
                                                    .findAll().deleteAllFromRealm();
                                            for (DeviceItemRespModel item : response.body().getDevices()) {
                                                Device device = realm.where(Device.class)
                                                        .equalTo(Device.ID, item.getId())
                                                        .findFirst();

                                                boolean isCurrent = false;
                                                if (device != null) {
                                                    isCurrent = device.getIsCurrent();
                                                }

                                                Device.DeviceBuilder db = Device.builder();
                                                db.id(item.getId());
                                                db.user(user);
                                                db.osType(item.getOsType());
                                                db.osVersion(item.getOsVersion());
                                                db.deviceType(item.getDeviceType());
                                                db.deviceName(item.getDeviceName());
                                                db.sdkVersion(item.getSdkVersion());
                                                db.createdAt(item.getCreatedAt());
                                                db.updatedAt(item.getUpdatedAt());
                                                db.isCurrent(isCurrent);

                                                realm.copyToRealmOrUpdate(db.build());
                                            }
                                        }
                                    });
                                repo.close();

                                listener.onSuccess();
                            }
                        } else {
                            listener.onFailure(responseIsUnsuccessful(response));
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        HyberError hyberError = new HyberError(HyberError.HyberErrorStatus.INTERNAL_ERROR, "Error in downloading all devices api request!");
                        HyberLogger.e(throwable, hyberError.toString());
                        listener.onFailure(hyberError);
                    }
                });
    }

    @Override
    public void revokeDevices(@NonNull final List<String> deviceIds, @NonNull final RevokeDevicesListener listener) {
        Repository repo = new Repository();
        repo.open();
        if (repo.getCurrentUser() == null) {
            listener.onFailure(new HyberError(HyberError.HyberErrorStatus.UNAUTHORIZED));
            repo.close();
            return;
        }
        repo.close();
        HyberLogger.i("Start revoke devices.");
        RevokeDevicesReqModel reqModel = new RevokeDevicesReqModel(deviceIds);
        HyberRestClient.revokeDevicesObservable(generateAuthorizedHeaders(), reqModel)
                .subscribe(new Action1<Response<Void>>() {
                    @Override
                    public void call(Response<Void> response) {
                        if (response.isSuccessful()) {
                            HyberLogger.i("Request for revoke devices is success.");
                            final Repository repo = new Repository();
                            repo.open();
                            repo.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    User user = repo.getCurrentUser();
                                    if (user != null) {
                                        realm.where(Device.class)
                                                .equalTo(Device.USER_ID, user.getId())
                                                .in(Device.ID, deviceIds.toArray(new String[0]))
                                                .findAll()
                                                .deleteAllFromRealm();
                                    }
                                }
                            });
                            repo.close();
                            listener.onSuccess();
                        } else {
                            listener.onFailure(responseIsUnsuccessful(response));
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        HyberError hyberError = new HyberError(HyberError.HyberErrorStatus.INTERNAL_ERROR, "Error in revoke devices api request!");
                        HyberLogger.e(throwable, hyberError.toString());
                        listener.onFailure(hyberError);
                    }
                });
    }

    @Override
    public void getMessageHistory(@NonNull final Long startDate, @NonNull final MessageHistoryListener listener) {
        Repository repo = new Repository();
        repo.open();
        if (repo.getCurrentUser() == null) {
            listener.onFailure(new HyberError(HyberError.HyberErrorStatus.UNAUTHORIZED));
            repo.close();
            return;
        } else {
            repo.close();
        }
        HyberLogger.i("Start downloading message history.");
        HyberRestClient.getMessageHistoryObservable(generateAuthorizedHeaders(), startDate)
                .subscribe(new Action1<Response<MessageHistoryRespEnvelope>>() {
                    @Override
                    public void call(Response<MessageHistoryRespEnvelope> response) {
                        if (response.isSuccessful()) {
                            HyberLogger.i("Request for downloading message history is success.");

                            if (response.body().getMessages() != null && response.body().getMessages().size() > 0) {
                                HyberLogger.i("Downloaded %d messages.", response.body().getMessages().size());
                                HyberLogger.d("LimitDays %d\nLimitMessages %d\nTimeLastMessage %d"
                                                + "\nFirst MessageId %s DrTime %s\nLast MessageId %s DrTime %s",
                                        response.body().getLimitDays(),
                                        response.body().getLimitMessages(),
                                        response.body().getLastTime(),
                                        response.body().getMessages().get(0).getMessageId(),
                                        response.body().getMessages().get(0).getTime(),
                                        response.body().getMessages().get(response.body().getMessages().size() - 1).getMessageId(),
                                        response.body().getMessages().get(response.body().getMessages().size() - 1).getTime());

                                Repository repo = new Repository();
                                repo.open();

                                User user = repo.getCurrentUser();
                                if (user == null) {
                                    listener.onFailure(new HyberError(HyberError.HyberErrorStatus.UNAUTHORIZED));
                                    return;
                                }

                                List<Message> messages = new ArrayList<>();

                                for (MessageRespModel messageModel : response.body().getMessages()) {

                                    boolean isRead = false;
                                    boolean isReported = true;
                                    Message message = repo.getMessageById(user, messageModel.getMessageId());
                                    if (message != null) {
                                        isRead = message.getIsRead();
                                        isReported = message.getIsReported();
                                    }

                                    Message.MessageBuilder mb = Message.builder();

                                    mb.id(messageModel.getMessageId());
                                    mb.user(user);
                                    mb.partner(messageModel.getPartner());
                                    mb.title(messageModel.getTitle());
                                    mb.body(messageModel.getBody());
                                    mb.date(messageModel.getTime());
                                    mb.imageUrl(messageModel.getImage() != null ? messageModel.getImage().getUrl() : null);
                                    mb.buttonUrl(messageModel.getButton() != null ? messageModel.getButton().getUrl() : null);
                                    mb.buttonText(messageModel.getButton() != null ? messageModel.getButton().getText() : null);
                                    mb.isRead(isRead);
                                    mb.isReported(isReported);

                                    messages.add(mb.build());
                                }
                                repo.saveMessagesOrUpdate(user, messages);
                                repo.close();
                            }

                            listener.onSuccess(response.body().getLastTime());
                        } else {
                            listener.onFailure(responseIsUnsuccessful(response));
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        HyberError hyberError = new HyberError(HyberError.HyberErrorStatus.INTERNAL_ERROR, "Error in downloading message history api request!");
                        HyberLogger.e(throwable, hyberError.toString());
                        listener.onFailure(hyberError);
                    }
                });
    }

    @Override
    public void sendMessageDeliveryReport(@NonNull final String messageId, @NonNull Date receivedAt,
                                          @NonNull final SendMessageDeliveryReportListener listener) {
        Repository repo = new Repository();
        repo.open();
        if (repo.getCurrentUser() == null) {
            listener.onFailure();
            repo.close();
            return;
        } else {
            repo.close();
        }
        HyberLogger.i("Start sending push delivery report.");
        MessageDeliveryReportReqModel reqModel = new MessageDeliveryReportReqModel(messageId, receivedAt);
        HyberRestClient.sendMessageDeliveryReportObservable(generateAuthorizedHeaders(), reqModel)
                .subscribe(new Action1<Response<MessageDeliveryReportRespModel>>() {
                    @Override
                    public void call(Response<MessageDeliveryReportRespModel> response) {
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
    public void sendMessageCallback(@NonNull final String messageId, @NonNull String answerText,
                                    @NonNull final SendMessageCallbackListener listener) {
        Repository repo = new Repository();
        repo.open();
        if (repo.getCurrentUser() == null) {
            listener.onFailure(new HyberError(HyberError.HyberErrorStatus.UNAUTHORIZED));
            repo.close();
            return;
        } else {
            repo.close();
        }
        HyberLogger.i("Start sending bidirectional answer.");
        MessageCallbackReqModel reqModel = new MessageCallbackReqModel(messageId, answerText);
        HyberRestClient.sendMessageCallbackObservable(generateAuthorizedHeaders(), reqModel)
                .subscribe(new Action1<Response<MessageCallbackRespModel>>() {
                    @Override
                    public void call(Response<MessageCallbackRespModel> response) {
                        if (response.isSuccessful()) {
                            HyberLogger.i("Request for sending bidirectional answer is success.");
                            listener.onSuccess(messageId);
                        } else {
                            listener.onFailure(responseIsUnsuccessful(response));
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        HyberError hyberError = new HyberError(HyberError.HyberErrorStatus.INTERNAL_ERROR, "Error in sending bidirectional answer api request!");
                        HyberLogger.e(throwable, hyberError.toString());
                        listener.onFailure(hyberError);
                    }
                });
    }

    interface AuthorizationListener {
        void onSuccess();

        void onFailure(@lombok.NonNull HyberError status);
    }

    interface SendMessageCallbackListener {
        void onSuccess(@lombok.NonNull String messageId);

        void onFailure(@lombok.NonNull HyberError status);
    }

    interface AllDevicesListener {
        void onSuccess();

        void onFailure(@lombok.NonNull HyberError status);
    }

    interface RevokeDevicesListener {
        void onSuccess();

        void onFailure(@lombok.NonNull HyberError status);
    }

    interface MessageHistoryListener {
        void onSuccess(@NonNull Long startDate);

        void onFailure(@lombok.NonNull HyberError status);
    }

    interface SendDeviceDataListener {
        void onSuccess();

        void onFailure(@lombok.NonNull HyberError status);
    }

    interface SendMessageDeliveryReportListener {
        void onSuccess(@NonNull String messageId);

        void onFailure();
    }
}
