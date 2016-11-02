package com.hyber.example;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import com.hyber.Hyber;
import com.hyber.HyberLogger;
import com.hyber.example.ui.AuthActivity;
import com.hyber.handler.CheckAuthorizationHandler;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.progressBar) ProgressBar mProgress;
    private final int maxProgress = 100;
    private final int progressWaitMills = 100;
    private final int minProgressStep = 3;
    private final int maxProgressStep = 15;
    private int mPrevProgressStatus = 0;
    private int mProgressStatus = 0;
    private boolean isFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        HyberLogger.i("I'm alive!");

        AndroidUtilities.checkDisplaySize(this, getResources().getConfiguration());

        ButterKnife.bind(this);

        mProgress.setMax(maxProgress);

        new ProgressTask().execute();

        Hyber.isAuthorized(new CheckAuthorizationHandler() {
            @Override
            public void onSuccess() {
                isFinished = true;
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }

            @Override
            public void onFailure() {
                isFinished = true;
                startActivity(new Intent(SplashActivity.this, AuthActivity.class));
            }
        });
    }

    class ProgressTask extends AsyncTask<Integer, Integer, String> {
        @Override
        protected String doInBackground(Integer... params) {
            while (mProgressStatus < maxProgress) {
                mPrevProgressStatus = mProgressStatus;
                if (isFinished) {
                    mProgressStatus += maxProgressStep;
                } else {
                    mProgressStatus += minProgressStep;
                    try {
                        Thread.sleep(progressWaitMills);
                    } catch (InterruptedException e) {
                        HyberLogger.e(e);
                    }
                }
                publishProgress(mProgressStatus);
            }
            return "Task Completed.";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //txt.setText("Running..."+ values[0]);
            ObjectAnimator.ofInt(mProgress, "progress", mPrevProgressStatus, mProgressStatus);
            mProgress.setProgress(values[0]);
            if (mProgress.getProgress() >= maxProgress
                    && !isFinished) {
                mPrevProgressStatus = mProgressStatus;
                mProgressStatus -= maxProgressStep;
            }
        }
    }

}
