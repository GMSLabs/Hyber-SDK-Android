package com.hyber;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.hawk.Hawk;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

class HyberRestClient {

    private static HyberApiService hyberApiService;

    static {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                //.addInterceptor(new StethoInterceptor())
                .addInterceptor(new HyberInterceptor())
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(Tweakables.STANDARD_ADI_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(Tweakables.STANDARD_ADI_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(Tweakables.STANDARD_ADI_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .setDateFormat(Tweakables.API_DATE_FORMAT)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Tweakables.BASE_API_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        hyberApiService = retrofit.create(HyberApiService.class);
    }

    static void registerDevice(@NonNull Long phone,
                               @NonNull String deviceOs, @NonNull String androidVersion,
                               @NonNull String deviceName, @NonNull String modelName,
                               @NonNull String deviceType, @NonNull final UserRegisterHandler handler) {

        Hawk.remove(Tweakables.HAWK_HyberAuthToken);

        registerDeviceObservable(new RegisterDeviceReqModel(phone, deviceOs, androidVersion, deviceName, modelName, deviceType))
                .subscribe(new Action1<Response<RegisterDeviceRespModel>>() {
                    @Override
                    public void call(Response<RegisterDeviceRespModel> response) {
                        Hyber.Log(Hyber.LOG_LEVEL.DEBUG, String.format(Locale.getDefault(),
                                "Success - Registration complete.\nData:%s",
                                response.message()));
                        if (response.isSuccessful()) {
                            SessionRespItemModel session = response.body().getSession();
                            if (session != null) {
                                if (session.getToken() != null) {
                                    Hawk.put(Tweakables.HAWK_HyberAuthToken, session.getToken());
                                }
                                if (session.getRefreshToken() != null) {
                                    Hawk.put(Tweakables.HAWK_HyberRefreshToken, session.getRefreshToken());
                                }
                                if (session.getExpirationDate() != null) {
                                    Hawk.put(Tweakables.HAWK_HyberTokenExpDate, session.getExpirationDate());
                                }
                            }
                            handler.onSuccess();
                        } else {
                            String errorBody = null;
                            Throwable throwable = null;
                            try {
                                errorBody = response.errorBody().string();
                            } catch (IOException e) {
                                throwable = e;
                            }
                            handler.onFailure(response.code(), errorBody, throwable);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Hyber.Log(Hyber.LOG_LEVEL.WARN, String.format(Locale.getDefault(),
                                "Failure: Registration unsuccessful.\nError:%s",
                                throwable.toString()));
                        handler.onThrowable(throwable);
                    }
                });
    }

    static void refreshToken(@NonNull String refreshToken, @NonNull final RefreshTokenHandler handler) {
        refreshTokenObservable(new RefreshTokenReqModel(refreshToken))
                .subscribe(new Action1<Response<RefreshTokenRespModel>>() {
                    @Override
                    public void call(Response<RefreshTokenRespModel> response) {
                        Hyber.Log(Hyber.LOG_LEVEL.DEBUG, String.format(Locale.getDefault(),
                                "Success - Refresh token complete.\nData:%s",
                                response.message()));
                        if (response.isSuccessful()) {
                            SessionRespItemModel session = response.body().getSession();
                            if (session != null) {
                                if (session.getToken() != null) {
                                    Hawk.put(Tweakables.HAWK_HyberAuthToken, session.getToken());
                                }
                                if (session.getRefreshToken() != null) {
                                    Hawk.put(Tweakables.HAWK_HyberRefreshToken, session.getRefreshToken());
                                }
                                if (session.getExpirationDate() != null) {
                                    Hawk.put(Tweakables.HAWK_HyberTokenExpDate, session.getExpirationDate());
                                }
                            }
                            handler.onSuccess();
                        } else {
                            String errorBody = null;
                            Throwable throwable = null;
                            try {
                                errorBody = response.errorBody().string();
                            } catch (IOException e) {
                                throwable = e;
                            }
                            handler.onFailure(response.code(), errorBody, throwable);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Hyber.Log(Hyber.LOG_LEVEL.WARN, String.format(Locale.getDefault(),
                                "Failure: Refresh token unsuccessful.\nError:%s",
                                throwable.toString()));
                        handler.onThrowable(throwable);
                    }
                });
    }

    static void updateFcmToken(@NonNull String fcmToken, @NonNull final DeviceUpdateHandler handler) {
        updateDeviceObservable(new UpdateDeviceReqModel(fcmToken))
                .subscribe(new Action1<Response<UpdateDeviceRespModel>>() {
                    @Override
                    public void call(Response<UpdateDeviceRespModel> response) {
                        Hyber.Log(Hyber.LOG_LEVEL.DEBUG, String.format(Locale.getDefault(),
                                "Success - FCM Token update complete.\nData:%s",
                                response.message()));
                        if (response.isSuccessful()) {
                            handler.onSuccess();
                        } else {
                            String errorBody = null;
                            Throwable throwable = null;
                            try {
                                errorBody = response.errorBody().string();
                            } catch (IOException e) {
                                throwable = e;
                            }
                            handler.onFailure(response.code(), errorBody, throwable);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Hyber.Log(Hyber.LOG_LEVEL.WARN, String.format(Locale.getDefault(),
                                "Failure: FCM Token update unsuccessful.\nError:%s",
                                throwable.toString()));
                        handler.onThrowable(throwable);
                    }
                });
    }

    static void updateDevice(@NonNull String deviceOs, @NonNull String androidVersion,
                             @NonNull String deviceName, @NonNull String modelName,
                             @NonNull String deviceType, @NonNull final DeviceUpdateHandler handler) {
        updateDeviceObservable(new UpdateDeviceReqModel(FirebaseInstanceId.getInstance().getToken(),
                deviceOs, androidVersion, deviceName, modelName, deviceType))
                .subscribe(new Action1<Response<UpdateDeviceRespModel>>() {
                    @Override
                    public void call(Response<UpdateDeviceRespModel> response) {
                        Hyber.Log(Hyber.LOG_LEVEL.DEBUG, String.format(Locale.getDefault(),
                                "Success - Device update complete.\nData:%s",
                                response.message()));
                        if (response.isSuccessful()) {
                            handler.onSuccess();
                        } else {
                            String errorBody = null;
                            Throwable throwable = null;
                            try {
                                errorBody = response.errorBody().string();
                            } catch (IOException e) {
                                throwable = e;
                            }
                            handler.onFailure(response.code(), errorBody, throwable);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Hyber.Log(Hyber.LOG_LEVEL.WARN, String.format(Locale.getDefault(),
                                "Failure: Device update unsuccessful.\nError:%s",
                                throwable.toString()));
                        handler.onThrowable(throwable);
                    }
                });
    }

    static void getMessageHistory(@NonNull final Long startDate, @NonNull final MessageHistoryHandler handler) {
        getMessageHistoryObservable(new MessageHistoryReqModel(startDate))
                .subscribe(new Action1<Response<MessageHistoryRespEnvelope>>() {
                    @Override
                    public void call(Response<MessageHistoryRespEnvelope> response) {
                        Hyber.Log(Hyber.LOG_LEVEL.DEBUG, String.format(Locale.getDefault(),
                                "Success - Message history complete.\nData:%s",
                                response.message()));
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                handler.onSuccess(startDate, response.body());
                            } else {
                                handler.onFailure(response.code(), response.raw().toString(), null);
                            }
                        } else {
                            String errorBody = null;
                            Throwable throwable = null;
                            try {
                                errorBody = response.errorBody().string();
                            } catch (IOException e) {
                                throwable = e;
                            }
                            handler.onFailure(response.code(), errorBody, throwable);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Hyber.Log(Hyber.LOG_LEVEL.WARN, String.format(Locale.getDefault(),
                                "Failure: Message history unsuccessful.\nError:%s",
                                throwable.toString()));
                        handler.onThrowable(throwable);
                    }
                });
    }

    static void sendPushDeliveryReport(@NonNull final String messageId, @NonNull Long receivedAt, @NonNull final PushDeliveryReportHandler handler) {
        sendPushDeliveryReportObservable(new PushDeliveryReportReqModel(messageId, receivedAt))
                .subscribe(new Action1<Response<Void>>() {
                    @Override
                    public void call(Response response) {
                        Hyber.Log(Hyber.LOG_LEVEL.DEBUG, String.format(Locale.getDefault(),
                                "Success - Push delivery report complete.\nData:%s",
                                response.message()));
                        if (response.isSuccessful()) {
                            handler.onSuccess(messageId);
                        } else {
                            String errorBody = null;
                            Throwable throwable = null;
                            try {
                                errorBody = response.errorBody().string();
                            } catch (IOException e) {
                                throwable = e;
                            }
                            handler.onFailure(messageId, response.code(), errorBody, throwable);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Hyber.Log(Hyber.LOG_LEVEL.WARN, String.format(Locale.getDefault(),
                                "Failure: Push delivery report unsuccessful.\nError:%s",
                                throwable.toString()));
                        handler.onThrowable(messageId, throwable);
                    }
                });
    }

    private static Observable<Response<RegisterDeviceRespModel>> registerDeviceObservable(
            @NonNull RegisterDeviceReqModel model) {
        return hyberApiService.registerDeviceObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static Observable<Response<RefreshTokenRespModel>> refreshTokenObservable(
            @NonNull RefreshTokenReqModel model) {
        return hyberApiService.refreshTokenObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static Observable<Response<UpdateDeviceRespModel>> updateDeviceObservable(
            @NonNull UpdateDeviceReqModel model) {
        return hyberApiService.updateDeviceObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static Observable<Response<MessageHistoryRespEnvelope>> getMessageHistoryObservable(
            @NonNull MessageHistoryReqModel model) {
        return hyberApiService.getMessageHistoryObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static Observable<Response<Void>> sendPushDeliveryReportObservable(
            @NonNull PushDeliveryReportReqModel model) {
        return hyberApiService.sendPushDeliveryReportObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    interface UserRegisterHandler {
        void onSuccess();

        void onFailure(int statusCode, @Nullable String response, @Nullable Throwable throwable);

        void onThrowable(@Nullable Throwable throwable);
    }

    interface RefreshTokenHandler {
        void onSuccess();

        void onFailure(int statusCode, @Nullable String response, @Nullable Throwable throwable);

        void onThrowable(@Nullable Throwable throwable);
    }

    interface DeviceUpdateHandler {
        void onSuccess();

        void onFailure(int statusCode, @Nullable String response, @Nullable Throwable throwable);

        void onThrowable(@Nullable Throwable throwable);
    }

    interface MessageHistoryHandler {
        void onSuccess(@NonNull Long startDate, @NonNull MessageHistoryRespEnvelope envelope);

        void onFailure(int statusCode, @Nullable String response, @Nullable Throwable throwable);

        void onThrowable(@Nullable Throwable throwable);
    }

    interface PushDeliveryReportHandler {
        void onSuccess(@NonNull String messageId);

        void onFailure(@NonNull String messageId, int statusCode, @Nullable String response, @Nullable Throwable throwable);

        void onThrowable(@NonNull String messageId, @Nullable Throwable throwable);
    }

}
