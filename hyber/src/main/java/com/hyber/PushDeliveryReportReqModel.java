package com.hyber;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class PushDeliveryReportReqModel {

    @SerializedName("messageId")
    private String mMessageId;

    @SerializedName("receivedAt")
    private Long mReceivedAt;

    public PushDeliveryReportReqModel() {

    }

    public PushDeliveryReportReqModel(@NonNull String messageId, @NonNull Long receivedAt) {
        this.mMessageId = messageId;
        this.mReceivedAt = receivedAt;
    }

}
