package com.hyber;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
class MessageCallbackReqModel {

    @SerializedName("messageId")
    private String messageId;
    @SerializedName("answer")
    private String answer;

}
