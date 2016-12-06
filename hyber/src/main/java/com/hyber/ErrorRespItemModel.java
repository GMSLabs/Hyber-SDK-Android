package com.hyber;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
class ErrorRespItemModel {

    @SerializedName("code")
    private Integer code;
    @SerializedName("description")
    private String description;

}
