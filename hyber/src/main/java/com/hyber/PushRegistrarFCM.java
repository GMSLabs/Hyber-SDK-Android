package com.hyber;

import android.app.Activity;
import android.app.AlertDialog;
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

class PushRegistrarFCM implements PushRegistrator {

    private static final int FCM_RETRY_COUNT = 3;
    private static final int FCM_RETRY_INTERVAL_SECONDS = 5;

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
                HyberLogger.e("No valid Google Play services APK found.");
                registeredHandler.complete(null);
            }
        } catch (Throwable t) {
            HyberLogger.e(t, "Could not register with GCM due to an error with the AndroidManifest.xml "
                                    + "file or with 'Google Play services'.");
            registeredHandler.complete(null);
        }
    }

    private boolean isGooglePlayStoreInstalled() {
        try {
            PackageManager pm = appContext.getPackageManager();
            PackageInfo info = pm.getPackageInfo("com.android.vending", PackageManager.GET_ACTIVITIES);
            String label = (String) info.applicationInfo.loadLabel(pm);
            return (label != null && !"Market".equals(label));
        } catch (Throwable ignored) {
            return false;
        }
    }

    private boolean checkPlayServices() {
        // GoogleApiAvailability is the replacement for GooglePlayServicesUtil added in 7.3.

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(appContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode) && isGooglePlayStoreInstalled()) {
                HyberLogger.i("Google Play services Recoverable Error: %d", resultCode);

                final SharedPreferences prefs = Hyber.getHyberPreferences(appContext);
                if (prefs.getBoolean("GT_DO_NOT_SHOW_MISSING_GPS", false))
                    return false;

                try {
                    showUpdateGPSDialog(resultCode);
                } catch (Exception e) {
                    HyberLogger.wtf(e, "showUpdateGPSDialog(resultCode)");
                }
            } else {
                HyberLogger.w("Google Play services error: This device is not supported. Code: %d", resultCode);
            }

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

    private void showUpdateGPSDialog(final int resultCode) {
        Hyber.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Activity activity = HyberActivityLifecycleHandler.getCurrActivity();
                if (activity == null || Hyber.getInitBuilder().isDisableGmsMissingPrompt())
                    return;

                String alertBodyText = getResourceString(activity, "hyber_gms_missing_alert_text",
                        "To receive push notifications please press 'Update' to enable 'Google Play services'.");
                String alertButtonUpdate = getResourceString(activity, "hyber_gms_missing_alert_button_update", "Update");
                String alertButtonSkip = getResourceString(activity, "hyber_gms_missing_alert_button_skip", "Skip");
                String alertButtonClose = getResourceString(activity, "hyber_gms_missing_alert_button_close", "Close");

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(alertBodyText).setPositiveButton(alertButtonUpdate, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            GooglePlayServicesUtil.getErrorPendingIntent(resultCode, activity, 0).send();
                        } catch (Exception e) {
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
                        HyberLogger.i("Device registered, Firebase registration push token = %s", registrationId);
                        registeredHandler.complete(registrationId);
                        break;
                    } else {
                        if (currentRetry >= (FCM_RETRY_COUNT - 1)) {
                            HyberLogger.e("FCM_RETRY_COUNT of %d exceed! Could not get a Firebase Registration Id",
                                    FCM_RETRY_COUNT);
                        } else {
                            HyberLogger.i("Google Play services returned SERVICE_NOT_AVAILABLE error."
                                    + " Current retry count: %d", currentRetry);
                            if (currentRetry == 2) {
                                // Retry 3 times before firing a null response and continuing a few more times.
                                registeredHandler.complete(null);
                            }
                            try {
                                Thread.sleep(TimeUnit.SECONDS.toMillis(FCM_RETRY_INTERVAL_SECONDS));
                            } catch (Throwable e) {
                                HyberLogger.wtf(e, "Thread.sleep error");
                            }
                        }
                    }
                }
            }
        }).start();
    }

}
