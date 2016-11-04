package com.hyber;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

interface ApiService {

    @POST("mobile-abonents/api/v1/user/registration")
    Observable<Response<RegisterUserRespModel>> registerUserObservable(@Body RegisterUserReqModel body);

    @POST("mobile-abonents/api/v1/user/refresh_token")
    Observable<Response<RefreshTokenRespModel>> refreshTokenObservable(@Body RefreshTokenReqModel body);

    @POST("mobile-abonents/api/v1/user/update")
    Observable<Response<UpdateUserRespModel>> updateDeviceObservable(@Body UpdateDeviceReqModel body);

    @POST("mobile-abonents/api/v1/message/history")
    Observable<Response<MessageHistoryRespEnvelope>> getMessageHistoryObservable(@Body MessageHistoryReqModel body);

    @POST("push-dr-receiver/api/v1/message/dr")
    Observable<Response<BaseResponse>> sendPushDeliveryReportObservable(@Body PushDeliveryReportReqModel body);

    @POST("push-callback-receiver/api/v1/message/callback")
    Observable<Response<BaseResponse>> sendBidirectionalAnswerObservable(@Body BidirectionalAnswerReqModel body);

    @POST("mobile-abonents/api/v1/device/me")
    Observable<Response> getMyDeviceObservable(@Body String body);

    @POST("mobile-abonents/api/v1/device/delete")
    Observable<Response> getRevokeDeviceObservable(@Body String body);

}
