package com.gms_worldwide.hybersdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Andrew Kochura.
 */
class HyberTools {

    private static final String TAG = "com.gms_worldwide.hybersdk;HyberTools";

    /**
     * Obj to string string.
     *
     * @param o the o
     * @return the string
     */
    public static String objToString(Object o) {
        return new Gson().toJson(o);
    }

    /**
     * Gets sd kversion.
     *
     * @return the sd kversion
     */
    public static String getSDKversion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * Print key hash.
     *
     * @param context the context
     */
    @SuppressLint("LongLogTag")
    public static void printKeyHash(Context context) {
        String[] fingerprints = getCertificateFingerprint(context);
        try {
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i(TAG, "KeyHash --> " +
                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
                assert fingerprints != null;
                for (String fingerprint : fingerprints)
                    Log.i(TAG, "Fingerprint --> " + fingerprint);
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get current certificate fingerprint
     *
     * @param ctx context of application
     * @return Base64 packed SHA fingerprint of your packet certificate
     */
    private static String[] getCertificateFingerprint(Context ctx) {
        try {
            if (ctx == null || ctx.getPackageManager() == null)
                return null;
            PackageInfo info = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            assert info.signatures != null;
            String[] result = new String[info.signatures.length];
            int i = 0;
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
//                result[i++] = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                result[i++] = toHex(md.digest());
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

    /**
     * Gets utc time.
     *
     * @return the utc time
     */
    public static long getUtcTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        return calendar.getTimeInMillis();
    }

    /**
     * Gets current time.
     *
     * @return the current time
     */
    public static long getCurrentTime() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        return calendar.getTimeInMillis();
    }

    /**
     * Gets start of day utc time.
     *
     * @param year  the year
     * @param month the month
     * @param day   the day
     * @return the start of day utc time
     */
    public static long getStartOfDayUtcTime(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.set(year, month, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * Gets start of day utc time.
     *
     * @param millsUTC the time un UTC format
     * @return the start of day utc time
     */
    public static long getStartOfDayUtcTime(long millsUTC) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(millsUTC);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * Gets end of day utc time.
     *
     * @param year  the year
     * @param month the month
     * @param day   the day
     * @return the end of day utc time
     */
    public static long getEndOfDayUtcTime(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.set(year, month, day);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    /**
     * Gets end of day utc time.
     *
     * @param millsUTC the time un UTC format
     * @return the end of day utc time
     */
    public static long getEndOfDayUtcTime(long millsUTC) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(millsUTC);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    /**
     * Conver string to integer array int [ ].
     *
     * @param s the s
     * @return the int [ ]
     */
    public static int[] converStringToIntegerArray(String s) {
        int[] integerArray = new int[0];
        if (!TextUtils.isEmpty(s) && !s.equals("[]")) {
            String[] stringArray = s
                    .replaceAll("\\[", "")
                    .replaceAll("\\]", "")
                    .split(",");

            integerArray = new int[stringArray.length];
            if (integerArray.length > 0)
                for (int i = 0; i < stringArray.length; i++) {
                    try {
                        integerArray[i] = Integer.parseInt(stringArray[i]);
                    } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                    }
                }
        }
        return integerArray;
    }

    /**
     * Integer array to string string.
     *
     * @param ints the ints
     * @return the string
     */
    public static String integerArrayToString(int[] ints) {
        if (ints == null || ints.length == 0)
            return "";

        return new Gson().toJson(ints);
    }



    public static boolean isEmailValid(CharSequence target) {
        if (target == null || target.toString().length() <= 0)
            return false;

        Pattern pattern = Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"
        );
        Matcher matcher = pattern.matcher(target);

        return matcher.matches();
    }

    public static boolean isPhoneValid(long mPhone) {
        String target = String.valueOf(mPhone);

        if (mPhone < 100000000000L || mPhone >= 10000000000000L)
            return false;

        Pattern pattern = Pattern.compile(       // sdd = space, dot, or dash
                "(\\+[0-9]+[\\- \\.]*)?" +      // +<digits><sdd>
                        "(\\([0-9]+\\)[\\- \\.]*)?" +   // (<digits>)<sdd>*
                        "([0-9][0-9\\- \\.]+[0-9])");   // <digit><digit|sdd>+<digit>;
        Matcher matcher = pattern.matcher(target);

        return matcher.matches() &&
                (target.startsWith("380") || target.startsWith("+380"));
    }
}
