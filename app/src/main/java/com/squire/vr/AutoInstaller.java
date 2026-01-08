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
        void onDone();
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

                // Pick the largest APK (likely the main game)
                File mainApk = apks.get(0);
                for (File f : apks) {
                    if (f.length() > mainApk.length()) mainApk = f;
                }
                
                Log.d(TAG, "Selected Main APK: " + mainApk.getAbsolutePath() + " (" + mainApk.length() + " bytes)");

                String apkName = mainApk.getName();
                String pkgName = apkName.endsWith(".apk") ? apkName.substring(0, apkName.length() - 4) : apkName;
                
                // 1. Move OBB Folder (Search same dir or parent)
                callback.onStatus("Step 1/2: Preparing OBB folder...");
                File obbSource = findObbFolder(mainApk.getParentFile(), pkgName);
                if (obbSource == null) {
                    obbSource = findObbFolder(extractRoot, pkgName);
                }

                if (obbSource != null && obbSource.exists()) {
                    Log.d(TAG, "Found OBB Source: " + obbSource.getAbsolutePath());
                    File obbDestRoot = new File("/sdcard/Android/obb/");
                    if (!obbDestRoot.exists()) obbDestRoot.mkdirs();
                    File finalObbDest = new File(obbDestRoot, pkgName);
                    
                    if (!moveFolder(obbSource, finalObbDest)) {
                        Log.e(TAG, "Failed to move OBB folder: " + pkgName);
                    } else {
                        Log.d(TAG, "OBB move successful: " + pkgName);
                    }
                } else {
                    Log.w(TAG, "No OBB folder found matching: " + pkgName);
                }

                // 2. Session Installation (Background-ish)
                callback.onStatus("Step 2/2: Installing APK...");
                installApkSession(context, mainApk, pkgName, callback);
                
                callback.onDone();

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

    private static File findObbFolder(File dir, String pkgName) {
        File folder = new File(dir, pkgName);
        if (folder.exists() && folder.isDirectory()) return folder;
        
        // Search one level up/down just in case of weird extraction nesting
        File parent = dir.getParentFile();
        if (parent != null) {
            folder = new File(parent, pkgName);
            if (folder.exists() && folder.isDirectory()) return folder;
        }
        return null;
    }

    private static void installApkSession(Context context, File apkFile, String pkgName, InstallCallback callback) throws Exception {
        Log.d(TAG, "Starting Session Install for: " + apkFile.getAbsolutePath() + " (Size: " + apkFile.length() + ")");
        if (apkFile.length() <= 0) {
            throw new Exception("APK file is empty: " + apkFile.getName());
        }

        PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        params.setAppPackageName(pkgName);

        int sessionId = packageInstaller.createSession(params);
        PackageInstaller.Session session = packageInstaller.openSession(sessionId);

        try (InputStream in = new FileInputStream(apkFile);
             OutputStream out = session.openWrite("package", 0, apkFile.length())) {
            
            byte[] buffer = new byte[1024 * 1024]; // 1MB buffer
            int n;
            long totalRead = 0;
            long fileSize = apkFile.length();
            
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
                totalRead += n;
                int progress = (int) ((totalRead * 100) / fileSize);
                if (progress % 10 == 0) { // Log every 10%
                    Log.d(TAG, "Streaming: " + progress + "% (" + totalRead + " bytes)");
                }
                callback.onStatus("Streaming APK: " + progress + "%");
            }
            session.fsync(out);
            Log.d(TAG, "Streaming complete. Total written: " + totalRead);
        }

        // Use Broadcast instead of Activity to avoid restart
        Intent intent = new Intent(context, InstallReceiver.class); 
        intent.setAction("com.squire.vr.INSTALL_COMPLETE");
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context, sessionId, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );

        callback.onStatus("Finalizing... Check Quest Prompt");
        Log.d(TAG, "Committing session: " + sessionId);
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
