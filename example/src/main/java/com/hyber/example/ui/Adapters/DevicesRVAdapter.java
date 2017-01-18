package com.hyber.example.ui.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hyber.example.HyberDeviceViewHolder;
import com.hyber.example.R;
import com.hyber.model.Device;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class DevicesRVAdapter extends RealmRecyclerViewAdapter<Device, DevicesRVAdapter.MyViewHolder> {

    public interface RevokeDeviceListener {
        void onRevoke(@lombok.NonNull String deviceId, @lombok.NonNull Boolean isCurrent);
    }

    private Context mContext;
    private RevokeDeviceListener revokeDeviceListener;

    public DevicesRVAdapter(Context context, OrderedRealmCollection<Device> data) {
        super(context, data, true);
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_device, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Device deviceItem = getData().get(position);
        holder.setId(deviceItem.getId());
        holder.setOsType(deviceItem.getOsType());
        holder.setOsVersion(deviceItem.getOsVersion());
        holder.setDeviceType(deviceItem.getDeviceType());
        holder.setDeviceName(deviceItem.getDeviceName());
        holder.setUpdatedAt(deviceItem.getUpdatedAt());
        holder.setIsCurrent(deviceItem.getIsCurrent());
    }

    public void setOnRevokeDeviceListener(RevokeDeviceListener listener) {
        this.revokeDeviceListener = listener;
    }

    public void removeOnRevokeDeviceListener(RevokeDeviceListener listener) {
        if (this.revokeDeviceListener.equals(listener))
            this.revokeDeviceListener = null;
    }

    class MyViewHolder extends HyberDeviceViewHolder {

        private String id;
        private Boolean isCurrent;
        @BindView(R.id.holder)
        CardView holderCardView;
        @BindView(R.id.osType)
        ImageView osTypeImageView;
        @BindView(R.id.osVersion)
        AppCompatTextView osVersionTextView;
        @BindView(R.id.deviceType)
        ImageView deviceTypeImageView;
        @BindView(R.id.deviceName)
        AppCompatTextView deviceNameTextView;
        @BindView(R.id.updatedAt)
        AppCompatTextView updatedAtTextView;
        @BindView(R.id.revoke)
        AppCompatImageButton revokeImageButton;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            revokeImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (revokeDeviceListener != null && id != null && isCurrent != null) {
                        revokeDeviceListener.onRevoke(id, isCurrent);
                    }
                }
            });
        }

        @Override
        public void setId(@NonNull String deviceId) {
            this.id = deviceId;
        }

        @Override
        public void setOsType(@NonNull String osType) {
            switch (osType) {
                case "android":
                    Picasso.with(mContext)
                            .load(R.mipmap.android_icon)
                            .into(osTypeImageView);
                    break;
                case "ios":
                    Picasso.with(mContext)
                            .load(R.mipmap.ios_icon)
                            .into(osTypeImageView);
                    break;
                default:
                    Picasso.with(mContext)
                            .load(R.mipmap.ic_launcher)
                            .into(osTypeImageView);
            }
        }

        @Override
        public void setOsVersion(@NonNull String osVersion) {
            osVersionTextView.setText(osVersion);
        }

        @Override
        public void setDeviceType(@NonNull String deviceType) {
            switch (deviceType) {
                case "tablet":
                    Picasso.with(mContext)
                            .load(R.mipmap.tablet_icon)
                            .into(deviceTypeImageView);
                    break;
                default:
                    Picasso.with(mContext)
                            .load(R.mipmap.phone_icon)
                            .into(deviceTypeImageView);
            }
        }

        @Override
        public void setDeviceName(@NonNull String deviceName) {
            deviceNameTextView.setText(deviceName);
        }

        @Override
        public void setUpdatedAt(@NonNull Date updatedAt) {
            DateFormat dateFormat = android.text.format.DateFormat.getMediumDateFormat(mContext);
            DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(mContext);
            updatedAtTextView.setText(dateFormat.format(updatedAt) + " " + timeFormat.format(updatedAt));
        }

        @Override
        public void setIsCurrent(@NonNull Boolean isCurrent) {
            this.isCurrent = isCurrent;
            if (isCurrent) {
                holderCardView.setBackgroundColor(Color.rgb(255, 239, 153));
            } else {
                holderCardView.setBackgroundColor(Color.WHITE);
            }
        }

    }

}
