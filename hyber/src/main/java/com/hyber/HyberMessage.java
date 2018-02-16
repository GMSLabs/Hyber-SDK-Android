package com.hyber;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class HyberMessage {

    @NonNull
    @SerializedName("phone")
    private String phone;
    @NonNull
    @SerializedName("messageId")
    private String messageId;
    @Nullable
    @SerializedName("title")
    private String title;
    @Nullable
    @SerializedName("body")
    private String body;
    @Nullable
    @SerializedName("image")
    private MessageImageItemRespModel image;
    @Nullable
    @SerializedName("button")
    private MessageButtonItemRespModel button;
    @NonNull
    @SerializedName("time")
    private Date time;
    @NonNull
    @SerializedName("partner")
    private String partner;

}
