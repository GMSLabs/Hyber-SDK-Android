package com.hyber;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
class BaseResponse {

    @SerializedName("error")
    private ErrorRespItemModel error;

}
