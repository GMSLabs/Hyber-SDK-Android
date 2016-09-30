package com.hyber.example;

import android.app.Application;
import android.app.NotificationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.crash.FirebaseCrash;
import com.hyber.Hyber;
import com.hyber.HyberError;
import com.hyber.HyberMessageModel;
import com.hyber.listener.HyberErrorListener;
import com.hyber.listener.HyberNotificationListener;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        //Initialisation Hyber SDK
        Hyber.with(this)
                .setLogLevel(Hyber.LogLevel.VERBOSE)
                .setErrorListener(new HyberErrorListener() {
                    @Override
                    public void onError(@NonNull HyberError error) {
                        String errorMessage = String.format(Locale.getDefault(),
                                "Hyber error code %d\n%s", error.getCode(), error.getDescription());
                        Timber.e(errorMessage);
                        Crashlytics.log(errorMessage);
                        FirebaseCrash.log(errorMessage);
                    }
                })
                .setNotificationListener(new HyberNotificationListener() {
                    @Override
                    public void onMessageReceived(HyberMessageModel hyberMessageModel) {
                        onNotification(hyberMessageModel);
                    }
                })
                .init();
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
