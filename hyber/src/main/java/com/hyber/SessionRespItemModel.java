package com.hyber;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

class SessionRespItemModel {

    @SerializedName("authToken")
    private String mToken;
    @SerializedName("refreshToken")
    private String mRefreshToken;
    @SerializedName("expirationDate")
    private Date mExpirationDate;

    @NonNull
    public String getToken() {
        return mToken;
    }

    @NonNull
    public String getRefreshToken() {
        return mRefreshToken;
    }

    @NonNull
    public Date getExpirationDate() {
        return mExpirationDate;
    }

}
