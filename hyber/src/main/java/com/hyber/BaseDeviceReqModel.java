package com.hyber;

import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor
@RequiredArgsConstructor(suppressConstructorProperties = true)
class BaseDeviceReqModel {

    @NonNull
    @SerializedName("osType")
    private String osType;

    @NonNull
    @SerializedName("osVersion")
    private String osVersion;

    @NonNull
    @SerializedName("deviceType")
    private String deviceType;

    @NonNull
    @SerializedName("deviceName")
    private String deviceName;

    @NonNull
    @SerializedName("sdkVersion")
    private String sdkVersion;

}
