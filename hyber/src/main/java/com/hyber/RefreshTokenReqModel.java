package com.hyber;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class RefreshTokenReqModel {

    @SerializedName("refreshToken")
    private String mRefreshToken;

    public RefreshTokenReqModel() {

    }

    public RefreshTokenReqModel(@NonNull String mRefreshToken) {
        this.mRefreshToken = mRefreshToken;
    }

}
