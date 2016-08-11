package com.hyber;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.UUID;

import timber.log.Timber;

public class Hyber {

    /**
     * Tag used on log messages.
     */
    static final String TAG = "Hyber";

    public static String sdkType = "native";
    public static final String sdkVersion = "000001";

    private static LOG_LEVEL visualLogLevel = LOG_LEVEL.NONE;
    private static LOG_LEVEL logCatLevel = LOG_LEVEL.WARN;

    private static OSUtils.DeviceType deviceType;
    private static OSUtils osUtils;

    static Context appContext;
    static String clientApiKey, applicationKey;
    static String installationID, fingerprint;

    static Hyber.Builder mInitBuilder;
    static boolean initDone;
    private static boolean startedRegistration;

    private static boolean foreground;

    private static String lastRegistrationId;
    private static boolean registerForPushFired;

    public static class Builder {
        Context mContext;
        boolean mPromptLocation;
        boolean mDisableGmsMissingPrompt;

        private Builder() {
        }

        private Builder(Context context) {
            mContext = context;
        }

        public Builder setAutoPromptLocation(boolean enable) {
            mPromptLocation = enable;
            return this;
        }

        public Builder disableGmsMissingPrompt(boolean disable) {
            mDisableGmsMissingPrompt = disable;
            return this;
        }

        public void init() {
            Hyber.init(this);
        }
    }

    public static Hyber.Builder startInit(Context context) {
        return new Hyber.Builder(context);
    }

