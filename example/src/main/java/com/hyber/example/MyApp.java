package com.hyber.example;

import android.app.Application;

import com.hyber.Hyber;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Initialisation Hyber SDK
        Hyber.startInit(this)
                .init();
    }

}
