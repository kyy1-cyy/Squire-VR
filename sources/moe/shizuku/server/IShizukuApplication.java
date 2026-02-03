package moe.shizuku.server;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.dex */
public interface IShizukuApplication extends IInterface {
    void bindApplication(Bundle bundle) throws RemoteException;

    void dispatchRequestPermissionResult(int i, Bundle bundle) throws RemoteException;

    void showPermissionConfirmation(int i, int i2, String str, int i3) throws RemoteException;

    public static class Default implements IShizukuApplication {
        @Override // moe.shizuku.server.IShizukuApplication
        public void bindApplication(Bundle data) throws RemoteException {
        }

        @Override // moe.shizuku.server.IShizukuApplication
        public void dispatchRequestPermissionResult(int requestCode, Bundle data) throws RemoteException {
        }

        @Override // moe.shizuku.server.IShizukuApplication
        public void showPermissionConfirmation(int requestUid, int requestPid, String requestPackageName, int requestCode) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IShizukuApplication {
        private static final String DESCRIPTOR = "moe.shizuku.server.IShizukuApplication";
        static final int TRANSACTION_bindApplication = 2;
        static final int TRANSACTION_dispatchRequestPermissionResult = 3;
        static final int TRANSACTION_showPermissionConfirmation = 10001;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IShizukuApplication asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IShizukuApplication)) {
                return (IShizukuApplication) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            Bundle _arg0;
            Bundle _arg1;
            switch (code) {
                case 2:
                    data.enforceInterface(DESCRIPTOR);
                    if (data.readInt() != 0) {
                        _arg0 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    bindApplication(_arg0);
                    return true;
                case 3:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg02 = data.readInt();
                    if (data.readInt() != 0) {
                        _arg1 = (Bundle) Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg1 = null;
                    }
                    dispatchRequestPermissionResult(_arg02, _arg1);
                    return true;
                case TRANSACTION_showPermissionConfirmation /* 10001 */:
                    data.enforceInterface(DESCRIPTOR);
                    int _arg03 = data.readInt();
                    int _arg12 = data.readInt();
                    String _arg2 = data.readString();
                    int _arg3 = data.readInt();
                    showPermissionConfirmation(_arg03, _arg12, _arg2, _arg3);
                    reply.writeNoException();
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }

        private static class Proxy implements IShizukuApplication {
            public static IShizukuApplication sDefaultImpl;
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

            @Override // moe.shizuku.server.IShizukuApplication
            public void bindApplication(Bundle data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (data != null) {
                        _data.writeInt(1);
                        data.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().bindApplication(data);
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // moe.shizuku.server.IShizukuApplication
            public void dispatchRequestPermissionResult(int requestCode, Bundle data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(requestCode);
                    if (data != null) {
                        _data.writeInt(1);
                        data.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().dispatchRequestPermissionResult(requestCode, data);
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // moe.shizuku.server.IShizukuApplication
            public void showPermissionConfirmation(int requestUid, int requestPid, String requestPackageName, int requestCode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(requestUid);
                    _data.writeInt(requestPid);
                    _data.writeString(requestPackageName);
                    _data.writeInt(requestCode);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_showPermissionConfirmation, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().showPermissionConfirmation(requestUid, requestPid, requestPackageName, requestCode);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IShizukuApplication impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static IShizukuApplication getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