    private static void init(Hyber.Builder inBuilder) {
        mInitBuilder = inBuilder;

        Context context = mInitBuilder.mContext;
        mInitBuilder.mContext = null; // Clear to prevent leaks.

        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            Hyber.init(context, bundle.getString("hyber_client_api_key"), bundle.getString("hyber_application_key"));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void init(Context context, String hyberClientApiKey, String hyberApplicationKey) {
        HyberDataSourceController.with(context);

        if (mInitBuilder == null)
            mInitBuilder = new Hyber.Builder();

        osUtils = new OSUtils();

        deviceType = osUtils.getDeviceType();

        installationID = Installation.id(context);
        fingerprint = Fingerprint.keyHash(context);

        // START: Init validation
        try {
            //noinspection ResultOfMethodCallIgnored
            UUID.fromString(hyberClientApiKey);
        } catch (Throwable t) {
            Log(LOG_LEVEL.FATAL, "Hyber ClientId format is invalid.\nExample: 'b2f7f966-d8cc-11e4-bed1-df8f05be55ba'\n", t);
            return;
        }

        try {
            //noinspection ResultOfMethodCallIgnored
            UUID.fromString(hyberApplicationKey);
        } catch (Throwable t) {
            Log(LOG_LEVEL.FATAL, "Hyber AppId format is invalid.\nExample: 'b2f7f966-d8cc-11e4-bed1-df8f05be55ba'\n", t);
            return;
        }

        if ("b2f7f966-d8cc-11e4-bed1-df8f05be55ba".equals(hyberClientApiKey))
            Log(LOG_LEVEL.WARN, "Hyber Example ClientID detected, please update to your client's id found on Hyber.com");

        if ("5eb5a37e-b458-11e3-ac11-000c2940e62c".equals(hyberApplicationKey))
            Log(LOG_LEVEL.WARN, "Hyber Example AppID detected, please update to your app's id found on Hyber.com");

        if (deviceType == OSUtils.DeviceType.FCM) {
            //TODO Validate integration params
        }

        try {
            Class.forName("android.support.v4.view.MenuCompat");
            try {
                Class.forName("android.support.v4.content.WakefulBroadcastReceiver");
                Class.forName("android.support.v4.app.NotificationManagerCompat");
            } catch (ClassNotFoundException e) {
                Log(LOG_LEVEL.FATAL, "The included Android Support Library v4 is to old or incomplete. Please update your project's android-support-v4.jar to the latest revision.", e);
            }
        } catch (ClassNotFoundException e) {
            Log(LOG_LEVEL.FATAL, "Could not find the Android Support Library v4. Please make sure android-support-v4.jar has been correctly added to your project.", e);
        }

        if (initDone) {
            if (context != null)
                appContext = context.getApplicationContext();

            return;
        }

        // END: Init validation
        boolean contextIsActivity = (context instanceof Activity);

        foreground = contextIsActivity;
        clientApiKey = hyberClientApiKey;
        applicationKey = hyberApplicationKey;
        appContext = context.getApplicationContext();

        initDone = true;
    }

    public static void setLogLevel(LOG_LEVEL inLogCatLevel, LOG_LEVEL inVisualLogLevel) {
        logCatLevel = inLogCatLevel;
        visualLogLevel = inVisualLogLevel;
    }

    public static void setLogLevel(int inLogCatLevel, int inVisualLogLevel) {
        setLogLevel(getLogLevel(inLogCatLevel), getLogLevel(inVisualLogLevel));
    }

    private static Hyber.LOG_LEVEL getLogLevel(int level) {
        switch (level) {
            case 0:
                return Hyber.LOG_LEVEL.NONE;
            case 1:
                return Hyber.LOG_LEVEL.FATAL;
            case 2:
                return Hyber.LOG_LEVEL.ERROR;
            case 3:
                return Hyber.LOG_LEVEL.WARN;
            case 4:
                return Hyber.LOG_LEVEL.INFO;
            case 5:
                return Hyber.LOG_LEVEL.DEBUG;
            case 6:
                return Hyber.LOG_LEVEL.VERBOSE;
        }

        if (level < 0)
            return Hyber.LOG_LEVEL.NONE;
        return Hyber.LOG_LEVEL.VERBOSE;
    }

    private static boolean atLogLevel(LOG_LEVEL level) {
        return level.compareTo(visualLogLevel) < 1 || level.compareTo(logCatLevel) < 1;
    }

    static void Log(LOG_LEVEL level, String message) {
        Log(level, message, null);
    }

    static void Log(Throwable throwable) {
        Timber.e(throwable, throwable.getLocalizedMessage());
    }

    static void Log(final LOG_LEVEL level, String message, Throwable throwable) {
        if (level.compareTo(logCatLevel) < 1) {
            if (level == LOG_LEVEL.VERBOSE)
                Log.v(TAG, message, throwable);
            else if (level == LOG_LEVEL.DEBUG)
                Log.d(TAG, message, throwable);
            else if (level == LOG_LEVEL.INFO)
                Log.i(TAG, message, throwable);
            else if (level == LOG_LEVEL.WARN)
                Log.w(TAG, message, throwable);
            else if (level == LOG_LEVEL.ERROR || level == LOG_LEVEL.FATAL)
                Log.e(TAG, message, throwable);
        }
    }

    static void onAppFocus() {
        foreground = true;

        try {
            startRegistrationOrOnSession();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static void startRegistrationOrOnSession() throws Throwable {
        if (startedRegistration)
            return;

        startedRegistration = true;

        PushRegistrator pushRegistrator;

        switch (deviceType) {
            case FCM:
                pushRegistrator = new PushRegistrarFCM();
                break;
            default:
                throw new Throwable("Firebase classes not found");
        }

        pushRegistrator.registerForPush(appContext, new PushRegistrator.RegisteredHandler() {
            @Override
            public void complete(String id) {
                lastRegistrationId = id;
                registerForPushFired = true;
                updateDeviceData();
            }
        });
    }

    private static void updateDeviceData() {
        Log(LOG_LEVEL.DEBUG, "updateDeviceData: registerForPushFired:" + registerForPushFired);

        if (!registerForPushFired)
            return;
    }

    static SharedPreferences getHyberPreferences(Context context) {
        return context.getSharedPreferences(Hyber.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    static void runOnUiThread(Runnable action) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(action);
    }

    public enum LOG_LEVEL {
        NONE, FATAL, ERROR, WARN, INFO, DEBUG, VERBOSE
    }

}
