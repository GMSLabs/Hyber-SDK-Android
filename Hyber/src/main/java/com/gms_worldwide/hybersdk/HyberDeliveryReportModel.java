package com.gms_worldwide.hybersdk;

/**
 * Created by Andrew Kochura.
 */
class HyberDeliveryReportModel {

    private long uniqAppDeviceId;
    private long msg_gms_uniq_id;
    private int status;

    /**
     * Instantiates a new Delivery report model.
     *
     * @param uniqAppDeviceId the uniq app device id
     * @param msg_gms_uniq_id the msg gms uniq id
     * @param status          the status
     */
    public HyberDeliveryReportModel(long uniqAppDeviceId, long msg_gms_uniq_id, int status) {
        this.uniqAppDeviceId = uniqAppDeviceId;
        this.msg_gms_uniq_id = msg_gms_uniq_id;
        this.status = status;
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
     * Gets msg gms uniq id.
     *
     * @return the msg gms uniq id
     */
    public long getMsg_gms_uniq_id() {
        return msg_gms_uniq_id;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public int getStatus() {
        return status;
    }
}
