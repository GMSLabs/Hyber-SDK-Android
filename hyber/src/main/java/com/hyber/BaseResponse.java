package com.hyber;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

class BaseResponse {

    @SerializedName("error")
    private ErrorRespItemModel mError;

    @Nullable
    ErrorRespItemModel getError() {
        return mError;
    }

}