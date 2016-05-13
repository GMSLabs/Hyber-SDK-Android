package com.gms_worldwide.hybersdk;

/**
 * Created by Andrew Kochura.
 */
class HyberRegistrationPushModel {
    private long uniqAppDeviceId;
    private String phone;
    private String email;
    private String gcmTokenId;
    private String device_type = "Android";
    private String device_version = HyberTools.getSDKversion();

    /**
     * Instantiates a new Registration push model.
     *
     * @param uniqAppDeviceId the uniq app device id
     * @param phone           the phone
     * @param email           the email
     * @param gcmTokenId      the gcm token id
     */
    public HyberRegistrationPushModel(long uniqAppDeviceId, String phone, String email, String gcmTokenId) {
        this.uniqAppDeviceId = uniqAppDeviceId;
        this.phone = phone;
        this.email = email;
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
     * Gets phone.
     *
     * @return the phone
     */
    public String getPhone() {
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
