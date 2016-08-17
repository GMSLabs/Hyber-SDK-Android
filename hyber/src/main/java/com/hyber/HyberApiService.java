package com.hyber;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

interface HyberApiService {

    @POST("register/device")
    Observable<Response<RegisterDeviceRespModel>> registerDeviceObservable(@Body RegisterDeviceReqModel body);

    @POST("refreshtoken/device")
    Observable<Response<RefreshTokenRespModel>> refreshTokenObservable(@Body RefreshTokenReqModel body);

    @POST("update/device")
    Observable<Response<UpdateDeviceRespModel>> updateDeviceObservable(@Body UpdateDeviceReqModel body);

    @POST("messages/history")
    Observable<Response<MessageHistoryRespModel>> getMessageHistoryObservable(@Body MessageHistoryReqModel body);

}
