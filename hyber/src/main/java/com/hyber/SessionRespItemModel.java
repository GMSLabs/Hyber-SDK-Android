package com.hyber;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
class SessionRespItemModel {

    @SerializedName("token")
    private String token;

}
