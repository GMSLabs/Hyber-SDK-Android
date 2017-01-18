package com.hyber;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
class DeviceUpdateReqModel {

    @SerializedName("fcmToken")
    private String fcmToken;
    @SerializedName("osType")
    private String osType;
    @SerializedName("osVersion")
    private String osVersion;
    @SerializedName("deviceType")
    private String deviceType;
    @SerializedName("deviceName")
    private String deviceName;
    @SerializedName("sdkVersion")
    private String sdkVersion;

}
