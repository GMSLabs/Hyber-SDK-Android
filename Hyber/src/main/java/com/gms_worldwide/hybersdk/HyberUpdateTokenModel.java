package com.gms_worldwide.hybersdk;

/**
 * Created by Andrew Kochura.
 */
class HyberUpdateTokenModel {
    private long uniqAppDeviceId;
    private String gcmTokenId;
    private String device_type = "Android";
    private String device_version = HyberTools.getSDKversion();

    /**
     * Instantiates a new Update token model.
     *
     * @param uniqAppDeviceId the uniq app device id
     * @param gcmTokenId      the gcm token id
     */
    public HyberUpdateTokenModel(long uniqAppDeviceId, String gcmTokenId) {
        this.uniqAppDeviceId = uniqAppDeviceId;
        this.gcmTokenId = gcmTokenId;
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
     * Gets gcm token id.
     *
     * @return the gcm token id
     */
    public String getGcmTokenId() {
        return gcmTokenId;
    }

    /**
     * Gets device type.
     *
     * @return the device type
     */
    public String getDevice_type() {
        return device_type;
    }

    /**
     * Gets device version.
     *
     * @return the device version
     */
    public String getDevice_version() {
        return device_version;
    }
}
