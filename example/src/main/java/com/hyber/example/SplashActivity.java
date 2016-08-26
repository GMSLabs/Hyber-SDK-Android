package com.hyber.example;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.messaging.RemoteMessage;
import com.hyber.Hyber;
import com.hyber.example.adapter.MyMessagesRVAdapter;

import java.util.Date;

import io.realm.Realm;
import timber.log.Timber;

public class SplashActivity extends AppCompatActivity {

    private TextView textView;
    private Button button1, button2, button3, button4;
    private Long mPhone;
    private RecyclerView mRecyclerView;
    private MyMessagesRVAdapter mAdapter;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        textView = (TextView) findViewById(R.id.textview);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);

        mRecyclerView = (RecyclerView) findViewById(R.id.received_messages_RecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        realm = Realm.getDefaultInstance();
        mAdapter = new MyMessagesRVAdapter(this, realm);
        mRecyclerView.setAdapter(mAdapter);

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
                    public void onSuccess(@NonNull Long recommendedNextTime) {
                        String s = "Message history onSuccess\n" +
                                "recommendedNextTime is " + new Date(recommendedNextTime).toString();
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

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SplashActivity.this, MessagesActivity.class);
                startActivity(i);
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

                textView.setText(message);
                mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
            }
        });
    }

}
