package com.hyber;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

class MessageRespModel {

    @SerializedName("messageId")
    private String mId;

    @SerializedName("partner")
    private String mPartner;

    @SerializedName("drTime")
    private Long mTime;

    @SerializedName("from")
    private String mTitle;

    @SerializedName("text")
    private String mBody;

    @SerializedName("to")
    private String mOrder;

    @SerializedName("options")
    private MessageOptionsRespModel mOptions;


    @NonNull
    @lombok.NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    @lombok.NonNull
    String getPartner() {
        return mPartner;
    }

    @NonNull
    @lombok.NonNull
    Long getTime() {
        return mTime;
    }

    @Nullable
    String getTitle() {
        return mTitle;
    }

    @Nullable
    String getBody() {
        return mBody;
    }

    @NonNull
    @lombok.NonNull
    String getOrder() {
        return mOrder;
    }

    @Nullable
    MessageOptionsRespModel getOptions() {
        return mOptions;
    }

}
