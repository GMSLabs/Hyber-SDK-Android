package com.hyber;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

class RefreshTokenReqModel {

    @SerializedName("refreshToken")
    private String mRefreshToken;

    public RefreshTokenReqModel() {

    }

    RefreshTokenReqModel(@NonNull String mRefreshToken) {
        this.mRefreshToken = mRefreshToken;
    }

}
