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
import com.hyber.handler.DeviceUpdateHandler;
import com.hyber.handler.UserRegistrationHandler;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class AuthorizationActivity extends AppCompatActivity {

    @BindView(R.id.statusTextView) TextView statusTextView;
    @BindView(R.id.userRegisterButton) Button userRegisterButton;
    private Long mPhone;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        ButterKnife.bind(this);

        showRegisterDialog(AuthorizationActivity.this);

        userRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterDialog(AuthorizationActivity.this);
            }
        });

    }

    private void deviceDataUpdate() {
        Hyber.deviceUpdate(new DeviceUpdateHandler() {
            @Override
            public void onSuccess() {
                startActivity(new Intent(AuthorizationActivity.this, MainActivity.class));
            }

            @Override
            public void onFailure() {
                showRegisterDialog(AuthorizationActivity.this);
            }
        });
    }

    private void userRegistration() {
        Hyber.userRegistration(mPhone, new UserRegistrationHandler() {
            @Override
            public void onSuccess() {
                String s = String.format(Locale.getDefault(), "User registration onSuccess\nWith phone %d", mPhone);
                Timber.d(s);
                statusTextView.setText(s);
                deviceDataUpdate();
            }

            @Override
            public void onFailure(String message) {
                String s = String.format(Locale.getDefault(), "User registration onFailure\nWith phone %d", mPhone);
                Timber.e("%s\n%s", s, message);
                statusTextView.setText(String.format(Locale.getDefault(), "%s\n%s", s, message));
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
                    userRegistration();
                } catch (NullPointerException e) {
                    Timber.e(e);
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
