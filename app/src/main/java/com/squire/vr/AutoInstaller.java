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
                String pkgName = apkName.endsWith(".apk") ? apkName.substring(0, apkName.length() - 4) : apkName;
                
                // 1. Move OBB Folder (Fully Recursive Search)
                callback.onStatus("Auto-Sideload: Searching for OBB Folder...");
                File obbSource = findObbFolderRecursive(extractRoot, pkgName);

                boolean obbMoved = false;
                String finalObbPath = "Not Found";

                if (obbSource != null && obbSource.exists()) {
                    callback.onStatus("Auto-Sideload: Moving OBB Folder...");
                    Log.d(TAG, "Found OBB Source: " + obbSource.getAbsolutePath());
                    File obbDestRoot = new File("/sdcard/Android/obb/");
                    if (!obbDestRoot.exists()) obbDestRoot.mkdirs();
                    File finalObbDest = new File(obbDestRoot, pkgName);
                    
                    if (moveFolder(obbSource, finalObbDest)) {
                        Log.d(TAG, "OBB move successful: " + pkgName);
                        obbMoved = true;
                        finalObbPath = finalObbDest.getAbsolutePath();
                    } else {
                        Log.e(TAG, "Failed to move OBB folder: " + pkgName);
                        finalObbPath = "Move Failed";
                    }
                } else {
                    Log.w(TAG, "No OBB folder found matching: " + pkgName);
                }

                // 2. Intent Installation
                callback.onStatus("Auto-Sideload: Opening APK Installer...");
                installApkIntent(context, mainApk);
                
                callback.onDone(obbMoved, finalObbPath);
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

    private static File findObbFolderRecursive(File dir, String pkgName) {
        if (dir == null || !dir.isDirectory()) return null;
        
        // Exact match check
        if (dir.getName().equalsIgnoreCase(pkgName)) return dir;

        File[] files = dir.listFiles();
        if (files == null) return null;
        
        for (File f : files) {
            if (f.isDirectory()) {
                // Check if this child is the target
                if (f.getName().equalsIgnoreCase(pkgName)) return f;
                // Otherwise recurse
                File found = findObbFolderRecursive(f, pkgName);
                if (found != null) return found;
            }
        }
        return null;
    }

    private static void installApkIntent(Context context, File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri apkUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            apkUri = androidx.core.content.FileProvider.getUriForFile(context, context.getPackageName() + ".provider", apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            apkUri = Uri.fromFile(apkFile);
        }
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
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
