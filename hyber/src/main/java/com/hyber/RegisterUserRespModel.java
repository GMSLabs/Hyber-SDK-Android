package com.hyber;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

class RegisterUserRespModel extends BaseResponse {

    @SerializedName("profile")
    private ProfileRespItemModel mProfile;

    @SerializedName("session")
    private SessionRespItemModel mSession;

    @Nullable
    public ProfileRespItemModel getProfile() {
        return mProfile;
    }

    @Nullable
    public SessionRespItemModel getSession() {
        return mSession;
    }

}
