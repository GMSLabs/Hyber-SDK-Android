package com.hyber;

import android.support.annotation.NonNull;

public class HyberError extends Exception {

    private HyberErrorStatus mStatus;

    HyberError() {
        super();
        this.mStatus = HyberErrorStatus.INTERNAL_ChuckNorrisException;
    }

    HyberError(@NonNull HyberErrorStatus status) {
        super(status.getDescription());
        this.mStatus = status;
    }

    HyberError(@NonNull HyberErrorStatus status, @NonNull Throwable cause) {
        super(status.getDescription(), cause);
        this.mStatus = status;
    }

    HyberError(@NonNull Throwable cause) {
        super(cause);
        this.mStatus = HyberErrorStatus.INTERNAL_ChuckNorrisException;
    }

    @NonNull
    public Integer getCode() {
        return mStatus.getCode();
    }

    @NonNull
    public String getDescription() {
        return mStatus.getDescription();
    }

}
