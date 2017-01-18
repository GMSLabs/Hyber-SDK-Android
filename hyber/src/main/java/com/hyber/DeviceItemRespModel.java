package com.hyber;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DeviceItemRespModel {

    @SerializedName("id")
    private String id;
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
    @SerializedName("createdAt")
    private Date createdAt;
    @SerializedName("updatedAt")
    private Date updatedAt;

}
