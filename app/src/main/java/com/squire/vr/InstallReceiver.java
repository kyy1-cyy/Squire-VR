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

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE);
        String message = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE);
        String pkgName = intent.getStringExtra("pkgName");
        String obbSourcePath = intent.getStringExtra("obbSourcePath");

        Log.d(TAG, "Install result received. Status: " + status + ", Pkg: " + pkgName);

        if (status == PackageInstaller.STATUS_PENDING_USER_ACTION) {
            // System prompt phase
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
            Log.d(TAG, "Install successful. Proceeding to OBB move.");
            if (obbSourcePath != null && pkgName != null) {
                File source = new File(obbSourcePath);
                File dest = new File("/sdcard/Android/obb/" + pkgName);
                if (source.exists()) {
                    if (!dest.exists()) dest.mkdirs();
                    if (moveFolder(source, dest)) {
                        obbMoved = true;
                        finalObbPath = dest.getAbsolutePath();
                        Log.d(TAG, "Post-install OBB move successful.");
                    } else {
                        Log.e(TAG, "Post-install OBB move failed.");
                        finalObbPath = "Move Failed";
                    }
                }
            }
        }

        // Notify MainActivity of the final result
        Intent resultIntent = new Intent(ACTION_INSTALL_RESULT);
        resultIntent.putExtra("success", success);
        resultIntent.putExtra("obbMoved", obbMoved);
        resultIntent.putExtra("obbPath", finalObbPath);
        resultIntent.putExtra("message", message);
        resultIntent.putExtra("pkgName", pkgName);
        context.sendBroadcast(resultIntent);
    }

    private boolean moveFolder(File source, File dest) {
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
