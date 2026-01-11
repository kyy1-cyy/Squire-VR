package com.squire.vr;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
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

                File mainApk = apks.get(0);
                Log.d(TAG, "Selected APK: " + mainApk.getAbsolutePath());

                String apkName = mainApk.getName();
                String folderName = apkName.replaceAll("(?i)\\.apk$", "").trim();

                String realPkgName = folderName; 
                try {
                    android.content.pm.PackageInfo info = context.getPackageManager().getPackageArchiveInfo(mainApk.getAbsolutePath(), 0);
                    if (info != null && info.packageName != null) {
                        realPkgName = info.packageName;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Failed to get real package name", e);
                }
                
                callback.onStatus("Auto-Sideload: Hunting for OBB...");
                File obbSource = null;
                File parentDir = mainApk.getParentFile();
                
                // 1. Direct match next to APK
                File candidate = new File(parentDir, folderName);
                if (candidate.exists() && candidate.isDirectory()) obbSource = candidate;
                

                // 2. Deep scan for folders
                if (obbSource == null) {
                    obbSource = findFolderRecursive(extractRoot, folderName, realPkgName);
                }

                // 3. NEW: Hunt for loose OBB files
                if (obbSource == null) {
                    File looseObb = findObbFileRecursive(extractRoot);
                    if (looseObb != null) {
                        File wrapper = new File(mainApk.getParentFile(), realPkgName);
                        if (!wrapper.exists()) wrapper.mkdirs();
                        if (looseObb.renameTo(new File(wrapper, looseObb.getName()))) {
                            obbSource = wrapper;
                            Log.d(TAG, "Wrapped loose OBB: " + looseObb.getName());
                        }
                    }
                }
                
                final String obbSourcePath = (obbSource != null) ? obbSource.getAbsolutePath() : null;
                callback.onStatus("Auto-Sideload: Installing APK...");
                installApkSession(context, mainApk, realPkgName, obbSourcePath, folderName, callback);
                
            } catch (Exception e) {
                Log.e(TAG, "AutoInstall failed", e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    private static File findFolderRecursive(File dir, String folderName, String pkgName) {
        if (dir == null || !dir.isDirectory()) return null;
        File[] children = dir.listFiles();
        if (children == null) return null;
        for (File f : children) {
            if (f.isDirectory()) {
                String name = f.getName().trim();
                if (name.equalsIgnoreCase(folderName) || (pkgName != null && name.equalsIgnoreCase(pkgName))) return f;
                File found = findFolderRecursive(f, folderName, pkgName);
                if (found != null) return found;
            }
        }
        return null;
    }

    private static File findObbFileRecursive(File dir) {
        if (dir == null || !dir.isDirectory()) return null;
        File[] children = dir.listFiles();
        if (children == null) return null;
        for (File f : children) {
            if (f.isDirectory()) {
                File found = findObbFileRecursive(f);
                if (found != null) return found;
            } else if (f.getName().toLowerCase().endsWith(".obb")) return f;
        }
        return null;
    }

    public static boolean moveFolder(File source, File dest) {
        if (source == null || !source.exists()) return false;
        if (source.isDirectory()) {
            if (!dest.exists() && !dest.mkdirs()) return false;
            File[] children = source.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (!moveFolder(child, new File(dest, child.getName()))) return false;
                }
            }
            return source.delete();
        } else {
            if (dest.exists()) dest.delete();
            if (source.renameTo(dest)) return true;
            try (InputStream in = new FileInputStream(source);
                 OutputStream out = new java.io.FileOutputStream(dest)) {
                byte[] buf = new byte[1024 * 64];
                int len;
                while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
                return source.delete();
            } catch (Exception e) { return false; }
        }
    }

    private static void findApks(File dir, List<File> apks) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) findApks(f, apks);
            else if (f.getName().toLowerCase().endsWith(".apk")) apks.add(f);
        }
    }

    private static void installApkSession(Context context, File apkFile, String pkgName, String obbSourcePath, String folderName, InstallCallback callback) throws Exception {
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

        Intent intent = new Intent(context, InstallReceiver.class);
        intent.setAction("com.squire.vr.INSTALL_COMPLETE");
        intent.putExtra("pkgName", pkgName);
        intent.putExtra("obbSourcePath", obbSourcePath);
        intent.putExtra("folderName", folderName);
        intent.putExtra("apkPath", apkFile.getAbsolutePath());
        
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            context, sessionId, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );

        callback.onStatus("Installing APK... Check Quest Prompt");
        session.commit(pendingIntent.getIntentSender());
    }
}
