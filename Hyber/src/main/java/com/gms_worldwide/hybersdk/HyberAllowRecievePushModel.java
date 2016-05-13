package com.gms_worldwide.hybersdk;

/**
 * Created by Andrew Kochura.
 */
class HyberAllowRecievePushModel {

    private long uniqAppDeviceId;
    private int push_allowed;

    /**
     * Instantiates a Hyber allow recieve push model.
     *
     * @param uniqAppDeviceId the uniq app device id
     * @param push_allowed    the push allowed
     */
    public HyberAllowRecievePushModel(long uniqAppDeviceId, int push_allowed) {
        this.uniqAppDeviceId = uniqAppDeviceId;
        this.push_allowed = push_allowed;
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
     * Gets push allowed.
     *
     * @return the push allowed
     */
    public int getPush_allowed() {
        return push_allowed;
    }
}
