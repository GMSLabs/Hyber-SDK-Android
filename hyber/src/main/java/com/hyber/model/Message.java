package com.hyber.model;

import android.support.annotation.Nullable;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Builder;

@Getter
@Builder
@RealmClass
public class Message extends RealmObject {

    public static final String ID = "id";
    public static final String USER = "user";
    public static final String PARTNER = "partner";
    public static final String TITLE = "title";
    public static final String BODY = "body";
    public static final String DATE = "date";
    public static final String IMAGE_URL = "imageUrl";
    public static final String BUTTON_URL = "buttonUrl";
    public static final String BUTTON_TEXT = "buttonText";
    public static final String IS_READ = "isRead";
    public static final String IS_REPORTED = "isReported";
    public static final String USER_ID = "user." + User.ID;

    @PrimaryKey
    @NonNull
    private String id;
    @NonNull
    private User user;
    @Required
    @NonNull
    private String partner;
    @Nullable
    private String title;
    @Nullable
    private String body;
    @Required
    @NonNull
    private Date date;
    @Nullable
    private String imageUrl;
    @Nullable
    private String buttonUrl;
    @Nullable
    private String buttonText;
    @Required
    @NonNull
    private Boolean isRead;
    @Required
    @NonNull
    private Boolean isReported;

    public Message() {
    }

    public Message(@NonNull String id, @NonNull User user, @NonNull String partner,
                   @Nullable String title, @Nullable String body, @NonNull Date date,
                   @Nullable String imageUrl, @Nullable String buttonUrl, @Nullable String buttonText,
                   @NonNull Boolean isRead, @NonNull Boolean isReported) {
        this.id = id;
        this.user = user;
        this.partner = partner;
        this.title = title;
        this.body = body;
        this.date = date;
        this.imageUrl = imageUrl;
        this.buttonUrl = buttonUrl;
        this.buttonText = buttonText;
        this.isRead = isRead;
        this.isReported = isReported;
    }

    public void setReadStatus(boolean status) {
        isRead = status;
    }

    public void setReportedStatus(boolean status) {
        isReported = status;
    }

}
