package com.hyber;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class RegisterUserReqModel {

    @SerializedName("userPhone")
    private Long mUserPhone;

    @SerializedName("osType")
    private String mOsType;

    @SerializedName("deviceName")
    private String mDeviceName;

    @SerializedName("modelName")
    private String mModelName;

    @SerializedName("osVersion")
    private String mAndroidVersion;

    @SerializedName("deviceType")
    private String mDeviceType;

    public RegisterUserReqModel() {

    }

    public RegisterUserReqModel(@NonNull Long mUserPhone, @NonNull String mOsType, @NonNull String mAndroidVersion, @NonNull String mDeviceName, @NonNull String mModelName, @NonNull String mDeviceType) {
        this.mUserPhone = mUserPhone;
        this.mOsType = mOsType;
        this.mDeviceName = mDeviceName;
        this.mModelName = mModelName;
        this.mAndroidVersion = mAndroidVersion;
        this.mDeviceType = mDeviceType;
    }

}
