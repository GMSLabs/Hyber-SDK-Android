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

class HyberRestClient {

    private static HyberApiService mHyberApiService_mobile_abonents;
    private static HyberApiService mHyberApiService_push_dr_receiver;
    private static HyberApiService mHyberApiService_push_callback_receiver;

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

        Retrofit retrofit_mobile_abonents = new Retrofit.Builder()
                .baseUrl(BuildConfig.HOST_mobile_abonents)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Retrofit retrofit_push_dr_receiver = new Retrofit.Builder()
                .baseUrl(BuildConfig.HOST_push_dr_receiver)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Retrofit retrofit_push_callback_receiver = new Retrofit.Builder()
                .baseUrl(BuildConfig.HOST_push_callback_receiver)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mHyberApiService_mobile_abonents = retrofit_mobile_abonents.create(HyberApiService.class);
        mHyberApiService_push_dr_receiver = retrofit_push_dr_receiver.create(HyberApiService.class);
        mHyberApiService_push_callback_receiver = retrofit_push_callback_receiver.create(HyberApiService.class);
    }

    static Observable<Response<RegisterUserRespModel>> registerUserObservable(
            @NonNull RegisterUserReqModel model) {
        return mHyberApiService_mobile_abonents.registerUserObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<RefreshTokenRespModel>> refreshTokenObservable(
            @NonNull RefreshTokenReqModel model) {
        return mHyberApiService_mobile_abonents.refreshTokenObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<UpdateUserRespModel>> updateUserObservable(
            @NonNull UpdateUserReqModel model) {
        return mHyberApiService_mobile_abonents.updateDeviceObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<MessageHistoryRespEnvelope>> getMessageHistoryObservable(
            @NonNull MessageHistoryReqModel model) {
        return mHyberApiService_mobile_abonents.getMessageHistoryObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<Void>> sendBidirectionalAnswerObservable(
            @NonNull BidirectionalAnswerReqModel model) {
        return mHyberApiService_push_callback_receiver.sendBidirectionalAnswerObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    static Observable<Response<Void>> sendPushDeliveryReportObservable(
            @NonNull PushDeliveryReportReqModel model) {
        return mHyberApiService_push_dr_receiver.sendPushDeliveryReportObservable(model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
