package com.hyber;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

class MessageOptionsRespModel {

    @SerializedName("img")
    private String mImageUrl;

    @SerializedName("action")
    private String mAction;

    @SerializedName("caption")
    private String mCaption;


    @Nullable
    String getImageUrl() {
        return mImageUrl;
    }

    @Nullable
    String getAction() {
        return mAction;
    }

    @Nullable
    String getCaption() {
        return mCaption;
    }

}
