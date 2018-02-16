package com.hyber;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
class MessageCallbackRespModel {

    @SerializedName("messageId")
    private String messageId;
    @SerializedName("acceptedAt")
    private String acceptedAt;

}
