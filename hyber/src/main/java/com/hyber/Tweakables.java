package com.hyber;

final class Tweakables {

    static final int STANDARD_API_TIMEOUT_SECONDS = 3;
    static final String X_HYBER_SDK_VERSION = "X-Hyber-SDK-Version";
    static final String X_HYBER_CLIENT_API_KEY = "X-Hyber-Client-API-Key";
    static final String X_HYBER_APP_FINGERPRINT = "X-Hyber-App-Fingerprint";
    static final String X_HYBER_INSTALLATION_ID = "X-Hyber-Installation-Id";

    static final String X_HYBER_AUTH_TOKEN = "X-Hyber-Auth-Token";

    static final String HAWK_HYBER_SENT_PUSH_TOKEN = "sentPushToken";
    static final String HAWK_HYBER_AUTH_TOKEN = "authToken";
    static final String HAWK_HYBER_REFRESH_TOKEN = "refreshToken";
    static final String HAWK_HYBER_TOKEN_EXP_DATE = "expirationDate";

    static final String API_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss Z";

    private Tweakables() {

    }
}
