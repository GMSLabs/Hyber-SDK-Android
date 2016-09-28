package com.hyber;

import android.support.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class User extends RealmObject {

    public static final String ID = "mId";
    public static final String PHONE = "mPhone";

    @PrimaryKey
    @Required
    private String mId;

    @Required
    private Long mPhone;

    public User() {

    }

    public User(@NonNull String id, @NonNull Long phone) {
        this.mId = id;
        this.mPhone = phone;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public Long getPhone() {
        return mPhone;
    }

    public void setPhone(Long phone) {
        this.mPhone = phone;
    }

}
