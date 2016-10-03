package com.hyber;

import com.orhanobut.hawk.Hawk;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class HyberAuthInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response;
        Request original = chain.request();

        // Request customization: add request headers
        Request.Builder requestBuilder = original.newBuilder()
                .header(Tweakables.X_HYBER_SDK_VERSION, HyberSdkVersion.BUILD)
                .header(Tweakables.X_HYBER_CLIENT_API_KEY, Hyber.getClientApiKey())
                .header(Tweakables.X_HYBER_APP_FINGERPRINT, Hyber.getFingerprint())
                .header(Tweakables.X_HYBER_INSTALLATION_ID, Hyber.getInstallationID());

        String token = Hawk.get(Tweakables.HAWK_HYBER_AUTH_TOKEN, "");
        if (token != null && !token.isEmpty()) {
            requestBuilder.addHeader(Tweakables.X_HYBER_AUTH_TOKEN, token);
        }

        response = chain.proceed(requestBuilder.build());

        return response;
    }

}
