package io.realm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.hyber.example.HyberMessageViewHolder;
import com.hyber.model.Message;

import java.util.HashMap;

import io.realm.internal.Table;

public abstract class HyberMessagesAdapter extends com.hyber.RealmRecyclerViewAdapter<Message, HyberMessageViewHolder> {

    private HashMap<Long, RealmFieldType> columnIndexRealmFieldTypeHashMap = new HashMap<>();

    public HyberMessagesAdapter(@NonNull Context context, @Nullable RealmResults<Message> data) {
        super(context, data, true);
        setAnimatedResults();
    }

    private void setAnimatedResults() {
        Long indexOfField;
        RealmFieldType typeOfField;

        indexOfField = adapterData.getTable().getTable().getColumnIndex(Message.ID);
        if (indexOfField == Table.NO_MATCH)
            throw new IllegalStateException("Animating the results requires a primaryKey.");
        typeOfField = adapterData.getTable().getColumnType(indexOfField);
        if (typeOfField != RealmFieldType.STRING)
            throw new IllegalStateException("Animating the results requires a primaryKey of type String type");
        columnIndexRealmFieldTypeHashMap.put(indexOfField, typeOfField);

        indexOfField = adapterData.getTable().getColumnIndex(Message.IS_REPORTED);
        if (indexOfField == Table.NO_MATCH)
            throw new IllegalStateException("Animating the results requires a isReported field.");
        typeOfField = adapterData.getTable().getColumnType(indexOfField);
        if (typeOfField != RealmFieldType.BOOLEAN)
            throw new IllegalStateException("Animating the results requires a primaryKey of type Boolean type");
        columnIndexRealmFieldTypeHashMap.put(indexOfField, typeOfField);
        setColumnIndexMap(columnIndexRealmFieldTypeHashMap);
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
        holder.setMessageIsRead(messageItem.getIsRead());
        holder.setMessageIsReported(messageItem.getIsReported());
    }

}
