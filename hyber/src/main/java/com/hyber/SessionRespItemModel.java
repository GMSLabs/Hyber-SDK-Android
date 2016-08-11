package com.hyber;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

class SessionRespItemModel {

    @SerializedName("authToken")
    private String mToken;
    @SerializedName("refreshToken")
    private String mRefreshToken;
    @SerializedName("expirationDate")
    private Date mExpirationDate;

    @Nullable
    public String getToken() {
        return mToken;
    }

    @Nullable
    public String getRefreshToken() {
        return mRefreshToken;
    }

    @Nullable
    public Date getExpirationDate() {
        return mExpirationDate;
    }

}
