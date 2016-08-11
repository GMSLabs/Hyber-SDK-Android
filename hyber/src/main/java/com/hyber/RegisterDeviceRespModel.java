package com.hyber;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class RegisterDeviceRespModel extends BaseResponce {

    @SerializedName("session")
    private SessionRespItemModel mSession;

    @Nullable
    public SessionRespItemModel getSession() {
        return mSession;
    }

}
