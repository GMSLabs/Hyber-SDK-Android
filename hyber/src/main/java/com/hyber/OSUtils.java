package com.hyber;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import java.util.Locale;

class OsUtils {

    static String getManifestMeta(Context context, String metaName) {
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getString(metaName);
        } catch (Throwable t) {
            Hyber.mLog(Hyber.LogLevel.ERROR, "", t);
        }

        return null;
    }

    static String getCorrectedLanguage() {
        String lang = Locale.getDefault().getLanguage();

        switch (lang) {
            case "iw":
                return "he";
            case "in":
                return "id";
            case "ji":
                return "yi";
            default:
                return lang;
        }
    }

    static String getDeviceOs() {
        return "Android";
    }

    static String getDeviceName() {
        return Build.DEVICE;
    }

    static String getModelName() {
        return Build.MODEL;
    }

    static String getAndroidVersion() {
        return Build.VERSION.RELEASE;
    }

    static String getDeviceFormat(Context context) {
        return isTablet(context) ? "tablet" : "phone";
    }

    private static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    DeviceType getDeviceType() {
        try {
            Class.forName("com.google.firebase.FirebaseApp");
            return DeviceType.FCM;
        } catch (ClassNotFoundException ignored) {
            Hyber.mLog(Hyber.LogLevel.INFO, "Is not Firebase messaging");
        }

        try {
            Class.forName("com.amazon.device.messaging.ADM");
            return DeviceType.ADM;
        } catch (ClassNotFoundException ignored) {
            Hyber.mLog(Hyber.LogLevel.INFO, "Is not Amazone messaging");
        }

        return DeviceType.GCM;
    }

    Integer getNetType() {
        ConnectivityManager cm = (ConnectivityManager) Hyber.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null) {
            int networkType = netInfo.getType();
            if (networkType == ConnectivityManager.TYPE_WIFI || networkType == ConnectivityManager.TYPE_ETHERNET)
                return 0;
            return 1;
        }

        return null;
    }

    String getCarrierName() {
        TelephonyManager manager = (TelephonyManager) Hyber.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
        String carrierName = manager.getNetworkOperatorName();
        return "".equals(carrierName) ? null : carrierName;
    }

    enum DeviceType {
        FCM, GCM, ADM
    }

}
