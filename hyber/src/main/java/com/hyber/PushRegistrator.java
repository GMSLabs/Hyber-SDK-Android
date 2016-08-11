package com.hyber;

import android.content.Context;

public interface PushRegistrator {

    void registerForPush(Context context, RegisteredHandler callback);

    interface RegisteredHandler {
        void complete(String token);
    }

}
