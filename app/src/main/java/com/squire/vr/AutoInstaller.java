package com.squire.vr;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class AutoInstaller {
    private static final String TAG = "SquireInstaller";

    public interface InstallCallback {
        void onStatus(String msg);
        void onDone(boolean obbMoved, String obbPath);
        void onError(String msg);
    }

    public static void processExtraction(Context context, File extractRoot, InstallCallback callback) {
        new Thread(() -> {
            try {
                callback.onStatus("Scanning for games...");
                List<File> apks = new ArrayList<>();
                findApks(extractRoot, apks);

                if (apks.isEmpty()) {
                    callback.onError("No APK found in extracted folder.");
                    return;
                }

                // Just pick the discovered APK
                File mainApk = apks.get(0);
                Log.d(TAG, "Selected APK: " + mainApk.getAbsolutePath());

                String apkName = mainApk.getName();
                int lastDot = apkName.lastIndexOf('.');
                String folderName = (lastDot > 0) ? apkName.substring(0, lastDot) : apkName;
                
                // 1. Find OBB Source (STRICT SIBLING ONLY per user request)
                callback.onStatus("Auto-Sideload: Looking for OBB sibling...");
                Log.d(TAG, "Search folder: " + folderName + " in " + mainApk.getParent());

                File obbSource = null;
                File parentDir = mainApk.getParentFile();
                File[] siblings = parentDir.listFiles();
                if (siblings != null) {
                    for (File s : siblings) {
                        if (s.isDirectory() && s.getName().equalsIgnoreCase(folderName)) {
                            obbSource = s;
                            Log.d(TAG, "OBB Sibling found: " + s.getAbsolutePath());
                            break;
                        }
                    }
                }
                
                String obbSourcePath = (obbSource != null) ? obbSource.getAbsolutePath() : null;

                // 2. Start Session Installation
                callback.onStatus("Auto-Sideload: Installing APK...");
                installApkSession(context, mainApk, folderName, obbSourcePath, callback);
                
                callback.onDone(false, "Staged");
            } catch (Exception e) {
                Log.e(TAG, "AutoInstall failed", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    private static void findApks(File dir, List<File> apks) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) {
                findApks(f, apks);
            } else if (f.getName().toLowerCase().endsWith(".apk")) {
                apks.add(f);
            }
        }
    }

    private static void installApkSession(Context context, File apkFile, String pkgName, String obbSourcePath, InstallCallback callback) throws Exception {
        PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        params.setAppPackageName(pkgName);

        int sessionId = packageInstaller.createSession(params);
        PackageInstaller.Session session = packageInstaller.openSession(sessionId);

        try (InputStream in = new FileInputStream(apkFile);
             OutputStream out = session.openWrite("package", 0, apkFile.length())) {
            
            byte[] buffer = new byte[1024 * 1024]; 
            int n;
            long totalRead = 0;
            long fileSize = apkFile.length();
            
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
                totalRead += n;
                int progress = (int) ((totalRead * 100) / fileSize);
                callback.onStatus("Preparing APK: " + progress + "%");
            }
            session.fsync(out);
        }

        // Custom action for InstallReceiver
        Intent intent = new Intent(context, InstallReceiver.class);
        intent.setAction("com.squire.vr.INSTALL_COMPLETE");
        intent.putExtra("pkgName", pkgName);
        intent.putExtra("obbSourcePath", obbSourcePath);
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context, sessionId, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );

        callback.onStatus("Installing APK... Check Quest Prompt");
        session.commit(pendingIntent.getIntentSender());
    }

    private static boolean moveFolder(File source, File dest) {
        if (source.isDirectory()) {
            if (!dest.exists()) dest.mkdirs();
            File[] children = source.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (!moveFolder(child, new File(dest, child.getName()))) return false;
                }
            }
            return source.delete();
        } else {
            return source.renameTo(dest);
        }
    }
}
