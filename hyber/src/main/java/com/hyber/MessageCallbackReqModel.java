package com.hyber;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
class MessageCallbackReqModel {

    @SerializedName("messageId")
    private String messageId;
    @SerializedName("answer")
    private String answer;

}
