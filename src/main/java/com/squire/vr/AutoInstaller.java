package com.squire.vr;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import okhttp3.HttpUrl;

/* loaded from: classes3.dex */
public class AutoInstaller {
    private static final String TAG = "SquireInstaller";

    public interface InstallCallback {
        void onDone(boolean z, String str);

        void onError(String str);

        void onStatus(String str);
    }

    public static void processExtraction(final Context context, final File extractRoot, final InstallCallback callback) {
        new Thread(new Runnable() { // from class: com.squire.vr.AutoInstaller$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AutoInstaller.lambda$processExtraction$0(callback, extractRoot, context);
            }
        }).start();
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x00de  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    static /* synthetic */ void lambda$processExtraction$0(InstallCallback callback, File extractRoot, Context context) {
        String realPkgName;
        File obbSource = null;
        File looseObb;
        try {
            callback.onStatus("Scanning for APK...");
            Log.d(TAG, "Scanning for APKs in: " + extractRoot.getAbsolutePath());
            List<File> apks = new ArrayList<>();
            findApks(extractRoot, apks);
            
            if (apks.isEmpty()) {
                Log.e(TAG, "No APK found. Listing root files:");
                File[] rootFiles = extractRoot.listFiles();
                if (rootFiles != null) {
                    for (File f : rootFiles) Log.e(TAG, " - " + f.getName() + (f.isDirectory() ? "/" : ""));
                }
                callback.onError("No APK found in extracted folder: " + extractRoot.getName());
                return;
            }
            
            // Take the first APK found
            File mainApk = apks.get(0);
            Log.d(TAG, "Found APK: " + mainApk.getAbsolutePath());
            
            // Try to parse Package Info
            String apkName = mainApk.getName();
            String realPkgName2 = apkName.replaceAll("(?i)\\.apk$", "").trim();
            try {
                PackageInfo info = context.getPackageManager().getPackageArchiveInfo(mainApk.getAbsolutePath(), 0);
                if (info != null && info.packageName != null) {
                    realPkgName2 = info.packageName;
                }
                realPkgName = realPkgName2;
            } catch (Exception e) {
                Log.e(TAG, "Failed to get real package name", e);
                realPkgName = realPkgName2;
            }
            Log.d(TAG, "Target Package Name: " + realPkgName);

            callback.onStatus("Preparing OBB...");
            File obbSource2 = null;
            File parentDir = mainApk.getParentFile();
            
            // Strategy 1: Look for folder named "com.package.name" in parent
            File candidate = new File(parentDir, realPkgName);
            if (candidate.exists() && candidate.isDirectory()) {
                obbSource2 = candidate;
                Log.d(TAG, "Found OBB folder (Strategy 1): " + candidate.getAbsolutePath());
            }
            
            // Strategy 2: Recursive search for "com.package.name"
            if (obbSource2 == null) {
                obbSource2 = findFolderRecursive(extractRoot, realPkgName, realPkgName);
                if (obbSource2 != null) Log.d(TAG, "Found OBB folder (Strategy 2): " + obbSource2.getAbsolutePath());
            }
            
            // Strategy 3: Loose OBB file
            if (obbSource2 == null && (looseObb = findObbFileRecursive(extractRoot)) != null) {
                File wrapper = new File(mainApk.getParentFile(), realPkgName);
                if (!wrapper.exists()) {
                    wrapper.mkdirs();
                }
                if (looseObb.renameTo(new File(wrapper, looseObb.getName()))) {
                    Log.d(TAG, "Wrapped loose OBB: " + looseObb.getName());
                    obbSource = wrapper;
                }
            } else {
                obbSource = obbSource2;
            }
            
            // Strategy 4: Fallback - Find ANY folder containing .obb files if we haven't found one yet
            if (obbSource == null) {
                 File anyObbFolder = findFolderWithObb(extractRoot);
                 if (anyObbFolder != null) {
                     Log.d(TAG, "Fallback: Found generic OBB folder: " + anyObbFolder.getAbsolutePath());
                     File fixedObbFolder = new File(anyObbFolder.getParentFile(), realPkgName);
                     if (anyObbFolder.renameTo(fixedObbFolder)) {
                         obbSource = fixedObbFolder;
                         Log.d(TAG, "Renamed fallback folder to: " + fixedObbFolder.getAbsolutePath());
                     } else {
                         obbSource = anyObbFolder;
                     }
                 }
            }

            String obbSourcePath = obbSource != null ? obbSource.getAbsolutePath() : null;
            
            // Use PackageInstaller Session instead of ACTION_VIEW for reliable install tracking
            callback.onStatus("Launching System Installer...");
            // installApkSession(context, mainApk, realPkgName, obbSourcePath, extractRoot.getName(), callback);

            // FALLBACK: Use standard intent for now to ensure prompt shows up
             Intent installIntent = new Intent(Intent.ACTION_VIEW);
             installIntent.setDataAndType(
                 androidx.core.content.FileProvider.getUriForFile(context, context.getPackageName() + ".provider", mainApk),
                 "application/vnd.android.package-archive"
             );
             installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
             
             final String finalPkgName = realPkgName;
             final String finalObbPath = obbSourcePath;
             
             context.startActivity(installIntent);
             
             new Thread(() -> {
                 try {
                     try { Thread.sleep(2500); } catch (InterruptedException e) {}
                     callback.onStatus("Installing... (Please confirm on device)");
 
                     boolean installed = false;
                     // Watch for the package to appear
                     // Increased timeout to 5 minutes (300 seconds) to give user time to click Install
                     for (int i = 0; i < 300; i++) {
                         try {
                             PackageInfo pi = context.getPackageManager().getPackageInfo(finalPkgName, 0);
                             // If it's an update, we must check if the version code changed or if the install time changed?
                             // Actually, simply checking if package exists is NOT enough for updates, 
                             // because it already existed before install started.
                             
                             // However, since we are using ACTION_VIEW intent, we don't get a callback when it's done.
                             // We are just polling.
                             
                             // If it was already installed, 'installed' becomes true immediately in the first loop iteration.
                             // This explains why updates say "Completed" instantly.
                             
                             // FIX: For updates, we need to wait for the user to actually go through the flow.
                             // But we can't easily know when they finished if we don't know the new version code ahead of time
                             // or if we don't have a reliable callback.
                             
                             // Workaround: We can't rely on polling for updates if we don't know the target version.
                             // But wait! We DO know the target version from the APK file!
                             
                             if (pi != null) {
                                 // Get APK version
                                 PackageInfo apkInfo = context.getPackageManager().getPackageArchiveInfo(mainApk.getAbsolutePath(), 0);
                                 if (apkInfo != null) {
                                     // If installed version matches APK version, we are good!
                                     if (pi.versionCode == apkInfo.versionCode) {
                                         installed = true;
                                         break;
                                     }
                                 } else {
                                     // If we can't read APK info (rare), fallback to just assuming it worked if pkg exists?
                                     // But that causes the instant complete bug.
                                     // Let's just wait a bit? No, that's bad UX.
                                     installed = true; 
                                     break; 
                                 }
                             }
                         } catch (Exception e) {}
                         Thread.sleep(1000);
                     }
                     
                     if (installed) {
                         if (finalObbPath != null) {
                             callback.onStatus("Moving OBB Folder...");
                             File source = new File(finalObbPath);
                             File dest = new File("/sdcard/Android/obb/" + finalPkgName);
                             if (moveFolder(source, dest)) {
                                 callback.onStatus("Game Installation Complete");
                                 try { Thread.sleep(1000); } catch (InterruptedException e) {}
                                 callback.onDone(true, dest.getAbsolutePath());
                             } else {
                                 callback.onError("Failed to move OBB folder");
                             }
                         } else {
                             callback.onStatus("Game Installation Complete");
                             try { Thread.sleep(1000); } catch (InterruptedException e) {}
                             callback.onDone(false, null);
                         }
                     } else {
                         callback.onError("Installation timed out or cancelled");
                     }
                 } catch (Exception e) {
                     callback.onError("Error during post-install: " + e.getMessage());
                 }
             }).start();
            
        } catch (Exception e2) {
            Log.e(TAG, "AutoInstall failed", e2);
            callback.onError(e2.getMessage());
        }
    }

