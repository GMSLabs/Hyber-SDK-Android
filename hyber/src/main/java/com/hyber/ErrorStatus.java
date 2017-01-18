package com.hyber;

import com.hyber.handler.HyberError.HyberErrorStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.hyber.handler.HyberError.HyberErrorStatus.INCORRECT_DEVICE_ID;
import static com.hyber.handler.HyberError.HyberErrorStatus.INCORRECT_MESSAGE_ID;
import static com.hyber.handler.HyberError.HyberErrorStatus.INTERNAL_ERROR;
import static com.hyber.handler.HyberError.HyberErrorStatus.MESSAGE_CALLBACK_NOT_ALLOWED;
import static com.hyber.handler.HyberError.HyberErrorStatus.MESSAGE_HISTORY_INCORRECT_START_TIME;
import static com.hyber.handler.HyberError.HyberErrorStatus.MESSAGE_HISTORY_NOT_ALLOWED;
import static com.hyber.handler.HyberError.HyberErrorStatus.SDK_CONFIGURED_INCORRECTLY;
import static com.hyber.handler.HyberError.HyberErrorStatus.TOKEN_IS_EXPIRED;
import static com.hyber.handler.HyberError.HyberErrorStatus.UNAUTHORIZED;


@NoArgsConstructor
@Getter
public enum ErrorStatus {

    SDK_API_ChuckNorrisError(1001, INTERNAL_ERROR, "SDK API Chuck Norris error"),

    SDK_INTEGRATION_ClientApiKeyIsInvalid(1011, SDK_CONFIGURED_INCORRECTLY, "Hyber Client Api Key is invalid"),

    /**
     * Start errors with HTTP Status 400
     **/
    mobileIncorrectHeadersFormat(2100, INTERNAL_ERROR, "Headers format is incorrect"),
    mobileIncorrectJsonFormat(2101, INTERNAL_ERROR, "Json format is incorrect"),
    mobilePushSettingsNotConfigured(2201, SDK_CONFIGURED_INCORRECTLY, "Mobile settings not configured"),
    mobileIncorrectClientApiKey(2202, SDK_CONFIGURED_INCORRECTLY, "The Client API key is incorrect"),
    mobileIncorrectAndroidFingerprint(2203, SDK_CONFIGURED_INCORRECTLY, "The Android Fingerprint is incorrect"),
    mobileIncorrectIosBundleId(2204, SDK_CONFIGURED_INCORRECTLY, "The iOS Bundle id is incorrect"),
    /** End errors with HTTP Status 400 **/

    /**
     * Start errors with HTTP Status 401
     **/
    mobileIncorrectPhoneOrPassword(2301, UNAUTHORIZED, "Incorrect phone or password"),
    mobileIncorrectSessionId(2302, UNAUTHORIZED, "The session id is incorrect"),
    mobileIncorrectAccessToken(2303, UNAUTHORIZED, "The access token is incorrect"),
    /** End errors with HTTP Status 401 **/

    /**
     * Start errors with HTTP Status 200
     **/
    mobileExpiredTimestamp(2401, TOKEN_IS_EXPIRED, "The timestamp is expired"),
    mobileMessageHistoryNotAllowed(2402, MESSAGE_HISTORY_NOT_ALLOWED, "The message history not allowed"),
    mobileMessageHistoryIncorrectStartTime(2403, MESSAGE_HISTORY_INCORRECT_START_TIME, "Incorrect message history start time"),
    mobileMessageCallbackNotAllowed(2404, MESSAGE_CALLBACK_NOT_ALLOWED, "The bidirectional answer not allowed"),
    mobileIncorrectMessageId(2405, INCORRECT_MESSAGE_ID, "The message id is incorrect"),
    mobileIncorrectDeviceId(2406, INCORRECT_DEVICE_ID, "The device id is incorrect");
    /**
     * End errors with HTTP Status 200
     **/

    private int code;
    private String description;
    private HyberErrorStatus externalHyberErrorStatus;

    ErrorStatus(int code, HyberErrorStatus externalHyberErrorStatus, String description) {
        this.code = code;
        this.externalHyberErrorStatus = externalHyberErrorStatus;
        this.description = description;
    }

    public static ErrorStatus byCode(int code) {
        for (ErrorStatus status : ErrorStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return ErrorStatus.SDK_API_ChuckNorrisError;
    }

}
