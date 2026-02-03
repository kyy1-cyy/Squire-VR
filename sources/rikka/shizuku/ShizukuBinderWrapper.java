package rikka.shizuku;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import java.io.FileDescriptor;
import java.util.Objects;

/* loaded from: classes4.dex */
public class ShizukuBinderWrapper implements IBinder {
    private final IBinder original;

    public ShizukuBinderWrapper(IBinder original) {
        this.original = (IBinder) Objects.requireNonNull(original);
    }

    @Override // android.os.IBinder
    public boolean transact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        boolean atLeast13 = !Shizuku.isPreV11() && Shizuku.getVersion() >= 13;
        Parcel newData = Parcel.obtain();
        try {
            newData.writeInterfaceToken(ShizukuApiConstants.BINDER_DESCRIPTOR);
            newData.writeStrongBinder(this.original);
            newData.writeInt(code);
            if (atLeast13) {
                newData.writeInt(flags);
            }
            newData.appendFrom(data, 0, data.dataSize());
            if (atLeast13) {
                Shizuku.transactRemote(newData, reply, 0);
            } else {
                Shizuku.transactRemote(newData, reply, flags);
            }
            return true;
        } finally {
            newData.recycle();
        }
    }

    @Override // android.os.IBinder
    public String getInterfaceDescriptor() throws RemoteException {
        return this.original.getInterfaceDescriptor();
    }

    @Override // android.os.IBinder
    public boolean pingBinder() {
        return this.original.pingBinder();
    }

    @Override // android.os.IBinder
    public boolean isBinderAlive() {
        return this.original.isBinderAlive();
    }

    @Override // android.os.IBinder
    public IInterface queryLocalInterface(String descriptor) {
        return null;
    }

    @Override // android.os.IBinder
    public void dump(FileDescriptor fd, String[] args) throws RemoteException {
        this.original.dump(fd, args);
    }

    @Override // android.os.IBinder
    public void dumpAsync(FileDescriptor fd, String[] args) throws RemoteException {
        this.original.dumpAsync(fd, args);
    }

    @Override // android.os.IBinder
    public void linkToDeath(IBinder.DeathRecipient recipient, int flags) throws RemoteException {
        this.original.linkToDeath(recipient, flags);
    }

    @Override // android.os.IBinder
    public boolean unlinkToDeath(IBinder.DeathRecipient recipient, int flags) {
        return this.original.unlinkToDeath(recipient, flags);
    }
}
