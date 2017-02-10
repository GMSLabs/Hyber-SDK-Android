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

import com.hyber.log.HyberLogger;

import java.util.Locale;
import java.util.UUID;

final class Utils {

    private Utils() {

    }

    static String getRandomUuid() {
        return UUID.randomUUID().toString();
    }

    static String getOsType() {
        return "android";
    }

    static String getFullDeviceName() {
        return getDeviceName() + " : " + getModelName();
    }

    private static String getDeviceName() {
        return Build.DEVICE;
    }

    private static String getModelName() {
        return Build.MODEL;
    }

    static String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    static String getDeviceType(Context context) {
        return isTablet(context) ? "tablet" : "phone";
    }

    private static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    static DeviceType getDeviceType() {
        try {
            Class.forName("com.google.firebase.FirebaseApp");
            return DeviceType.FCM;
        } catch (ClassNotFoundException ignored) {
            HyberLogger.d("Is not Firebase messaging");
        }

        try {
            Class.forName("com.amazon.device.messaging.ADM");
            return DeviceType.ADM;
        } catch (ClassNotFoundException ignored) {
            HyberLogger.d("Is not Amazone messaging");
        }

        return DeviceType.GCM;
    }

    enum DeviceType {
        FCM, GCM, ADM
    }

}
