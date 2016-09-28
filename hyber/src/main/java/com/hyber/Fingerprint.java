package com.hyber;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

final class Fingerprint {

    private static final String TAG_FINGERPRINT = "FINGERPRINT";

    private static String sFingerprint = null;

    private Fingerprint() {

    }

    static synchronized String keyHash(Context context) {
        if (sFingerprint == null) {
            try {
                String packageName = context.getApplicationContext().getPackageName();
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName,
                        PackageManager.GET_SIGNATURES);
                for (Signature signature : packageInfo.signatures) {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    sFingerprint = Base64.encodeToString(md.digest(), Base64.DEFAULT)
                            .replace("\n", "");
                }
            } catch (PackageManager.NameNotFoundException e) {
                Hyber.mLog(Hyber.LogLevel.ERROR,
                        String.format(Locale.getDefault(), "Name not found.\n%s", e.toString()));
            } catch (NoSuchAlgorithmException e) {
                Hyber.mLog(Hyber.LogLevel.ERROR,
                        String.format(Locale.getDefault(), "No such an algorithm.\n%s", e.toString()));
            } catch (Exception e) {
                Hyber.mLog(Hyber.LogLevel.ERROR,
                        String.format(Locale.getDefault(), "Exception.\n%s", e.toString()));
            }
        }
        return sFingerprint;
    }
}
