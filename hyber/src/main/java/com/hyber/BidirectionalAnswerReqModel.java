package com.hyber;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class BidirectionalAnswerReqModel {

    @SerializedName("messageId")
    private String mMessageId;

    @SerializedName("text")
    private String mAnswerText;

    public BidirectionalAnswerReqModel() {

    }

    public BidirectionalAnswerReqModel(@NonNull String messageId, @NonNull String answerText) {
        this.mMessageId = messageId;
        this.mAnswerText = answerText;
    }

}
