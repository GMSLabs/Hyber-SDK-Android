package com.hyber;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.iid.FirebaseInstanceId;
import com.hyber.log.HyberLogger;
import com.hyber.model.Session;
import com.hyber.model.User;

import java.io.IOException;

import retrofit2.Response;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

final class ApiBusinessModel implements IApiBusinessModel {

    private static final String TAG = "ApiBusinessModel";

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

    @Override
    public void authorize(@NonNull final Long phone, @NonNull final AuthorizationListener listener) {
        HyberLogger.i("Start user registration.");

        RegisterUserReqModel reqModel = new RegisterUserReqModel(String.valueOf(phone),
                OsUtils.getDeviceOs(), OsUtils.getAndroidVersion(),
                OsUtils.getDeviceFormat(mContextReference),
                OsUtils.getDeviceName(), SdkVersion.BUILD);

        RestClient.registerUserObservable(reqModel)
                .subscribe(new Action1<Response<RegisterUserRespModel>>() {
                    @Override
                    public void call(Response<RegisterUserRespModel> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getError() == null) {
                                HyberLogger.i("Request for user registration is success.");

                                Repository repo = new Repository();
                                repo.open();

                                if (repo.getCurrentUser() != null)
                                    repo.clearUserData(repo.getCurrentUser());

                                if (response.body().getProfile() != null
                                        && response.body().getSession() != null) {
                                    HyberLogger.i("User profile with session is provided.");
                                    Session session = new Session(
                                            response.body().getSession().getToken(),
                                            response.body().getSession().getRefreshToken(),
                                            response.body().getSession().getExpirationDate(),
                                            false);
                                    User user = new User(
                                            response.body().getProfile().getUserId(),
                                            response.body().getProfile().getUserPhone(),
                                            session);
                                    repo.saveNewUser(user);
                                    HyberLogger.i("User data with session saved.");
                                    listener.onSuccess();
                                } else {
                                    if (response.body().getError() != null) {
                                        HyberLogger.w(ErrorStatus.byCode(response.body().getError().getCode()).toString());
                                    } else {
                                        HyberLogger.tag(TAG);
                                        HyberLogger.wtf("User not registered, session data is not provided!");
                                    }
                                }
                                repo.close();
                            } else {
                                HyberLogger.w(ErrorStatus.byCode(response.body().getError().getCode()).toString());
                                listener.onFailure();
                            }
                        } else {
                            responseIsUnsuccessful(response);
                            listener.onFailure();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        HyberLogger.e(throwable, "Error in user registration api request!");
                        listener.onFailure();
                    }
                });
    }

    private void responseIsUnsuccessful(Response response) {
        try {
            HyberLogger.e("HTTP response code: %d\nEndpoint URL:\n%s\n----------------------\n%s",
                    response.code(), response.raw().request().url().toString(), response.errorBody().string());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T extends BaseResponse> Observable<Response<T>> tokenActualProcessorObservable(final Observable<Response<T>> currObservable,
                                                                       final Response<T> currResponse) {
        if (currResponse.isSuccessful()) {
            if (currResponse.body() != null && currResponse.body().getError() != null
                    && currResponse.body().getError().getCode().equals(ErrorStatus.mobileExpiredToken.code())) {
                HyberLogger.i("User auth token expired.\nStart refreshing auth token.");

                Repository repo = new Repository();
                repo.open();
                User user = repo.getCurrentUser();
                if (user == null) {
                    repo.close();
                    return Observable.just(currResponse);
                }

                RefreshTokenReqModel reqModel = new RefreshTokenReqModel(user.getSession().getRefreshToken());
                repo.close();

                return RestClient.refreshTokenObservable(reqModel)
                        .flatMap(new Func1<Response<RefreshTokenRespModel>, Observable<Response<T>>>() {
                            @Override
                            public Observable<Response<T>> call(Response<RefreshTokenRespModel> response) {
                                if (response.isSuccessful()) {
                                    HyberLogger.i("Request for refresh user auth token is success.");
                                    SessionRespItemModel sessionModel = response.body().getSession();
                                    HyberLogger.i("User new session data updating.");
                                    Repository repo = new Repository();
                                    repo.open();
                                    User user = repo.getCurrentUser();
                                    if (user != null) {
                                        repo.updateUserSession(user, sessionModel.getToken(),
                                                sessionModel.getRefreshToken(), sessionModel.getExpirationDate());
                                    }
                                    repo.close();
                                    HyberLogger.i("User session data is updated.");
                                    HyberLogger.i("Continue execute current observable!");
                                    return currObservable;
                                } else {
                                    responseIsUnsuccessful(response);
                                    return Observable.just(currResponse);
                                }
                            }
                        })
                        .doOnError(new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                HyberLogger.e("Error in refresh user auth token api request!", throwable);
                            }
                        });
            }
        } else {
            responseIsUnsuccessful(currResponse);
        }
        return Observable.just(currResponse);
    }

    @Override
    public void sendDeviceData(@NonNull final SendDeviceDataListener listener) {
        HyberLogger.i("Start sending user device data.");

        Repository repo = new Repository();
        repo.open();
        if (repo.getCurrentUser() == null) {
            repo.close();
            listener.onFailure();
            return;
        } else {
            repo.close();
        }

        final UpdateDeviceReqModel reqModel = new UpdateDeviceReqModel(
                FirebaseInstanceId.getInstance().getToken(),
                OsUtils.getDeviceOs(), OsUtils.getAndroidVersion(),
                OsUtils.getDeviceFormat(mContextReference),
                OsUtils.getDeviceName(), SdkVersion.BUILD);

        RestClient.updateUserObservable(reqModel)
                .flatMap(new Func1<Response<UpdateUserRespModel>, Observable<Response<UpdateUserRespModel>>>() {
                    @Override
                    public Observable<Response<UpdateUserRespModel>> call(Response<UpdateUserRespModel> response) {
                        return tokenActualProcessorObservable(
                                RestClient.updateUserObservable(reqModel), response);
                    }
                })
                .subscribe(new Action1<Response<UpdateUserRespModel>>() {
                    @Override
                    public void call(Response<UpdateUserRespModel> response) {
                        if (response.isSuccessful()) {
                            HyberLogger.i("Request for update user device data is success.");
                            if (response.body().getError() == null) {
                                listener.onSuccess();
                            } else {
                                HyberLogger.i(ErrorStatus.byCode(response.body().getError().getCode()).toString()
                                        + "\nResponse for update user device data api with error!");
                                listener.onFailure();
                            }
                        } else {
                            responseIsUnsuccessful(response);
                            listener.onFailure();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        HyberLogger.e(throwable, "Error in update user device data api request!");
                        listener.onFailure();
                    }
                });
    }

    @Override
    public void sendBidirectionalAnswer(@NonNull final String messageId, @NonNull String answerText,
                                        @NonNull final SendBidirectionalAnswerListener listener) {
        HyberLogger.i("Start sending bidirectional answer.");

        final BidirectionalAnswerReqModel reqModel = new BidirectionalAnswerReqModel(
                messageId,
                answerText);

        RestClient.sendBidirectionalAnswerObservable(reqModel)
                .flatMap(new Func1<Response<BaseResponse>, Observable<Response<BaseResponse>>>() {
                    @Override
                    public Observable<Response<BaseResponse>> call(Response<BaseResponse> response) {
                        return tokenActualProcessorObservable(
                                RestClient.sendBidirectionalAnswerObservable(reqModel), response);
                    }
                })
                .subscribe(new Action1<Response<BaseResponse>>() {
                    @Override
                    public void call(Response<BaseResponse> response) {
                        if (response.isSuccessful()) {
                            HyberLogger.i("Request for sending bidirectional answer is success.");
                            listener.onSuccess(messageId);
                        } else {
                            responseIsUnsuccessful(response);
                            listener.onFailure();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        HyberLogger.e(throwable, "Error in sending bidirectional answer api request!");
                        listener.onFailure();
                    }
                });
    }

    @Override
    public void sendPushDeliveryReport(@NonNull final String messageId, @NonNull Long receivedAt,
                                       @NonNull final SendPushDeliveryReportListener listener) {
        HyberLogger.i("Start sending push delivery report.");
        final Long r = System.currentTimeMillis();
        final PushDeliveryReportReqModel reqModel = new PushDeliveryReportReqModel(messageId, r);

        RestClient.sendPushDeliveryReportObservable(reqModel)
                .flatMap(new Func1<Response<BaseResponse>, Observable<Response<BaseResponse>>>() {
                    @Override
                    public Observable<Response<BaseResponse>> call(Response<BaseResponse> response) {
                        return tokenActualProcessorObservable(
                                RestClient.sendPushDeliveryReportObservable(reqModel.setReceivedAt(r)), response);
                    }
                })
                .subscribe(new Action1<Response<BaseResponse>>() {
                    @Override
                    public void call(Response<BaseResponse> response) {
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
    public void getMessageHistory(@NonNull final Long startDate, @NonNull final MessageHistoryListener listener) {
        HyberLogger.i("Start downloading message history.");

        final MessageHistoryReqModel reqModel = new MessageHistoryReqModel(startDate);

        RestClient.getMessageHistoryObservable(reqModel)
                .flatMap(new Func1<Response<MessageHistoryRespEnvelope>, Observable<Response<MessageHistoryRespEnvelope>>>() {
                    @Override
                    public Observable<Response<MessageHistoryRespEnvelope>> call(Response<MessageHistoryRespEnvelope> response) {
                        return tokenActualProcessorObservable(
                                RestClient.getMessageHistoryObservable(reqModel), response);
                    }
                })
                .subscribe(new Action1<Response<MessageHistoryRespEnvelope>>() {
                    @Override
                    public void call(Response<MessageHistoryRespEnvelope> response) {
                        if (response.isSuccessful()) {
                            HyberLogger.i("Request for downloading message history is success.");
                            HyberLogger.i("Downloaded %d messages.", response.body().getMessages().size());

                            if (response.body().getMessages() != null && response.body().getMessages().size() > 0) {
                                HyberLogger.d("LimitDays %d\nLimitMessages %d\nTimeLastMessage %d"
                                                + "\nFirst MessageId %s DrTime %d\nLast MessageId %s DrTime %d",
                                        response.body().getLimitDays(),
                                        response.body().getLimitMessages(),
                                        response.body().getLastTime(),
                                        response.body().getMessages().get(0).getId(),
                                        response.body().getMessages().get(0).getTime(),
                                        response.body().getMessages().get(response.body().getMessages().size() - 1).getId(),
                                        response.body().getMessages().get(response.body().getMessages().size() - 1).getTime());
                            }
                            listener.onSuccess(startDate, response.body());
                        } else {
                            responseIsUnsuccessful(response);
                            listener.onFailure();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        HyberLogger.e(throwable, "Error in downloading message history api request!");
                        listener.onFailure();
                    }
                });
    }

    interface AuthorizationListener {
        void onSuccess();

        void onFailure();
    }

    interface SendDeviceDataListener {
        void onSuccess();

        void onFailure();
    }

    interface SendBidirectionalAnswerListener {
        void onSuccess(@NonNull String messageId);

        void onFailure();
    }

    interface MessageHistoryListener {
        void onSuccess(@NonNull Long startDate, @NonNull MessageHistoryRespEnvelope envelope);

        void onFailure();
    }

    interface SendPushDeliveryReportListener {
        void onSuccess(@NonNull String messageId);

        void onFailure();
    }
}
