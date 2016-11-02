package com.hyber.example;

import com.hyber.HyberLogger;

public final class LocaleController {

    private static volatile LocaleController mInstance = null;

    private LocaleController() {
    }

    public static LocaleController getInstance() {
        LocaleController localInstance = mInstance;
        if (localInstance == null) {
            synchronized (LocaleController.class) {
                localInstance = mInstance;
                if (localInstance == null) {
                    localInstance = new LocaleController();
                    mInstance = localInstance;
                }
            }
        }
        return localInstance;
    }

    public static String getString(String key, int res) {
        return getInstance().getStringInternal(key, res);
    }

    private String getStringInternal(String key, int res) {
        String value = null;
        try {
            value = ApplicationLoader.applicationContext.getString(res);
        } catch (Exception e) {
            HyberLogger.e("hyber-messenger", e);
        }
        if (value == null) {
            value = "LOC_ERR:" + key;
        }
        return value;
    }

}
