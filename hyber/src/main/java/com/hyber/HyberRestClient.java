package com.hyber;

import android.support.annotation.NonNull;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

final class HyberRestClient {

    private static HyberApiService mHyberApiService;

    static {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(new HyberAuthInterceptor())
                .addInterceptor(new HyberHttpLoggingInterceptor().setLevel(HyberHttpLoggingInterceptor.Level.FULL))
                .connectTimeout(Tweakables.STANDARD_API_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(Tweakables.STANDARD_API_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(Tweakables.STANDARD_API_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .setDateFormat(Tweakables.API_DATE_FORMAT)
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

    static Observable<Response<RegisterUserRespModel>> registerUserObservable(
            @NonNull RegisterUserReqModel model) {
        return mHyberApiService.registerUserObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<RefreshTokenRespModel>> refreshTokenObservable(
            @NonNull RefreshTokenReqModel model) {
        return mHyberApiService.refreshTokenObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<UpdateUserRespModel>> updateUserObservable(
            @NonNull UpdateUserReqModel model) {
        return mHyberApiService.updateDeviceObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<MessageHistoryRespEnvelope>> getMessageHistoryObservable(
            @NonNull MessageHistoryReqModel model) {
        return mHyberApiService.getMessageHistoryObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<Void>> sendBidirectionalAnswerObservable(
            @NonNull BidirectionalAnswerReqModel model) {
        return mHyberApiService.sendBidirectionalAnswerObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<Void>> sendPushDeliveryReportObservable(
            @NonNull PushDeliveryReportReqModel model) {
        return mHyberApiService.sendPushDeliveryReportObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
