package com.gms_worldwide.hybersdk;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Andrew Kochura.
 */
final class HyberServiceUtils {
    private static final String TAG = "com.gms_worldwide.hybersdk.HyberServiceUtils";
    private static final String WAKE_LOCK_EXTRA = "hyberWakeLockId";

    /**
     * Run intent in service boolean.
     *
     * @param context the context
     * @param intent  the intent
     * @param clazz   the clazz
     * @return the boolean
     */
/*
     * Same as Context.startService, but logs an error if starting the service fails.
     */
    public static boolean runIntentInService(
            Context context, Intent intent, Class<? extends Service> clazz) {
        boolean startedService = false;

        if (intent != null) {
            if (clazz != null) {
                intent.setClass(context, clazz);
            }

            ComponentName name = context.startService(intent);

            startedService = (name != null);
            if (!startedService) {
                throw new RuntimeException("Could not start the service. Make sure that the XML tag "
                        + "<service android:name=\"" + clazz + "\" /> is in your "
                        + "AndroidManifest.xml as a child of the <application> element.");
            }
        }

        return startedService;
    }
}