package com.hyber;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
class MessageButtonItemRespModel {

    @SerializedName("text")
    private String text;
    @SerializedName("url")
    private String url;

}
