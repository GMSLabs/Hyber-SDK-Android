package com.hyber.model;

import android.support.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

@RealmClass
public class User extends RealmObject {

    public static final String ID = "mId";
    public static final String PHONE = "mPhone";
    public static final String SESSION = "mSession";
    public static final String INDEX_NUMBER = "mIndexNumber";

    @PrimaryKey
    @Required
    @Index
    private String mId;

    @Required
    @Index
    private String mPhone;

    private Session mSession;

    @Required
    private Integer mIndexNumber;

    public User() {

    }

    public User(@NonNull @lombok.NonNull String id, @NonNull @lombok.NonNull String phone,
                @NonNull @lombok.NonNull Session session/*, @NonNull @lombok.NonNull Integer indexNumber*/) {
        this.mId = id;
        this.mPhone = phone;
        this.mSession = session;
        this.mIndexNumber = 0;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public String getPhone() {
        return mPhone;
    }

    @NonNull
    public Session getSession() {
        return mSession;
    }

    @NonNull
    public Integer getIndexNumber() {
        return mIndexNumber;
    }

//    void setIndexNumber(@NonNull @lombok.NonNull Integer indexNumber) {
//        this.mIndexNumber = indexNumber;
//    }

}
