package com.hyber.listener;

import android.support.annotation.NonNull;

import com.hyber.HyberError;

public interface HyberErrorListener {

    void onError(@NonNull HyberError error);

}
