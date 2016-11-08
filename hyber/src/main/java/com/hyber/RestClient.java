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

final class RestClient {

    private static ApiService mApiServiceMobileAbonents;
    private static ApiService mApiServicePushDrReceiver;
    private static ApiService mApiServicePushCallbackReceiver;

    static {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .addInterceptor(new AuthInterceptor())
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.FULL))
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

        mApiServiceMobileAbonents = mRetrofitMobileAbonents.create(ApiService.class);
        mApiServicePushDrReceiver = mRetrofitPushDrReceiver.create(ApiService.class);
        mApiServicePushCallbackReceiver = mRetrofitPushCallbackReceiver.create(ApiService.class);
    }

    private RestClient() {

    }

    static Observable<Response<RegisterUserRespModel>> registerUserObservable(
            @NonNull RegisterUserReqModel model) {
        return mApiServiceMobileAbonents.registerUserObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<RefreshTokenRespModel>> refreshTokenObservable(
            @NonNull RefreshTokenReqModel model) {
        return mApiServiceMobileAbonents.refreshTokenObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<UpdateUserRespModel>> updateUserObservable(
            @NonNull UpdateUserReqModel model) {
        return mApiServiceMobileAbonents.updateDeviceObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<MessageHistoryRespEnvelope>> getMessageHistoryObservable(
            @NonNull MessageHistoryReqModel model) {
        return mApiServiceMobileAbonents.getMessageHistoryObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<Void>> sendBidirectionalAnswerObservable(
            @NonNull BidirectionalAnswerReqModel model) {
        return mApiServicePushCallbackReceiver.sendBidirectionalAnswerObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<Void>> sendPushDeliveryReportObservable(
            @NonNull PushDeliveryReportReqModel model) {
        return mApiServicePushDrReceiver.sendPushDeliveryReportObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
