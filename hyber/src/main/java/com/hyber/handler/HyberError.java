package com.hyber.handler;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter
@ToString
public class HyberError {

    public enum HyberErrorStatus {
        SDK_CONFIGURED_INCORRECTLY,
        INTERNAL_ERROR,
        UNAUTHORIZED,
        TOKEN_IS_EXPIRED,
        MESSAGE_HISTORY_NOT_ALLOWED,
        MESSAGE_HISTORY_INCORRECT_START_TIME,
        MESSAGE_CALLBACK_NOT_ALLOWED,
        INCORRECT_MESSAGE_ID,
        INCORRECT_DEVICE_ID
    }

    @NonNull
    private HyberErrorStatus status;
    @Nullable
    private String message;

    public HyberError(HyberErrorStatus status) {
        this.status = status;
    }

    public HyberError(HyberErrorStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
