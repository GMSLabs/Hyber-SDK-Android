package com.hyber;

import android.support.annotation.NonNull;

import java.util.Date;
import java.util.List;

interface IApiBusinessModel {

    void authorize(@NonNull String phone, @NonNull String password,
                   @NonNull ApiBusinessModel.AuthorizationListener listener);

    void sendDeviceData(@NonNull ApiBusinessModel.SendDeviceDataListener listener);

    void getAllDevices(@NonNull ApiBusinessModel.AllDevicesListener listener);

    void revokeDevices(@NonNull List<String> deviceIds, @NonNull ApiBusinessModel.RevokeDevicesListener listener);

    void getMessageHistory(@NonNull Long startDate,
                           @NonNull ApiBusinessModel.MessageHistoryListener listener);

    void sendMessageDeliveryReport(@NonNull String messageId, @NonNull Date receivedAt,
                                   @NonNull ApiBusinessModel.SendMessageDeliveryReportListener listener);

    void sendMessageCallback(@NonNull String messageId, @NonNull String answerText,
                             @NonNull ApiBusinessModel.SendMessageCallbackListener listener);

}
