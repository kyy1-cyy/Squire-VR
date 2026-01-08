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

                for (File apk : apks) {
                    String apkName = apk.getName();
                    if (!apkName.endsWith(".apk")) continue;
                    
                    String pkgName = apkName.substring(0, apkName.length() - 4);
                    
                    // 1. Session Installation (Background-ish)
                    callback.onStatus("Step 1/2: Installing APK...");
                    installApkSession(context, apk, pkgName, callback);

                    // 2. Move OBB Folder
                    File obbSource = findObbFolder(apk.getParentFile(), pkgName);
                    if (obbSource != null) {
                        callback.onStatus("Step 2/2: Moving OBB folder...");
                        File obbDest = new File("/sdcard/Android/obb/" + pkgName);
                        if (!obbDest.exists()) obbDest.mkdirs();
                        
                        if (!moveFolder(obbSource, obbDest)) {
                            Log.e(TAG, "Failed to move OBB folder: " + pkgName);
                        }
                    }
                }
                
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
                callback.onStatus("Streaming APK: " + progress + "%");
            }
            session.fsync(out);
        }

        // Trigger the system prompt
        Intent intent = new Intent(context, MainActivity.class); // MainActivity handles result
        intent.setAction("com.squire.vr.INSTALL_COMPLETE");
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, sessionId, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );

        callback.onStatus("Finalizing... Check Quest Prompt");
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
