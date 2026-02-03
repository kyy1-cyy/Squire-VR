package com.squire.vr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import java.io.File;
import okhttp3.HttpUrl;

/* loaded from: classes3.dex */
public class InstallReceiver extends BroadcastReceiver {
    public static final String ACTION_INSTALL_RESULT = "com.squire.vr.INSTALL_RESULT";
    public static final String ACTION_INSTALL_STATUS = "com.squire.vr.INSTALL_STATUS";
    private static final String TAG = "SquireInstallReceiver";

    private void sendStatus(Context context, String msg) {
        Intent statusIntent = new Intent(ACTION_INSTALL_STATUS);
        statusIntent.putExtra(NotificationCompat.CATEGORY_STATUS, msg);
        context.sendBroadcast(statusIntent);
    }

    /* JADX WARN: Removed duplicated region for block: B:63:0x0263  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x0267  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x026e A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:68:0x0270  */
    @Override // android.content.BroadcastReceiver
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onReceive(Context context, Intent intent) {
        int status;
        String message;
        String str;
        boolean obbMoved;
        String finalObbPath;
        boolean obbMoved2;
        String finalObbPath2;
        File file;
        int status2 = intent.getIntExtra("android.content.pm.extra.STATUS", 1);
        String message2 = intent.getStringExtra("android.content.pm.extra.STATUS_MESSAGE");
        String pkgName = intent.getStringExtra("pkgName");
        String obbSourcePath = intent.getStringExtra("obbSourcePath");
        String folderName = intent.getStringExtra("folderName");
        String apkPath = intent.getStringExtra("apkPath");
        Log.d(TAG, "Install result received. Status: " + status2 + ", Pkg: " + pkgName);
        if (status2 == -1) {
            Intent confirmIntent = (Intent) intent.getParcelableExtra("android.intent.extra.INTENT");
            if (confirmIntent != null) {
                confirmIntent.addFlags(268435456);
                context.startActivity(confirmIntent);
                return;
            }
            return;
        }
        boolean success = status2 == 0;
        if (success) {
            sendStatus(context, "Install Success! Checking OBB...");
            if (obbSourcePath == null) {
                status = status2;
                message = message2;
                str = "pkgName";
                obbMoved = false;
                finalObbPath = "Skipped";
            } else {
                obbMoved = false;
                sendStatus(context, "Auto-Sideload: Moving OBB Folder...");
                File source = new File(obbSourcePath);
                finalObbPath = "Skipped";
                message = message2;
                File destPkg = new File("/sdcard/Android/obb/" + pkgName);
                if (folderName != null) {
                    status = status2;
                    file = new File("/sdcard/Android/obb/" + folderName);
                } else {
                    status = status2;
                    file = null;
                }
                File destFolder = file;
                if (source.exists()) {
                    str = "pkgName";
                    if (AutoInstaller.moveFolder(source, destPkg)) {
                        String finalObbPath3 = destPkg.getAbsolutePath();
                        sendStatus(context, "Auto-Sideload: OBB Moved to " + pkgName);
                        obbMoved2 = true;
                        finalObbPath2 = finalObbPath3;
                    } else if (destFolder != null && !folderName.equals(pkgName) && AutoInstaller.moveFolder(source, destFolder)) {
                        finalObbPath2 = destFolder.getAbsolutePath();
                        sendStatus(context, "Auto-Sideload: OBB Moved to " + folderName);
                        obbMoved2 = true;
                    }
                    if (obbMoved2 && apkPath != null) {
                        try {
                            File apkFile = new File(apkPath);
                            File parentDir = apkFile.getParentFile();
                            if (parentDir != null && parentDir.exists()) {
                                String apkName = apkFile.getName();
                                try {
                                    String targetFolderName = apkName.replaceAll("(?i)\\.apk$", HttpUrl.FRAGMENT_ENCODE_SET).trim();
                                    File candidateObb = new File(parentDir, targetFolderName);
                                    Log.d(TAG, "Checking for OBB folder: " + candidateObb.getAbsolutePath());
                                    sendStatus(context, "Scanning for OBB: " + targetFolderName);
                                    if (!candidateObb.exists() || !candidateObb.isDirectory()) {
                                        sendStatus(context, "Warning: OBB Folder Not Found (" + targetFolderName + ")");
                                    } else {
                                        sendStatus(context, "Found OBB! Moving...");
                                        File destPkg2 = new File("/sdcard/Android/obb/" + pkgName);
                                        if (AutoInstaller.moveFolder(candidateObb, destPkg2)) {
                                            obbMoved2 = true;
                                            finalObbPath2 = destPkg2.getAbsolutePath();
                                            Log.d(TAG, "Moved OBB folder to: " + finalObbPath2);
                                            sendStatus(context, "Success! OBB Moved to " + pkgName);
                                        } else {
                                            Log.e(TAG, "Failed to move OBB folder: " + candidateObb.getAbsolutePath());
                                            sendStatus(context, "Error: OBB Move Failed! Check Permissions.");
                                        }
                                    }
                                } catch (Exception e) {
                                    e = e;
                                    Log.e(TAG, "Error in post-install OBB check", e);
                                    sendStatus(context, "Error: OBB Check Crashed: " + e.getMessage());
                                    if (obbSourcePath != null) {
                                    }
                                    Intent resultIntent = new Intent(ACTION_INSTALL_RESULT);
                                    resultIntent.putExtra("success", success);
                                    resultIntent.putExtra(str, pkgName);
                                    resultIntent.putExtra("obbMoved", obbMoved2);
                                    resultIntent.putExtra("obbPath", finalObbPath2);
                                    context.sendBroadcast(resultIntent);
                                    return;
                                }
                            }
                        } catch (Exception e2) {
                            e = e2;
                        }
                    } else if (!obbMoved2) {
                        sendStatus(context, "Install Complete. No OBB moved.");
                    }
                    if ((obbSourcePath != null || apkPath != null) && apkPath != null) {
                        try {
                            new File(apkPath).getParentFile();
                        } catch (Exception e3) {
                        }
                    }
                    Intent resultIntent2 = new Intent(ACTION_INSTALL_RESULT);
                    resultIntent2.putExtra("success", success);
                    resultIntent2.putExtra(str, pkgName);
                    resultIntent2.putExtra("obbMoved", obbMoved2);
                    resultIntent2.putExtra("obbPath", finalObbPath2);
                    context.sendBroadcast(resultIntent2);
                    return;
                }
                str = "pkgName";
            }
            obbMoved2 = obbMoved;
            finalObbPath2 = finalObbPath;
            if (obbMoved2) {
                if (!obbMoved2) {
                }
            }
            if (obbSourcePath != null) {
                new File(apkPath).getParentFile();
            } else {
                new File(apkPath).getParentFile();
            }
            Intent resultIntent22 = new Intent(ACTION_INSTALL_RESULT);
            resultIntent22.putExtra("success", success);
            resultIntent22.putExtra(str, pkgName);
            resultIntent22.putExtra("obbMoved", obbMoved2);
            resultIntent22.putExtra("obbPath", finalObbPath2);
            context.sendBroadcast(resultIntent22);
            return;
        }
        Log.e(TAG, "Install failed. Status: " + status2 + ", Message: " + message2);
        sendStatus(context, "Install Failed! " + message2);
        Intent resultIntent3 = new Intent(ACTION_INSTALL_RESULT);
        resultIntent3.putExtra("success", false);
        resultIntent3.putExtra("pkgName", pkgName);
        context.sendBroadcast(resultIntent3);
    }
}
