package com.hyber;

import android.content.Context;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.regex.Pattern;

import io.realm.Realm;

public final class HyberDataSourceController {

    private static final int REALM_PROVIDER_LIMIT = 1000;
    private static HyberDataSourceController singleton = null;

    private HyberDataSourceController(Context context) {
        Realm.init(context);

        RealmInspectorModulesProvider rimProvider = RealmInspectorModulesProvider.builder(context)
                .withFolder(context.getFilesDir())
                .withMetaTables()
                .withDescendingOrder()
                .withLimit(REALM_PROVIDER_LIMIT)
                .databaseNamePattern(Pattern.compile(".+\\.realm"))
                .build();

        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                        .enableWebKitInspector(rimProvider)
                        .build());

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

    private static void checkInitialized() {
        if (singleton == null) {
            throw new IllegalStateException(
                    "HyberDataSourceController must be initialized by calling HyberDataSourceController.with(Context)"
                            + " prior to calling HyberDataSourceController methods");
        }
    }

}
