package com.hyber.example.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.hyber.MessageViewHolder;
import com.hyber.example.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.HyberMessageHistoryBaseRecyclerViewAdapter;

public class MyMessagesRVAdapter extends HyberMessageHistoryBaseRecyclerViewAdapter {

    public interface OnMessageActionListener {
        void onAction(@NonNull String action);
    }

    public interface OnMessageAnswerListener {
        void onAction(@NonNull String messageId, @NonNull String answerText);
    }

    private WeakReference<Context> mContextWeakReference;
    private OnMessageActionListener onMessageActionListener;
    private OnMessageAnswerListener onMessageAnswerListener;

    public MyMessagesRVAdapter(Context context) {
        super(true, true);
        this.mContextWeakReference = new WeakReference<>(context);
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MyHolder(itemView);
    }

    public void setOnMessageActionListener(@Nullable OnMessageActionListener listener) {
        this.onMessageActionListener = listener;
    }

    public void setOnMessageAnswerListener(@Nullable OnMessageAnswerListener listener) {
        this.onMessageAnswerListener = listener;
    }

    private class MyHolder extends MessageViewHolder {

        private String mMessageId;
        private AppCompatTextView messageId;
        private AppCompatTextView messageAlphaName;
        private AppCompatTextView messageText;
        private AppCompatImageView messageImage;
        private AppCompatButton messageButton;
        private AppCompatImageButton answerButton;
        private AppCompatTextView messageDrStatus;
        private AppCompatTextView messageTime;

        private boolean isAnswerMode = false;
        private RelativeLayout messageAnswerLayout;
        private TextInputEditText messageInputAnswerEditText;
        private AppCompatImageButton messageSendAnswerAppCompatImageButton;

        private String mAction;

        public MyHolder(View itemView) {
            super(itemView);
            this.messageId = (AppCompatTextView) itemView.findViewById(R.id.messageId);
            this.messageAlphaName = (AppCompatTextView) itemView.findViewById(R.id.messageAlphaName);
            this.messageText = (AppCompatTextView) itemView.findViewById(R.id.messageText);
            this.messageImage = (AppCompatImageView) itemView.findViewById(R.id.messageImage);
            this.messageButton = (AppCompatButton) itemView.findViewById(R.id.messageButton);
            this.answerButton = (AppCompatImageButton) itemView.findViewById(R.id.answerButton);
            this.messageDrStatus = (AppCompatTextView) itemView.findViewById(R.id.messageDrStatus);
            this.messageTime = (AppCompatTextView) itemView.findViewById(R.id.messageTime);

            this.messageAnswerLayout = (RelativeLayout) itemView.findViewById(R.id.messageAnswerLayout);
            this.messageInputAnswerEditText = (TextInputEditText) itemView.findViewById(R.id.messageInputAnswerEditText);
            this.messageSendAnswerAppCompatImageButton = (AppCompatImageButton) itemView.findViewById(R.id.messageSendAnswerAppCompatImageButton);
        }

        @Override
        public void setMessageId(@NonNull String id) {
            this.mMessageId = id;
            this.messageId.setText(mMessageId);
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
        public void isMessageBidirectionalAvailable(@NonNull Boolean isBiDirMessage) {
            messageAnswerLayout.setVisibility(View.GONE);
            if (isBiDirMessage) {
                this.answerButton.setVisibility(View.VISIBLE);
                this.answerButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isAnswerMode) {
                            isAnswerMode = false;
                            messageAnswerLayout.setVisibility(View.GONE);
                        } else {
                            isAnswerMode = true;
                            messageAnswerLayout.setVisibility(View.VISIBLE);
                            messageInputAnswerEditText.requestFocus();
                            messageSendAnswerAppCompatImageButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String answerMessage = messageInputAnswerEditText.getText().toString();
                                    if (answerMessage.isEmpty()) {
                                    } else {
                                        if (onMessageAnswerListener != null) {
                                            isAnswerMode = false;
                                            messageAnswerLayout.setVisibility(View.GONE);
                                            onMessageAnswerListener.onAction(mMessageId, answerMessage);
                                        }
                                    }
                                }
                            });

                        }
                    }
                });
            } else {
                this.answerButton.setVisibility(View.GONE);
                this.answerButton.setOnClickListener(null);
            }
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
