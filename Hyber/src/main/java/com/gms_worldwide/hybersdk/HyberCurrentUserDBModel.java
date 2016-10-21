package com.gms_worldwide.hybersdk;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by Andrew Kochura.
 */
class HyberCurrentUserDBModel implements Serializable {

    /**
     * The Id.
     */
// id is generated by the database and set on the object default (id = 1) for current user
    @DatabaseField(generatedId = true)
    public int id = HyberConstants.CurrentUserIdInDb;
    @DatabaseField(columnName = "uniqAppDeviceId")
    private long uniqAppDeviceId;
    @DatabaseField(columnName = "gcmTokenId")
    private String gcmTokenId = "";
    @DatabaseField(columnName = "phone")
    private long phone = 0;
    @DatabaseField(columnName = "email")
    private String email = "";
    @DatabaseField(columnName = "lastLatitude")
    private double latitude = 0;
    @DatabaseField(columnName = "lastLongitude")
    private double longitude = 0;
    @DatabaseField(columnName = "fio")
    private String fio = "";
    @DatabaseField(columnName = "region")
    private String region = "";
    @DatabaseField(columnName = "city")
    private String city = "";
    @DatabaseField(columnName = "sex")
    private int sex = -1;
    @DatabaseField(columnName = "age")
    private int age;
    @DatabaseField(columnName = "allow_use_current_location")
    private boolean allow_use_current_location = true;
    @DatabaseField(columnName = "ads_source")
    private String ads_source = "";
    @DatabaseField(columnName = "interests")
    private String interests = "";
    @DatabaseField(columnName = "allowed_receive_push")
    private boolean allowed_receive_push;
    @DatabaseField(columnName = "created_date")
    private long created_date;


    /**
     * Instantiates a Hyber current user db model.
     */
    public HyberCurrentUserDBModel() {

    }

    /**
     * Instantiates a Hyber current user db model.
     *
     * @param id the id
     */
    public HyberCurrentUserDBModel(int id) {
        this.id = id;
    }

    /**
     * Instantiates a Hyber current user db model.
     *
     * @param uniqAppDeviceId            the uniq app device id
     * @param gcmTokenId                 the gcm token id
     * @param phone                      the phone
     * @param email                      the email
     * @param latitude                   the latitude
     * @param longitude                  the longitude
     * @param region                     the region
     * @param city                       the city
     * @param sex                        the sex
     * @param allow_use_current_location the allow use current location
     * @param ads_source                 the ads source
     * @param interests                  the interests
     * @param created_date               the created date
     */
    public HyberCurrentUserDBModel(int uniqAppDeviceId, String gcmTokenId, long phone, String email,
                                   double latitude, double longitude, String fio, String region, String city,
                                   int sex, boolean allow_use_current_location, String ads_source,
                                   String interests, long created_date) {
        this.uniqAppDeviceId = uniqAppDeviceId;
        this.gcmTokenId = gcmTokenId;
        this.phone = phone;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fio = fio;
        this.region = region;
        this.city = city;
        this.sex = sex;
        this.allow_use_current_location = allow_use_current_location;
        this.ads_source = ads_source;
        this.interests = interests;
        this.created_date = created_date;
    }

    /**
     * Instantiates a Hyber current user db model.
     *
     * @param uniqAppDeviceId            the uniq app device id
     * @param gcmTokenId                 the gcm token id
     * @param phone                      the phone
     * @param email                      the email
     * @param latitude                   the latitude
     * @param longitude                  the longitude
     * @param fio                        the fio
     * @param region                     the region
     * @param city                       the city
     * @param sex                        the sex
     * @param age                        the age
     * @param allow_use_current_location the allow use current location
     * @param ads_source                 the ads source
     * @param interests                  the interests
     * @param allowed_receive_push       the allowed receive push
     * @param created_date               the created date
     */
    public HyberCurrentUserDBModel(long uniqAppDeviceId, String gcmTokenId, long phone,
                                   String email, double latitude, double longitude, String fio, String region,
                                   String city, int sex, int age, boolean allow_use_current_location,
                                   String ads_source, String interests, boolean allowed_receive_push, long created_date) {
        this.uniqAppDeviceId = uniqAppDeviceId;
        this.gcmTokenId = gcmTokenId;
        this.phone = phone;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fio = fio;
        this.region = region;
        this.city = city;
        this.sex = sex;
        this.age = age;
        this.allow_use_current_location = allow_use_current_location;
        this.ads_source = ads_source;
        this.interests = interests;
        this.allowed_receive_push = allowed_receive_push;
        this.created_date = created_date;
    }

