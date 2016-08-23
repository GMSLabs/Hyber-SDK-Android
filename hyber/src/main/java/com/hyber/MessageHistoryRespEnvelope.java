package com.hyber;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MessageHistoryRespEnvelope extends BaseResponce {

    @SerializedName("limitDays")
    private Integer mLimitDays;

    @SerializedName("limitMessages")
    private Integer mLimitMessages;

    @SerializedName("messages")
    private List<MessageRespModel> mMessages;

    @NonNull
    Integer getLimitDays() {
        return mLimitDays;
    }

    @NonNull
    Integer getLimitMessages() {
        return mLimitMessages;
    }

    @NonNull
    List<MessageRespModel> getMessages() {
        return mMessages;
    }

}
