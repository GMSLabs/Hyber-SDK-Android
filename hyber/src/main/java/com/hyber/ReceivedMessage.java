package com.hyber;

import android.support.annotation.NonNull;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class ReceivedMessage extends RealmObject {

    public static final String ID = "mId";
    public static final String RECEIVED_AT = "mReceivedAt";
    public static final String IS_REPORTED = "isReported";

    @PrimaryKey
    @Required
    private String mId;

    @Required
    private Date mReceivedAt;

    @Required
    private Boolean isReported;

    private String mAlphaName;

    private String mText;

    public ReceivedMessage() {

    }

    public ReceivedMessage(@NonNull String id, @NonNull String name, @NonNull String text, @NonNull Date receivedAt) {
        this.mId = id;
        this.mAlphaName = name;
        this.mText = text;
        this.mReceivedAt = receivedAt;

        setReported(false);
    }


    @NonNull
    public String getId() {
        return mId;
    }

    public void setId(@NonNull String id) {
        this.mId = id;
    }

    @NonNull
    public Date getReceivedAt() {
        return mReceivedAt;
    }

    public void setReceivedAt(@NonNull Date date) {
        this.mReceivedAt = date;
    }

    @NonNull
    public Boolean getReported() {
        return isReported;
    }

    public void setReported(@NonNull Boolean status) {
        isReported = status;
    }

    public String getAlphaName() {
        return mAlphaName;
    }

    public void setAlphaName(@NonNull String name) {
        this.mAlphaName = name;
    }

    public String getText() {
        return mText;
    }

    public void setText(@NonNull String text) {
        this.mText = text;
    }

}
