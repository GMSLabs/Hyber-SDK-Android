package com.hyber;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MessageHistoryRespEnvelope extends BaseResponce {

    @SerializedName("limitDays")
    private Integer mLimitDays;

    @SerializedName("limitMessages")
    private Integer mLimitMessages;

    @SerializedName("timeLastMessage")
    private Long mTimeLastMessage;

    @SerializedName("messages")
    private List<MessageRespModel> mMessages;

    @Nullable
    Integer getLimitDays() {
        return mLimitDays;
    }

    @Nullable
    Integer getLimitMessages() {
        return mLimitMessages;
    }

    @Nullable
    public Long getTimeLastMessage() {
        return mTimeLastMessage;
    }

    @Nullable
    List<MessageRespModel> getMessages() {
        return mMessages;
    }

}
