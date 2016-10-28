package com.hyber;

import android.support.annotation.NonNull;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

@RealmClass
public class Session extends RealmObject {

    public static final String TOKEN = "mToken";
    public static final String REFRESH_TOKEN = "mRefreshToken";
    public static final String EXPIRATION_DATE = "mExpirationDate";
    public static final String IS_EXPIRED = "isExpired";

    @Index
    @Required
    private String mToken;

    @Index
    @Required
    private String mRefreshToken;

    @Required
    private Date mExpirationDate;

    @Required
    private Boolean mExpired;

    public Session() {

    }

    public Session(@NonNull @lombok.NonNull String token, @NonNull @lombok.NonNull String refreshToken,
                   @NonNull @lombok.NonNull Date expirationDate, @NonNull @lombok.NonNull Boolean expired) {
        this.mToken = token;
        this.mRefreshToken = refreshToken;
        this.mExpirationDate = expirationDate;
        this.mExpired = expired;
    }

    public void setToken(@NonNull @lombok.NonNull String token) {
        this.mToken = token;
    }

    public void setRefreshToken(@NonNull @lombok.NonNull String refreshToken) {
        this.mRefreshToken = refreshToken;
    }

    public void setExpirationDate(@NonNull @lombok.NonNull Date expirationDate) {
        this.mExpirationDate = expirationDate;
    }

    public void setExpired(@NonNull @lombok.NonNull Boolean expired) {
        this.mExpired = expired;
    }

    @NonNull
    @lombok.NonNull
    public String getToken() {
        return mToken;
    }

    @NonNull
    @lombok.NonNull
    public String getRefreshToken() {
        return mRefreshToken;
    }

    @NonNull
    @lombok.NonNull
    public Date getExpirationDate() {
        return mExpirationDate;
    }

    @NonNull
    @lombok.NonNull
    public Boolean isExpired() {
        return mExpired;
    }

}
