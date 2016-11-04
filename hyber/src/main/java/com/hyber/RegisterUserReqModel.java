package com.hyber;

import com.google.gson.annotations.SerializedName;

import lombok.NoArgsConstructor;

@NoArgsConstructor
class RegisterUserReqModel extends BaseDeviceReqModel {

    @lombok.NonNull
    @SerializedName("userPhone")
    private String userPhone;

    RegisterUserReqModel(String userPhone, String osType, String osVersion, String deviceType, String deviceName, String sdkVersion) {
        super(osType, osVersion, deviceType, deviceName, sdkVersion);
        this.userPhone = userPhone;
    }

}
