package com.hyber;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class MessageHistoryReqModel {

    @SerializedName("startDate")
    private Long mStartDate;

    public MessageHistoryReqModel() {

    }

    public MessageHistoryReqModel(@NonNull Long mStartDate) {
        this.mStartDate = mStartDate;
    }

}
