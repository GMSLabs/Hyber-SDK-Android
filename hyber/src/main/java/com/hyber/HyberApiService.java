package com.hyber;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

interface HyberApiService {

    @POST("mobile-abonents/register/device")
    Observable<Response<RegisterUserRespModel>> registerUserObservable(@Body RegisterUserReqModel body);

    @POST("mobile-abonents/refreshtoken/device")
    Observable<Response<RefreshTokenRespModel>> refreshTokenObservable(@Body RefreshTokenReqModel body);

    @POST("mobile-abonents/update/device")
    Observable<Response<UpdateUserRespModel>> updateDeviceObservable(@Body UpdateUserReqModel body);

    @POST("mobile-abonents/messages/history")
    Observable<Response<MessageHistoryRespEnvelope>> getMessageHistoryObservable(@Body MessageHistoryReqModel body);

    @POST("push-dr-receiver/sdk_api/dr")
    Observable<Response<Void>> sendPushDeliveryReportObservable(@Body PushDeliveryReportReqModel body);

    @POST("push-callback-receiver/api/callback")
    Observable<Response<Void>> sendBidirectionalAnswerObservable(@Body BidirectionalAnswerReqModel body);

}
