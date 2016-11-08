package com.hyber;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;

import com.hyber.log.HyberLogger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

final class AppFingerprint {

    private static final String TAG = "AppFingerprint";

    private static String sFingerprint = null;

    private AppFingerprint() {

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
            } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
                HyberLogger.tag(TAG);
                HyberLogger.e(e);
            } catch (Exception e) {
                HyberLogger.tag(TAG);
                HyberLogger.wtf(e);
            }
        }
        return sFingerprint;
    }
}
