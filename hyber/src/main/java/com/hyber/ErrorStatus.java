package com.hyber;

import java.util.Locale;

enum ErrorStatus {

    hyberChuckNorrisException(1000, "Hyber Chuck Norris exception"),

    hyberApiError(1001, "Hyber API error"),

    hyberIncorrectHeadersFormat(2100, "Headers format in api request is wrong"),
    mobileIncorrectJsonFormat(2101, "Json format in api request is wrong"),
    mobileIncorrectClientApiKey(2110, "The client api key used in the request is incorrect"),
    mobileIncorrectAndroidFingerprint(2111, "The Android fingerprint used in the request is incorrect"),
    mobileIncorrectIosBundleId(2112, "The iOS Bundle Id used in the request is incorrect"),
    mobileIncorrectInstallationId(2113, "The Installation Id used in the request is incorrect"),
    mobileUndefinedErrorInClientSettings(2120, "Unspecified error when checking client settings"),
    mobileApplicationSettingsNotConfigured(2121, "Not configured settings for the client application"),
    mobileMessageHistoryNotConfigured(2122, "History of messages is disabled for the client"),
    mobileBidirectionalSettingsNotConfigured(2123, "Bidirectional answer for messages is disabled for the client"),
    mobileIncorrectUserCredentials(2132, "The user credentials used in the request is incorrect"),
    mobileInvalidToken(2133, "The access token used in the request is incorrect"),
    mobileExpiredToken(2134, "The access token used in the request is expired"),
    mobileInvalidRefreshToken(2135, "The refresh token used in the request is incorrect or not exists"),
    mobileDeviceNotFound(2141, "Device not found"),
    mobileIncorrectMessageId(2142, "Not correct message id");

    private int code;
    private String state;

    ErrorStatus(int code, String state) {
        this.code = code;
        this.state = state;
    }

    public Integer code() {
        return code;
    }

    public String state() {
        return state;
    }

    public static ErrorStatus byCode(int code) {
        for (ErrorStatus status : ErrorStatus.values()) {
            if (status.code() == code) {
                return status;
            }
        }
        return ErrorStatus.hyberChuckNorrisException;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%d ==> %s", code, state);
    }

}
