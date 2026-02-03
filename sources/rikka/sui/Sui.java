package rikka.sui;

import android.os.IBinder;
import android.os.Parcel;
import rikka.shizuku.Shizuku;
import rikka.shizuku.SystemServiceHelper;

/* loaded from: classes4.dex */
public class Sui {
    private static final int BRIDGE_ACTION_GET_BINDER = 2;
    private static final String BRIDGE_SERVICE_DESCRIPTOR = "android.app.IActivityManager";
    private static final String BRIDGE_SERVICE_NAME = "activity";
    private static final int BRIDGE_TRANSACTION_CODE = 1599296841;
    private static boolean isSui;

    private static IBinder requestBinder() {
        IBinder binder = SystemServiceHelper.getSystemService(BRIDGE_SERVICE_NAME);
        if (binder == null) {
            return null;
        }
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(BRIDGE_SERVICE_DESCRIPTOR);
            data.writeInt(2);
            binder.transact(BRIDGE_TRANSACTION_CODE, data, reply, 0);
            reply.readException();
            IBinder received = reply.readStrongBinder();
            if (received != null) {
                return received;
            }
        } finally {
            try {
                return null;
            } finally {
            }
        }
        return null;
    }

    public static boolean isSui() {
        return isSui;
    }

    public static boolean init(String packageName) {
        IBinder binder = requestBinder();
        if (binder != null) {
            Shizuku.onBinderReceived(binder, packageName);
            isSui = true;
            return true;
        }
        isSui = false;
        return false;
    }
}
