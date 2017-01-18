package com.hyber;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
@Getter
class MessageDeliveryReportRespModel {

    @SerializedName("messageId")
    private String messageId;
    @SerializedName("acceptedAt")
    private Date acceptedAt;

}
