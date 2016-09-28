package com.hyber;

import android.support.annotation.NonNull;

interface IMainApiBusinessModel {

    void authorize(@NonNull Long phone,
                   @NonNull MainApiBusinessModel.AuthorizationListener listener);

    void sendDeviceData(@NonNull MainApiBusinessModel.SendDeviceDataListener listener);

    void sendBidirectionalAnswer(@NonNull String messageId, @NonNull String answerText,
                                 @NonNull MainApiBusinessModel.SendBidirectionalAnswerListener listener);

    void sendPushDeliveryReport(@NonNull String messageId, @NonNull Long receivedAt,
                                @NonNull MainApiBusinessModel.SendPushDeliveryReportListener listener);

    void getMessageHistory(@NonNull Long startDate,
                           @NonNull MainApiBusinessModel.MessageHistoryListener listener);

}
