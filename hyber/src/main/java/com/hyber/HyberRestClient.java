package com.hyber;

import android.support.annotation.NonNull;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

final class HyberRestClient {

    static final int STANDARD_API_TIMEOUT_SECONDS = 30;
    static final String API_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss Z";

    private static HyberApiService mHyberApiService;

    static {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addNetworkInterceptor(new StethoInterceptor())
//                .addInterceptor(new HyberHttpLoggingInterceptor().setLevel(HyberHttpLoggingInterceptor.Level.FULL))
                .connectTimeout(STANDARD_API_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(STANDARD_API_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(STANDARD_API_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .setDateFormat(API_DATE_FORMAT)
                .create();

        Retrofit mRetrofitMobileAbonents = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://mobile.hyber.im")
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mHyberApiService = mRetrofitMobileAbonents.create(HyberApiService.class);
    }

    private HyberRestClient() {

    }

    static Observable<Response<DeviceRegistrationRespModel>> registerUserObservable(
            Map<String, String> headers, @NonNull DeviceRegistrationReqModel model) {
        return mHyberApiService.deviceRegistrationObservable(headers, model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<DeviceUpdateRespModel>> updateUserObservable(
            Map<String, String> headers, @NonNull DeviceUpdateReqModel model) {
        return mHyberApiService.deviceUpdateObservable(headers, model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<DevicesRespEnvelope>> getAllDevicesObservable(
            Map<String, String> headers) {
        return mHyberApiService.devicesObservable(headers)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<Void>> revokeDevicesObservable(
            Map<String, String> headers, @NonNull RevokeDevicesReqModel model) {
        return mHyberApiService.revokeDeviceObservable(headers, model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<MessageHistoryRespEnvelope>> getMessageHistoryObservable(
            Map<String, String> headers, @NonNull Long startDate) {
        return mHyberApiService.messageHistoryObservable(headers, startDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<MessageDeliveryReportRespModel>> sendMessageDeliveryReportObservable(
            Map<String, String> headers, @NonNull MessageDeliveryReportReqModel model) {
        return mHyberApiService.messageDeliveryReportObservable(headers, model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<MessageCallbackRespModel>> sendMessageCallbackObservable(
            Map<String, String> headers, @NonNull MessageCallbackReqModel model) {
        return mHyberApiService.messageCallbackObservable(headers, model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
