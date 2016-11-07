package com.hyber;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hyber.log.HyberLogger;
import com.hyber.model.Session;
import com.hyber.model.User;

import java.io.IOException;

import okhttp3.ResponseBody;
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

        RegisterUserReqModel reqModel = new RegisterUserReqModel(phone,
                OsUtils.getDeviceOs(), OsUtils.getAndroidVersion(),
                OsUtils.getDeviceName(), OsUtils.getModelName(),
                OsUtils.getDeviceFormat(mContextReference));

        RestClient.registerUserObservable(reqModel)
                .subscribe(new Action1<Response<RegisterUserRespModel>>() {
                    @Override
                    public void call(Response<RegisterUserRespModel> response) {
                        if (response.isSuccessful()) {
                            HyberLogger.i("Request for user registration is success.");

                            Repository repo = new Repository();
                            repo.open();

                            if (repo.getCurrentUser() != null)
                                repo.clearUserData(repo.getCurrentUser());

                            SessionRespItemModel sessionModel = response.body().getSession();
                            if (sessionModel != null) {
                                HyberLogger.i("User session is provided.");
                                Session session = new Session(sessionModel.getToken(),
                                        sessionModel.getRefreshToken(), sessionModel.getExpirationDate(), false);
                                User user = new User(String.valueOf(phone), String.valueOf(phone), session);
                                repo.saveNewUser(user);
                                HyberLogger.i("User data saved.");
                                listener.onSuccess();
                            } else {
                                if (response.body().getError() != null) {
                                    HyberLogger.w(ErrorStatus.byCode(response.body().getError().getCode()).toString());
                                } else {
                                    HyberLogger.tag(TAG);
                                    HyberLogger.wtf("User not registered, session data is not provided!");
                                }
                                listener.onFailure();
                            }

                            repo.close();
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
        switch (response.code()) {
            case 404: HyberLogger.e("url: %s\nresponse code: %d - %s",
                    response.raw().request().url().toString(), response.code(), response.message());
                break;
            case 500: HyberLogger.e("url: %s\nresponse code: %d - %s",
                    response.raw().request().url().toString(), response.code(), response.message());
                break;
            default:
                try {
                    BaseResponse errorResp = new Gson().fromJson(response.errorBody().string(), BaseResponse.class);
                    if (errorResp != null && errorResp.getError() != null
                            && errorResp.getError().getCode() != null) {
                        HyberLogger.e(ErrorStatus.byCode(errorResp.getError().getCode()).toString()
                                + "\nurl: %s\nresponse code: %d - %s",
                                response.raw().request().url().toString(), response.code(), response.message());
                    }
                } catch (IOException | JsonSyntaxException e) {
                    HyberLogger.e(e);
                    HyberLogger.e("url: %s\nresponse code: %d - %s",
                            response.raw().request().url().toString(), response.code(), response.message());
                }
        }
    }

    private <T> Observable<Response<T>> tokenActualProcessorObservable(final Observable<Response<T>> currObservable,
                                                                       final Response<T> currResponse) {
        String errorBody = null;
        try {
            if (currResponse.isSuccessful()) {
                return Observable.just(currResponse);
            } else {
                errorBody = currResponse.errorBody().string();
                BaseResponse errorResp = new Gson().fromJson(errorBody, BaseResponse.class);
                if (errorResp != null && errorResp.getError() != null
                        && errorResp.getError().getCode() != null
                        && errorResp.getError().getDescription() != null
                        && errorResp.getError().getCode().intValue()
                        != ErrorStatus.SDK_API_mobileAuthTokenExpired.getCode()
                        && errorResp.getError().getCode().intValue()
                        != ErrorStatus.SDK_API_notCorrectAuthorizationDataOrTokenExpired.getCode()) {
                    return Observable.just((Response<T>) Response.error(ResponseBody.create(null, errorBody),
                            currResponse.raw()));
                }
            }
        } catch (IOException | JsonSyntaxException e) {
            e.printStackTrace();
            if (errorBody == null) {
                return Observable.just(currResponse);
            } else {
                return Observable.just((Response<T>) Response.error(ResponseBody.create(null, errorBody),
                        currResponse.raw()));
            }
        }

        HyberLogger.i("User auth token expired.\nStart refreshing auth token.");

        Repository repo = new Repository();
        repo.open();
        User user = repo.getCurrentUser();
        if (user == null) {
            return Observable.empty();
        }

        RefreshTokenReqModel reqModel = new RefreshTokenReqModel(user.getSession().getRefreshToken());
        repo.close();

        final String finalErrorBody = errorBody;
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
                            if (finalErrorBody == null) {
                                return Observable.just(currResponse);
                            } else {
                                return Observable.just((Response<T>) Response.error(ResponseBody.create(null, finalErrorBody),
                                        currResponse.raw()));
                            }
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

        final UpdateUserReqModel reqModel = new UpdateUserReqModel(
                FirebaseInstanceId.getInstance().getToken(),
                OsUtils.getDeviceOs(), OsUtils.getAndroidVersion(),
                OsUtils.getDeviceName(), OsUtils.getModelName(),
                OsUtils.getDeviceFormat(mContextReference));

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
                .flatMap(new Func1<Response<Void>, Observable<Response<Void>>>() {
                    @Override
                    public Observable<Response<Void>> call(Response<Void> response) {
                        return tokenActualProcessorObservable(
                                RestClient.sendBidirectionalAnswerObservable(reqModel), response);
                    }
                })
                .subscribe(new Action1<Response<Void>>() {
                    @Override
                    public void call(Response<Void> response) {
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

        final PushDeliveryReportReqModel reqModel = new PushDeliveryReportReqModel(messageId, receivedAt);

        RestClient.sendPushDeliveryReportObservable(reqModel)
                .flatMap(new Func1<Response<Void>, Observable<Response<Void>>>() {
                    @Override
                    public Observable<Response<Void>> call(Response<Void> response) {
                        return tokenActualProcessorObservable(
                                RestClient.sendPushDeliveryReportObservable(reqModel), response);
                    }
                })
                .subscribe(new Action1<Response<Void>>() {
                    @Override
                    public void call(Response<Void> response) {
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
                                        response.body().getTimeLastMessage(),
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
