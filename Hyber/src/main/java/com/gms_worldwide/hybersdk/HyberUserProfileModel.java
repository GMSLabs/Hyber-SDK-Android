package com.gms_worldwide.hybersdk;

/**
 * Created by Andrew Kochura.
 */
public class HyberUserProfileModel {

    private long uniqAppDeviceId;
    private long phone;
    private String email;
    private boolean allowed_receive_push;
    private boolean allowed_use_current_location;
    private int sex;
    private String city;
    private String region;
    private String fio;
    private int age;
    private double latitude;
    private double longitude;
    private int[] interests;
    private int[] ads_source;
    private long created_date;

    /**
     * Instantiates a new User profile model.
     *
     * @param uniqAppDeviceId              the uniq app device id
     * @param phone                        the phone
     * @param email                        the email
     * @param allowed_receive_push         the allowed receive push
     * @param allowed_use_current_location the allowed use current location
     * @param sex                          the sex
     * @param city                         the city
     * @param region                       the region
     * @param fio                          the fio
     * @param age                          the age
     * @param latitude                     the latitude
     * @param longitude                    the longitude
     * @param interests                    the interests
     * @param ads_source                   the ads source
     * @param created_date                 the created date
     */
    public HyberUserProfileModel(long uniqAppDeviceId, long phone, String email,
                                 boolean allowed_receive_push, boolean allowed_use_current_location,
                                 int sex, String city, String region, String fio, int age,
                                 double latitude, double longitude, int[] interests, int[] ads_source, long created_date) {
        this.uniqAppDeviceId = uniqAppDeviceId;
        this.phone = phone;
        this.email = email;
        this.allowed_receive_push = allowed_receive_push;
        this.allowed_use_current_location = allowed_use_current_location;
        this.sex = sex;
        this.city = city;
        this.region = region;
        this.fio = fio;
        this.age = age;
        this.latitude = latitude;
        this.longitude = longitude;
        this.interests = interests;
        this.ads_source = ads_source;
        this.created_date = created_date;
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
     * Gets phone.
     *
     * @return the phone
     */
    public long getPhone() {
        return phone;
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
     * Is allowed use current location boolean.
     *
     * @return the boolean
     */
    public boolean isAllowed_use_current_location() {
        return allowed_use_current_location;
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
     * Gets sex.
     *
     * @return the sex
     */
    public int getSex() {
        return sex;
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
     * Gets region.
     *
     * @return the region
     */
    public String getRegion() {
        return region;
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
     * Gets age.
     *
     * @return the age
     */
    public int getAge() {
        return age;
    }

    /**
     * Gets latitude.
     *
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Gets longitude.
     *
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Get interests int [ ].
     *
     * @return the int [ ]
     */
    public int[] getInterests() {
        return interests;
    }

    /**
     * Get ads source int [ ].
     *
     * @return the int [ ]
     */
    public int[] getAds_source() {
        return ads_source;
    }

    /**
     * Gets created date.
     *
     * @return the created date
     */
    public long getCreated_date() {
        return created_date;
    }
}