    private static File findFolderWithObb(File dir) {
        if (dir == null || !dir.isDirectory()) return null;
        File[] files = dir.listFiles();
        if (files == null) return null;
        
        for (File f : files) {
            if (f.isDirectory()) {
                File found = findFolderWithObb(f);
                if (found != null) return found;
            } else if (f.getName().toLowerCase().endsWith(".obb")) {
                return dir; // This directory contains an obb file
            }
        }
        return null;
    }

    private static File findFolderRecursive(File dir, String folderName, String pkgName) {
        File[] children;
        if (dir == null || !dir.isDirectory() || (children = dir.listFiles()) == null) {
            return null;
        }
        for (File f : children) {
            if (f.isDirectory()) {
                String name = f.getName().trim();
                if (name.equalsIgnoreCase(folderName) || (pkgName != null && name.equalsIgnoreCase(pkgName))) {
                    return f;
                }
                File found = findFolderRecursive(f, folderName, pkgName);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static File findObbFileRecursive(File dir) {
        File[] children;
        if (dir == null || !dir.isDirectory() || (children = dir.listFiles()) == null) {
            return null;
        }
        for (File f : children) {
            if (f.isDirectory()) {
                File found = findObbFileRecursive(f);
                if (found != null) {
                    return found;
                }
            } else if (f.getName().toLowerCase().endsWith(".obb")) {
                return f;
            }
        }
        return null;
    }

    public static boolean moveFolder(File source, File dest) throws IOException {
        if (source == null || !source.exists()) {
            return false;
        }
        if (source.isDirectory()) {
            if (!dest.exists() && !dest.mkdirs()) {
                return false;
            }
            File[] children = source.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (!moveFolder(child, new File(dest, child.getName()))) {
                        return false;
                    }
                }
            }
            return source.delete();
        }
        if (dest.exists()) {
            dest.delete();
        }
        if (source.renameTo(dest)) {
            return true;
        }
        try {
            InputStream in = new FileInputStream(source);
            try {
                OutputStream out = new FileOutputStream(dest);
                try {
                    byte[] buf = new byte[65536];
                    while (true) {
                        int len = in.read(buf);
                        if (len > 0) {
                            out.write(buf, 0, len);
                        } else {
                            boolean zDelete = source.delete();
                            out.close();
                            in.close();
                            return zDelete;
                        }
                    }
                } finally {
                }
            } finally {
            }
        } catch (Exception e) {
            return false;
        }
    }

    private static void findApks(File dir, List<File> apks) {
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                findApks(f, apks);
            } else if (f.getName().toLowerCase().endsWith(".apk")) {
                apks.add(f);
            }
        }
    }

    private static void installApkSession(Context context, File apkFile, String pkgName, String obbSourcePath, String folderName, final InstallCallback callback) throws Exception {
        InputStream in;
        Throwable th;
        Throwable th2 = null;
        byte[] buffer = null;
        long totalRead = 0;
        long fileSize = 0;
        int lastProgress = -1;
        byte[] buffer2;
        final PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(1);
        params.setAppPackageName(pkgName);
        final int sessionId = packageInstaller.createSession(params);
        PackageInstaller.Session session = packageInstaller.openSession(sessionId);
        PackageInstaller.SessionCallback sessionCallback = new PackageInstaller.SessionCallback() { // from class: com.squire.vr.AutoInstaller.1
            @Override // android.content.pm.PackageInstaller.SessionCallback
            public void onCreated(int sessionId2) {
            }

            @Override // android.content.pm.PackageInstaller.SessionCallback
            public void onBadgingChanged(int sessionId2) {
            }

            @Override // android.content.pm.PackageInstaller.SessionCallback
            public void onActiveChanged(int sessionId2, boolean active) {
            }

            @Override // android.content.pm.PackageInstaller.SessionCallback
            public void onProgressChanged(int id, float progress) {
                if (id == sessionId) {
                    int pct = (int) (100.0f * progress);
                    callback.onStatus("System Installing: " + pct + "%");
                }
            }

            @Override // android.content.pm.PackageInstaller.SessionCallback
            public void onFinished(int id, boolean success) {
                if (id == sessionId) {
                    packageInstaller.unregisterSessionCallback(this);
                }
            }
        };
        packageInstaller.registerSessionCallback(sessionCallback, new Handler(Looper.getMainLooper()));
        InputStream in2 = new FileInputStream(apkFile);
        try {
            in = in2;
            try {
                OutputStream out = session.openWrite("package", 0L, apkFile.length());
                try {
                    buffer = new byte[524288];
                    totalRead = 0;
                    fileSize = apkFile.length();
                    lastProgress = -1;
                } catch (Throwable th3) {
                    th2 = th3;
                }
                while (true) {
                    PackageInstaller.SessionParams params2 = params;
                    try {
                        int n = in.read(buffer);
                        PackageInstaller.SessionCallback sessionCallback2 = sessionCallback;
                        if (n == -1) {
                            break;
                        }
                        try {
                            out.write(buffer, 0, n);
                            int sessionId2 = sessionId;
                            totalRead += n;
                            float progressFloat = totalRead / fileSize;
                            int progress = (int) (100.0f * progressFloat);
                            try {
                                session.setStagingProgress(progressFloat);
                                if (progress > lastProgress) {
                                    buffer2 = buffer;
                                    callback.onStatus("Preparing APK: " + progress + "%");
                                    lastProgress = progress;
                                } else {
                                    buffer2 = buffer;
                                }
                                params = params2;
                                sessionCallback = sessionCallback2;
                                buffer = buffer2;
                            } catch (Throwable th4) {
                                th2 = th4;
                            }
                        } catch (Throwable th5) {
                            th2 = th5;
                        }
                    } catch (Throwable th6) {
                        th2 = th6;
                    }
                    try {
                        if (out == null) {
                            throw th2;
                        }
                        try {
                            out.close();
                            throw th2;
                        } catch (Throwable th7) {
                            th2.addSuppressed(th7);
                            throw th2;
                        }
                    } catch (Throwable th8) {
                        th = th8;
                        try {
                            in.close();
                            throw th;
                        } catch (Throwable th9) {
                            th.addSuppressed(th9);
                            throw th;
                        }
                    }
                }
                int n2 = sessionId;
                try {
                    session.fsync(out);
                    if (out != null) {
                        try {
                            out.close();
                        } catch (Throwable th10) {
                            th = th10;
                            in.close();
                            throw th;
                        }
                    }
                    in.close();
                    Intent intent = new Intent(context, (Class<?>) InstallReceiver.class);
                    intent.setAction("com.squire.vr.INSTALL_COMPLETE");
                    intent.putExtra("pkgName", pkgName);
                    intent.putExtra("obbSourcePath", obbSourcePath);
                    intent.putExtra("folderName", folderName);
                    intent.putExtra("apkPath", apkFile.getAbsolutePath());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, n2, intent, 167772160);
                    callback.onStatus("Installing APK... Check Quest Prompt");
                    session.commit(pendingIntent.getIntentSender());
                } catch (Throwable th11) {
                    th2 = th11;
                }
            } catch (Throwable th12) {
                th = th12;
            }
        } catch (Throwable th13) {
            in = in2;
            th = th13;
        }
    }
}
