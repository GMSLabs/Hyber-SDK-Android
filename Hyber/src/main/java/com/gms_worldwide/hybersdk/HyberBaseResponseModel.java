package com.gms_worldwide.hybersdk;

/**
 * Created by Andrew Kochura.
 */
class HyberBaseResponseModel {

    private boolean status;
    private long uniqAppDeviceId;
    private String response;

    /**
     * Instantiates a new Base response model.
     *
     * @param status          the status
     * @param uniqAppDeviceId the uniq app device id
     * @param response        the response
     */
    public HyberBaseResponseModel(boolean status, long uniqAppDeviceId, String response) {
        this.status = status;
        this.uniqAppDeviceId = uniqAppDeviceId;
        this.response = response;
    }

    /**
     * Is success boolean.
     *
     * @return the boolean
     */
    public boolean isSuccess() {
        return status;
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
     * Gets response.
     *
     * @return the response
     */
    public String getResponse() {
        return response;
    }

}
