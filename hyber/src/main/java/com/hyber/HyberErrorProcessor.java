package com.hyber;

import android.support.annotation.NonNull;

import rx.Observable;
import rx.Subscriber;

final class HyberErrorProcessor {

    private static HyberErrorProcessor instance;

    private Subscriber<? super HyberError> mHyberErrorSubscriber;
    private Observable<HyberError> mHyberErrorObservable;

    private HyberErrorProcessor() {
        mHyberErrorObservable =
                Observable.create(new Observable.OnSubscribe<HyberError>() {
                    @Override
                    public void call(Subscriber<? super HyberError> subscriber) {
                        mHyberErrorSubscriber = subscriber;
                    }
                });
    }

    public static HyberErrorProcessor getInstance() {
        if (instance == null)
            instance = new HyberErrorProcessor();
        return instance;
    }

    @NonNull
    Observable<HyberError> getHyberErrorObservable() {
        return mHyberErrorObservable;
    }

    void onError(@NonNull HyberError error) {
        mHyberErrorSubscriber.onNext(error);
    }

}
