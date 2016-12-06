package com.hyber;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
class MessageDeliveryReportReqModel {

    @SerializedName("messageId")
    private String messageId;
    @SerializedName("receivedAt")
    private Date receivedAt;

}
