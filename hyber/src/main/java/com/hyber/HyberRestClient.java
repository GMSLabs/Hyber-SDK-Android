package com.hyber;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

final class HyberRestClient {

    private static HyberApiService mHyberApiServiceMobileAbonents;
    private static HyberApiService mHyberApiServicePushDrReceiver;
    private static HyberApiService mHyberApiServicePushCallbackReceiver;

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

        Retrofit mRetrofitMobileAbonents = new Retrofit.Builder()
                .baseUrl(BuildConfig.HOST_mobile_abonents)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Retrofit mRetrofitPushDrReceiver = new Retrofit.Builder()
                .baseUrl(BuildConfig.HOST_push_dr_receiver)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Retrofit mRetrofitPushCallbackReceiver = new Retrofit.Builder()
                .baseUrl(BuildConfig.HOST_push_callback_receiver)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mHyberApiServiceMobileAbonents = mRetrofitMobileAbonents.create(HyberApiService.class);
        mHyberApiServicePushDrReceiver = mRetrofitPushDrReceiver.create(HyberApiService.class);
        mHyberApiServicePushCallbackReceiver = mRetrofitPushCallbackReceiver.create(HyberApiService.class);
    }

    private HyberRestClient() {

    }

    static Observable<Response<RegisterUserRespModel>> registerUserObservable(
            @NonNull RegisterUserReqModel model) {
        return mHyberApiServiceMobileAbonents.registerUserObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<RefreshTokenRespModel>> refreshTokenObservable(
            @NonNull RefreshTokenReqModel model) {
        return mHyberApiServiceMobileAbonents.refreshTokenObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<UpdateUserRespModel>> updateUserObservable(
            @NonNull UpdateUserReqModel model) {
        return mHyberApiServiceMobileAbonents.updateDeviceObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<MessageHistoryRespEnvelope>> getMessageHistoryObservable(
            @NonNull MessageHistoryReqModel model) {
        return mHyberApiServiceMobileAbonents.getMessageHistoryObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<Void>> sendBidirectionalAnswerObservable(
            @NonNull BidirectionalAnswerReqModel model) {
        return mHyberApiServicePushCallbackReceiver.sendBidirectionalAnswerObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<Void>> sendPushDeliveryReportObservable(
            @NonNull PushDeliveryReportReqModel model) {
        return mHyberApiServicePushDrReceiver.sendPushDeliveryReportObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
