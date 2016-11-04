package com.hyber;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

class MessageImageRespModel {

    @SerializedName("url")
    private String mUrl;

    @NonNull
    public String getUrl() {
        return mUrl;
    }

}
