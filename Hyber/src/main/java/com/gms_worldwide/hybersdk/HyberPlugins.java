package com.gms_worldwide.hybersdk;

import android.content.Context;
import android.net.SSLSessionCache;

/**
 * Created by Andrew Kochura.
 */
class HyberPlugins {

    private static final Object LOCK = new Object();
    private static HyberPlugins instance;
    /**
     * The Lock.
     */
    final Object lock = new Object();
    private final String projectId;
    private final String clientKey;
    private HyberApiClient restClient;
    private HyberDatabaseHelper databaseHelper;
    private String gcmID = "";
    private HyberPlugins(String projectId, String clientKey) {
        this.projectId = projectId;
        this.clientKey = clientKey;
    }

    /**
     * Initialize.
     *
     * @param projectId the project id
     * @param clientKey the client key
     */
// TODO(grantland): Move towards a Config/Builder parameter pattern to allow other configurations
    // such as path (disabled for Android), etc.
    static void initialize(String projectId, String clientKey) {
        HyberPlugins.set(new HyberPlugins(projectId, clientKey));
    }

    /**
     * Set.
     *
     * @param plugins the plugins
     */
    static void set(HyberPlugins plugins) {
        synchronized (LOCK) {
            if (instance != null) {
                throw new IllegalStateException("HyberPlugins is already initialized");
            }
            instance = plugins;
        }
    }

    /**
     * Get Hyber plugins.
     *
     * @return the Hyber plugins
     */
    static HyberPlugins get() {
        synchronized (LOCK) {
            return instance;
        }
    }

    /**
     * Reset.
     */
    static void reset() {
        synchronized (LOCK) {
            instance = null;
        }
    }

    /**
     * Gets project id.
     *
     * @return the project id
     */
    String getProjectId() {
        return projectId;
    }

    /**
     * Gets client key.
     *
     * @return the client key
     */
    String getClientKey() {
        return clientKey;
    }

    /**
     * New rest api client Hyber api client.
     *
     * @return the Hyber api client
     */
    HyberApiClient newRestApiClient() {
        return new HyberApiClient();
    }

    /**
     * New database helper Hyber database helper.
     *
     * @return the Hyber database helper
     */
    HyberDatabaseHelper newDatabaseHelper() {
        return new HyberDatabaseHelper(Hyber.getApplicationContext());
    }

    /**
     * Rest client Hyber api client.
     *
     * @return the Hyber api client
     */
    HyberApiClient restClient() {
        synchronized (lock) {
            if (restClient == null) {
                restClient = newRestApiClient();
            }
            return restClient;
        }
    }

    /**
     * Gets database helper.
     *
     * @return the database helper
     */
    HyberDatabaseHelper getDatabaseHelper() {
        synchronized (lock) {
            if (databaseHelper == null) {
                databaseHelper = newDatabaseHelper();
            }
            return databaseHelper;
        }
    }

    /**
     * Sets gcm id.
     *
     * @param gcm_id the gcm id
     */
    void setGcmID(String gcm_id) {
        synchronized (lock) {
            gcmID = gcm_id;
        }
    }

    /**
     * Gets last kown gcm id.
     *
     * @return the last kown gcm id
     */
    String getLastKownGcmID() {
        synchronized (lock) {
            return gcmID;
        }
    }

    /**
     * The type Android.
     */
    static class Android extends HyberPlugins {
        private final Context applicationContext;

        private Android(Context context, String projectId, String clientKey) {
            super(projectId, clientKey);
            applicationContext = context.getApplicationContext();
        }

        /**
         * Initialize.
         *
         * @param context   the context
         * @param projectId the project id
         * @param clientKey the client key
         */
        static void initialize(Context context, String projectId, String clientKey) {
            HyberPlugins.set(new Android(context, projectId, clientKey));
        }

        /**
         * Get Hyber plugins . android.
         *
         * @return the Hyber plugins . android
         */
        static HyberPlugins.Android get() {
            return (HyberPlugins.Android) HyberPlugins.get();
        }

        /**
         * Application context context.
         *
         * @return the context
         */
        Context applicationContext() {
            return applicationContext;
        }

        @Override
        public HyberApiClient newRestApiClient() {
            SSLSessionCache sslSessionCache = new SSLSessionCache(applicationContext);
            return new HyberApiClient();
        }

    }
}