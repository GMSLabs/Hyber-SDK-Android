package com.hyber;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

class ErrorRespItemModel {

    @SerializedName("code")
    private Integer mCode;
    @SerializedName("description")
    private Integer mDescription;

    @Nullable
    Integer getCode() {
        return mCode;
    }

    @Nullable
    Integer getDescription() {
        return mDescription;
    }

}
