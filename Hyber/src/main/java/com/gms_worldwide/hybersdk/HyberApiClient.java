package com.gms_worldwide.hybersdk;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Andrew Kochura.
 */
class HyberApiClient {

    /**
     * The constant STANDARD_TIMEOUT.
     */
    public static final int STANDARD_TIMEOUT = 20;  // seconds
    private static final String API_BASE_URL = HyberConstants.BASE_URL;
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    private HyberWebService hyberWebService;

    /**
     * Instantiates a Hyber api client.
     */
    public HyberApiClient() {

        interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);

        hyberWebService = createService(HyberWebService.class);

    }

    /**
     * Create service s.
     *
     * @param <S>          the type parameter
     * @param serviceClass the service class
     * @return the s
     */
    public static <S> S createService(Class<S> serviceClass) {

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request request = original.newBuilder()
                        .header(HyberHttpHeaders.CONTENT_TYPE, "applicaton/json")
                        .header(HyberHttpHeaders.AUTHORIZATION, "app_key=" + HyberPlugins.get().getClientKey())
                        .method(original.method(), original.body())
                        .build();

                Response response = chain.proceed(request);

                // Customize or return the response
                return response;
            }
        });

        httpClient.networkInterceptors().add(interceptor);

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(serviceClass);
    }

    /**
     * Delivery report observable observable.
     *
     * @param uniqAppDeviceId the uniq app device id
     * @param msg_gms_uniq_id the msg gms uniq id
     * @param status          the status
     * @return the observable
     */
    public Observable<retrofit2.Response<Void>> deliveryReportObservable(long uniqAppDeviceId, long msg_gms_uniq_id,
                                                                         int status) {
        HyberDeliveryReportModel hyberDeliveryReportModel =
                new HyberDeliveryReportModel(uniqAppDeviceId, msg_gms_uniq_id, status);
        return hyberWebService.deliveryReportObservable(hyberDeliveryReportModel);
    }

    /**
     * Registration observable observable.
     *
     * @param phone    the phone
     * @param email    the email
     * @param gcmToken the gcm token
     * @return the observable
     */
    public Observable<retrofit2.Response<HyberBaseResponseModel>> registrationObservable(
            long phone, String email, String gcmToken) {
        String p = null;
        if (phone > 0)
            p = String.valueOf(phone);
        if (email.equals(""))
            email = null;
        HyberRegistrationPushModelWithNull registrationPushModel =
                new HyberRegistrationPushModelWithNull(p, email, gcmToken);
        return hyberWebService.addAbonentObservable(registrationPushModel);
    }

    /**
     * Update token observable observable.
     *
     * @param uniqAppDeviceId the uniq app device id
     * @param gcmToken        the gcm token
     * @return the observable
     */
    public Observable<retrofit2.Response<HyberBaseResponseModel>> updateTokenObservable(
            long uniqAppDeviceId, String gcmToken) {
        return hyberWebService.updateTokenObservable(
                new HyberUpdateTokenModel(uniqAppDeviceId, gcmToken));
    }

    /**
     * Abonent profile observable observable.
     *
     * @param uniqAppDeviceId the uniq app device id
     * @param sex             the sex
     * @param city            the city
     * @param region          the region
     * @param age             the age
     * @param interests       the interests
     * @param ads_source      the ads source
     * @param latitude        the latitude
     * @param longitude       the longitude
     * @return the observable
     */
    public Observable<retrofit2.Response<HyberBaseResponseModel>> abonentProfileObservable(
            long uniqAppDeviceId, int sex, String city,
            String region, String fio, int age, int[] interests,
            int[] ads_source, double latitude, double longitude) {
        HyberUserInfoModel hyberUserInfoModel =
                new HyberUserInfoModel(uniqAppDeviceId, sex, city, region, fio,
                        age, interests, ads_source, latitude, longitude);
        return hyberWebService.abonentProfileObservable(hyberUserInfoModel);
    }

    /**
     * Abonent location observable observable.
     *
     * @param uniqAppDeviceId the uniq app device id
     * @param latitude        the latitude
     * @param longitude       the longitude
     * @return the observable
     */
    public Observable<retrofit2.Response<HyberBaseResponseModel>> abonentLocationObservable(
            long uniqAppDeviceId, double latitude, double longitude) {
        HyberUserLocationModel hyberUserLocationModel =
                new HyberUserLocationModel(uniqAppDeviceId, latitude, longitude);
        return hyberWebService.abonentLocationObservable(hyberUserLocationModel);
    }

    /**
     * Update phone email observable observable.
     *
     * @param uniqAppDeviceId the uniq app device id
     * @param phone           the phone
     * @param email           the email
     * @return the observable
     */
    public Observable<retrofit2.Response<HyberBaseResponseModel>> updatePhoneEmailObservable(
            long uniqAppDeviceId, long phone, String email) {
        String p = null;
        if (phone > 0)
            p = String.valueOf(phone);
        if (email.equals(""))
            email = null;
        HyberUpdatePhoneEmailModel hyberUpdatePhoneEmailModel =
                new HyberUpdatePhoneEmailModel(uniqAppDeviceId, p, email);
        return hyberWebService.updatePhoneEmailObservable(hyberUpdatePhoneEmailModel);
    }

    /**
     * Allow recieve push observable observable.
     *
     * @param uniqAppDeviceId the uniq app device id
     * @param isOn            the is on
     * @return the observable
     */
    public Observable<retrofit2.Response<HyberBaseResponseModel>> allowRecievePushObservable(long uniqAppDeviceId, boolean isOn) {
        HyberAllowRecievePushModel model =
                new HyberAllowRecievePushModel(uniqAppDeviceId, (isOn) ? 1 : 0);
        return hyberWebService.allowRecievePushObservable(model);
    }

    /**
     * Get user profile observable observable.
     *
     * @param uniqAppDeviceId the uniq app device id
     * @return the observable
     */
    public Observable<HyberUserProfileResponseModel> getUserProfileObservable(long uniqAppDeviceId) {
        HyberGetUserProfileRequestModel model =
                new HyberGetUserProfileRequestModel(uniqAppDeviceId);
        return hyberWebService.getUserProfileObservable(model);
    }

    /**
     * Get viber messages observable observable.
     *
     * @param phone           the phone
     * @param uniqAppDeviceId the uniq app device id
     * @param date_utc        the date utc
     * @return the observable
     */
    public Observable<retrofit2.Response<HyberMessagesEnvelope>> getViberMessagesObservable(
            long phone, long uniqAppDeviceId, long date_utc) {
        return hyberWebService.getMessagesObservable(HyberConstants.VIBER_CHANNEL,
                new HyberMessagesRequestModel(uniqAppDeviceId, phone, date_utc));
    }

    private interface HyberWebService {

        /**
         * Delivery report observable observable.
         *
         * @param o the o
         * @return the observable
         */
        @POST(HyberConstants.OTT_URL_PATH + "deliveryReport")
        Observable<retrofit2.Response<Void>> deliveryReportObservable(@Body Object o);

        /**
         * Gets messages observable.
         *
         * @param channel                   the channel
         * @param hyberMessagesRequestModel the Hyber messages request model
         * @return the messages observable
         */
        @POST(HyberConstants.OTT_URL_PATH + "requestMessages/{channel}")
        Observable<retrofit2.Response<HyberMessagesEnvelope>> getMessagesObservable(@Path("channel") String channel, @Body HyberMessagesRequestModel hyberMessagesRequestModel);

        /**
         * Add abonent observable observable.
         *
         * @param o the o
         * @return the observable
         */
        @POST(HyberConstants.MAIN_URL_PATH + "lib_add_abonent")
        Observable<retrofit2.Response<HyberBaseResponseModel>> addAbonentObservable(@Body Object o);

        /**
         * Update token observable observable.
         *
         * @param o the o
         * @return the observable
         */
        @POST(HyberConstants.MAIN_URL_PATH + "lib_update_token")
        Observable<retrofit2.Response<HyberBaseResponseModel>> updateTokenObservable(@Body Object o);

        /**
         * Abonent profile observable observable.
         *
         * @param o the o
         * @return the observable
         */
        @POST(HyberConstants.MAIN_URL_PATH + "abonent_profile")
        Observable<retrofit2.Response<HyberBaseResponseModel>> abonentProfileObservable(@Body Object o);

        /**
         * Abonent location observable observable.
         *
         * @param o the o
         * @return the observable
         */
        @POST(HyberConstants.MAIN_URL_PATH + "location")
        Observable<retrofit2.Response<HyberBaseResponseModel>> abonentLocationObservable(@Body Object o);

        /**
         * Update phone email observable observable.
         *
         * @param o the o
         * @return the observable
         */
        @POST(HyberConstants.MAIN_URL_PATH + "lib_update_phone_email")
        Observable<retrofit2.Response<HyberBaseResponseModel>> updatePhoneEmailObservable(@Body Object o);

        /**
         * Allow recieve push observable observable.
         *
         * @param o the o
         * @return the observable
         */
        @POST(HyberConstants.MAIN_URL_PATH + "lib_alow_recieve_push")
        Observable<retrofit2.Response<HyberBaseResponseModel>> allowRecievePushObservable(@Body Object o);

        /**
         * Gets user profile observable.
         *
         * @param o the o
         * @return the user profile observable
         */
        @POST(HyberConstants.MAIN_URL_PATH + "get_profile")
        Observable<HyberUserProfileResponseModel> getUserProfileObservable(@Body Object o);

    }

}
