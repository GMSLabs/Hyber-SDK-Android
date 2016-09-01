package com.hyber;

public enum SendDeviceDataErrorStatus {

    SENDING_UNSUCCESSFUL(3001, "Sending device data unsuccessful!");

    private int code;
    private String description;

    SendDeviceDataErrorStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

}
