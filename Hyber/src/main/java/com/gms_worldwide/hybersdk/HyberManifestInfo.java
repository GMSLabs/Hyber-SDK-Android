package com.gms_worldwide.hybersdk;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

/**
 * A utility class for retrieving app metadata such as the app name, default icon, whether or not
 * the app declares the correct permissions for push, etc.
 */

/**
 * Created by Andrew Kochura.
 */
class HyberManifestInfo {

    private static final String TAG = "com.gms_worldwide.hybersdk.HyberManifestInfo";

    private static final Object lock = new Object();
    private static int iconId = 0;

    private static ApplicationInfo getApplicationInfo(Context context, int flags) {
        try {
            return context.getPackageManager().getApplicationInfo(context.getPackageName(), flags);
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    /**
     * Gets application metadata.
     *
     * @param context the context
     * @return A {@link Bundle} if meta-data is specified in AndroidManifest, otherwise null.
     */
    public static Bundle getApplicationMetadata(Context context) {
        ApplicationInfo info = getApplicationInfo(context, PackageManager.GET_META_DATA);
        if (info != null) {
            return info.metaData;
        }
        return null;
    }

    /**
     * Returns the default icon id used by this application, as specified by the android:icon
     * attribute in the <application> element of the manifest.
     *
     * @return the icon id
     */
    public static int getIconId() {
        synchronized (lock) {
            if (iconId == 0) {
                iconId = getContext().getApplicationInfo().icon;
            }
        }

        return iconId;
    }

    private static Context getContext() {
        return Hyber.getApplicationContext();
    }

}
