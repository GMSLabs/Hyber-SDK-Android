package com.hyber;

import android.content.Context;

import com.hyber.log.HyberLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.UUID;

final class InstallationId {

    private static final String TAG = "InstallationId";

    private static String sID = null;

    private InstallationId() {

    }

    public static synchronized String id(Context context) {
        if (sID == null) {
            File installation = new File(context.getFilesDir(), TAG);
            try {
                if (!installation.exists())
                    writeInstallationFile(installation);
                sID = readInstallationFile(installation);
            } catch (Exception e) {
                HyberLogger.tag(TAG);
                HyberLogger.wtf(e);
            }
        }
        return sID;
    }

    private static String readInstallationFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeInstallationFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);
        String id = UUID.randomUUID().toString();
        out.write(id.getBytes());
        out.close();
    }
}