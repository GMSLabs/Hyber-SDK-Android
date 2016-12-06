package com.hyber;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
class DeviceRegistrationRespModel extends BaseResponse {

    @SerializedName("profile")
    private ProfileRespItemModel profile;
    @SerializedName("session")
    private SessionRespItemModel session;

}
