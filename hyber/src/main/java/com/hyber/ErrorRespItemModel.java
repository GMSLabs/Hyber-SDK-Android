package com.hyber;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

class ErrorRespItemModel {

    @SerializedName("code")
    private Integer mCode;
    @SerializedName("description")
    private Integer mDescription;

    @Nullable
    public Integer getCode() {
        return mCode;
    }

    @Nullable
    public Integer getDescription() {
        return mDescription;
    }

}
