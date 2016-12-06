package com.hyber;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
class ProfileRespItemModel {

    @SerializedName("userId")
    private String userId;
    @SerializedName("userPhone")
    private String userPhone;
    @SerializedName("createdAt")
    private Date createdAt;

}
