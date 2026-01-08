package com.squire.vr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.util.Log;
import android.widget.Toast;

public class InstallReceiver extends BroadcastReceiver {
    private static final String TAG = "SquireInstallReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE);
        String message = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE);

        switch (status) {
            case PackageInstaller.STATUS_PENDING_USER_ACTION:
                // This is where the system "Install" prompt comes from
                Intent confirmIntent = (Intent) intent.getParcelableExtra(Intent.EXTRA_INTENT);
                if (confirmIntent != null) {
                    confirmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(confirmIntent);
                }
                break;
            case PackageInstaller.STATUS_SUCCESS:
                Log.d(TAG, "Install successful!");
                Toast.makeText(context, "Squire: Installation Success!", Toast.LENGTH_SHORT).show();
                break;
            case PackageInstaller.STATUS_FAILURE:
            case PackageInstaller.STATUS_FAILURE_ABORTED:
            case PackageInstaller.STATUS_FAILURE_BLOCKED:
            case PackageInstaller.STATUS_FAILURE_CONFLICT:
            case PackageInstaller.STATUS_FAILURE_INCOMPATIBLE:
            case PackageInstaller.STATUS_FAILURE_INVALID:
            case PackageInstaller.STATUS_FAILURE_STORAGE:
                Log.e(TAG, "Install failed: " + status + " - " + message);
                Toast.makeText(context, "Install failed: " + message, Toast.LENGTH_LONG).show();
                break;
            default:
                Log.d(TAG, "Install status unknown: " + status);
        }
    }
}
