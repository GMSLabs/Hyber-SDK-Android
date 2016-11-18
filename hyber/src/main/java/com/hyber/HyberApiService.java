package com.hyber;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

interface HyberApiService {

    @POST(BuildConfig.API_URL_ma_register)
    Observable<Response<RegisterUserRespModel>> registerUserObservable(@Body RegisterUserReqModel body);

    @POST(BuildConfig.API_URL_ma_refreshtoken)
    Observable<Response<RefreshTokenRespModel>> refreshTokenObservable(@Body RefreshTokenReqModel body);

    @POST(BuildConfig.API_URL_ma_update_device)
    Observable<Response<UpdateUserRespModel>> updateDeviceObservable(@Body UpdateUserReqModel body);

    @POST(BuildConfig.API_URL_ma_message_history)
    Observable<Response<MessageHistoryRespEnvelope>> getMessageHistoryObservable(@Body MessageHistoryReqModel body);

    @POST(BuildConfig.API_URL_pd_receiver)
    Observable<Response<Void>> sendPushDeliveryReportObservable(@Body PushDeliveryReportReqModel body);

    @POST(BuildConfig.API_URL_pc_receiver)
    Observable<Response<Void>> sendBidirectionalAnswerObservable(@Body BidirectionalAnswerReqModel body);

}
