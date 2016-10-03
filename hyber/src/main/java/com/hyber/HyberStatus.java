package com.hyber;

public enum HyberStatus {

    SDK_ChuckNorrisError(1001, "SDK API Chuck Norris error"),

    SDK_INTEGRATION_ClientApiKeyIsInvalid(1011, "Hyber Client Api Key is invalid"),

    SDK_API_404Error(1021, "Hyber API 404 error"),
    SDK_API_ResponseIsUnsuccessful(1022, "Hyber API response is unsuccessful"),

    SDK_API_notCorrectAuthorizationFormat(1131, "Incorrect auth format"),
    SDK_API_pushSettingsNotFound(1126, "Push not configured"),
    SDK_API_pushAndroidFingerprintIsIncorrect(1152, "Push Android fingerprint is incorrect"),
    //TODO code duplicate SDK_API_phoneNumberNotExist(1104, "Phone number missing"),
    SDK_API_internalException(1127, "Internal server error"),
    SDK_API_bindingFailed(1128, "Malformed request"),

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

    HyberStatus(int code, String description) {
        this.mCode = code;
        this.mDescription = description;
    }

    public static HyberStatus byCode(int code) {
        for (HyberStatus status : HyberStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return HyberStatus.SDK_ChuckNorrisError;
    }

    public Integer getCode() {
        return mCode;
    }

    public String getDescription() {
        return mDescription;
    }

}
