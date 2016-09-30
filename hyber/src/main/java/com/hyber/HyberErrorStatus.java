package com.hyber;

public enum HyberErrorStatus {

    INTERNAL_ChuckNorrisException(1001, "Internal SDK Chuck Norris exception"),

    SDK_API_mobileOsTypeNotFound(1101, "os type not found"),
    SDK_API_mobileOsVersionNotFound(1102, "os version not found"),
    SDK_API_mobileDeviceTypeNotFound(1103, "device type not found"),
    SDK_API_notCorrectAuthorizationDataOrTokenExpired(1104, "not correct authorization data or token has expired"),
    SDK_API_refreshTokenNotExist(1105, "refreshToken not exist or not correct"),
    SDK_API_mobileStartDateGetMessagesNotExist(1106, "startDate not exist"),
    SDK_API_mobileNotAllowedGetMessagesHistory(1107, "not allowed get messages history"),
    SDK_API_pushIncorrectAuthenticationData(1108, "incorrect authorization data"),
    SDK_API_pushTokenExpired(1109, "token is expired"),
    SDK_API_incorrectMessageId(1110, "incorrect message ID"),
    SDK_API_mobileNotCorrectAuthToken(1111, "not correctAuthToken"),
    SDK_API_mobileAuthTokenExpired(1112, "authToken has expired"),
    SDK_API_mobileAbonentWithInstallationIdNotFound(1113, "abonent with authToken not found");

    private int mCode;
    private String mDescription;

    HyberErrorStatus(int code, String description) {
        this.mCode = code;
        this.mDescription = description;
    }

    public Integer getCode() {
        return mCode;
    }

    public String getDescription() {
        return mDescription;
    }

}
