package rikka.shizuku;

import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import moe.shizuku.api.BinderContainer;
import rikka.sui.Sui;

/* loaded from: classes4.dex */
public class ShizukuProvider extends ContentProvider {
    public static final String ACTION_BINDER_RECEIVED = "moe.shizuku.api.action.BINDER_RECEIVED";
    private static final String EXTRA_BINDER = "moe.shizuku.privileged.api.intent.extra.BINDER";
    public static final String MANAGER_APPLICATION_ID = "moe.shizuku.privileged.api";
    public static final String METHOD_GET_BINDER = "getBinder";
    public static final String METHOD_SEND_BINDER = "sendBinder";
    public static final String PERMISSION = "moe.shizuku.manager.permission.API_V23";
    private static final String TAG = "ShizukuProvider";
    private static boolean enableMultiProcess = false;
    private static boolean isProviderProcess = false;
    private static boolean enableSuiInitialization = true;

    public static void setIsProviderProcess(boolean isProviderProcess2) {
        isProviderProcess = isProviderProcess2;
    }

    public static void enableMultiProcessSupport(boolean isProviderProcess2) {
        Log.d(TAG, "Enable built-in multi-process support (from " + (isProviderProcess2 ? "provider process" : "non-provider process") + ")");
        isProviderProcess = isProviderProcess2;
        enableMultiProcess = true;
    }

    public static void disableAutomaticSuiInitialization() {
        enableSuiInitialization = false;
    }

    public static void requestBinderForNonProviderProcess(Context context) {
        Bundle reply;
        if (isProviderProcess) {
            return;
        }
        Log.d(TAG, "request binder in non-provider process");
        BroadcastReceiver receiver = new BroadcastReceiver() { // from class: rikka.shizuku.ShizukuProvider.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                BinderContainer container = (BinderContainer) intent.getParcelableExtra(ShizukuProvider.EXTRA_BINDER);
                if (container != null && container.binder != null) {
                    Log.i(ShizukuProvider.TAG, "binder received from broadcast");
                    Shizuku.onBinderReceived(container.binder, context2.getPackageName());
                }
            }
        };
        if (Build.VERSION.SDK_INT >= 33) {
            context.registerReceiver(receiver, new IntentFilter(ACTION_BINDER_RECEIVED), 4);
        } else {
            context.registerReceiver(receiver, new IntentFilter(ACTION_BINDER_RECEIVED));
        }
        try {
            reply = context.getContentResolver().call(Uri.parse("content://" + context.getPackageName() + ".shizuku"), METHOD_GET_BINDER, (String) null, new Bundle());
        } catch (Throwable th) {
            reply = null;
        }
        if (reply != null) {
            reply.setClassLoader(BinderContainer.class.getClassLoader());
            BinderContainer container = (BinderContainer) reply.getParcelable(EXTRA_BINDER);
            if (container != null && container.binder != null) {
                Log.i(TAG, "Binder received from other process");
                Shizuku.onBinderReceived(container.binder, context.getPackageName());
            }
        }
    }

    @Override // android.content.ContentProvider
    public void attachInfo(Context context, ProviderInfo info) {
        super.attachInfo(context, info);
        if (info.multiprocess) {
            throw new IllegalStateException("android:multiprocess must be false");
        }
        if (!info.exported) {
            throw new IllegalStateException("android:exported must be true");
        }
        isProviderProcess = true;
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        if (enableSuiInitialization && !Sui.isSui()) {
            boolean result = Sui.init(getContext().getPackageName());
            Log.d(TAG, "Initialize Sui: " + result);
            return true;
        }
        return true;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:18:0x0041  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x0051 A[RETURN] */
    @Override // android.content.ContentProvider
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public Bundle call(String method, String arg, Bundle extras) {
        Bundle reply;
        if (Sui.isSui()) {
            Log.w(TAG, "Provider called when Sui is available. Are you using Shizuku and Sui at the same time?");
            return new Bundle();
        }
        if (extras == null) {
            return null;
        }
        extras.setClassLoader(BinderContainer.class.getClassLoader());
        reply = new Bundle();
        switch (method) {
            case "sendBinder":
                handleSendBinder(extras);
                return reply;
            case "getBinder":
                if (!handleGetBinder(reply)) {
                    return null;
                }
                return reply;
        }
    }

    private void handleSendBinder(Bundle extras) {
        if (Shizuku.pingBinder()) {
            Log.d(TAG, "sendBinder is called when already a living binder");
            return;
        }
        BinderContainer container = (BinderContainer) extras.getParcelable(EXTRA_BINDER);
        if (container != null && container.binder != null) {
            Log.d(TAG, "binder received");
            Shizuku.onBinderReceived(container.binder, getContext().getPackageName());
            if (enableMultiProcess) {
                Log.d(TAG, "broadcast binder");
                Intent intent = new Intent(ACTION_BINDER_RECEIVED).putExtra(EXTRA_BINDER, container).setPackage(getContext().getPackageName());
                getContext().sendBroadcast(intent);
            }
        }
    }

    private boolean handleGetBinder(Bundle reply) {
        IBinder binder = Shizuku.getBinder();
        if (binder == null || !binder.pingBinder()) {
            return false;
        }
        reply.putParcelable(EXTRA_BINDER, new BinderContainer(binder));
        return true;
    }

    @Override // android.content.ContentProvider
    public final Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override // android.content.ContentProvider
    public final String getType(Uri uri) {
        return null;
    }

    @Override // android.content.ContentProvider
    public final Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override // android.content.ContentProvider
    public final int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public final int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
