package com.hyber;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

class RefreshTokenRespModel extends BaseResponse {

    @SerializedName("session")
    private SessionRespItemModel mSession;

    @Nullable
    public SessionRespItemModel getSession() {
        return mSession;
    }

}
