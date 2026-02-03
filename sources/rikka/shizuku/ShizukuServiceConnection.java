package rikka.shizuku;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import java.util.HashSet;
import java.util.Set;
import moe.shizuku.server.IShizukuServiceConnection;
import rikka.shizuku.Shizuku;

/* loaded from: classes4.dex */
class ShizukuServiceConnection extends IShizukuServiceConnection.Stub {
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());
    private IBinder binder;
    private final ComponentName componentName;
    private final Set<ServiceConnection> connections = new HashSet();
    private boolean dead = false;

    public ShizukuServiceConnection(Shizuku.UserServiceArgs args) {
        this.componentName = args.componentName;
    }

    public void addConnection(ServiceConnection conn) {
        if (conn != null) {
            this.connections.add(conn);
        }
    }

    public void removeConnection(ServiceConnection conn) {
        if (conn != null) {
            this.connections.remove(conn);
        }
    }

    public void clearConnections() {
        this.connections.clear();
    }

    @Override // moe.shizuku.server.IShizukuServiceConnection
    public void connected(final IBinder binder) throws RemoteException {
        MAIN_HANDLER.post(new Runnable() { // from class: rikka.shizuku.ShizukuServiceConnection$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1860lambda$connected$0$rikkashizukuShizukuServiceConnection(binder);
            }
        });
        this.binder = binder;
        try {
            this.binder.linkToDeath(new IBinder.DeathRecipient() { // from class: rikka.shizuku.ShizukuServiceConnection$$ExternalSyntheticLambda2
                @Override // android.os.IBinder.DeathRecipient
                public final void binderDied() {
                    this.f$0.died();
                }
            }, 0);
        } catch (RemoteException e) {
        }
    }

    /* renamed from: lambda$connected$0$rikka-shizuku-ShizukuServiceConnection, reason: not valid java name */
    /* synthetic */ void m1860lambda$connected$0$rikkashizukuShizukuServiceConnection(IBinder binder) {
        for (ServiceConnection conn : this.connections) {
            conn.onServiceConnected(this.componentName, binder);
        }
    }

    @Override // moe.shizuku.server.IShizukuServiceConnection
    public void died() {
        this.binder = null;
        if (this.dead) {
            return;
        }
        this.dead = true;
        MAIN_HANDLER.post(new Runnable() { // from class: rikka.shizuku.ShizukuServiceConnection$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m1861lambda$died$1$rikkashizukuShizukuServiceConnection();
            }
        });
    }

    /* renamed from: lambda$died$1$rikka-shizuku-ShizukuServiceConnection, reason: not valid java name */
    /* synthetic */ void m1861lambda$died$1$rikkashizukuShizukuServiceConnection() {
        for (ServiceConnection conn : this.connections) {
            conn.onServiceDisconnected(this.componentName);
        }
        this.connections.clear();
        ShizukuServiceConnections.remove(this);
    }
}
