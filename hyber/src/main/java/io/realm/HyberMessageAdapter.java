package io.realm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.hyber.HyberMessageViewHolder;
import com.hyber.model.Message;

import java.util.HashMap;

import io.realm.internal.TableOrView;

public abstract class HyberMessageAdapter extends com.hyber.RealmRecyclerViewAdapter<Message, HyberMessageViewHolder> {

    private Boolean hasAnimateResults;
    private HashMap<Long, RealmFieldType> columnIndexRealmFieldTypeHashMap = new HashMap<>();

    public HyberMessageAdapter(@NonNull Context context, @Nullable RealmResults<Message> data, boolean autoUpdate, boolean animateResults) {
        super(context, data, autoUpdate);
        // If automatic updates aren't enabled, then animateResults should be false as well.
        this.hasAnimateResults = (hasAutoUpdates & animateResults);
        setAnimatedResults();
    }

    private void setAnimatedResults() {
        if (hasAnimateResults) {
            Long indexOfField;
            RealmFieldType typeOfField;

            indexOfField = adapterData.getTableOrView().getTable().getPrimaryKey();
            if (indexOfField == TableOrView.NO_MATCH)
                throw new IllegalStateException("Animating the results requires a primaryKey.");
            typeOfField = adapterData.getTableOrView().getColumnType(indexOfField);
            if (typeOfField != RealmFieldType.STRING)
                throw new IllegalStateException("Animating the results requires a primaryKey of type String type");
            columnIndexRealmFieldTypeHashMap.put(indexOfField, typeOfField);

            indexOfField = adapterData.getTableOrView().getTable().getColumnIndex(Message.IS_REPORTED);
            if (indexOfField == TableOrView.NO_MATCH)
                throw new IllegalStateException("Animating the results requires a isReported field.");
            typeOfField = adapterData.getTableOrView().getColumnType(indexOfField);
            if (typeOfField != RealmFieldType.BOOLEAN)
                throw new IllegalStateException("Animating the results requires a primaryKey of type Boolean type");
            columnIndexRealmFieldTypeHashMap.put(indexOfField, typeOfField);
            setColumnIndexMap(columnIndexRealmFieldTypeHashMap);
        }
    }

    public abstract HyberMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(HyberMessageViewHolder holder, int position) {
        Message messageItem = getData().get(position);

        holder.setMessageId(messageItem.getId());
        holder.setMessagePartner(messageItem.getPartner());
        holder.setMessageTitle(messageItem.getTitle());
        holder.setMessageBody(messageItem.getBody());
        holder.setMessageImageUrl(messageItem.getImageUrl());
        holder.setMessageButtonUrl(messageItem.getButtonUrl());
        holder.setMessageButtonText(messageItem.getButtonText());
        holder.setMessageDate(messageItem.getDate());
        holder.setMessageIsRead(messageItem.isRead());

        holder.setMessageIsReported(messageItem.isReported());
    }

}
