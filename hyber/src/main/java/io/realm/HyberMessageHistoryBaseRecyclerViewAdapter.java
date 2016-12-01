package io.realm;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.hyber.Message;
import com.hyber.MessageViewHolder;
import com.hyber.Repository;

import java.util.ArrayList;
import java.util.List;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import io.realm.internal.RealmObjectProxy;
import io.realm.internal.Row;
import io.realm.internal.TableOrView;

public abstract class HyberMessageHistoryBaseRecyclerViewAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private static final List<Long> EMPTY_LIST = new ArrayList<>(0);
    private List mIds;
    private OnChangeListener mChangeListener;
    private boolean mAutomaticUpdate = false;
    private boolean mAnimateResults = false;
    private RealmResults<Message> mRealmResults;
    private RealmChangeListener<RealmResults<Message>> mRealmChangeListener;
    private long animatePrimaryColumnIndex;
    private RealmFieldType animatePrimaryIdType;
    private long animateIsReportedColumnIndex;
    private RealmFieldType animateIsReportedType;
    private Repository repo;

    public HyberMessageHistoryBaseRecyclerViewAdapter(boolean automaticUpdate, boolean animateResults) {
        this.mAutomaticUpdate = automaticUpdate;
        this.mAnimateResults = animateResults;
        this.repo = new Repository();
        this.repo.open();
        if (repo.getCurrentUser() != null) {
            this.mRealmResults = repo.getMessages(repo.getCurrentUser());
        }
        this.mRealmChangeListener = (!mAutomaticUpdate) ? null : getRealmChangeListener();

        setAnimatedResults();
        updateRealmResults(mRealmResults);
    }

    public HyberMessageHistoryBaseRecyclerViewAdapter(boolean automaticUpdate, boolean animateResults,
                                                      @NonNull RealmResults<Message> queryResults) {
        this.mAutomaticUpdate = automaticUpdate;
        this.mAnimateResults = animateResults;
        this.mRealmResults = queryResults;
        this.mRealmChangeListener = (!mAutomaticUpdate) ? null : getRealmChangeListener();

        setAnimatedResults();
        updateRealmResults(mRealmResults);
    }

    private void setAnimatedResults() {
        // If automatic updates aren't enabled, then animateResults should be false as well.
        this.mAnimateResults = (mAutomaticUpdate && mAnimateResults);
        if (mAnimateResults) {
            animatePrimaryColumnIndex = mRealmResults.getTableOrView().getTable()
                    .getPrimaryKey();
            if (animatePrimaryColumnIndex == TableOrView.NO_MATCH) {
                throw new IllegalStateException(
                        "Animating the results requires a primaryKey.");
            }
            animatePrimaryIdType = mRealmResults.getTableOrView().getColumnType(animatePrimaryColumnIndex);
            if (animatePrimaryIdType != RealmFieldType.STRING) {
                throw new IllegalStateException(
                        "Animating requires a primary key of type String");
            }

            animateIsReportedColumnIndex = mRealmResults.getTableOrView().getTable()
                    .getColumnIndex(Message.IS_REPORTED);
            if (animateIsReportedColumnIndex == TableOrView.NO_MATCH) {
                throw new IllegalStateException(
                        "Animating the results requires a isReported.");
            }
            animateIsReportedType = mRealmResults.getTableOrView().getColumnType(animateIsReportedColumnIndex);
            if (animateIsReportedType != RealmFieldType.BOOLEAN) {
                throw new IllegalStateException(
                        "Animating requires a isReported of type Boolean");
            }
        }
    }

    public void setOnChangeListener(@NonNull OnChangeListener listener) {
        this.mChangeListener = listener;
    }

    public abstract MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(MessageViewHolder viewHolder, int position) {
        final Message messageItem = mRealmResults.get(position);
        viewHolder.setMessageId(messageItem.getId());
        viewHolder.setPartner(messageItem.getPartner());

        viewHolder.setMessageAlphaName(messageItem.getTitle());
        viewHolder.setMessageText(messageItem.getBody());

        viewHolder.setMessageImageUrl(messageItem.getImageUrl());
        viewHolder.setMessageAction(messageItem.getButtonUrl());
        viewHolder.setMessageCaption(messageItem.getButtonText());

        viewHolder.setMessageDate(messageItem.getDate());
        viewHolder.setReadStatus(messageItem.isRead());
        viewHolder.setDeliveryReportStatus(messageItem.isReported());
    }

    @Override
    public int getItemCount() {
        return mRealmResults.size();
    }

    public String getMessageId(int position) {
        return mRealmResults.get(position).getId();
    }

    public void updateRealmResults(RealmResults<Message> queryResults) {
        if (mRealmChangeListener != null && mRealmResults != null) {
            mRealmResults.removeChangeListener(mRealmChangeListener);
        }

        mRealmResults = queryResults;
        if (mRealmChangeListener != null && mRealmResults != null) {
            mRealmResults.addChangeListener(mRealmChangeListener);
        }

        mIds = getIdsOfRealmResults();

        notifyDataSetChanged();
    }

    private List getIdsOfRealmResults() {
        if (mRealmResults == null || mRealmResults.size() == 0) {
            return EMPTY_LIST;
        }
        List ids = new ArrayList(mRealmResults.size());
        for (int i = 0; i < mRealmResults.size(); i++) {
            ids.add(getRealmRowId(i));
        }
        return ids;
    }

    private Object getRealmRowId(int realmIndex) {
        Object rowPrimaryId;
        RealmObjectProxy proxy = (RealmObjectProxy) mRealmResults.get(realmIndex);
        Row row = proxy.realmGet$proxyState().getRow$realm();

        if (animatePrimaryIdType == RealmFieldType.INTEGER) {
            rowPrimaryId = row.getLong(animatePrimaryColumnIndex);
        } else if (animatePrimaryIdType == RealmFieldType.STRING) {
            rowPrimaryId = row.getString(animatePrimaryColumnIndex);
        } else {
            throw new IllegalStateException("Unknown animatePrimaryIdType");
        }

        if (animateIsReportedColumnIndex != -1) {
            String rowPrimaryIdStr = (rowPrimaryId instanceof String)
                    ? (String) rowPrimaryId : String.valueOf(rowPrimaryId);
            if (animateIsReportedType == RealmFieldType.BOOLEAN) {
                return rowPrimaryIdStr + String.valueOf(row.getLong(animateIsReportedColumnIndex));
            } else {
                throw new IllegalStateException("Unknown animateIsReportedType");
            }
        } else {
            return rowPrimaryId;
        }
    }

    private RealmChangeListener<RealmResults<Message>> getRealmChangeListener() {
        return new RealmChangeListener<RealmResults<Message>>() {
            @Override
            public void onChange(RealmResults<Message> element) {
                if (mIds != null && !mIds.isEmpty()) {
                    //TODO updateRowWrappers();
                    List newIds = getIdsOfRealmResults();
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
                    mIds = getIdsOfRealmResults();
                }
            }
        };
    }

    public interface OnChangeListener {
        void onItemRangeInserted(int positionStart, int itemCount);

        void onItemRangeRemoved(int positionStart, int itemCount);

        void onItemRangeChanged(int positionStart, int itemCount);
    }

}
