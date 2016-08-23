package com.hyber;

import android.support.annotation.NonNull;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class ReceivedMessage extends RealmObject {

    static final String ID = "mId";
    static final String RECEIVED_AT = "mReceivedAt";
    static final String IS_REPORTED = "isReported";

    @PrimaryKey
    @Required
    private String mId;

    @Required
    private Date mReceivedAt;

    @Required
    private Boolean isReported;

    public ReceivedMessage() {

    }

    ReceivedMessage(@NonNull String id) {
        this.mId = id;
        this.mReceivedAt = new Date();
        this.isReported = false;
    }

    @NonNull
    String getId() {
        return mId;
    }

    @NonNull
    Date getReceivedAt() {
        return mReceivedAt;
    }

    @NonNull
    Boolean isReported() {
        return isReported;
    }

    void setReportedComplete() {
        isReported = true;
    }

}
