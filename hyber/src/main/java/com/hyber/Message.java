package com.hyber;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

@RealmClass
public class Message extends RealmObject {

    public static final String ID = "mId";
    public static final String USER = "mUser";
    public static final String PARTNER = "mPartner";
    public static final String TITLE = "mTitle";
    public static final String BODY = "mBody";
    public static final String DATE = "mDate";
    public static final String IMAGE_URL = "mImageUrl";
    public static final String BUTTON_URL = "mButtonUrl";
    public static final String BUTTON_TEXT = "mButtonText";
    public static final String IS_READ = "isRead";
    public static final String IS_REPORTED = "isReported";
    public static final String USER_ID = "mUser.mId";

    @PrimaryKey
    @Required
    @Index
    private String mId;
    private User mUser;
    @Required
    private String mPartner;
    private String mTitle;
    private String mBody;
    @Required
    private Date mDate;
    private String mImageUrl;
    private String mButtonUrl;
    private String mButtonText;
    @Required
    private Boolean isRead;
    @Required
    private Boolean isReported;

    public Message() {

    }

    public Message(@NonNull @lombok.NonNull String id, @NonNull @lombok.NonNull User user,
                   @NonNull @lombok.NonNull String partner, @Nullable String title, @Nullable String body,
                   @NonNull @lombok.NonNull Date date,
                   @Nullable String imageUrl, @Nullable String buttonUrl, @Nullable String buttonText,
                   @NonNull @lombok.NonNull Boolean isRead, @NonNull @lombok.NonNull Boolean isReported) {
        this.mId = id;
        this.mUser = user;
        this.mPartner = partner;
        this.mTitle = title;
        this.mBody = body;
        this.mDate = date;
        this.mImageUrl = imageUrl;
        this.mButtonUrl = buttonUrl;
        this.mButtonText = buttonText;
        this.isRead = isRead;
        this.isReported = isReported;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public User getUser() {
        return mUser;
    }

    @NonNull
    public String getPartner() {
        return mPartner;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    @Nullable
    public String getBody() {
        return mBody;
    }

    @NonNull
    public Date getDate() {
        return mDate;
    }

    @Nullable
    public String getImageUrl() {
        return mImageUrl;
    }

    @Nullable
    public String getButtonUrl() {
        return mButtonUrl;
    }

    @Nullable
    public String getButtonText() {
        return mButtonText;
    }

    @NonNull
    public Boolean isRead() {
        return isRead;
    }

    @NonNull
    public Boolean isReported() {
        return isReported;
    }

    void setReadStatus(boolean status) {
        isRead = status;
    }

    void setReportedStatus(boolean status) {
        isReported = status;
    }

}
