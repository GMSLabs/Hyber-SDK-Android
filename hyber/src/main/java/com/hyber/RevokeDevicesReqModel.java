package com.hyber;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
class RevokeDevicesReqModel {

    @SerializedName("devices")
    private List<String> deviceIds;

}
