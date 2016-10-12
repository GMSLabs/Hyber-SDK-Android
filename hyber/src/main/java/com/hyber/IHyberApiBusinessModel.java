package com.hyber;

import android.support.annotation.NonNull;

interface IHyberApiBusinessModel {

    void authorize(@NonNull Long phone,
                   @NonNull HyberApiBusinessModel.AuthorizationListener listener);

    void sendDeviceData(@NonNull HyberApiBusinessModel.SendDeviceDataListener listener);

    void sendBidirectionalAnswer(@NonNull String messageId, @NonNull String answerText,
                                 @NonNull HyberApiBusinessModel.SendBidirectionalAnswerListener listener);

    void sendPushDeliveryReport(@NonNull String messageId, @NonNull Long receivedAt,
                                @NonNull HyberApiBusinessModel.SendPushDeliveryReportListener listener);

    void getMessageHistory(@NonNull Long startDate,
                           @NonNull HyberApiBusinessModel.MessageHistoryListener listener);

}
