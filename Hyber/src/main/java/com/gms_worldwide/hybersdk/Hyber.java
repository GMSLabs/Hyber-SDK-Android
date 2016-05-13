package com.gms_worldwide.hybersdk;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by Andrew Kochura.
 */
public class Hyber {

    private static final String TAG = "com.gms_worldwide.hybersdk.Hyber";

    private static final String GMS_Hyber_CLIENT_KEY = "com.gms_worldwide.hybersdk.CLIENT_KEY";
    private static final String GMS_Hyber_PROJECT_ID = "com.gms_worldwide.hybersdk.PROJECT_ID";

    private static Context mContext;

    private static HyberMessageHelper hyberMessageHelper;
    private static HyberUserHelper hyberUserHelper;
    private static HyberAlphaNamesHelper hyberAlphaNamesHelper;


    /**
     * Authenticates this client as belonging to your application.
     * <p/>
     * You must define {@code com.gms_worldwide.hybersdk.PROJECT_ID} and {@code com.gms_worldwide.hybersdk.CLIENT_KEY}
     * {@code meta-data} in your {@code AndroidManifest.xml}:
     * <pre>
     * &lt;manifest ...&gt;
     *
     * ...
     *
     *   &lt;application ...&gt;
     *     &lt;meta-data
     *       android:name="com.gms_worldwide.hybersdk.PROJECT_ID"
     *       android:value="@string/gms_hyber_project_id" /&gt;
     *     &lt;meta-data
     *       android:name="com.gms_worldwide.hybersdk.CLIENT_KEY"
     *       android:value="@string/gms_hyber_client_key" /&gt;
     *
     *       ...
     *
     *   &lt;/application&gt;
     * &lt;/manifest&gt;
     * </pre>
     * <p/>
     * This must be called before your application can use the Hyber library.
     * The recommended way is to put a call to {@code Hyber.initialize}
     * in your {@code Application}'s {@code onCreate} method:
     * <p/>
     * <pre>
     * public class MyApplication extends Application {
     *   public void onCreate() {
     *     Hyber.initialize(this);
     *   }
     * }
     * </pre>
     *
     * @param context The active {@link Context} for your application.
     */
    public static void initialize(Context context) {

        mContext = context;
        Context applicationContext = context.getApplicationContext();
        String projectId;
        String clientKey;
        Bundle metaData = HyberManifestInfo.getApplicationMetadata(applicationContext);
        if (metaData != null) {
            projectId = metaData.getString(GMS_Hyber_PROJECT_ID);
            clientKey = metaData.getString(GMS_Hyber_CLIENT_KEY);

            //HyberInfoLog.i(TAG, "projectId = " + projectId);
            //HyberInfoLog.i(TAG, "clientKey = " + clientKey);

            if (projectId == null) {
                throw new RuntimeException("ProjectId not defined. " +
                        "You must provide ProjectId in AndroidManifest.xml.\n" +
                        "<meta-data\n" +
                        "    android:name=\"" + GMS_Hyber_PROJECT_ID + "\"\n" +
                        "    android:value=\"<Your Project Id>\" />");
            }
            if (clientKey == null) {
                throw new RuntimeException("ClientKey not defined. " +
                        "You must provide ClientKey in AndroidManifest.xml.\n" +
                        "<meta-data\n" +
                        "    android:name=\"" + GMS_Hyber_CLIENT_KEY + "\"\n" +
                        "    android:value=\"<Your Client Key>\" />");
            }
        } else {
            throw new RuntimeException("Can't get Application Metadata");
        }
        initialize(context, projectId, clientKey);
    }

    /**
     * Authenticates this client as belonging to your application.
     * <p/>
     * This method is only required if you intend to use a different {@code projectId} or
     * {@code clientKey} than is defined by {@code com.gms_worldwide.hybersdk.APPLICATION_ID} or
     * {@code com.gms_worldwide.hybersdk.CLIENT_KEY} in your {@code AndroidManifest.xml}.
     * <p/>
     * This must be called before your
     * application can use the Hyber library. The recommended way is to put a call to
     * {@code Hyber.initialize} in your {@code Application}'s {@code onCreate} method:
     * <p/>
     * <pre>
     * public class MyApplication extends Application {
     *   public void onCreate() {
     *     Hyber.initialize(this, &quot;your application id&quot;, &quot;your client key&quot;);
     *   }
     * }
     * </pre>
     *
     * @param context   The active {@link Context} for your application.
     * @param projectId The project id provided in the Google developer console.
     * @param clientKey The client key provided in the Hyber dashboard.
     */
    private static void initialize(Context context, String projectId, String clientKey) {
        HyberPlugins.Android.initialize(context, projectId, clientKey);

        hyberMessageHelper = HyberMessageHelper.getInstance();
        hyberUserHelper = HyberUserHelper.getInstance();
        hyberAlphaNamesHelper = HyberAlphaNamesHelper.getInstance();

        HyberRegistrationIntentService.startServiceIfRequired(context);

    }

    /**
     * Gets application context.
     *
     * @return the application context
     */
    static Context getApplicationContext() {
        checkContext();
        return HyberPlugins.Android.get().applicationContext();
    }

    /**
     * Check context.
     */
    static void checkContext() {
        if (HyberPlugins.Android.get().applicationContext() == null) {
            throw new RuntimeException("applicationContext is null. "
                    + "You must call Hyber.initialize(Context)"
                    + " before using the Hyber library.");
        }
    }

    /**
     * Message helper is tool for controlling all messages in SDK.
     *
     * @return helper instance
     */
    public static HyberMessageHelper getMessageHelper() {
        return hyberMessageHelper;
    }

    /**
     * User helper is tool for controlling the user data.
     *
     * @return helper instance
     */
    public static HyberUserHelper getUserHelper() {
        return hyberUserHelper;
    }

    /**
     * AlphaNames helper is tool for controlling alpha names in SDK.
     *
     * @return helper instance
     */
    public static HyberAlphaNamesHelper getAlphaNamesHelper() {
        return hyberAlphaNamesHelper;
    }

    /**
     * Tool for get your client key from manifest meta data.
     *
     * @return string with client key
     */
    public static String getClientKey() {
        return HyberPlugins.get().getClientKey();
    }

    /**
     * Tool for show in logcat application fingerprint certificate.
     */
    public static void logFingerprint() {
        HyberTools.printKeyHash(mContext);
    }

}
