package com.hyber.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.messaging.RemoteMessage;
import com.hyber.Hyber;

import timber.log.Timber;

public class SplashActivity extends AppCompatActivity {

    private TextView textView;
    private Button button1, button2, button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        textView = (TextView) findViewById(R.id.textview);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Hyber.userRegistration(380999100119L, new Hyber.UserRegistrationHandler() {
                    @Override
                    public void onSuccess() {
                        String s = "User registration onSuccess";
                        Timber.d(s);
                        textView.setText(s);
                    }

                    @Override
                    public void onFailure(String message) {
                        String s = "User registration onFailure";
                        Timber.e(s + "\n" + message);
                        textView.setText(s + "\n" + message);
                    }
                });
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Hyber.deviceUpdate(new Hyber.DeviceUpdateHandler() {
                    @Override
                    public void onSuccess() {
                        String s = "Device update onSuccess";
                        Timber.d(s);
                        textView.setText(s);
                    }

                    @Override
                    public void onFailure(String message) {
                        String s = "Device update onFailure";
                        Timber.e(s + "\n" + message);
                        textView.setText(s + "\n" + message);
                    }
                });
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Hyber.getMessageHistory(System.currentTimeMillis(), new Hyber.MessageHistoryHandler() {
                    @Override
                    public void onSuccess() {
                        String s = "Message history onSuccess";
                        Timber.d(s);
                        textView.setText(s);
                    }

                    @Override
                    public void onFailure(String message) {
                        String s = "Message history onFailure";
                        Timber.e(s + "\n" + message);
                        textView.setText(s + "\n" + message);
                    }
                });
            }
        });

        Hyber.notificationListener(new Hyber.NotificationListener() {
            @Override
            public void onMessageReceived(RemoteMessage remoteMessage) {
                String message = "";
                message += "From: " + remoteMessage.getFrom();

                // Check if message contains a data payload.
                if (remoteMessage.getData().size() > 0) {
                    message += "\nMessage data payload: " + remoteMessage.getData();
                }

                // Check if message contains a notification payload.
                if (remoteMessage.getNotification() != null) {
                    message += "\nMessage Notification Body: " + remoteMessage.getNotification().getBody();
                }

                textView.setText(message);
            }
        });
    }

}
