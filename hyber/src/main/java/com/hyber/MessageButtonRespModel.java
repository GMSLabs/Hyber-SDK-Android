package com.hyber;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

class MessageButtonRespModel {

    @SerializedName("text")
    private String mText;

    @SerializedName("url")
    private String mUrl;


    @Nullable
    public String getText() {
        return mText;
    }

    @Nullable
    public String getUrl() {
        return mUrl;
    }

}
