package com.hyber;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public abstract class MessageRVAbstractAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    public interface OnChangeListener {
        void onChange();
    }

    private Realm mRealm;
    private RealmResults<Message> mRealmResults;
    private RealmChangeListener<RealmResults<Message>> mRealmChangeListener;
    private OnChangeListener mChangeListener;

    public MessageRVAbstractAdapter(Realm realm) {
        this.mRealm = realm;
        mRealmResults = mRealm
                .where(Message.class)
                .findAllSorted(Message.RECEIVED_AT, Sort.ASCENDING);
        mRealmChangeListener = new RealmChangeListener<RealmResults<Message>>() {
            @Override
            public void onChange(RealmResults<Message> results) {
                MessageRVAbstractAdapter.this.notifyDataSetChanged();
                //TODO Add support of change items and range of items handler
                if (mChangeListener != null)
                    mChangeListener.onChange();
            }
        };
        mRealmResults.addChangeListener(mRealmChangeListener);
    }

    public void setOnChangeListener(@NonNull OnChangeListener listener) {
        this.mChangeListener = listener;
    }

    public abstract MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(MessageViewHolder viewHolder, int position) {
        final Message messageItem = mRealmResults.get(position);
        viewHolder.setMessageId(messageItem.getId());

        viewHolder.setMessageAlphaName(messageItem.getAlphaName());
        viewHolder.setMessageText(messageItem.getText());

        viewHolder.setMessageImageUrl(messageItem.getImageUrl());
        viewHolder.setMessageAction(messageItem.getAction());
        viewHolder.setMessageCaption(messageItem.getCaption());
        viewHolder.setMessageBidirectionalUrl(messageItem.getBidirectionalUrl());

        viewHolder.setMessageDate(messageItem.getReceivedAt());
        viewHolder.setDeliveryReportStatus(messageItem.isReported());
    }

    @Override
    public int getItemCount() {
        return mRealmResults.size();
    }

}
