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
        File obbSource;
        File looseObb;
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
            String realPkgName2 = apkName.replaceAll("(?i)\\.apk$", HttpUrl.FRAGMENT_ENCODE_SET).trim();
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
            callback.onStatus("Auto-Sideload: Hunting for OBB...");
            File obbSource2 = null;
            File parentDir = mainApk.getParentFile();
            File candidate = new File(parentDir, realPkgName2);
            if (candidate.exists() && candidate.isDirectory()) {
                obbSource2 = candidate;
            }
            if (obbSource2 == null) {
                obbSource2 = findFolderRecursive(extractRoot, realPkgName2, realPkgName);
            }
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
            String obbSourcePath = obbSource != null ? obbSource.getAbsolutePath() : null;
            callback.onStatus("Auto-Sideload: Installing APK...");
            installApkSession(context, mainApk, realPkgName, obbSourcePath, realPkgName2, callback);
        } catch (Exception e2) {
            Log.e(TAG, "AutoInstall failed", e2);
            callback.onError(e2.getMessage());
        }
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
        Throwable th2;
        byte[] buffer;
        long totalRead;
        long fileSize;
        int lastProgress;
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
                                sessionId = sessionId2;
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
