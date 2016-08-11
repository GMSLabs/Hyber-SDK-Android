package com.hyber;

import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class ActivityLifecycleHandler {

    static boolean nextResumeIsFirstActivity;
    static Activity curActivity;
    static FocusHandlerThread focusHandlerThread = new FocusHandlerThread();
    private static ActivityAvailableListener mActivityAvailableListener;

    // Note: Only supports one callback, create a list when this needs to be used by more than the permissions dialog.
    static void setActivityAvailableListener(ActivityAvailableListener activityAvailableListener) {
        if (curActivity != null) {
            activityAvailableListener.available(curActivity);
            mActivityAvailableListener = activityAvailableListener;
        } else
            mActivityAvailableListener = activityAvailableListener;
    }

    public static void removeActivityAvailableListener(ActivityAvailableListener activityAvailableListener) {
        mActivityAvailableListener = null;
    }

    private static void setCurActivity(Activity activity) {
        curActivity = activity;
        if (mActivityAvailableListener != null)
            mActivityAvailableListener.available(curActivity);
    }

    static void onActivityCreated(Activity activity) {

    }

    static void onActivityStarted(Activity activity) {

    }

    static void onActivityResumed(Activity activity) {
        setCurActivity(activity);

        logCurActivity();
        handleFocus();
    }

    static void onActivityPaused(Activity activity) {
        if (activity == curActivity) {
            curActivity = null;
            handleLostFocus();
        }

        logCurActivity();
    }

    static void onActivityStopped(Activity activity) {
        Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "onActivityStopped: " + activity.getClass().getName());

        if (activity == curActivity) {
            curActivity = null;
            handleLostFocus();
        }

        logCurActivity();
    }

    static void onActivityDestroyed(Activity activity) {
        Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "onActivityDestroyed: " + activity.getClass().getName());

        if (activity == curActivity) {
            curActivity = null;
            handleLostFocus();
        }

        logCurActivity();
    }

    static private void logCurActivity() {
        Hyber.Log(Hyber.LOG_LEVEL.DEBUG, "curActivity is NOW: " + (curActivity != null ? "" + curActivity.getClass().getName() + ":" + curActivity : "null"));
    }

    static private void handleLostFocus() {
        focusHandlerThread.runRunnable(new AppFocusRunnable());
    }

    static private void handleFocus() {
        if (focusHandlerThread.hasBackgrounded() || nextResumeIsFirstActivity) {
            nextResumeIsFirstActivity = false;
            focusHandlerThread.resetBackgroundState();
            Hyber.onAppFocus();
        } else
            focusHandlerThread.stopScheduledRunnable();
    }

    interface ActivityAvailableListener {
        void available(Activity activity);
    }

    static class FocusHandlerThread extends HandlerThread {
        Handler mHandler = null;
        private AppFocusRunnable appFocusRunnable;

        FocusHandlerThread() {
            super("FocusHandlerThread");
            start();
            mHandler = new Handler(getLooper());
        }

        Looper getHandlerLooper() {
            return mHandler.getLooper();
        }

        void resetBackgroundState() {
            if (appFocusRunnable != null)
                appFocusRunnable.backgrounded = false;
        }

        void stopScheduledRunnable() {
            mHandler.removeCallbacksAndMessages(null);
        }

        void runRunnable(AppFocusRunnable runnable) {
            if (appFocusRunnable != null && appFocusRunnable.backgrounded && !appFocusRunnable.completed)
                return;

            appFocusRunnable = runnable;
            mHandler.removeCallbacksAndMessages(null);
            mHandler.postDelayed(runnable, 2000);
        }

        boolean hasBackgrounded() {
            if (appFocusRunnable != null)
                return appFocusRunnable.backgrounded;
            return false;
        }
    }

    static private class AppFocusRunnable implements Runnable {
        private boolean backgrounded, completed;

        public void run() {
            if (curActivity != null)
                return;

            backgrounded = true;
//            Hyber.onAppLostFocus(false);
            completed = true;
        }
    }

}
