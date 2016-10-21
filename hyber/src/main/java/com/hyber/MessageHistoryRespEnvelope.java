package com.hyber;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class MessageHistoryRespEnvelope extends BaseResponse {

    @SerializedName("limitDays")
    private Integer mLimitDays;

    @SerializedName("limitMessages")
    private Integer mLimitMessages;

    @SerializedName("timeLastMessage")
    private Long mTimeLastMessage;

    @SerializedName("messages")
    private List<MessageRespModel> mMessages;

    @NonNull
    @lombok.NonNull
    Integer getLimitDays() {
        return mLimitDays;
    }

    @NonNull
    @lombok.NonNull
    Integer getLimitMessages() {
        return mLimitMessages;
    }

    @NonNull
    @lombok.NonNull
    Long getTimeLastMessage() {
        return mTimeLastMessage;
    }

    @NonNull
    @lombok.NonNull
    List<MessageRespModel> getMessages() {
        return mMessages;
    }

}
