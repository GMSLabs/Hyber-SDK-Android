package com.gms_worldwide.starter;

import android.app.Application;
import android.util.Log;

import com.gms_worldwide.hybersdk.Hyber;
import com.gms_worldwide.hybersdk.HyberUserProfileModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Locale;

import rx.Subscriber;

/**
 * Created by Andrew Kochura.
 */
public class AppController extends Application {

    private static final String TAG = AppController.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        Hyber.initialize(this);

        ArrayList<String> alphas = new ArrayList<>();
        alphas.add("TEST ALPHAS");

        Hyber.getAlphaNamesHelper().addAlphaName(alphas);

        Hyber.getMessageHelper().getNewMessageNotifier()
                .subscribe(
                        message -> {
                            Log.i(TAG, String.format(Locale.getDefault(), "%s --> %s: %s",
                                    message.getTypeDesc(), message.getFrom(), message.getMessage()));
                        },
                        Throwable::printStackTrace);

        Hyber.getUserHelper().loginUser(380998887766L, "testGmsW@gms-worldwide.com")
                .subscribe(new Subscriber<HyberUserProfileModel>() {
                    @Override
                    public void onCompleted() {
                        //close login process dialog
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(HyberUserProfileModel hyberUserProfileModel) {
                        Log.i(TAG, String.format("%s\n%s",
                                "User login success -->",
                                new Gson().toJson(hyberUserProfileModel)));
                    }
                });
    }
}
