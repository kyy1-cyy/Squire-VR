package rikka.shizuku;

import android.os.IBinder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.ArraySet;
import android.util.Log;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import moe.shizuku.server.IRemoteProcess;

/* loaded from: classes4.dex */
public class ShizukuRemoteProcess extends Process implements Parcelable {
    private static final Set<ShizukuRemoteProcess> CACHE = Collections.synchronizedSet(new ArraySet());
    public static final Parcelable.Creator<ShizukuRemoteProcess> CREATOR = new Parcelable.Creator<ShizukuRemoteProcess>() { // from class: rikka.shizuku.ShizukuRemoteProcess.1
        @Override // android.os.Parcelable.Creator
        public ShizukuRemoteProcess createFromParcel(Parcel in) {
            return new ShizukuRemoteProcess(in);
        }

        @Override // android.os.Parcelable.Creator
        public ShizukuRemoteProcess[] newArray(int size) {
            return new ShizukuRemoteProcess[size];
        }
    };
    private static final String TAG = "ShizukuRemoteProcess";
    private InputStream is;
    private OutputStream os;
    private IRemoteProcess remote;

    ShizukuRemoteProcess(IRemoteProcess remote) throws RemoteException {
        this.remote = remote;
        try {
            this.remote.asBinder().linkToDeath(new IBinder.DeathRecipient() { // from class: rikka.shizuku.ShizukuRemoteProcess$$ExternalSyntheticLambda0
                @Override // android.os.IBinder.DeathRecipient
                public final void binderDied() {
                    this.f$0.m1859lambda$new$0$rikkashizukuShizukuRemoteProcess();
                }
            }, 0);
        } catch (RemoteException e) {
            Log.e(TAG, "linkToDeath", e);
        }
        CACHE.add(this);
    }

    /* renamed from: lambda$new$0$rikka-shizuku-ShizukuRemoteProcess, reason: not valid java name */
    /* synthetic */ void m1859lambda$new$0$rikkashizukuShizukuRemoteProcess() {
        this.remote = null;
        Log.v(TAG, "remote process is dead");
        CACHE.remove(this);
    }

    @Override // java.lang.Process
    public OutputStream getOutputStream() {
        if (this.os == null) {
            try {
                this.os = new ParcelFileDescriptor.AutoCloseOutputStream(this.remote.getOutputStream());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        return this.os;
    }

    @Override // java.lang.Process
    public InputStream getInputStream() {
        if (this.is == null) {
            try {
                this.is = new ParcelFileDescriptor.AutoCloseInputStream(this.remote.getInputStream());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
        return this.is;
    }

    @Override // java.lang.Process
    public InputStream getErrorStream() {
        try {
            return new ParcelFileDescriptor.AutoCloseInputStream(this.remote.getErrorStream());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // java.lang.Process
    public int waitFor() throws InterruptedException {
        try {
            return this.remote.waitFor();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // java.lang.Process
    public int exitValue() {
        try {
            return this.remote.exitValue();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override // java.lang.Process
    public void destroy() {
        try {
            this.remote.destroy();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean alive() {
        try {
            return this.remote.alive();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean waitForTimeout(long timeout, TimeUnit unit) throws InterruptedException {
        try {
            return this.remote.waitForTimeout(timeout, unit.toString());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public IBinder asBinder() {
        return this.remote.asBinder();
    }

    private ShizukuRemoteProcess(Parcel in) {
        this.remote = IRemoteProcess.Stub.asInterface(in.readStrongBinder());
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongBinder(this.remote.asBinder());
    }
}
