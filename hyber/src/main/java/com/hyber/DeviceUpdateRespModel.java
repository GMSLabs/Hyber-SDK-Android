package com.hyber;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
class DeviceUpdateRespModel extends BaseResponse {

    @SerializedName("deviceId")
    private String deviceId;

}
