package com.hyber;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

class BaseResponce {

    @SerializedName("error")
    private ErrorRespItemModel mError;

    @Nullable
    public ErrorRespItemModel getError() {
        return mError;
    }

}