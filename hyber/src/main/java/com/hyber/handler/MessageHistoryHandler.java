package com.hyber.handler;

import android.support.annotation.NonNull;

public interface MessageHistoryHandler {

    void onSuccess(@NonNull Long recommendedNextTime);

    void onFailure(String message);

}
