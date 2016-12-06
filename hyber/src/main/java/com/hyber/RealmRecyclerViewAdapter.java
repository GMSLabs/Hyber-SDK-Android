package com.hyber;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import io.realm.RealmChangeListener;
import io.realm.RealmFieldType;
import io.realm.RealmModel;
import io.realm.RealmResults;
import io.realm.internal.RealmObjectProxy;
import io.realm.internal.Row;

/**
 * The RealmRecyclerViewAdapter class is an abstract utility class for binding RecyclerView UI elements to Realm data.
 * <p>
 * This adapter will automatically handle any updates to its data and call notifyDataSetChanged() as appropriate.
 * Currently there is no support for RecyclerView's data callback methods like notifyItemInserted(int), notifyItemRemoved(int),
 * notifyItemChanged(int) etc.
 * It means that, there is no possibility to use default data animations.
 * <p>
 * The RealmAdapter will stop receiving updates if the Realm instance providing the {@link RealmResults} is
 * closed.
 *
 * @param <T>  type of {@link RealmModel} stored in the adapter.
 * @param <VH> type of RecyclerView.ViewHolder used in the adapter.
 */
public abstract class RealmRecyclerViewAdapter<T extends RealmModel, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private static final List<String> EMPTY_LIST = new ArrayList<>(0);

    private final LayoutInflater inflater;
    @NonNull
    protected final Context context;
    @Nullable
    protected RealmResults<T> adapterData;
    private final RealmChangeListener<RealmResults<T>> listener;
    protected final boolean hasAutoUpdates;
    private List<String> mIds;
    private OnChangeListener mChangeListener;
    private HashMap<Long, RealmFieldType> columnIndexRealmFieldTypeHashMap;

    public RealmRecyclerViewAdapter(@NonNull Context context, @Nullable RealmResults<T> data, boolean autoUpdate) {
        //noinspection ConstantConditions
        if (context == null) {
            throw new IllegalArgumentException("Context can not be null");
        }

        this.context = context;
        this.adapterData = data;
        this.inflater = LayoutInflater.from(context);
        this.hasAutoUpdates = autoUpdate;

        this.listener = hasAutoUpdates ? new RealmChangeListener<RealmResults<T>>() {
            @Override
            public void onChange(RealmResults<T> results) {
                notifyChangesProcessor(results);
            }
        } : null;
    }

    protected void setColumnIndexMap(HashMap<Long, RealmFieldType> columnIndexRealmFieldTypeHashMap) {
        this.columnIndexRealmFieldTypeHashMap = columnIndexRealmFieldTypeHashMap;
    }

    public void setOnChangeListener(OnChangeListener listener) {
        this.mChangeListener = listener;
    }

    private void notifyChangesProcessor(RealmResults<T> results) {
        if (this.columnIndexRealmFieldTypeHashMap == null)
            return;

        if (mIds != null && !mIds.isEmpty()) {
            List<String> newIds = getIdentifiersOfRealmResults(columnIndexRealmFieldTypeHashMap);
            // If the list is now empty, just notify the recyclerView of the change.
            if (newIds.isEmpty()) {
                mIds = newIds;
                notifyDataSetChanged();
                return;
            }
            Patch patch = DiffUtils.diff(mIds, newIds);
            List<Delta> deltas = patch.getDeltas();
            mIds = newIds;
            if (!deltas.isEmpty()) {
                for (Delta delta : deltas) {
                    if (delta.getType() == Delta.TYPE.INSERT) {
                        notifyItemRangeInserted(
                                delta.getRevised().getPosition(),
                                delta.getRevised().size());
                        if (mChangeListener != null)
                            mChangeListener.onItemRangeInserted(
                                    delta.getRevised().getPosition(),
                                    delta.getRevised().size());
                    } else if (delta.getType() == Delta.TYPE.DELETE) {
                        notifyItemRangeRemoved(
                                delta.getOriginal().getPosition(),
                                delta.getOriginal().size());
                        if (mChangeListener != null)
                            mChangeListener.onItemRangeRemoved(
                                    delta.getRevised().getPosition(),
                                    delta.getRevised().size());
                    } else {
                        notifyItemRangeChanged(
                                delta.getRevised().getPosition(),
                                delta.getRevised().size());
                        if (mChangeListener != null)
                            mChangeListener.onItemRangeChanged(
                                    delta.getRevised().getPosition(),
                                    delta.getRevised().size());
                    }
                }
            }
        } else {
            notifyDataSetChanged();
            mIds = getIdentifiersOfRealmResults(columnIndexRealmFieldTypeHashMap);
        }
    }

    private List<String> getIdentifiersOfRealmResults(HashMap<Long, RealmFieldType> columnIndexRealmFieldTypeHashMap) {
        if (adapterData == null || adapterData.size() == 0
                || columnIndexRealmFieldTypeHashMap == null || columnIndexRealmFieldTypeHashMap.isEmpty()) {
            return EMPTY_LIST;
        }
        List<String> ids = new ArrayList<>(adapterData.size());
        for (int i = 0; i < adapterData.size(); i++) {
            ids.add(getRealmRowIdentifier(i, columnIndexRealmFieldTypeHashMap));
        }
        return ids;
    }

    private String getRealmRowIdentifier(int realmIndex, HashMap<Long, RealmFieldType> columnIndexRealmFieldTypeHashMap) {
        String rowIdentifier = "";

        RealmObjectProxy proxy = (RealmObjectProxy) adapterData.get(realmIndex);
        Row row = proxy.realmGet$proxyState().getRow$realm();

        for (Entry<Long, RealmFieldType> entry : columnIndexRealmFieldTypeHashMap.entrySet()) {
            switch (entry.getValue()) {
                case STRING:
                    rowIdentifier += row.getString(entry.getKey());
                    break;
                case INTEGER:
                    rowIdentifier += String.valueOf(row.getLong(entry.getKey()));
                    break;
                case BOOLEAN:
                    rowIdentifier += String.valueOf(row.getBoolean(entry.getKey()));
                    break;
                default:
                    throw new IllegalStateException("Unsupported RealmFieldType, use only STRING, INTEGER or BOOLEAN field types");
            }
        }
        return rowIdentifier;
    }

    @Override
    public void onAttachedToRecyclerView(final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (hasAutoUpdates && isDataValid()) {
            //noinspection ConstantConditions
            addListener(adapterData);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(final RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (hasAutoUpdates && isDataValid()) {
            //noinspection ConstantConditions
            removeListener(adapterData);
        }
    }

    /**
     * Returns the current ID for an item. Note that item IDs are not stable so you cannot rely on the item ID being the
     * same after notifyDataSetChanged() or {@link #updateData(RealmResults)} has been called.
     *
     * @param index position of item in the adapter.
     * @return current item ID.
     */
    @Override
    public long getItemId(final int index) {
        return index;
    }

    @Override
    public int getItemCount() {
        //noinspection ConstantConditions
        return isDataValid() ? adapterData.size() : 0;
    }

    /**
     * Returns the item associated with the specified position.
     * Can return {@code null} if provided Realm instance by {@link RealmResults} is closed.
     *
     * @param index index of the item.
     * @return the item at the specified position, {@code null} if adapter data is not valid.
     */
    @SuppressWarnings("WeakerAccess")
    @Nullable
    public T getItem(int index) {
        //noinspection ConstantConditions
        return isDataValid() ? adapterData.get(index) : null;
    }

    /**
     * Returns data associated with this adapter.
     *
     * @return adapter data.
     */
    @Nullable
    public RealmResults<T> getData() {
        return adapterData;
    }

    /**
     * Updates the data associated to the Adapter. Useful when the query has been changed.
     * If the query does not change you might consider using the automaticUpdate feature.
     *
     * @param data the new {@link RealmResults} to display.
     */
    @SuppressWarnings("WeakerAccess")
    public void updateData(@Nullable RealmResults<T> data) {
        if (hasAutoUpdates) {
            if (adapterData != null) {
                removeListener(adapterData);
            }
            if (data != null) {
                addListener(data);
            }
        }

        this.adapterData = data;
        notifyDataSetChanged();
    }

    private void addListener(@NonNull RealmResults<T> data) {
        RealmResults realmResults = (RealmResults) data;
        //noinspection unchecked
        realmResults.addChangeListener(listener);
    }

    private void removeListener(@NonNull RealmResults<T> data) {
        RealmResults realmResults = (RealmResults) data;
        realmResults.removeChangeListener(listener);
    }

    private boolean isDataValid() {
        return adapterData != null && adapterData.isValid();
    }

    public interface OnChangeListener {
        void onItemRangeInserted(int positionStart, int itemCount);

        void onItemRangeRemoved(int positionStart, int itemCount);

        void onItemRangeChanged(int positionStart, int itemCount);
    }

}
