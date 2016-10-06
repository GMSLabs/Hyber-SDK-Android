package com.hyber;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

class RegisterUserRespModel extends BaseResponse {

    @SerializedName("userPhone")
    private String mPhone;

    @SerializedName("session")
    private SessionRespItemModel mSession;

    @Nullable
    public String getPhone() {
        return mPhone;
    }

    @Nullable
    public SessionRespItemModel getSession() {
        return mSession;
    }

}
