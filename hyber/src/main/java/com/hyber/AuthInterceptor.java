package com.hyber;

import com.hyber.model.User;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class AuthInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response;
        Request original = chain.request();

        // Request customization: add request headers
        Request.Builder requestBuilder = original.newBuilder()
                .header(Tweakables.X_HYBER_CLIENT_API_KEY, Hyber.getClientApiKey())
                .header(Tweakables.X_HYBER_APP_FINGERPRINT, Hyber.getFingerprint())
                .header(Tweakables.X_HYBER_INSTALLATION_ID, Hyber.getInstallationID());


        Repository repo = new Repository();
        repo.open();
        User user = repo.getCurrentUser();
        if (user != null) {
            requestBuilder.addHeader(Tweakables.X_HYBER_AUTH_TOKEN, user.getSession().getToken());
        }
        repo.close();

        response = chain.proceed(requestBuilder.build());

        return response;
    }

}
