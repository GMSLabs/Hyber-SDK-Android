package com.hyber;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MessageRespModel {

    @SerializedName("phone")
    private String phone;
    @SerializedName("messageId")
    private String messageId;
    @SerializedName("title")
    private String title;
    @SerializedName("body")
    private String body;
    @SerializedName("image")
    private MessageImageItemRespModel image;
    @SerializedName("button")
    private MessageButtonItemRespModel button;
    @SerializedName("time")
    private Date time;
    @SerializedName("partner")
    private String partner;

}
