package com.hyber;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.hyber.log.HyberLogger;
import com.hyber.model.User;

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

        Repository repo = new Repository();
        repo.open();
        User user = repo.getCurrentUser();
        if (user != null && refreshedToken != null) {
            repo.updateFcmToken(user, refreshedToken);
        }
        repo.close();

        Hyber.updateDeviceData();
    }
    // [END refresh_token]

}
