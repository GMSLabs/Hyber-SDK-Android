package com.hyber;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class MessageRespModel {

    @SerializedName("messageId")
    private String mId;

    @SerializedName("partner")
    private String mPartner;

    @SerializedName("drTime")
    private Long mTime;

    @SerializedName("from")
    private String mAlphaName;

    @SerializedName("text")
    private String mText;

    @SerializedName("to")
    private String mOrder;

    @SerializedName("options")
    private MessageOptionsRespModel mOptions;


    @Nullable
    public String getId() {
        return mId;
    }

    @Nullable
    String getPartner() {
        return mPartner;
    }

    @Nullable
    Long getTime() {
        return mTime;
    }

    @Nullable
    String getAlphaName() {
        return mAlphaName;
    }

    @Nullable
    String getText() {
        return mText;
    }

    @Nullable
    String getOrder() {
        return mOrder;
    }

    @Nullable
    MessageOptionsRespModel getOptions() {
        return mOptions;
    }

    @Nullable
    Message toRealmMessageHistory() {
        if (mId != null && mPartner != null && mTime != null &&
                mAlphaName != null && mText != null && mOrder != null) {
            Message mh = new Message(mId, mPartner, new Date(mTime),
                    mAlphaName, mText, mOrder);

            if (mOptions != null)
                mh.setOptions(mOptions.getImageUrl(), mOptions.getAction(),
                        mOptions.getCaption(), mOptions.getBiDirUrl());

            mh.setAsFromHistory();
            return mh;
        } else {
            return null;
        }
    }

}
