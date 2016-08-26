package com.hyber.example;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.statusTextView) TextView statusTextView;
    @BindView(R.id.button1) Button button1;
    @BindView(R.id.button2) Button button2;
    @BindView(R.id.button3) Button button3;
    @BindView(R.id.button4) Button button4;
    private Long mPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);

        Hyber.deviceUpdate(new Hyber.DeviceUpdateHandler() {
            @Override
            public void onSuccess() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }

            @Override
            public void onFailure(String message) {
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
                                    statusTextView.setText(s);
                                }

                                @Override
                                public void onFailure(String message) {
                                    String s = "User registration onFailure\nWith phone " + mPhone;
                                    Timber.e(s + "\n" + message);
                                    statusTextView.setText(s + "\n" + message);
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
                                    statusTextView.setText(s);
                                }

                                @Override
                                public void onFailure(String message) {
                                    String s = "User registration onFailure\nWith phone " + mPhone;
                                    Timber.e(s + "\n" + message);
                                    statusTextView.setText(s + "\n" + message);
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
                        statusTextView.setText(s);
                    }

                    @Override
                    public void onFailure(String message) {
                        String s = "Device update onFailure";
                        Timber.e(s + "\n" + message);
                        statusTextView.setText(s + "\n" + message);
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
                    public void onSuccess(@NonNull Long recommendedNextTime) {
                        String s = "Message history onSuccess\n" +
                                "recommendedNextTime is " + new Date(recommendedNextTime).toString();
                        Timber.d(s);
                        statusTextView.setText(s);
                    }

                    @Override
                    public void onFailure(String message) {
                        String s = "Message history onFailure";
                        Timber.e(s + "\n" + message);
                        statusTextView.setText(s + "\n" + message);
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
                    ce.putCustomAttribute("Message data payload", "exists");
                    message += "\nMessage data payload: " + remoteMessage.getData();
                }

                // Check if message contains a notification payload.
                if (remoteMessage.getNotification() != null) {
                    ce.putCustomAttribute("Message Notification Body", "exists");
                    message += "\nMessage Notification Body: " + remoteMessage.getNotification().getBody();
                }

                Answers.getInstance().logCustom(ce);

                statusTextView.setText(message);
            }
        });
    }

}
