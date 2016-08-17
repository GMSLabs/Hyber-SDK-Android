package com.hyber.example;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.messaging.RemoteMessage;
import com.hyber.Hyber;

import timber.log.Timber;

public class SplashActivity extends AppCompatActivity {

    private TextView textView;
    private Button button1, button2, button3;
    private Long mPhone;

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
                Answers.getInstance().logCustom(new CustomEvent("Button")
                        .putCustomAttribute("name", button1.getText().toString()));
                AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);

                // Set up the input
                final EditText input = new EditText(SplashActivity.this);
                // Specify the type of input expected
                input.setInputType(InputType.TYPE_CLASS_PHONE | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            mPhone = Long.parseLong(input.getText().toString());
                        } catch (Exception e) {
                            mPhone = null;
                        }
                        if (mPhone != null) {
                            Hyber.userRegistration(mPhone, new Hyber.UserRegistrationHandler() {
                                @Override
                                public void onSuccess() {
                                    String s = "User registration onSuccess\nWith phone " + mPhone;
                                    Timber.d(s);
                                    textView.setText(s);
                                }

                                @Override
                                public void onFailure(String message) {
                                    String s = "User registration onFailure\nWith phone " + mPhone;
                                    Timber.e(s + "\n" + message);
                                    textView.setText(s + "\n" + message);
                                }
                            });
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mPhone = null;
                        dialogInterface.cancel();
                    }
                });

                builder.show();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Answers.getInstance().logCustom(new CustomEvent("Button")
                        .putCustomAttribute("name", button2.getText().toString()));
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
                Answers.getInstance().logCustom(new CustomEvent("Button")
                        .putCustomAttribute("name", button3.getText().toString()));
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

                CustomEvent ce = new CustomEvent("RemoteMessage");

                String message = "";
                message += "From: " + remoteMessage.getFrom();

                ce.putCustomAttribute("From", remoteMessage.getFrom());

                // Check if message contains a data payload.
                if (remoteMessage.getData().size() > 0) {
                    ce.putCustomAttribute("Message data payload", "" + remoteMessage.getData());
                    message += "\nMessage data payload: " + remoteMessage.getData();
                }

                // Check if message contains a notification payload.
                if (remoteMessage.getNotification() != null) {
                    ce.putCustomAttribute("Message Notification Body", remoteMessage.getNotification().getBody());
                    message += "\nMessage Notification Body: " + remoteMessage.getNotification().getBody();
                }

                Answers.getInstance().logCustom(ce);

                textView.setText(message);
            }
        });
    }

}
