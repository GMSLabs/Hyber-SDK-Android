package com.hyber;

import com.google.gson.annotations.SerializedName;

import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
class UpdateDeviceReqModel extends BaseDeviceReqModel {

    @NonNull
    @SerializedName("fcmToken")
    private String fcmToken;

    UpdateDeviceReqModel(String fcmToken, String osType, String osVersion, String deviceType, String deviceName, String sdkVersion) {
        super(osType, osVersion, deviceType, deviceName, sdkVersion);
        this.fcmToken = fcmToken;
    }

}
