package com.hyber;

import java.util.Map;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

interface HyberApiService {

    @POST(BuildConfig.API_URL_ma_registration)
    Single<Response<DeviceRegistrationRespModel>> deviceRegistrationObservable(@HeaderMap Map<String, String> headers, @Body DeviceRegistrationReqModel body);

    @POST(BuildConfig.API_URL_ma_update_device)
    Single<Response<DeviceUpdateRespModel>> deviceUpdateObservable(@HeaderMap Map<String, String> headers, @Body DeviceUpdateReqModel body);

    @GET(BuildConfig.API_URL_ma_all_devices)
    Single<Response<DevicesRespEnvelope>> devicesObservable(@HeaderMap Map<String, String> headers);

    @POST(BuildConfig.API_URL_ma_revoke_device)
    Single<Response<Void>> revokeDeviceObservable(@HeaderMap Map<String, String> headers, @Body RevokeDevicesReqModel body);

    @GET(BuildConfig.API_URL_ma_message_history)
    Single<Response<MessageHistoryRespEnvelope>> messageHistoryObservable(@HeaderMap Map<String, String> headers, @Query("startDate") Long startDate);

    @POST(BuildConfig.API_URL_pd_receiver)
    Single<Response<MessageDeliveryReportRespModel>> messageDeliveryReportObservable(@HeaderMap Map<String, String> headers, @Body MessageDeliveryReportReqModel body);

    @POST(BuildConfig.API_URL_pc_receiver)
    Single<Response<MessageCallbackRespModel>> messageCallbackObservable(@HeaderMap Map<String, String> headers, @Body MessageCallbackReqModel body);

}
