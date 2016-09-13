package com.hyber;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class BidirectionalAnswerReqModel {

    @SerializedName("messageId")
    private String mMessageId;

    @SerializedName("text")
    private String mAnswerText;

    @SerializedName("time")
    private Long mSentAt;

    public BidirectionalAnswerReqModel() {

    }

    public BidirectionalAnswerReqModel(@NonNull String messageId, @NonNull String answerText, @NonNull Long sentAt) {
        this.mMessageId = messageId;
        this.mAnswerText = answerText;
        this.mSentAt = sentAt;
    }

}
