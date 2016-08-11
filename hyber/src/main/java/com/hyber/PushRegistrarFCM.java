package com.hyber;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.TimeUnit;

public class PushRegistrarFCM implements PushRegistrator {

    private static int FCM_RETRY_COUNT = 3;
    private static int FCM_RETRY_INTERVAL_SECONDS = 5;

    private Context appContext;
    private RegisteredHandler registeredHandler;

    @Override
    public void registerForPush(Context context, RegisteredHandler callback) {
        appContext = context;
        registeredHandler = callback;

        try {
            if (checkPlayServices())
                registerInBackground();
            else {
                Hyber.Log(Hyber.LOG_LEVEL.ERROR, "No valid Google Play services APK found.");
                registeredHandler.complete(null);
            }
        } catch (Throwable t) {
            Hyber.Log(Hyber.LOG_LEVEL.ERROR, "Could not register with GCM due to an error with the AndroidManifest.xml file or with 'Google Play services'.", t);
            registeredHandler.complete(null);
        }
    }

    private boolean isGooglePlayStoreInstalled() {
        try {
            PackageManager pm = appContext.getPackageManager();
            PackageInfo info = pm.getPackageInfo("com.android.vending", PackageManager.GET_ACTIVITIES);
            String label = (String) info.applicationInfo.loadLabel(pm);
            return (label != null && !label.equals("Market"));
        } catch (Throwable e) {}

        return false;
    }

    private boolean checkPlayServices() {
        // GoogleApiAvailability is the replacement for GooglePlayServicesUtil added in 7.3.

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(appContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode) && isGooglePlayStoreInstalled()) {
                Hyber.Log(Hyber.LOG_LEVEL.INFO, "Google Play services Recoverable Error: " + resultCode);

                final SharedPreferences prefs = Hyber.getHyberPreferences(appContext);
                if (prefs.getBoolean("GT_DO_NOT_SHOW_MISSING_GPS", false))
                    return false;

                try {
                    ShowUpdateGPSDialog(resultCode);
                } catch (Throwable t) {}
            } else
                Hyber.Log(Hyber.LOG_LEVEL.WARN, "Google Play services error: This device is not supported. Code:" + resultCode);

            return false;
        }

        return true;
    }

    private String getResourceString(Context context, String key, String defaultStr) {
        Resources resources = context.getResources();
        int bodyResId = resources.getIdentifier(key, "string", context.getPackageName());
        if (bodyResId != 0)
            return resources.getString(bodyResId);
        return defaultStr;
    }

    private void ShowUpdateGPSDialog(final int resultCode) {
        Hyber.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Activity activity = ActivityLifecycleHandler.curActivity;
                if (activity == null || Hyber.mInitBuilder.mDisableGmsMissingPrompt)
                    return;

                String alertBodyText = getResourceString(activity, "hyber_gms_missing_alert_text", "To receive push notifications please press 'Update' to enable 'Google Play services'.");
                String alertButtonUpdate = getResourceString(activity, "hyber_gms_missing_alert_button_update", "Update");
                String alertButtonSkip = getResourceString(activity, "hyber_gms_missing_alert_button_skip", "Skip");
                String alertButtonClose = getResourceString(activity, "hyber_gms_missing_alert_button_close", "Close");

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(alertBodyText).setPositiveButton(alertButtonUpdate, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            GooglePlayServicesUtil.getErrorPendingIntent(resultCode, activity, 0).send();
                        } catch (CanceledException e) {
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                    }
                }).setNegativeButton(alertButtonSkip, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final SharedPreferences prefs = Hyber.getHyberPreferences(activity);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("GT_DO_NOT_SHOW_MISSING_GPS", true);
                        editor.commit();
                    }
                }).setNeutralButton(alertButtonClose, null).create().show();
            }
        });
    }

    private void registerInBackground() {
        new Thread(new Runnable() {
            public void run() {
                String registrationId = null;

                for (int currentRetry = 0; currentRetry < FCM_RETRY_COUNT; currentRetry++) {
                    registrationId = FirebaseInstanceId.getInstance().getToken();
                    if (registrationId != null) {
                        Hyber.Log(Hyber.LOG_LEVEL.INFO, "Device registered, Firebase registration push token = " + registrationId);
                        registeredHandler.complete(registrationId);
                        break;
                    } else {
                        if (currentRetry >= (FCM_RETRY_COUNT - 1))
                            Hyber.Log(Hyber.LOG_LEVEL.ERROR, "FCM_RETRY_COUNT of " + FCM_RETRY_COUNT + " exceed! Could not get a Firebase Registration Id");
                        else {
                            Hyber.Log(Hyber.LOG_LEVEL.INFO, "Google Play services returned SERVICE_NOT_AVAILABLE error. Current retry count: " + currentRetry);
                            if (currentRetry == 2) {
                                // Retry 3 times before firing a null response and continuing a few more times.
                                registeredHandler.complete(null);
                            }
                            try {
                                Thread.sleep(TimeUnit.SECONDS.toMillis(FCM_RETRY_INTERVAL_SECONDS));
                            } catch (Throwable t) {}
                        }
                    }
                }
            }
        }).start();
    }

}
