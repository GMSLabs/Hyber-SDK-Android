package com.hyber;

import com.google.gson.annotations.SerializedName;

public class UpdateUserReqModel {

    @SerializedName("fcmToken")
    private String mFcmToken;

    @SerializedName("priority")
    private Integer mPriority;

    @SerializedName("osType")
    private String mOsType;

    @SerializedName("osVersion")
    private String mOsVersion;

    @SerializedName("deviceName")
    private String mDeviceName;

    @SerializedName("modelName")
    private String mModelName;

    @SerializedName("deviceType")
    private String mDeviceType;

    public UpdateUserReqModel() {

    }

    public UpdateUserReqModel(String mFcmToken) {
        this.mFcmToken = mFcmToken;
    }

    public UpdateUserReqModel(String mOsType, String mOsVersion, String mDeviceName, String mModelName, String mDeviceType) {
        this.mOsType = mOsType;
        this.mOsVersion = mOsVersion;
        this.mDeviceName = mDeviceName;
        this.mModelName = mModelName;
        this.mDeviceType = mDeviceType;
    }

    public UpdateUserReqModel(String mFcmToken, String mOsType, String mOsVersion, String mDeviceName, String mModelName, String mDeviceType) {
        this.mFcmToken = mFcmToken;
        this.mOsType = mOsType;
        this.mOsVersion = mOsVersion;
        this.mDeviceName = mDeviceName;
        this.mModelName = mModelName;
        this.mDeviceType = mDeviceType;
    }

    public UpdateUserReqModel(String mFcmToken, Integer mPriority, String mOsType, String mOsVersion, String mDeviceName, String mModelName, String mDeviceType) {
        this.mFcmToken = mFcmToken;
        this.mPriority = mPriority;
        this.mOsType = mOsType;
        this.mOsVersion = mOsVersion;
        this.mDeviceName = mDeviceName;
        this.mModelName = mModelName;
        this.mDeviceType = mDeviceType;
    }

}
