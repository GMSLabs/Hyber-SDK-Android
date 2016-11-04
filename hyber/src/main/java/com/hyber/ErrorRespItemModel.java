package com.hyber;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

class ErrorRespItemModel {

    @SerializedName("code")
    private Integer mCode;
    @SerializedName("message")
    private String mMessage;


    @NonNull
    Integer getCode() {
        return mCode;
    }

    @NonNull
    String getMessage() {
        return mMessage;
    }

}
