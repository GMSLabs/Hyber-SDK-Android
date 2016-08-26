package com.hyber.example.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyber.MessageRVAbstractAdapter;
import com.hyber.MessageViewHolder;
import com.hyber.example.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;

public class MyMessagesRVAdapter extends MessageRVAbstractAdapter {

    public interface OnMessageActionListener {
        void onAction(@NonNull String action);
    }

    private WeakReference<Context> mContextWeakReference;
    private OnMessageActionListener onMessageActionListener;

    public MyMessagesRVAdapter(Context context, Realm realm) {
        super(realm);
        this.mContextWeakReference = new WeakReference<>(context);
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MyHolder(itemView);
    }

    public void setOnMessageActionListener(@NonNull OnMessageActionListener listener) {
        this.onMessageActionListener = listener;
    }

    private class MyHolder extends MessageViewHolder {

        private AppCompatTextView messageId;
        private AppCompatTextView messageAlphaName;
        private AppCompatTextView messageText;
        private AppCompatImageView messageImage;
        private AppCompatButton messageButton;
        private AppCompatTextView messageDrStatus;
        private AppCompatTextView messageTime;

        private String mAction;

        public MyHolder(View itemView) {
            super(itemView);
            this.messageId = (AppCompatTextView) itemView.findViewById(R.id.messageId);
            this.messageAlphaName = (AppCompatTextView) itemView.findViewById(R.id.messageAlphaName);
            this.messageText = (AppCompatTextView) itemView.findViewById(R.id.messageText);
            this.messageImage = (AppCompatImageView) itemView.findViewById(R.id.messageImage);
            this.messageButton = (AppCompatButton) itemView.findViewById(R.id.messageButton);
            this.messageDrStatus = (AppCompatTextView) itemView.findViewById(R.id.messageDrStatus);
            this.messageTime = (AppCompatTextView) itemView.findViewById(R.id.messageTime);
        }

        @Override
        public void setMessageId(@NonNull String id) {
            this.messageId.setText(id);
        }

        @Override
        public void setMessageAlphaName(@NonNull String alphaName) {
            this.messageAlphaName.setText(alphaName);
        }

        @Override
        public void setMessageText(@NonNull String text) {
            this.messageText.setText(text);
        }

        @Override
        public void setMessageImageUrl(@Nullable String imageUrl) {
            if (imageUrl == null) {
                this.messageImage.setVisibility(View.GONE);
            } else {
                if (mContextWeakReference.get() != null) {
                    Picasso.with(mContextWeakReference.get())
                            .load(imageUrl)
                            .into(this.messageImage, new Callback() {
                                @Override
                                public void onSuccess() {
                                    messageImage.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onError() {
                                    messageImage.setVisibility(View.GONE);
                                }
                            });
                }
            }
        }

        @Override
        public void setMessageAction(@Nullable String action) {
            if (action == null) {
                this.messageButton.setVisibility(View.GONE);
                this.messageButton.setOnClickListener(null);
            } else {
                this.messageButton.setVisibility(View.VISIBLE);
                this.mAction = action;
                this.messageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (onMessageActionListener != null) {
                            onMessageActionListener.onAction(mAction);
                        }
                    }
                });
            }
        }

        @Override
        public void setMessageCaption(@Nullable String caption) {
            if (caption == null) {
                this.messageButton.setVisibility(View.GONE);
            } else {
                this.messageButton.setVisibility(View.VISIBLE);
                this.messageButton.setText(caption);
            }
        }

        @Override
        public void setMessageBidirectionalUrl(@Nullable String biDirUrl) {
            //TODO
        }

        @Override
        public void setMessageDate(@NonNull Date date) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss.SSS", Locale.getDefault());
            this.messageTime.setText(formatter.format(date));
        }

        @Override
        public void setDeliveryReportStatus(@NonNull Boolean drStatus) {
            if (drStatus) {
                this.messageDrStatus.setText("reported");
                this.messageDrStatus.setTextColor(Color.GREEN);
            } else {
                this.messageDrStatus.setText("unreported");
                this.messageDrStatus.setTextColor(Color.RED);
            }
        }
    }

}
