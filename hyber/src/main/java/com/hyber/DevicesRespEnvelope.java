package com.hyber;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
class DevicesRespEnvelope extends BaseResponse {

    @SerializedName("devices")
    private List<DeviceItemRespModel> devices;

}
