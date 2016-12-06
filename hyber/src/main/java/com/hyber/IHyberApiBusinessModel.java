package com.hyber;

import android.support.annotation.NonNull;

interface IHyberApiBusinessModel {

    void authorize(@NonNull Long phone,
                   @NonNull ApiBusinessModel.AuthorizationListener listener);

    void sendDeviceData(@NonNull ApiBusinessModel.SendDeviceDataListener listener);

    void sendBidirectionalAnswer(@NonNull String messageId, @NonNull String answerText,
                                 @NonNull ApiBusinessModel.SendBidirectionalAnswerListener listener);

    void sendPushDeliveryReport(@NonNull String messageId, @NonNull Long receivedAt,
                                @NonNull ApiBusinessModel.SendPushDeliveryReportListener listener);

    void getMessageHistory(@NonNull Long startDate,
                           @NonNull ApiBusinessModel.MessageHistoryListener listener);

}
