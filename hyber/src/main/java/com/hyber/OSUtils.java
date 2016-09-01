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

    enum DeviceType {
        FCM, GCM, ADM
    }

    static String getManifestMeta(Context context, String metaName) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getString(metaName);
        } catch (Throwable t) {
            Hyber.Log(Hyber.LOG_LEVEL.ERROR, "", t);
        }

        return null;
    }

    static String getCorrectedLanguage() {
        String lang = Locale.getDefault().getLanguage();

        if (lang.equals("iw"))
            return "he";
        if (lang.equals("in"))
            return "id";
        if (lang.equals("ji"))
            return "yi";

        return lang;
    }

    DeviceType getDeviceType() {
        try {
            Class.forName("com.google.firebase.FirebaseApp");
            return DeviceType.FCM;
        } catch (ClassNotFoundException e) {
        }

        try {
            Class.forName("com.amazon.device.messaging.ADM");
            return DeviceType.ADM;
        } catch (ClassNotFoundException e) {
        }

        return DeviceType.GCM;
    }

    Integer getNetType() {
        ConnectivityManager cm = (ConnectivityManager) Hyber.appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
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
        TelephonyManager manager = (TelephonyManager) Hyber.appContext.getSystemService(Context.TELEPHONY_SERVICE);
        String carrierName = manager.getNetworkOperatorName();
        return "".equals(carrierName) ? null : carrierName;
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

}
