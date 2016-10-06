package com.hyber.example;

import android.app.Application;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.crash.FirebaseCrash;
import com.hyber.Hyber;
import com.hyber.HyberLogger;
import com.hyber.HyberMessageModel;
import com.hyber.HyberStatus;
import com.hyber.listener.HyberNotificationListener;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class MyApp extends Application {

    private AlertDialog mAlertDialog;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        HyberLogger.plant(new HyberLogger.DebugTree(), new HyberCrashReportingTree(), new HyberUICrashReportingTree());

        //Initialisation Hyber SDK
        Hyber.with(this)
                .setNotificationListener(new HyberNotificationListener() {
                    @Override
                    public void onMessageReceived(HyberMessageModel hyberMessageModel) {
                        onNotification(hyberMessageModel);
                    }
                })
                .init();
    }

    private class HyberCrashReportingTree extends HyberLogger.Tree {

        @Override
        protected void log(int priority, @Nullable String tag, @Nullable HyberStatus status, @Nullable String message,
                           @Nullable Throwable t) {
            if (priority <= Log.INFO) {
                return;
            }
            if (t != null) {
                Crashlytics.logException(t);
                FirebaseCrash.report(t);
            } else if (status != null) {
                Crashlytics.log(priority, tag == null ? "MyHyber" : tag,
                        String.format(Locale.getDefault(), "%d ==> %s",
                                status.getCode(), status.getDescription()));
                FirebaseCrash.logcat(priority, tag == null ? "MyHyber" : tag,
                        String.format(Locale.getDefault(), "%d ==> %s", status.getCode(), status.getDescription()));
            } else if (message != null) {
                Crashlytics.log(message);
                FirebaseCrash.log(message);
            }
        }
    }

    private class HyberUICrashReportingTree extends HyberLogger.Tree {

        @Override
        protected void log(int priority, @Nullable String tag, @Nullable HyberStatus status, @Nullable String message,
                           @Nullable Throwable t) {
            if (priority < Log.WARN) {
                return;
            }

            if (mAlertDialog != null && mAlertDialog.isShowing())
                mAlertDialog.dismiss();

            AlertDialog.Builder mAlertDialogBuilder =
                    new AlertDialog.Builder(new ContextThemeWrapper(MyApp.this, R.style.errorDialog));

            switch (priority) {
                case Log.WARN:
                    mAlertDialogBuilder.setTitle("WARN");
                    break;
                case Log.ERROR:
                    mAlertDialogBuilder.setTitle("ERROR");
                    break;
                case Log.ASSERT:
                    mAlertDialogBuilder.setTitle("ASSERT");
                    break;
                default: mAlertDialogBuilder.setTitle("VERBOSE");
            }

            if (t != null) {
                mAlertDialogBuilder.setMessage(t.getLocalizedMessage());
            } else if (status != null) {
                mAlertDialogBuilder.setMessage(String.format(Locale.getDefault(), "%d ==> %s",
                        status.getCode(), status.getDescription()));
            } else if (message != null) {
                mAlertDialogBuilder.setMessage(message);
            }

            mAlertDialog = mAlertDialogBuilder.create();
            mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            mAlertDialog.show();
        }
    }

    private void onNotification(HyberMessageModel hyberMessageModel) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(MyApp.this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(hyberMessageModel.getAlpha())
                        .setContentText(String.format(Locale.getDefault(),
                                "%s ==> %s",
                                hyberMessageModel.getId(), hyberMessageModel.getText()))
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE);

        // Sets an ID for the notification
        int mNotificationId = Integer.parseInt(hyberMessageModel.getId());
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

}
