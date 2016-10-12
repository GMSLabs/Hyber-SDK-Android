package com.hyber;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class HyberFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "HyberFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        HyberLogger.tag(TAG).i("Refreshed FCM token: %s", refreshedToken);

        sendRegistrationToServer();
    }
    // [END refresh_token]

    private void sendRegistrationToServer() {
        HyberApiBusinessModel.getInstance(this)
                .sendDeviceData(new HyberApiBusinessModel.SendDeviceDataListener() {
                    @Override
                    public void onSuccess() {
                        HyberLogger.i("Refreshed FCM token sent to Hyber.");
                    }

                    @Override
                    public void onFailure() {
                        HyberLogger.w("Refreshed FCM token can not sent to Hyber.");
                    }
                });
    }

}
