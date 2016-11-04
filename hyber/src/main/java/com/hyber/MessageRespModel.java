package com.hyber;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

class MessageRespModel {

    @SerializedName("messageId")
    private String mId;

    @SerializedName("partner")
    private String mPartner;

    @SerializedName("time")
    private Long mTime;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("body")
    private String mBody;

    @SerializedName("ownerPhone")
    private String mOrder;

    @SerializedName("image")
    private MessageImageRespModel mImage;

    @SerializedName("button")
    private MessageButtonRespModel mButton;


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
    public MessageImageRespModel getImage() {
        return mImage;
    }

    @Nullable
    public MessageButtonRespModel getButton() {
        return mButton;
    }

}
