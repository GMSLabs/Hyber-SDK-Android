package com.gms_worldwide.hybersdk;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by Andrew Kochura.
 */
public class HyberRegistrationIntentService extends IntentService {

    /**
     * The Action start if required.
     */
    static final String ACTION_START_IF_REQUIRED =
            "com.gms_worldwide.hybersdk.HyberRegistrationIntentService.startIfRequired";
    private static final String TAG = "com.gms_worldwide.hybersdk.HyberRegIntentService";
    private static final String[] TOPICS = {"global"};

    /**
     * Instantiates a new Registration intent service.
     */
    public HyberRegistrationIntentService() {
        super(TAG);
    }

    /**
     * Start service if required.
     *
     * @param context the context
     */
    static void startServiceIfRequired(Context context) {

        HyberServiceUtils.runIntentInService(
                context, new Intent(HyberRegistrationIntentService.ACTION_START_IF_REQUIRED), HyberRegistrationIntentService.class);

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {

            HyberCurrentUserDBModel currentUserDBModel =
                    HyberPlugins.get().getDatabaseHelper().getCurrentUser();

            if (currentUserDBModel == null)
                currentUserDBModel = new HyberCurrentUserDBModel();

            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(HyberPlugins.get().getProjectId(),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]

            HyberGcmTokenSender.sendRegistrationToServer(token);
            //HyberPlugins.get().setGcmToken(token);
            currentUserDBModel.setGcmTokenId(token);
            HyberPlugins.get().getDatabaseHelper().updateCurrentUser(currentUserDBModel);

            subscribeTopics(token);

            // [END register_for_gcm]
        } catch (Exception e) {
            e.printStackTrace();
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            //Hyber.getPreferencesManager().setGcmRegistrationToken("");
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent("registrationComplete");
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
    // [END subscribe_topics]

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }

}