package com.hyber.listener;

import android.support.annotation.NonNull;

public interface DeliveryReportListener {

    void onDeliveryReportSent(@NonNull String messageId);

    void onFailure();

}
