package com.hyber;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

class ErrorRespItemModel {

    @SerializedName("code")
    private Integer mCode;
    @SerializedName("description")
    private String mDescription;

    public ErrorRespItemModel() {

    }

    public ErrorRespItemModel(@NonNull Integer code, @NonNull String description) {
        this.mCode = code;
        this.mDescription = description;
    }

    @Nullable
    Integer getCode() {
        return mCode;
    }

    @Nullable
    String getDescription() {
        return mDescription;
    }

}
