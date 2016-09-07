package com.hyber;

import android.support.annotation.NonNull;

interface IAuthorizationModel {

    void authorize(@NonNull Long phone, @NonNull MainApiBusinessModel.AuthorizationListener listener);

    void refreshToken(@NonNull MainApiBusinessModel.RefreshTokenListener listener);

    void sendPushToken(@NonNull MainApiBusinessModel.SendPushTokenListener listener);

    void sendDeviceData(@NonNull MainApiBusinessModel.SendDeviceDataListener listener);

}
