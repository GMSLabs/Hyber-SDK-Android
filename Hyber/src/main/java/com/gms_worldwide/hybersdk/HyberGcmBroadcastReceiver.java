package com.gms_worldwide.hybersdk;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by Andrew Kochura.
 */
public class HyberGcmBroadcastReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = "com.gms_worldwide.hybersdk.HyberGcmBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        HyberGcmListenerService.sendNotification(intent.getExtras().getString("title"), intent.getExtras());
    }
}
