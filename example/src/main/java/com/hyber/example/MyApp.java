package com.hyber.example;

import android.app.Application;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;

import com.hyber.Hyber;
import com.crashlytics.android.Crashlytics;
import com.hyber.HyberMessageModel;

import io.fabric.sdk.android.Fabric;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        // Logging set to help debug issues, remove before releasing your app.
        Hyber.setLogLevel(Hyber.LOG_LEVEL.VERBOSE, Hyber.LOG_LEVEL.WARN);

        //Initialisation Hyber SDK
        Hyber.startInit(this)
                .init();

        Hyber.notificationListener(new Hyber.NotificationListener() {
            @Override
            public void onMessageReceived(HyberMessageModel hyberMessageModel) {

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(MyApp.this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(hyberMessageModel.getAlpha())
                                .setContentText(hyberMessageModel.getId() + " ==> " + hyberMessageModel.getText())
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
        });
    }

}
