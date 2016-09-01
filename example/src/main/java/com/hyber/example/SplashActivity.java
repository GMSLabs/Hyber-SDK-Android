package com.hyber.example;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hyber.Hyber;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.statusTextView) TextView statusTextView;
    @BindView(R.id.userRegisterButton) Button userRegisterButton;
    private Long mPhone;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);

        Hyber.deviceUpdate(new Hyber.DeviceUpdateHandler() {
            @Override
            public void onSuccess() {
                updatePushToken();
            }

            @Override
            public void onFailure(String message) {
                showRegisterDialog(SplashActivity.this);
            }
        });

        userRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterDialog(SplashActivity.this);
            }
        });

    }

    private void userRegistration() {
        Hyber.userRegistration(mPhone, new Hyber.UserRegistrationHandler() {
            @Override
            public void onSuccess() {
                String s = "HyberUser registration onSuccess\nWith phone " + mPhone;
                Timber.d(s);
                statusTextView.setText(s);
                updatePushToken();
            }

            @Override
            public void onFailure(String message) {
                String s = "HyberUser registration onFailure\nWith phone " + mPhone;
                Timber.e("%s\n%s", s, message);
                statusTextView.setText(s + "\n" + message);
            }
        });
    }

    private void updatePushToken() {
        Hyber.pushTokenUpdate(new Hyber.PushTokenUpdateHandler() {
            @Override
            public void onSuccess() {
                String s = "Push token update onSuccess";
                Timber.d(s);
                statusTextView.setText(s);
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }

            @Override
            public void onFailure(String message) {
                String s = "Push token update onFailure";
                Timber.e("%s\n%s", s, message);
                statusTextView.setText(s + "\n" + message);
            }
        });
    }

    private void showRegisterDialog(Context context) {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Set up the input
        final EditText input = new EditText(context);
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_PHONE | InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        builder.setView(input);

        builder.setTitle("Enter phone number");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    mPhone = Long.parseLong(input.getText().toString());
                } catch (Exception e) {
                    mPhone = null;
                }
                if (mPhone != null) {
                    userRegistration();
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

        mAlertDialog = builder.create();
        mAlertDialog.show();
    }

}
