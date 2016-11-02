package com.hyber.example;

import android.os.Handler;
import android.os.Looper;

import com.hyber.HyberLogger;

import java.util.concurrent.CountDownLatch;

public class DispatchQueue extends Thread {

    private volatile Handler handler = null;
    private CountDownLatch syncLatch = new CountDownLatch(1);

    public DispatchQueue(final String threadName) {
        setName(threadName);
        start();
    }

    public void postRunnable(Runnable runnable) {
        postRunnable(runnable, 0);
    }

    public void postRunnable(Runnable runnable, long delay) {
        try {
            syncLatch.await();
            if (delay <= 0) {
                handler.post(runnable);
            } else {
                handler.postDelayed(runnable, delay);
            }
        } catch (Exception e) {
            HyberLogger.e(e);
        }
    }

    @Override
    public void run() {
        Looper.prepare();
        handler = new Handler();
        syncLatch.countDown();
        Looper.loop();
    }

}
