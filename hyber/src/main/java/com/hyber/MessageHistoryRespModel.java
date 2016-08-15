package com.hyber;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MessageHistoryRespModel extends BaseResponce {

    @SerializedName("mMessages")
    private List<MessageRespItemModel> mMessages;

    @Nullable
    public List<MessageRespItemModel> getMessages() {
        return mMessages;
    }

}
