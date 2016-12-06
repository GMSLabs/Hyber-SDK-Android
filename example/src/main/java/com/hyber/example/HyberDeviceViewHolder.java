package com.hyber.example;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Date;

public abstract class HyberDeviceViewHolder extends RecyclerView.ViewHolder {

    public HyberDeviceViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void setId(@NonNull String deviceId);

    public abstract void setOsType(@NonNull String osType);

    public abstract void setOsVersion(@NonNull String osVersion);

    public abstract void setDeviceType(@NonNull String deviceType);

    public abstract void setDeviceName(@NonNull String deviceName);

    public abstract void setUpdatedAt(@NonNull Date updatedAt);

    public abstract void setIsCurrent(@NonNull Boolean isCurrent);

}
