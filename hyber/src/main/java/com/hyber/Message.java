package com.hyber;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Message extends RealmObject {

    public static final String ID = "mId";
    public static final String PARTNER = "mPartner";
    public static final String ALPHA_NAME = "mAlphaName";
    public static final String TIME = "mTime";
    public static final String ORDER = "mOrder";

    public static final String IMAGE_URL = "mImageUrl";
    public static final String ACTION = "mAction";
    public static final String CAPTION = "mCaption";
    public static final String BIDIRECTIONAL_URL = "mBiDirUrl";

    public static final String RECEIVED_AT = "mReceivedAt";
    public static final String IS_REPORTED = "isReported";

    @PrimaryKey
    @Required
    private String mId;

    @Required
    private String mPartner;

    @Required
    private Date mTime;

    @Required
    private String mAlphaName;

    @Required
    private String mText;

    @Required
    private String mOrder;

    private String mImageUrl;

    private String mAction;

    private String mCaption;

    private String mBiDirUrl;

    @Required
    private Date mReceivedAt;

    @Required
    private Boolean isReported;

    public Message() {

    }

    public Message(@NonNull String id, @NonNull String partner, @NonNull Date time,
                   @NonNull String alphaName, @NonNull String text, @NonNull String order) {
        this.mId = id;
        this.mPartner = partner;
        this.mTime = time;
        this.mAlphaName = alphaName;
        this.mText = text;
        this.mOrder = order;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public String getPartner() {
        return mPartner;
    }

    @NonNull
    public Date getTime() {
        return mTime;
    }

    @NonNull
    public String getAlphaName() {
        return mAlphaName;
    }

    @NonNull
    public String getText() {
        return mText;
    }

    @NonNull
    public String getOrder() {
        return mOrder;
    }

    public void setOptions(String imageUrl, String action, String caption, String biDirUrl) {
        this.mImageUrl = imageUrl;
        this.mAction = action;
        this.mCaption = caption;
        this.mBiDirUrl = biDirUrl;
    }

    @Nullable
    public String getImageUrl() {
        return mImageUrl;
    }

    @Nullable
    public String getAction() {
        return mAction;
    }

    @Nullable
    public String getCaption() {
        return mCaption;
    }

    @Nullable
    public String getBidirectionalUrl() {
        return mBiDirUrl;
    }

    @NonNull
    public Date getReceivedAt() {
        return mReceivedAt;
    }

    @NonNull
    public Boolean isReported() {
        return isReported;
    }

    void setAsFromHistory() {
        this.mReceivedAt = this.mTime;
        this.isReported = true;
    }

    void setAsNewReceived() {
        this.mReceivedAt = new Date();
        this.isReported = false;
    }

    void setReportedComplete() {
        isReported = true;
    }

}
