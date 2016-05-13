package com.gms_worldwide.hybersdk;

/**
 * Created by Andrew Kochura.
 */
class HyberUpdatePhoneEmailModel {
    private long uniqAppDeviceId;
    private String phone;
    private String email;

    /**
     * Instantiates a new Update phone email model.
     *
     * @param uniqAppDeviceId the uniq app device id
     * @param phone           the phone
     * @param email           the email
     */
    public HyberUpdatePhoneEmailModel(long uniqAppDeviceId, String phone, String email) {
        this.uniqAppDeviceId = uniqAppDeviceId;
        this.phone = phone;
        this.email = email;
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

}
