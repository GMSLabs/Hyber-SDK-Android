package com.hyber.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Builder;

@Getter
@Setter
@Builder
@RealmClass
public class Device extends RealmObject {

    public static final String ID = "id";
    public static final String USER = "user";
    public static final String OS_TYPE = "osType";
    public static final String OS_VERSION = "osVersion";
    public static final String DEVICE_TYPE = "deviceType";
    public static final String DEVICE_NAME = "deviceName";
    public static final String SDK_VERSION = "sdkVersion";
    public static final String CREATED_AT = "createdAt";
    public static final String UPDATED_AT = "updatedAt";
    public static final String IS_CURRENT = "isCurrent";
    public static final String USER_ID = "user." + User.ID;

    @PrimaryKey @NonNull
    private String id;
    private User user;
    @Required @NonNull
    private String osType;
    @Required @NonNull
    private String osVersion;
    @Required @NonNull
    private String deviceType;
    @Required @NonNull
    private String deviceName;
    @Required @NonNull
    private String sdkVersion;
    @Required @NonNull
    private Date createdAt;
    @Required @NonNull
    private Date updatedAt;
    @Required @NonNull
    private Boolean isCurrent;

    public Device() {
    }

    public Device(@NonNull String id, @NonNull User user, @NonNull String osType, @NonNull String osVersion,
                  @NonNull String deviceType, @NonNull String deviceName, @NonNull String sdkVersion,
                  @NonNull Date createdAt, @NonNull Date updatedAt, @NonNull Boolean isCurrent) {
        this.id = id;
        this.user = user;
        this.osType = osType;
        this.osVersion = osVersion;
        this.deviceType = deviceType;
        this.deviceName = deviceName;
        this.sdkVersion = sdkVersion;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isCurrent = isCurrent;
    }

}
