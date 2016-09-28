package com.hyber;

enum SendPushTokenErrorStatus {

    SENDING_UNSUCCESSFUL(3001, "Sending push token unsuccessful!");

    private int code;
    private String description;

    SendPushTokenErrorStatus(int code, String description) {
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
