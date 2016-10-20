package com.hyber;

import android.content.Context;

//import com.facebook.stetho.Stetho;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.HawkBuilder;
import com.orhanobut.hawk.LogLevel;
//import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

//import java.util.Arrays;
//import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public final class HyberDataSourceController {

//    private static final int REALM_PROVIDER_LIMIT = 1000;
    private static HyberDataSourceController singleton = null;

    private static int hyberRealmSchemaVersion = 1;
    private static String hyberRealmSchemaName = "Hyber.realm";
    private static RealmConfiguration hyberRealmConfig = null;

    private HyberDataSourceController(Context context) {
        hyberRealmConfig = new RealmConfiguration.Builder()
                .name(hyberRealmSchemaName)
                .schemaVersion(hyberRealmSchemaVersion)
                .deleteRealmIfMigrationNeeded()
//                .modules(Realm.getDefaultModule(), new HyberSchemaModule())
//                .migration(new HyberMigration())
                .build();

        Hawk.init(context)
                .setPassword(HyberFingerprint.keyHash(context))
                .setEncryptionMethod(HawkBuilder.EncryptionMethod.HIGHEST)
                .setStorage(HawkBuilder.newSharedPrefStorage(context))
                .setLogLevel(LogLevel.NONE)
                .build();

//        Stetho.initialize(
//                Stetho.newInitializerBuilder(context)
//                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
//                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(context).build())
//                        .build());
//
//        String key = "HyberDataSourceControllerKeyForRealmInspectorModulesProviderZZZZ";
//        RealmInspectorModulesProvider.builder(context)
//                .withFolder(context.getCacheDir())
//                .withEncryptionKey("encrypted.realm", key.getBytes())
//                .withMetaTables()
//                .withDescendingOrder()
//                .withLimit(REALM_PROVIDER_LIMIT)
//                .databaseNamePattern(Pattern.compile(".+\\.realm"))
//                .build();
    }

    /**
     * The global default {@link HyberDataSourceController} instance.
     * <p>
     * This instance is automatically initialized with defaults that are suitable to most
     * implementations.
     * <p>
     */
    static HyberDataSourceController with(Context context) {
        if (singleton == null) {
            synchronized (HyberDataSourceController.class) {
                if (singleton == null) {
                    singleton = new HyberDataSourceController(context);
                }
            }
        }
        return singleton;
    }

    static HyberDataSourceController getInstance() {
        checkInitialized();
        return singleton;
    }

    public Realm getRealmInstance() {
        return Realm.getInstance(hyberRealmConfig);
    }

    private static void checkInitialized() {
        if (singleton == null) {
            throw new IllegalStateException(
                    "HyberDataSourceController must be initialized by calling HyberDataSourceController.with(Context)"
                            + " prior to calling HyberDataSourceController methods");
        }
    }

}
