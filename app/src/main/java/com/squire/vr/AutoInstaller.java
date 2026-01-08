package com.squire.vr;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.core.content.FileProvider;
import java.io.File;
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
                    // 1. Check for OBB folder
                    String apkName = apk.getName();
                    if (apkName.endsWith(".apk")) {
                        String pkgName = apkName.substring(0, apkName.length() - 4);
                        File obbSource = new File(apk.getParentFile(), pkgName);
                        
                        if (obbSource.exists() && obbSource.isDirectory()) {
                            callback.onStatus("Moving OBB: " + pkgName);
                            File obbDest = new File("/sdcard/Android/obb/" + pkgName);
                            if (!obbDest.exists()) obbDest.mkdirs();
                            
                            if (!moveFolder(obbSource, obbDest)) {
                                Log.e(TAG, "Failed to move OBB folder: " + pkgName);
                            }
                        }
                    }

                    // 2. Trigger Installer
                    callback.onStatus("Opening Installer: " + apk.getName());
                    installApk(context, apk);
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

    private static void installApk(Context context, File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri apkUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            apkUri = Uri.fromFile(apkFile);
        }
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
