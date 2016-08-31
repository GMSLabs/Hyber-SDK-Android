package com.hyber;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class HyberMessageModel {

    @SerializedName("mess_id")
    private String mId;

    @SerializedName("alpha")
    private String mAlpha;

    @SerializedName("text")
    private String mText;

    @SerializedName("options")
    private FCMessageOptionsModel mOptions;


    @Nullable
    public String getId() {
        return mId;
    }

    @Nullable
    public String getAlpha() {
        return mAlpha;
    }

    @Nullable
    public String getText() {
        return mText;
    }

    @Nullable
    public FCMessageOptionsModel getOptions() {
        return mOptions;
    }

}
