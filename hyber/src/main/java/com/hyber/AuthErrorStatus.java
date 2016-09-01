package com.hyber;

public enum AuthErrorStatus {

    USER_SESSION_DATA_IS_NOT_PROVIDED(2001, "User session data is not provided!"),
    USER_PHONE_INCORRECT(2002, "User phone number incorrect!");

    private int code;
    private String description;

    AuthErrorStatus(int code, String description) {
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
