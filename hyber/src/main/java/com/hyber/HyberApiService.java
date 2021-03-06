package com.hyber;

import java.util.Map;

import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

interface HyberApiService {

    @POST(BuildConfig.API_URL_ma_registration)
    Observable<Response<DeviceRegistrationRespModel>> deviceRegistrationObservable(@HeaderMap Map<String, String> headers, @Body DeviceRegistrationReqModel body);

    @POST(BuildConfig.API_URL_ma_update_device)
    Observable<Response<DeviceUpdateRespModel>> deviceUpdateObservable(@HeaderMap Map<String, String> headers, @Body DeviceUpdateReqModel body);

    @GET(BuildConfig.API_URL_ma_all_devices)
    Observable<Response<DevicesRespEnvelope>> devicesObservable(@HeaderMap Map<String, String> headers);

    @POST(BuildConfig.API_URL_ma_revoke_device)
    Observable<Response<Void>> revokeDeviceObservable(@HeaderMap Map<String, String> headers, @Body RevokeDevicesReqModel body);

    @GET(BuildConfig.API_URL_ma_message_history)
    Observable<Response<MessageHistoryRespEnvelope>> messageHistoryObservable(@HeaderMap Map<String, String> headers, @Query("startDate") Long startDate);

    @POST(BuildConfig.API_URL_pd_receiver)
    Observable<Response<MessageDeliveryReportRespModel>> messageDeliveryReportObservable(@HeaderMap Map<String, String> headers, @Body MessageDeliveryReportReqModel body);

    @POST(BuildConfig.API_URL_pc_receiver)
    Observable<Response<MessageCallbackRespModel>> messageCallbackObservable(@HeaderMap Map<String, String> headers, @Body MessageCallbackReqModel body);

}
