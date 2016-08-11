package com.hyber;

import android.content.Context;

import com.facebook.stetho.Stetho;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.hawk.HawkBuilder;
import com.orhanobut.hawk.LogLevel;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.regex.Pattern;

import io.realm.Realm;
import io.realm.RealmConfiguration;

class HyberDataSourceController {

    private static HyberDataSourceController singleton = null;

    private HyberDataSourceController(Context context) {
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(context)
                .name(Realm.DEFAULT_REALM_NAME)
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);

        Hawk.init(context)
                .setPassword("kfhdhfeksjfkesglfkjeslkjfgsrljfnekjfh")
                .setEncryptionMethod(HawkBuilder.EncryptionMethod.HIGHEST)
                .setStorage(HawkBuilder.newSharedPrefStorage(context))
                .setLogLevel(LogLevel.FULL)
                .build();

        if (BuildConfig.DEBUG) {
            RealmInspectorModulesProvider realmInspectorModulesProvider =
                    RealmInspectorModulesProvider.builder(context)
                            .withFolder(context.getCacheDir())
                            .withEncryptionKey("encrypted.realm", "kjdfdgsfnvihdyeldhlugdkysgfdsfhyesgflakhdfdsgflkdsjhfdguehfseygf".getBytes())
                            .withMetaTables()
                            .withDescendingOrder()
                            .withLimit(1000)
                            .databaseNamePattern(Pattern.compile(".+\\.realm"))
                            .build();

            Stetho.initialize(
                    Stetho.newInitializerBuilder(context)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                            .enableWebKitInspector(realmInspectorModulesProvider)
                            .build());
        }

    }

    /**
     * The global default {@link HyberDataSourceController} instance.
     * <p>
     * This instance is automatically initialized with defaults that are suitable to most
     * implementations.
     * <p>
     */
    public static HyberDataSourceController with(Context context) {
        if (singleton == null) {
            synchronized (HyberDataSourceController.class) {
                if (singleton == null) {
                    singleton = new HyberDataSourceController(context);
                }
            }
        }
        return singleton;
    }

}
