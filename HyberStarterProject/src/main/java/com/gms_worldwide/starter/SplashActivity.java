package com.gms_worldwide.starter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Andrew Kochura.
 */
public class SplashActivity extends AppCompatActivity {

    String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

}