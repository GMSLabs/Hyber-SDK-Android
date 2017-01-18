package com.hyber.model;

import android.support.annotation.Nullable;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@RealmClass
public class User extends RealmObject {

    public static final String ID = "id";
    public static final String PHONE = "phone";
    public static final String CREATED_AT = "createdAt";
    public static final String AUTH_TOKEN = "authToken";
    public static final String SESSION_ID = "sessionId";
    public static final String FCM_TOKEN = "fcmToken";
    public static final String IS_FCM_TOKEN_SENT = "isFcmTokenSent";
    public static final String DEVICES = "devices";

    @PrimaryKey
    @NonNull
    private String id;
    @Required
    @NonNull
    private String phone;
    @Required
    @NonNull
    private Date createdAt;
    @Required
    @NonNull
    private String authToken;
    @Required
    @NonNull
    private String sessionId;
    private String fcmToken;
    @Required
    @NonNull
    private Boolean isFcmTokenSent = false;

    public User() {
    }

    public User(@NonNull String id, @NonNull String phone, @NonNull Date createdAt, @NonNull String authToken, @NonNull String sessionId) {
        this(id, phone, createdAt, authToken, sessionId, null);
    }

    public User(@NonNull String id, @NonNull String phone, @NonNull Date createdAt, @NonNull String authToken, @NonNull String sessionId, @Nullable String fcmToken) {
        this.id = id;
        this.phone = phone;
        this.createdAt = createdAt;
        this.authToken = authToken;
        this.sessionId = sessionId;
        this.fcmToken = fcmToken;
    }

}
