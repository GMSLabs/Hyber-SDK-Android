package com.hyber;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

class MessageModel {

    @SerializedName("mess_id")
    private String mId;

    @SerializedName("alpha")
    private String mAlpha;

    @SerializedName("text")
    private String mText;

    @SerializedName("options")
    private MessageOptionsModel mOptions;


    @NonNull
    @lombok.NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    @lombok.NonNull
    public String getAlpha() {
        return mAlpha;
    }

    @NonNull
    @lombok.NonNull
    public String getText() {
        return mText;
    }

    @Nullable
    public MessageOptionsModel getOptions() {
        return mOptions;
    }

}
