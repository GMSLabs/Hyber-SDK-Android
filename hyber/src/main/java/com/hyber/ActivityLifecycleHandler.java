package com.hyber;

import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import com.hyber.log.HyberLogger;

import java.lang.ref.WeakReference;

final class ActivityLifecycleHandler {

    private static final String TAG = "ActivityLifecycleHandler";

    private static final int DELAY_MILLS = 2000;
    private static boolean nextResumeIsFirstActivity;
    private static WeakReference<Activity> curActivity;
    private static FocusHandlerThread focusHandlerThread = new FocusHandlerThread();
    private static ActivityAvailableListener mActivityAvailableListener;

    private ActivityLifecycleHandler() {

    }

    static Activity getCurrActivity() {
        return curActivity.get();
    }

    // Note: Only supports one callback, create a list when this needs to be used by more than the permissions dialog.
    static void setActivityAvailableListener(ActivityAvailableListener activityAvailableListener) {
        if (curActivity.isEnqueued()) {
            activityAvailableListener.available(curActivity.get());
            mActivityAvailableListener = activityAvailableListener;
        } else {
            mActivityAvailableListener = activityAvailableListener;
        }
    }

    public static void removeActivityAvailableListener(ActivityAvailableListener activityAvailableListener) {
        mActivityAvailableListener = null;
    }

    private static void setCurActivity(Activity activity) {
        curActivity = new WeakReference<>(activity);
        if (mActivityAvailableListener != null)
            mActivityAvailableListener.available(curActivity.get());
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
        if (activity == curActivity.get()) {
            curActivity.clear();
            handleLostFocus();
        }

        logCurActivity();
    }

    static void onActivityStopped(Activity activity) {
        HyberLogger.tag(TAG);
        HyberLogger.d("onActivityStopped: %s", activity.getClass().getName());

        if (activity == curActivity.get()) {
            curActivity.clear();
            handleLostFocus();
        }

        logCurActivity();
    }

    static void onActivityDestroyed(Activity activity) {
        HyberLogger.tag(TAG);
        HyberLogger.d("onActivityDestroyed: %s", activity.getClass().getName());

        if (activity == curActivity.get()) {
            curActivity.clear();
            handleLostFocus();
        }

        logCurActivity();
    }

    private static void logCurActivity() {
        HyberLogger.tag(TAG);
        HyberLogger.d("curActivity is NOW: %s", (curActivity.get() != null
                ? curActivity.get().getClass().getName() + ":" + curActivity.get() : "null"));
    }

    private static void handleLostFocus() {
        focusHandlerThread.runRunnable(new AppFocusRunnable());
    }

    private static void handleFocus() {
        if (focusHandlerThread.hasBackgrounded() || nextResumeIsFirstActivity) {
            nextResumeIsFirstActivity = false;
            focusHandlerThread.resetBackgroundState();
            Hyber.onAppFocus();
        } else {
            focusHandlerThread.stopScheduledRunnable();
        }
    }

    interface ActivityAvailableListener {
        void available(Activity activity);
    }

    static class FocusHandlerThread extends HandlerThread {
        private Handler mHandler = null;
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
            mHandler.postDelayed(runnable, DELAY_MILLS);
        }

        boolean hasBackgrounded() {
            if (appFocusRunnable != null)
                return appFocusRunnable.backgrounded;
            return false;
        }
    }

    private static class AppFocusRunnable implements Runnable {
        private boolean backgrounded, completed;

        public void run() {
            if (curActivity.isEnqueued())
                return;

            backgrounded = true;
//            Hyber.onAppLostFocus(false);
            completed = true;
        }
    }
}
