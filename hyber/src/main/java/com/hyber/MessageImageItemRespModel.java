package com.hyber;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
class MessageImageItemRespModel {

    @SerializedName("url")
    private String url;

}
