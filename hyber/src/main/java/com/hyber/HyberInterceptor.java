package com.hyber;

import com.orhanobut.hawk.Hawk;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HyberInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        // Request customization: add request headers
        Request.Builder requestBuilder = original.newBuilder()
                .header(Tweakables.X_Hyber_Client_API_Key, Hyber.clientApiKey)
                .header(Tweakables.X_Hyber_Application_Key, Hyber.applicationKey)
                .header(Tweakables.X_Hyber_App_Fingerprint, Hyber.fingerprint)
                .header(Tweakables.X_Hyber_Installation_Id, Hyber.installationID);

        String token = Hawk.get(Tweakables.HAWK_HyberAuthToken, "");
        if (token == null || token.isEmpty()) {
            return chain.proceed(requestBuilder.build());
        } else {
            requestBuilder.addHeader(Tweakables.X_Hyber_Auth_Token, token);
        }

        return chain.proceed(requestBuilder.build());
    }

}
