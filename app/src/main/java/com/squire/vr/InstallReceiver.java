package com.squire.vr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.util.Log;
import java.io.File;

public class InstallReceiver extends BroadcastReceiver {
    private static final String TAG = "SquireInstallReceiver";
    public static final String ACTION_INSTALL_RESULT = "com.squire.vr.INSTALL_RESULT";
    public static final String ACTION_INSTALL_STATUS = "com.squire.vr.INSTALL_STATUS";

    private void sendStatus(Context context, String msg) {
        Intent statusIntent = new Intent(ACTION_INSTALL_STATUS);
        statusIntent.putExtra("status", msg);
        context.sendBroadcast(statusIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE);
        String message = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE);
        String pkgName = intent.getStringExtra("pkgName");
        String obbSourcePath = intent.getStringExtra("obbSourcePath");
        String folderName = intent.getStringExtra("folderName");
        String apkPath = intent.getStringExtra("apkPath");

        Log.d(TAG, "Install result received. Status: " + status + ", Pkg: " + pkgName);

        if (status == PackageInstaller.STATUS_PENDING_USER_ACTION) {
            Intent confirmIntent = (Intent) intent.getParcelableExtra(Intent.EXTRA_INTENT);
            if (confirmIntent != null) {
                confirmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(confirmIntent);
            }
            return;
        }

        boolean success = (status == PackageInstaller.STATUS_SUCCESS);
        boolean obbMoved = false;
        String finalObbPath = "Skipped";

        if (success) {
            sendStatus(context, "Install Success! Checking OBB...");

            if (obbSourcePath != null) {
                sendStatus(context, "Auto-Sideload: Moving OBB Folder...");

                File source = new File(obbSourcePath);
                File destPkg = new File("/sdcard/Android/obb/" + pkgName);
                File destFolder = (folderName != null) ? new File("/sdcard/Android/obb/" + folderName) : null;
                
                if (source.exists()) {
                    if (AutoInstaller.moveFolder(source, destPkg)) {
                        obbMoved = true;
                        finalObbPath = destPkg.getAbsolutePath();
                        sendStatus(context, "Auto-Sideload: OBB Moved to " + pkgName);
                    } else if (destFolder != null && !folderName.equals(pkgName)) {
                        if (AutoInstaller.moveFolder(source, destFolder)) {
                           obbMoved = true;
                           finalObbPath = destFolder.getAbsolutePath();
                           sendStatus(context, "Auto-Sideload: OBB Moved to " + folderName);
                        }
                    }
                }
            }
            
            // NEW LOGIC: Check for OBB folder in APK directory if not already moved
            if (!obbMoved && apkPath != null) {
                try {
                    File apkFile = new File(apkPath);
                    File parentDir = apkFile.getParentFile();
                    if (parentDir != null && parentDir.exists()) {
                        String apkName = apkFile.getName();
                        // remove extension to get folder name
                        String targetFolderName = apkName.replaceAll("(?i)\\.apk$", "").trim();
                        File candidateObb = new File(parentDir, targetFolderName);
                        
                        Log.d(TAG, "Checking for OBB folder: " + candidateObb.getAbsolutePath());
                        sendStatus(context, "Scanning for OBB: " + targetFolderName);
                        
                        if (candidateObb.exists() && candidateObb.isDirectory()) {
                            sendStatus(context, "Found OBB! Moving...");
                            
                            File destPkg = new File("/sdcard/Android/obb/" + pkgName);
                            
                            // Move it to /sdcard/Android/obb/[pkgName]
                            if (AutoInstaller.moveFolder(candidateObb, destPkg)) {
                                obbMoved = true;
                                finalObbPath = destPkg.getAbsolutePath();
                                Log.d(TAG, "Moved OBB folder to: " + finalObbPath);
                                sendStatus(context, "Success! OBB Moved to " + pkgName);
                            } else {
                                Log.e(TAG, "Failed to move OBB folder: " + candidateObb.getAbsolutePath());
                                sendStatus(context, "Error: OBB Move Failed! Check Permissions.");
                            }
                        } else {
                             sendStatus(context, "Warning: OBB Folder Not Found (" + targetFolderName + ")");
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in post-install OBB check", e);
                    sendStatus(context, "Error: OBB Check Crashed: " + e.getMessage());
                }
            } else if (!obbMoved) {
                 sendStatus(context, "Install Complete. No OBB moved.");
            }
            
            // CLEANUP
            if (obbSourcePath != null || apkPath != null) {
                try {
                    File root = null;
                    if (apkPath != null) root = new File(apkPath).getParentFile();
                    // Optional cleanup logic
                } catch (Exception e) {}
            }

            // Send final result to MainActivity
            Intent resultIntent = new Intent(ACTION_INSTALL_RESULT);
            resultIntent.putExtra("success", success);
            resultIntent.putExtra("pkgName", pkgName);
            resultIntent.putExtra("obbMoved", obbMoved);
            resultIntent.putExtra("obbPath", finalObbPath);
            context.sendBroadcast(resultIntent);

        } else {
            Log.e(TAG, "Install failed. Status: " + status + ", Message: " + message);
            sendStatus(context, "Install Failed! " + message);
            
            // Send failure result
            Intent resultIntent = new Intent(ACTION_INSTALL_RESULT);
            resultIntent.putExtra("success", false);
            resultIntent.putExtra("pkgName", pkgName);
            context.sendBroadcast(resultIntent);
        }
    }
}
