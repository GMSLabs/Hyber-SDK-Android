package com.hyber;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

class MessageHistoryReqModel {

    @SerializedName("startDate")
    private Long mStartDate;

    MessageHistoryReqModel() {

    }

    MessageHistoryReqModel(@NonNull Long mStartDate) {
        this.mStartDate = mStartDate;
    }

}
