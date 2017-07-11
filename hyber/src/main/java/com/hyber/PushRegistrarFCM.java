package com.hyber;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.hyber.log.HyberLogger;

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
        registerInBackground();
    }

    private void registerInBackground() {
        new Thread(new Runnable() {
            public void run() {
                String registrationId = null;

                for (int currentRetry = 0; currentRetry < FCM_RETRY_COUNT; currentRetry++) {
                    try {
                        FirebaseApp.initializeApp(appContext);
                    } catch (Exception e) {
                        HyberLogger.e(e);
                    }
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
