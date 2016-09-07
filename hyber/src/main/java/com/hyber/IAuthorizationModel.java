package com.hyber;

import android.support.annotation.NonNull;

interface IAuthorizationModel {

    void authorize(@NonNull Long phone, @NonNull MainApiBusinessModel.AuthorizationListener listener);

    void sendDeviceData(@NonNull MainApiBusinessModel.SendDeviceDataListener listener);

}
