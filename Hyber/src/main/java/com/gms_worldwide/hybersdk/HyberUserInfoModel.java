package com.gms_worldwide.hybersdk;

/**
 * Created by Andrew Kochura.
 */
class HyberUserInfoModel {

    private long uniqAppDeviceId;
    private int sex;
    private String city;
    private String region;
    private String fio;
    private int age;
    private int[] interests;
    private int[] ads_source;
    private double latitude;
    private double longitude;

    /**
     * Instantiates a new User info model.
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
     */
    public HyberUserInfoModel(long uniqAppDeviceId, int sex, String city, String region, String fio, int age,
                              int[] interests, int[] ads_source, double latitude, double longitude) {
        this.uniqAppDeviceId = uniqAppDeviceId;
        this.sex = sex;
        this.city = city;
        this.region = region;
        this.fio = fio;
        this.age = age;
        this.interests = interests;
        this.ads_source = ads_source;
        this.latitude = latitude;
        this.longitude = longitude;
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
}
