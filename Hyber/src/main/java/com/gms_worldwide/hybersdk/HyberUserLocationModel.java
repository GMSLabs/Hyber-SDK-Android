package com.gms_worldwide.hybersdk;

/**
 * Created by Andrew Kochura.
 */
class HyberUserLocationModel {

    private long uniqAppDeviceId;
    private double latitude;
    private double longitude;

    /**
     * Instantiates a new User location model.
     *
     * @param uniqAppDeviceId the uniq app device id
     * @param latitude        the latitude
     * @param longitude       the longitude
     */
    public HyberUserLocationModel(long uniqAppDeviceId, double latitude, double longitude) {
        this.uniqAppDeviceId = uniqAppDeviceId;
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
