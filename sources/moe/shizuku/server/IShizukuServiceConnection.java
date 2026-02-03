package moe.shizuku.server;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.dex */
public interface IShizukuServiceConnection extends IInterface {
    void connected(IBinder iBinder) throws RemoteException;

    void died() throws RemoteException;

    public static class Default implements IShizukuServiceConnection {
        @Override // moe.shizuku.server.IShizukuServiceConnection
        public void connected(IBinder service) throws RemoteException {
        }

        @Override // moe.shizuku.server.IShizukuServiceConnection
        public void died() throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IShizukuServiceConnection {
        private static final String DESCRIPTOR = "moe.shizuku.server.IShizukuServiceConnection";
        static final int TRANSACTION_connected = 1;
        static final int TRANSACTION_died = 2;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IShizukuServiceConnection asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IShizukuServiceConnection)) {
                return (IShizukuServiceConnection) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    data.enforceInterface(DESCRIPTOR);
                    IBinder _arg0 = data.readStrongBinder();
                    connected(_arg0);
                    return true;
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    died();
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        private static class Proxy implements IShizukuServiceConnection {
            public static IShizukuServiceConnection sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // moe.shizuku.server.IShizukuServiceConnection
            public void connected(IBinder service) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(service);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().connected(service);
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // moe.shizuku.server.IShizukuServiceConnection
            public void died() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().died();
                    }
                } finally {
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IShizukuServiceConnection impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static IShizukuServiceConnection getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
