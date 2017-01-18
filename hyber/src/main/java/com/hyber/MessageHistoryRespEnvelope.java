package com.hyber;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
class MessageHistoryRespEnvelope extends BaseResponse {

    @SerializedName("limitDays")
    private Integer limitDays;
    @SerializedName("limitMessages")
    private Integer limitMessages;
    @SerializedName("lastTime")
    private Long lastTime;
    @SerializedName("messages")
    private List<MessageRespModel> messages;

}
