package com.hyber;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Profile extends RealmObject {

    public static final String ID = "mId";
    public static final String PHONE = "mPhone";

    @PrimaryKey
    @Required
    private String mId;

    @Required
    private Long mPhone;

    public Profile() {

    }

    public Profile(@NonNull String id, @NonNull Long phone) {
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
