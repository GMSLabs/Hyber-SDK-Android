package com.hyber.example;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.hyber.Hyber;
import com.hyber.HyberLogger;
import com.hyber.HyberMessageModel;
import com.hyber.HyberStatus;
import com.hyber.Message;
import com.hyber.Repository;
import com.hyber.User;
import com.hyber.listener.HyberNotificationListener;

import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class MyApp extends Application {

    private Handler mHandler;
    private AlertDialog mAlertDialog;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        mHandler = new Handler();

        HyberLogger.plant(new HyberLogger.DebugTree(), new HyberCrashReportingTree(), new HyberUICrashReportingTree());

        //Initialisation Hyber SDK
        Hyber.with(this)
                .setNotificationListener(new HyberNotificationListener() {
                    @Override
                    public void onMessageReceived(RemoteMessage remoteMessage) {
                        if (remoteMessage.getNotification() != null) {
                            onNotification(remoteMessage.getNotification());
                        } else if (remoteMessage.getData() != null) {
                            onNotificationFromData(remoteMessage.getData());
                        }
                    }
                })
                .init();
    }

    private class HyberCrashReportingTree extends HyberLogger.Tree {

        @Override
        protected void log(int priority, @Nullable String tag, @Nullable HyberStatus status, @Nullable String message,
                           @Nullable Throwable t) {
            if (priority <= Log.WARN) {
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
        protected void log(final int priority, @Nullable String tag, @Nullable final HyberStatus status,
                           @Nullable final String message, @Nullable final Throwable t) {
            if (priority < Log.WARN) {
                return;
            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mAlertDialog != null && mAlertDialog.isShowing())
                        mAlertDialog.dismiss();

                    final AlertDialog.Builder mAlertDialogBuilder =
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
                        default:
                            mAlertDialogBuilder.setTitle("VERBOSE");
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
                    if (mAlertDialog.getWindow() != null) {
                        mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        mAlertDialog.show();
                    }
                }
            });
        }
    }

    private void onNotification(@NonNull @lombok.NonNull RemoteMessage.Notification notification) {
        Intent i = new Intent(this, SplashActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(MyApp.this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(notification.getTitle())
                        .setContentText(notification.getBody())
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentIntent(pendingIntent);
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(0, mBuilder.build());
    }

    private void onNotificationFromData(@NonNull @lombok.NonNull Map<String, String> data) {
        String messageData = data.get("message");
        if (messageData != null) {
            try {
                Intent i = new Intent(this, SplashActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i, PendingIntent.FLAG_UPDATE_CURRENT);

                HyberMessageModel messageModel = new Gson().fromJson(messageData, HyberMessageModel.class);

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(MyApp.this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(messageModel.getAlpha())
                                .setContentText(messageModel.getText())
                                .setAutoCancel(true)
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setContentIntent(pendingIntent);
                // Gets an instance of the NotificationManager service
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                // Builds the notification and issues it.
                mNotifyMgr.notify(0, mBuilder.build());
            } catch (Exception e) {
                HyberLogger.e(e);
            }
        } else {
            HyberLogger.w("Message object not exist in RemoteMessage data payload");
        }
    }

}
