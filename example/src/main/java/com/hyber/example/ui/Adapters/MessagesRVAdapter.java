package com.hyber.example.ui.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.hyber.Hyber;
import com.hyber.HyberMessageViewHolder;
import com.hyber.example.R;
import com.hyber.model.Message;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.HyberMessageAdapter;
import io.realm.RealmResults;

public class MessagesRVAdapter extends HyberMessageAdapter {

    public interface OnMessageActionListener {
        void onAction(@NonNull String action);
    }

    public interface OnMessageAnswerListener {
        void onAction(@NonNull String messageId, @NonNull String answerText);
    }

    private WeakReference<Context> mContextWeakReference;
    private OnMessageActionListener onMessageActionListener;
    private OnMessageAnswerListener onMessageAnswerListener;

    public MessagesRVAdapter(Context context, RealmResults<Message> results, boolean autoUpdate, boolean animateResults) {
        super(context, results, autoUpdate, animateResults);
        this.mContextWeakReference = new WeakReference<>(context);
    }

    @Override
    public HyberMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

    class MyHolder extends HyberMessageViewHolder {

        @BindView(R.id.messageId) AppCompatTextView messageId;
        @BindView(R.id.messagePartner) AppCompatTextView messagePartner;
        @BindView(R.id.messageAlphaName) AppCompatTextView messageAlphaName;
        @BindView(R.id.messageText) AppCompatTextView messageText;
        @BindView(R.id.messageImage) AppCompatImageView messageImage;
        @BindView(R.id.messageButton) AppCompatButton messageButton;
        @BindView(R.id.answerButton) AppCompatImageButton answerButton;
        @BindView(R.id.messageDrStatus) AppCompatTextView messageDrStatus;
        @BindView(R.id.messageTime) AppCompatTextView messageTime;
        @BindView(R.id.messageAnswerLayout) RelativeLayout messageAnswerLayout;
        @BindView(R.id.messageInputAnswerEditText) TextInputEditText messageInputAnswerEditText;
        @BindView(R.id.messageSendAnswerAppCompatImageButton) AppCompatImageButton messageSendAnswerAppCompatImageButton;

        private String mMessageId;
        private boolean isAnswerMode = false;
        private String mAction;

        public MyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void setMessageId(@NonNull String id) {
            this.mMessageId = id;
            this.messageId.setText(mMessageId);
        }

        @Override
        public void setMessagePartner(@NonNull String partner) {
            messagePartner.setText(partner);
        }

        @Override
        public void setMessageTitle(@Nullable String alphaName) {
            if (alphaName == null)
                return;
            this.messageAlphaName.setText(alphaName);
        }

        @Override
        public void setMessageBody(@Nullable String text) {
            if (text == null)
                return;
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
        public void setMessageButtonUrl(@Nullable String action) {
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
        public void setMessageButtonText(@Nullable String caption) {
            if (caption == null) {
                this.messageButton.setVisibility(View.GONE);
            } else {
                this.messageButton.setVisibility(View.VISIBLE);
                this.messageButton.setText(caption);
            }
        }

        @Override
        public void serMessageIsBidirectional(@NonNull Boolean isBiDirMessage) {
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
                            messageSendAnswerAppCompatImageButton.setOnClickListener(
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            String answerMessage = messageInputAnswerEditText.getText().toString();
                                            if (!answerMessage.isEmpty()) {
                                                if (onMessageAnswerListener != null) {
                                                    isAnswerMode = false;
                                                    messageAnswerLayout.setVisibility(View.GONE);
                                                    onMessageAnswerListener.onAction(mMessageId, answerMessage);
                                                }
                                            }
                                        }
                                    }
                            );
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
        public void setMessageIsRead(@NonNull Boolean readStatus) {

        }

        @Override
        public void setMessageIsReported(@NonNull Boolean drStatus) {
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
