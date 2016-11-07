package com.hyber;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Date;

public abstract class MessageViewHolder extends RecyclerView.ViewHolder {

    public MessageViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void setMessageId(@NonNull String id);

    public abstract void setPartner(@NonNull String partner);

    public abstract void setMessageAlphaName(@Nullable String alphaName);

    public abstract void setMessageText(@Nullable String text);

    public abstract void setMessageImageUrl(@Nullable String imageUrl);

    public abstract void setMessageAction(@Nullable String action);

    public abstract void setMessageCaption(@Nullable String caption);

    public abstract void isMessageBidirectionalAvailable(@NonNull Boolean isBiDirMessage);

    public abstract void setMessageDate(@NonNull Date date);

    public abstract void setReadStatus(@NonNull Boolean readStatus);

    public abstract void setDeliveryReportStatus(@NonNull Boolean drStatus);

}
