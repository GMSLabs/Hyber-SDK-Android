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
                .connectTimeout(Tweakables.STANDARD_API_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(Tweakables.STANDARD_API_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(Tweakables.STANDARD_API_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .setDateFormat(Tweakables.API_DATE_FORMAT)
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.HOST)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        hyberApiService = retrofit.create(HyberApiService.class);
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

    static Observable<Response<RegisterUserRespModel>> registerUserObservable(
            @NonNull RegisterUserReqModel model) {
        return hyberApiService.registerUserObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<RefreshTokenRespModel>> refreshTokenObservable(
            @NonNull RefreshTokenReqModel model) {
        return hyberApiService.refreshTokenObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<UpdateUserRespModel>> updateUserObservable(
            @NonNull UpdateUserReqModel model) {
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
