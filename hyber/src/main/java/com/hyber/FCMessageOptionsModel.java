package com.hyber;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class FCMessageOptionsModel {

    @SerializedName("caption")
    private String mCaptionText;

    @SerializedName("action_url")
    private String mActionUrl;

    @SerializedName("img_url")
    private String mImageUrl;

    @SerializedName("bidirectional_url")
    private String mBidirectionalUrl;


    @Nullable
    public String getCaptionText() {
        return mCaptionText;
    }

    @Nullable
    public String getActionUrl() {
        return mActionUrl;
    }

    @Nullable
    public String getImageUrl() {
        return mImageUrl;
    }

    @Nullable
    public String getBidirectionalUrl() {
        return mBidirectionalUrl;
    }

}
