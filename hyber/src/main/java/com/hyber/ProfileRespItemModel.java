package com.hyber;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

class ProfileRespItemModel {

    @SerializedName("userId")
    private String mUserId;
    @SerializedName("userPhone")
    private String mUserPhone;

    @NonNull
    public String getUserId() {
        return mUserId;
    }

    @NonNull
    public String getUserPhone() {
        return mUserPhone;
    }

}
