package com.gms_worldwide.hybersdk;

/**
 * Created by Andrew Kochura.
 */
class HyberGetUserProfileRequestModel {
    private long uniqAppDeviceId;

    /**
     * Instantiates a new Get user profile request model.
     *
     * @param uniqAppDeviceId the uniq app device id
     */
    public HyberGetUserProfileRequestModel(long uniqAppDeviceId) {
        this.uniqAppDeviceId = uniqAppDeviceId;
    }

    /**
     * Gets uniq app device id.
     *
     * @return the uniq app device id
     */
    public long getUniqAppDeviceId() {
        return uniqAppDeviceId;
    }
}
