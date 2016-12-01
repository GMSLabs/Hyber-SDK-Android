package com.hyber.example;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.hyber.Hyber;
import com.hyber.HyberLogger;
import com.hyber.HyberMessageModel;
import com.hyber.HyberStatus;
import com.hyber.listener.HyberNotificationListener;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class ApplicationLoader extends Application {

    public static volatile Context applicationContext;
    public static volatile Handler applicationHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationContext = getApplicationContext();

        Fabric.with(this, new Crashlytics());

        applicationHandler = new Handler(applicationContext.getMainLooper());

        HyberLogger.plant(new HyberLogger.DebugTree(), new CrashReportingTree(), new UIErrorTree());

        //Initialisation Hyber SDK
        Hyber.with(this, BuildConfig.HYBER_CLIENT_API_KEY)
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
            AndroidUtilities.checkDisplaySize(applicationContext, newConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onNotification(@NonNull @lombok.NonNull RemoteMessage.Notification notification) {
        Intent i = new Intent(this, SplashActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ApplicationLoader.this)
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

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

                HyberMessageModel messageModel = new Gson().fromJson(messageData, HyberMessageModel.class);

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(ApplicationLoader.this)
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

    private class CrashReportingTree extends HyberLogger.Tree {

        @Override
        protected void log(int priority, @Nullable String tag, @Nullable HyberStatus status, @Nullable String message,
                           @Nullable Throwable t) {
            if (priority <= Log.WARN) {
                return;
            }

            if (t != null) {
                Crashlytics.logException(t);
                FirebaseCrash.report(t);
            }

            if (status != null) {
                Crashlytics.log(priority, tag == null ? "MyHyber" : tag,
                        String.format(Locale.getDefault(), "%d ==> %s",
                                status.getCode(), status.getDescription()));
                FirebaseCrash.logcat(priority, tag == null ? "MyHyber" : tag,
                        String.format(Locale.getDefault(), "%d ==> %s", status.getCode(), status.getDescription()));
            }
        }
    }

    private class UIErrorTree extends HyberLogger.Tree {

        @Override
        protected void log(final int priority, @Nullable String tag, @Nullable final HyberStatus status,
                           @Nullable final String message, @Nullable final Throwable t) {
            if (priority < Log.WARN) {
                return;
            }

            StringBuilder sb = new StringBuilder();
            switch (priority) {
                case Log.WARN:
                    sb.append("WARN:");
                    break;
                case Log.ERROR:
                    sb.append("ERROR:");
                    break;
                case Log.ASSERT:
                    sb.append("ASSERT:");
                    break;
                default:
                    sb.append("VERBOSE:");
            }

            if (t != null) {
                sb.append(t.getLocalizedMessage())
                        .append("\n");
            }
            if (status != null) {
                sb.append(String.format(Locale.getDefault(), "%d ==> %s",
                        status.getCode(), status.getDescription()))
                        .append("\n");
            }
            if (message != null) {
                sb.append(message);
            }

            Toast.makeText(applicationContext, sb.toString(), Toast.LENGTH_LONG).show();
        }
    }

}
