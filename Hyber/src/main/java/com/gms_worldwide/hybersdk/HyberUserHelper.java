package com.gms_worldwide.hybersdk;

import android.text.TextUtils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Andrew Kochura.
 */
public class HyberUserHelper {

    private static final String TAG = "com.gms_worldwide.hybersdk.HyberUserHelper";

    private static HyberUserHelper instance = null;

    /**
     * Instantiates a Hyber user helper.
     */
    protected HyberUserHelper() {

    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static HyberUserHelper getInstance() {
        if (instance == null) {
            instance = new HyberUserHelper();
        }
        return instance;
    }

    /**
     * This method send location data to cloud user storage
     *
     * @param latitude  the latitude
     * @param longitude the longitude
     * @return true if operation executed with success signature.
     */
    public Observable<Boolean> updateUserLocation(final double latitude, final double longitude) {
        return HyberPlugins.get().restClient()
                .abonentLocationObservable(getCurrentUser().getUniqAppDeviceId(),
                        latitude, longitude)
                .timeout(HyberApiClient.STANDARD_TIMEOUT, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<retrofit2.Response<HyberBaseResponseModel>, Boolean>() {
                    @Override
                    public Boolean call(retrofit2.Response<HyberBaseResponseModel> response) {
                        if (response.body().isSuccess()) {
                            HyberCurrentUserDBModel userDBModel =
                                    HyberPlugins.get().getDatabaseHelper().getCurrentUser();
                            userDBModel.setLatitude(latitude);
                            userDBModel.setLongitude(longitude);
                            HyberPlugins.get().getDatabaseHelper()
                                    .updateCurrentUser(userDBModel);
                        }
                        return response.body().isSuccess();
                    }
                });
    }

    /**
     * Update user data in cloud.
     * You must send all user data, not only updated fields.
     * Because cloud make update for full user data package.
     *
     * @param sex                        the sex
     * @param city                       the city
     * @param region                     the region
     * @param age                        the age
     * @param interests                  you may associate integers with your own list of interests.
     * @param ads_source                 call to GMS Worldwide for more info.
     * @param latitude                   the latitude
     * @param longitude                  the longitude
     * @param allow_use_current_location the allow use current location
     * @return true if operation executed with success signature.
     */
    public Observable<Boolean> updateUserProfile(final int sex, final String city, final String region, final String fio,
                                                 final int age, final int[] interests, final int[] ads_source,
                                                 final double latitude, final double longitude,
                                                 final boolean allow_use_current_location) {
        return HyberPlugins.get().restClient()
                .abonentProfileObservable(getCurrentUser().getUniqAppDeviceId(),
                        sex, city, region, fio, age, interests, ads_source, latitude, longitude)
                .timeout(HyberApiClient.STANDARD_TIMEOUT, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<retrofit2.Response<HyberBaseResponseModel>, Boolean>() {
                    @Override
                    public Boolean call(retrofit2.Response<HyberBaseResponseModel> response) {
                        if (response.body().isSuccess()) {
                            HyberCurrentUserDBModel userDBModel =
                                    HyberPlugins.get().getDatabaseHelper().getCurrentUser();
                            userDBModel.setSex(sex);
                            userDBModel.setCity(city);
                            userDBModel.setRegion(region);
                            userDBModel.setFio(fio);
                            userDBModel.setAge(age);
                            userDBModel.setInterests(HyberTools.integerArrayToString(interests));
                            userDBModel.setAds_source(HyberTools.integerArrayToString(ads_source));
                            userDBModel.setLatitude(latitude);
                            userDBModel.setLongitude(longitude);
                            userDBModel.setAllow_use_current_location(allow_use_current_location);
                            HyberPlugins.get().getDatabaseHelper()
                                    .updateCurrentUser(userDBModel);
                        }
                        return response.body().isSuccess();
                    }
                });
    }

    /**
     * Check user login in app or not/
     *
     * @return true if user login.
     */
    public boolean isUserLogin() {
        if (HyberPlugins.get().getDatabaseHelper().getCurrentUser() != null &&
                HyberPlugins.get().getDatabaseHelper().getCurrentUser().getUniqAppDeviceId() > 0)
            return true;
        return false;
    }

    /**
     * Return user profile from SDK storage.
     *
     * @return current user
     */
    public HyberUserProfileModel getCurrentUser() {
        HyberCurrentUserDBModel userDBModel = HyberPlugins.get().getDatabaseHelper().getCurrentUser();
        if (userDBModel != null) {
            return userDBModel.getUserProfileModel();
        } else {
            return null;
        }
    }

    /**
     * Change email for current user.
     *
     * @param email the email
     * @return true if operation executed with success signature.
     */
    public Observable<Boolean> updateUserEmail(final String email) {
        HyberCurrentUserDBModel userDBModel =
                HyberPlugins.get().getDatabaseHelper().getCurrentUser();
        return HyberPlugins.get().restClient()
                .updatePhoneEmailObservable(userDBModel.getUniqAppDeviceId(), 0, email)
                .timeout(HyberApiClient.STANDARD_TIMEOUT, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<retrofit2.Response<HyberBaseResponseModel>, Boolean>() {
                    @Override
                    public Boolean call(retrofit2.Response<HyberBaseResponseModel> response) {
                        if (response.body().isSuccess()) {
                            HyberCurrentUserDBModel userDBModel =
                                    HyberPlugins.get().getDatabaseHelper().getCurrentUser();
                            userDBModel.setEmail(email);
                            HyberPlugins.get().getDatabaseHelper()
                                    .updateCurrentUser(userDBModel);
                        }
                        return response.body().isSuccess();
                    }
                });
    }

    /**
     * Change phone for current user.
     *
     * @param phone the phone
     * @return true if operation executed with success signature.
     */
    public Observable<Boolean> updateUserPhone(final long phone) {
        HyberCurrentUserDBModel userDBModel =
                HyberPlugins.get().getDatabaseHelper().getCurrentUser();
        return HyberPlugins.get().restClient()
                .updatePhoneEmailObservable(userDBModel.getUniqAppDeviceId(), phone, null)
                .timeout(HyberApiClient.STANDARD_TIMEOUT, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<retrofit2.Response<HyberBaseResponseModel>, Boolean>() {
                    @Override
                    public Boolean call(retrofit2.Response<HyberBaseResponseModel> response) {
                        if (response.body().isSuccess()) {
                            HyberCurrentUserDBModel userDBModel =
                                    HyberPlugins.get().getDatabaseHelper().getCurrentUser();
                            userDBModel.setPhone(phone);
                            HyberPlugins.get().getDatabaseHelper()
                                    .updateCurrentUser(userDBModel);
                        }
                        return response.body().isSuccess();
                    }
                });
    }

    /**
     * Authorize your user in app
     * One of field must be not empty (phone == 0 is empty)
     *
     * @param phone the phone
     * @param email the email
     * @return if user logged in, your retrieve current user profile model.
     * @throws IllegalArgumentException the no such field exception
     */
    public Observable<HyberUserProfileModel> loginUser(long phone, String email) throws IllegalArgumentException {
        if (phone == 0 && TextUtils.isEmpty(email)) {
            throw new IllegalArgumentException();
        }
        return HyberPlugins.get().restClient()
                .registrationObservable(phone, email, HyberPlugins.get().getLastKownGcmID())
                .timeout(HyberApiClient.STANDARD_TIMEOUT, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.newThread())
                .flatMap(new Func1<retrofit2.Response<HyberBaseResponseModel>, Observable<HyberUserProfileModel>>() {
                    @Override
                    public Observable<HyberUserProfileModel> call(retrofit2.Response<HyberBaseResponseModel> response) {
                        /**
                         * После удачной авторизации, получаем с сервера
                         * информацию о профиле пользователя.
                         */
                        return HyberPlugins.get().restClient()
                                .getUserProfileObservable(response.body().getUniqAppDeviceId())
                                .timeout(HyberApiClient.STANDARD_TIMEOUT, TimeUnit.SECONDS)
                                .map(new Func1<HyberUserProfileResponseModel, HyberUserProfileModel>() {
                                    @Override
                                    public HyberUserProfileModel call(HyberUserProfileResponseModel profileResponseModel) {
                                        /**
                                         * Обновляем профиль пользователя в базе данных.
                                         */
                                        HyberCurrentUserDBModel userDBModel =
                                                HyberPlugins.get().getDatabaseHelper().getCurrentUser();
                                        userDBModel.updateUserInfo(profileResponseModel);
                                        HyberPlugins.get().getDatabaseHelper()
                                                .updateCurrentUser(userDBModel);
                                        return profileResponseModel.getUserProfileModel();
                                    }
                                });
                    }
                });
    }

    /**
     * When current user make logout,
     * call this method for clean user data from the SDK storage.
     *
     * @return true if operation executed with success signature.
     */
    public Observable<Boolean> logOutUser() {
        return HyberPlugins.get().restClient()
                .allowRecievePushObservable(HyberPlugins.get().getDatabaseHelper()
                        .getCurrentUser().getUniqAppDeviceId(), false)
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<retrofit2.Response<HyberBaseResponseModel>, Boolean>() {
                    @Override
                    public Boolean call(retrofit2.Response<HyberBaseResponseModel> response) {
                        //if (baseResponseModel.isSuccess())
                        HyberPlugins.get().getDatabaseHelper().cleanUserData();
                        return true;//baseResponseModel.isSuccess();
                    }
                });
    }

}
