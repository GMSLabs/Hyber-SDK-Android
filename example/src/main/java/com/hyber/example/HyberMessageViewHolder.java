package com.hyber.example;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Date;

public abstract class HyberMessageViewHolder extends RecyclerView.ViewHolder {

    public HyberMessageViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void setMessageId(@NonNull String id);

    public abstract void setMessagePartner(@NonNull String partner);

    public abstract void setMessageTitle(@Nullable String alphaName);

    public abstract void setMessageBody(@Nullable String text);

    public abstract void setMessageImageUrl(@Nullable String imageUrl);

    public abstract void setMessageButtonUrl(@Nullable String action);

    public abstract void setMessageButtonText(@Nullable String caption);

    public abstract void setMessageDate(@NonNull Date date);

    public abstract void setMessageIsRead(@NonNull Boolean readStatus);

    public abstract void setMessageIsReported(@NonNull Boolean drStatus);

}
