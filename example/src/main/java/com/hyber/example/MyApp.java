package com.hyber.example;

import android.app.Application;

import com.hyber.Hyber;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Logging set to help debug issues, remove before releasing your app.
        Hyber.setLogLevel(Hyber.LOG_LEVEL.VERBOSE, Hyber.LOG_LEVEL.WARN);

        //Initialisation Hyber SDK
        Hyber.startInit(this)
                .init();
    }

}