    /**
     * Update user info.
     *
     * @param hyberUserProfileResponseModel the user profile response model
     */
    public void updateUserInfo(HyberUserProfileResponseModel hyberUserProfileResponseModel) {
        this.uniqAppDeviceId = hyberUserProfileResponseModel.getUniqAppDeviceId();
        this.phone = hyberUserProfileResponseModel.getPhone();
        this.email = hyberUserProfileResponseModel.getEmail();
        this.latitude = hyberUserProfileResponseModel.getLatitude();
        this.longitude = hyberUserProfileResponseModel.getLongitude();
        this.fio = hyberUserProfileResponseModel.getFio();
        this.region = hyberUserProfileResponseModel.getRegion();
        this.city = hyberUserProfileResponseModel.getCity();
        this.sex = hyberUserProfileResponseModel.getSex();
        this.age = hyberUserProfileResponseModel.getAge();
        this.ads_source = HyberTools.integerArrayToString(hyberUserProfileResponseModel.getAds_source());
        this.interests = HyberTools.integerArrayToString(hyberUserProfileResponseModel.getInterests());
        this.allowed_receive_push = hyberUserProfileResponseModel.getAllowed_receive_push() == 1;
        this.created_date = hyberUserProfileResponseModel.getCreated_date();
    }

    /**
     * Gets user profile model.
     *
     * @return the user profile model
     */
    public HyberUserProfileModel getUserProfileModel() {
        return new HyberUserProfileModel(uniqAppDeviceId, phone, email,
                allowed_receive_push, allow_use_current_location, sex, city, region, fio,
                age, latitude, longitude,
                HyberTools.converStringToIntegerArray(interests),
                HyberTools.converStringToIntegerArray(ads_source), created_date);
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets uniq app device id.
     *
     * @return the uniq app device id
     */
    public long getUniqAppDeviceId() {
        return uniqAppDeviceId;
    }

    /**
     * Sets uniq app device id.
     *
     * @param uniqAppDeviceId the uniq app device id
     */
    public void setUniqAppDeviceId(long uniqAppDeviceId) {
        this.uniqAppDeviceId = uniqAppDeviceId;
    }

    /**
     * Gets gcm token id.
     *
     * @return the gcm token id
     */
    public String getGcmTokenId() {
        return gcmTokenId;
    }

    /**
     * Sets gcm token id.
     *
     * @param gcmTokenId the gcm token id
     */
    public void setGcmTokenId(String gcmTokenId) {
        this.gcmTokenId = gcmTokenId;
    }

    /**
     * Gets phone.
     *
     * @return the phone
     */
    public long getPhone() {
        return phone;
    }

    /**
     * Sets phone.
     *
     * @param phone the phone
     */
    public void setPhone(long phone) {
        this.phone = phone;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets latitude.
     *
     * @return the latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Sets latitude.
     *
     * @param latitude the latitude
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets longitude.
     *
     * @return the longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Sets longitude.
     *
     * @param longitude the longitude
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets fio.
     *
     * @return the fio
     */
    public String getFio() {
        return fio;
    }

    /**
     * Sets fio.
     *
     * @param fio the region
     */
    public void setFio(String fio) {
        this.fio = fio;
    }

    /**
     * Gets region.
     *
     * @return the region
     */
    public String getRegion() {
        return region;
    }

    /**
     * Sets region.
     *
     * @param region the region
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * Gets city.
     *
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets city.
     *
     * @param city the city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets sex.
     *
     * @return the sex
     */
    public int getSex() {
        return sex;
    }

    /**
     * Sets sex.
     *
     * @param sex the sex
     */
    public void setSex(int sex) {
        this.sex = sex;
    }

    /**
     * Gets age.
     *
     * @return the age
     */
    public int getAge() {
        return age;
    }

    /**
     * Sets age.
     *
     * @param age the age
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Is allow use current location boolean.
     *
     * @return the boolean
     */
    public boolean isAllow_use_current_location() {
        return allow_use_current_location;
    }

    /**
     * Sets allow use current location.
     *
     * @param allow_use_current_location the allow use current location
     */
    public void setAllow_use_current_location(boolean allow_use_current_location) {
        this.allow_use_current_location = allow_use_current_location;
    }

    /**
     * Gets ads source.
     *
     * @return the ads source
     */
    public String getAds_source() {
        return ads_source;
    }

    /**
     * Sets ads source.
     *
     * @param ads_source the ads source
     */
    public void setAds_source(String ads_source) {
        this.ads_source = ads_source;
    }

    /**
     * Gets interests.
     *
     * @return the interests
     */
    public String getInterests() {
        return interests;
    }

    /**
     * Sets interests.
     *
     * @param interests the interests
     */
    public void setInterests(String interests) {
        this.interests = interests;
    }

    /**
     * Is allowed receive push boolean.
     *
     * @return the boolean
     */
    public boolean isAllowed_receive_push() {
        return allowed_receive_push;
    }

    /**
     * Sets allowed receive push.
     *
     * @param allowed_receive_push the allowed receive push
     */
    public void setAllowed_receive_push(boolean allowed_receive_push) {
        this.allowed_receive_push = allowed_receive_push;
    }
}