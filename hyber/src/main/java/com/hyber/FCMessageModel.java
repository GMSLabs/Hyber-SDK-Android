package com.hyber;

import com.google.gson.annotations.SerializedName;

public class FCMessageModel {

    @SerializedName("id")
    private String mId;

    @SerializedName("alpha")
    private String mAlpha;

    @SerializedName("text")
    private String mText;


    public String getId() {
        return mId;
    }

    public String getAlpha() {
        return mAlpha;
    }

    public String getText() {
        return mText;
    }

}
