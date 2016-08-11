package com.hyber;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.hawk.Hawk;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import timber.log.Timber;

class HyberRestClient {

    private static HyberApiService hyberApiService;

    static {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new StethoInterceptor())
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
        registerDeviceObservable(new RegisterDeviceReqModel(phone, deviceOs, androidVersion, deviceName, modelName, deviceType))
                .subscribe(new Action1<Response<RegisterDeviceRespModel>>() {
                    @Override
                    public void call(Response<RegisterDeviceRespModel> response) {
                        Timber.d("Success - Register complete.\nData:%s",
                                response.message());
                        if (response.isSuccessful()) {
                            if (response.body().getSession() != null &&
                                    response.body().getSession().getToken() != null &&
                                    response.body().getSession().getExpirationDate() != null) {
                                DateFormat df = new SimpleDateFormat(Tweakables.API_DATE_FORMAT, Locale.US);
                                Hawk.chain()
                                        .put(Tweakables.HAWK_HyberAuthToken, response.body().getSession().getToken())
                                        .put(Tweakables.HAWK_HyberTokenExpDate, df.format(response.body().getSession().getExpirationDate()))
                                        .commit();
                                if (response.body().getSession().getRefreshToken() != null) {
                                    Hawk.chain()
                                            .put(Tweakables.HAWK_HyberRefreshToken, response.body().getSession().getRefreshToken())
                                            .commit();
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
                        Timber.d("Failure: Register unsuccessful.\nError:%s",
                                throwable.toString());
                        handler.onThrowable(throwable);
                    }
                });
    }

    static void refreshToken(@NonNull String refreshToken, @NonNull final RefreshTokenHandler handler) {
        refreshTokenObservable(new RefreshTokenReqModel(refreshToken))
                .subscribe(new Action1<Response<RefreshTokenRespModel>>() {
                    @Override
                    public void call(Response<RefreshTokenRespModel> response) {
                        Timber.d("Success - Refresh token complete.\nData:%s",
                                response.message());
                        if (response.isSuccessful()) {
                            if (response.body().getSession() != null &&
                                    response.body().getSession().getToken() != null &&
                                    response.body().getSession().getExpirationDate() != null) {
                                DateFormat df = new SimpleDateFormat(Tweakables.API_DATE_FORMAT, Locale.US);
                                Hawk.chain()
                                        .put(Tweakables.HAWK_HyberAuthToken, response.body().getSession().getToken())
                                        .put(Tweakables.HAWK_HyberTokenExpDate, df.format(response.body().getSession().getExpirationDate()))
                                        .commit();
                                if (response.body().getSession().getRefreshToken() != null) {
                                    Hawk.chain()
                                            .put(Tweakables.HAWK_HyberRefreshToken, response.body().getSession().getRefreshToken())
                                            .commit();
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
                        Timber.d("Failure: Refresh token unsuccessful.\nError:%s",
                                throwable.toString());
                        handler.onThrowable(throwable);
                    }
                });
    }

    static void updateFcmToken(@NonNull String fcmToken, @NonNull final DeviceUpdateHandler handler) {
        updateDeviceObservable(new UpdateDeviceReqModel(fcmToken))
                .subscribe(new Action1<Response<UpdateDeviceRespModel>>() {
                    @Override
                    public void call(Response<UpdateDeviceRespModel> response) {
                        Timber.d("Success - Device update complete.\nData:%s",
                                response.message());
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
                        Timber.d("Failure: Device update unsuccessful.\nError:%s",
                                throwable.toString());
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
                        Timber.d("Success - Device update complete.\nData:%s",
                                response.message());
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
                        Timber.d("Failure: Device update unsuccessful.\nError:%s",
                                throwable.toString());
                        handler.onThrowable(throwable);
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

}
