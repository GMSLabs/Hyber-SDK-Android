package com.gms_worldwide.hybersdk;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Andrew Kochura.
 */
public class HyberInstanceIDListenerService extends InstanceIDListenerService {

    private static final String TAG = "com.gms_worldwide.hybersdk.HyberInstanceIDLS";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // If an exception happens while fetching the new token or updating our registration data
        // on a third-party server, this ensures that we'll attempt the update at a later time.
        //Hyber.getPreferencesManager().setGcmRegistrationToken("");

        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, HyberRegistrationIntentService.class);
        startService(intent);
    }
    // [END refresh_token]

}
