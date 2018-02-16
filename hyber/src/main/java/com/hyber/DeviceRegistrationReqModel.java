package com.hyber;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
class DeviceRegistrationReqModel {

    @SerializedName("userPhone")
    private String userPhone;
    @SerializedName("userPass")
    private String userPass;
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
