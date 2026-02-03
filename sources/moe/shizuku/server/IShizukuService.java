package moe.shizuku.server;

import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import moe.shizuku.server.IRemoteProcess;
import moe.shizuku.server.IShizukuApplication;
import moe.shizuku.server.IShizukuServiceConnection;

/* loaded from: classes.dex */
public interface IShizukuService extends IInterface {
    int addUserService(IShizukuServiceConnection iShizukuServiceConnection, Bundle bundle) throws RemoteException;

    void attachApplication(IShizukuApplication iShizukuApplication, Bundle bundle) throws RemoteException;

    void attachUserService(IBinder iBinder, Bundle bundle) throws RemoteException;

    int checkPermission(String str) throws RemoteException;

    boolean checkSelfPermission() throws RemoteException;

    void dispatchPackageChanged(Intent intent) throws RemoteException;

    void dispatchPermissionConfirmationResult(int i, int i2, int i3, Bundle bundle) throws RemoteException;

    void exit() throws RemoteException;

    int getFlagsForUid(int i, int i2) throws RemoteException;

    String getSELinuxContext() throws RemoteException;

    String getSystemProperty(String str, String str2) throws RemoteException;

    int getUid() throws RemoteException;

    int getVersion() throws RemoteException;

    boolean isHidden(int i) throws RemoteException;

    IRemoteProcess newProcess(String[] strArr, String[] strArr2, String str) throws RemoteException;

    int removeUserService(IShizukuServiceConnection iShizukuServiceConnection, Bundle bundle) throws RemoteException;

    void requestPermission(int i) throws RemoteException;

    void setSystemProperty(String str, String str2) throws RemoteException;

    boolean shouldShowRequestPermissionRationale() throws RemoteException;

    void updateFlagsForUid(int i, int i2, int i3) throws RemoteException;

    public static class Default implements IShizukuService {
        @Override // moe.shizuku.server.IShizukuService
        public int getVersion() throws RemoteException {
            return 0;
        }

        @Override // moe.shizuku.server.IShizukuService
        public int getUid() throws RemoteException {
            return 0;
        }

        @Override // moe.shizuku.server.IShizukuService
        public int checkPermission(String permission) throws RemoteException {
            return 0;
        }

        @Override // moe.shizuku.server.IShizukuService
        public IRemoteProcess newProcess(String[] cmd, String[] env, String dir) throws RemoteException {
            return null;
        }

        @Override // moe.shizuku.server.IShizukuService
        public String getSELinuxContext() throws RemoteException {
            return null;
        }

        @Override // moe.shizuku.server.IShizukuService
        public String getSystemProperty(String name, String defaultValue) throws RemoteException {
            return null;
        }

        @Override // moe.shizuku.server.IShizukuService
        public void setSystemProperty(String name, String value) throws RemoteException {
        }

        @Override // moe.shizuku.server.IShizukuService
        public int addUserService(IShizukuServiceConnection conn, Bundle args) throws RemoteException {
            return 0;
        }

        @Override // moe.shizuku.server.IShizukuService
        public int removeUserService(IShizukuServiceConnection conn, Bundle args) throws RemoteException {
            return 0;
        }

        @Override // moe.shizuku.server.IShizukuService
        public void requestPermission(int requestCode) throws RemoteException {
        }

        @Override // moe.shizuku.server.IShizukuService
        public boolean checkSelfPermission() throws RemoteException {
            return false;
        }

        @Override // moe.shizuku.server.IShizukuService
        public boolean shouldShowRequestPermissionRationale() throws RemoteException {
            return false;
        }

        @Override // moe.shizuku.server.IShizukuService
        public void attachApplication(IShizukuApplication application, Bundle args) throws RemoteException {
        }

        @Override // moe.shizuku.server.IShizukuService
        public void exit() throws RemoteException {
        }

        @Override // moe.shizuku.server.IShizukuService
        public void attachUserService(IBinder binder, Bundle options) throws RemoteException {
        }

        @Override // moe.shizuku.server.IShizukuService
        public void dispatchPackageChanged(Intent intent) throws RemoteException {
        }

        @Override // moe.shizuku.server.IShizukuService
        public boolean isHidden(int uid) throws RemoteException {
            return false;
        }

        @Override // moe.shizuku.server.IShizukuService
        public void dispatchPermissionConfirmationResult(int requestUid, int requestPid, int requestCode, Bundle data) throws RemoteException {
        }

        @Override // moe.shizuku.server.IShizukuService
        public int getFlagsForUid(int uid, int mask) throws RemoteException {
            return 0;
        }

        @Override // moe.shizuku.server.IShizukuService
        public void updateFlagsForUid(int uid, int mask, int value) throws RemoteException {
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IShizukuService {
        private static final String DESCRIPTOR = "moe.shizuku.server.IShizukuService";
        static final int TRANSACTION_addUserService = 12;
        static final int TRANSACTION_attachApplication = 18;
        static final int TRANSACTION_attachUserService = 102;
        static final int TRANSACTION_checkPermission = 5;
        static final int TRANSACTION_checkSelfPermission = 16;
        static final int TRANSACTION_dispatchPackageChanged = 103;
        static final int TRANSACTION_dispatchPermissionConfirmationResult = 105;
        static final int TRANSACTION_exit = 101;
        static final int TRANSACTION_getFlagsForUid = 106;
        static final int TRANSACTION_getSELinuxContext = 9;
        static final int TRANSACTION_getSystemProperty = 10;
        static final int TRANSACTION_getUid = 4;
        static final int TRANSACTION_getVersion = 3;
        static final int TRANSACTION_isHidden = 104;
        static final int TRANSACTION_newProcess = 8;
        static final int TRANSACTION_removeUserService = 13;
        static final int TRANSACTION_requestPermission = 15;
        static final int TRANSACTION_setSystemProperty = 11;
        static final int TRANSACTION_shouldShowRequestPermissionRationale = 17;
        static final int TRANSACTION_updateFlagsForUid = 107;

        public Stub() {
            attachInterface(this, "moe.shizuku.server.IShizukuService");
        }

        public static IShizukuService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface("moe.shizuku.server.IShizukuService");
            if (iin != null && (iin instanceof IShizukuService)) {
                return (IShizukuService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            Bundle bundle;
            Bundle bundle2;
            Bundle bundle3;
            Bundle bundle4;
            Intent intent;
            Bundle bundle5;
            switch (i) {
                case 3:
                    parcel.enforceInterface("moe.shizuku.server.IShizukuService");
                    int version = getVersion();
                    parcel2.writeNoException();
                    parcel2.writeInt(version);
                    return true;
                case 4:
                    parcel.enforceInterface("moe.shizuku.server.IShizukuService");
                    int uid = getUid();
                    parcel2.writeNoException();
                    parcel2.writeInt(uid);
                    return true;
                case 5:
                    parcel.enforceInterface("moe.shizuku.server.IShizukuService");
                    int iCheckPermission = checkPermission(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(iCheckPermission);
                    return true;
                case 8:
                    parcel.enforceInterface("moe.shizuku.server.IShizukuService");
                    IRemoteProcess iRemoteProcessNewProcess = newProcess(parcel.createStringArray(), parcel.createStringArray(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeStrongBinder(iRemoteProcessNewProcess != null ? iRemoteProcessNewProcess.asBinder() : null);
                    return true;
                case 9:
                    parcel.enforceInterface("moe.shizuku.server.IShizukuService");
                    String sELinuxContext = getSELinuxContext();
                    parcel2.writeNoException();
                    parcel2.writeString(sELinuxContext);
                    return true;
                case 10:
                    parcel.enforceInterface("moe.shizuku.server.IShizukuService");
                    String systemProperty = getSystemProperty(parcel.readString(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeString(systemProperty);
                    return true;
                case 11:
                    parcel.enforceInterface("moe.shizuku.server.IShizukuService");
                    setSystemProperty(parcel.readString(), parcel.readString());
                    parcel2.writeNoException();
                    return true;
                case 12:
                    parcel.enforceInterface("moe.shizuku.server.IShizukuService");
                    IShizukuServiceConnection iShizukuServiceConnectionAsInterface = IShizukuServiceConnection.Stub.asInterface(parcel.readStrongBinder());
                    if (parcel.readInt() != 0) {
                        bundle = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                    } else {
                        bundle = null;
                    }
                    int iAddUserService = addUserService(iShizukuServiceConnectionAsInterface, bundle);
                    parcel2.writeNoException();
                    parcel2.writeInt(iAddUserService);
                    return true;
                case 13:
                    parcel.enforceInterface("moe.shizuku.server.IShizukuService");
                    IShizukuServiceConnection iShizukuServiceConnectionAsInterface2 = IShizukuServiceConnection.Stub.asInterface(parcel.readStrongBinder());
                    if (parcel.readInt() != 0) {
                        bundle2 = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                    } else {
                        bundle2 = null;
                    }
                    int iRemoveUserService = removeUserService(iShizukuServiceConnectionAsInterface2, bundle2);
                    parcel2.writeNoException();
                    parcel2.writeInt(iRemoveUserService);
                    return true;
                case 15:
                    parcel.enforceInterface("moe.shizuku.server.IShizukuService");
                    requestPermission(parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 16:
                    parcel.enforceInterface("moe.shizuku.server.IShizukuService");
                    boolean zCheckSelfPermission = checkSelfPermission();
                    parcel2.writeNoException();
                    parcel2.writeInt(zCheckSelfPermission ? 1 : 0);
                    return true;
                case 17:
                    parcel.enforceInterface("moe.shizuku.server.IShizukuService");
                    boolean zShouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale();
                    parcel2.writeNoException();
                    parcel2.writeInt(zShouldShowRequestPermissionRationale ? 1 : 0);
                    return true;
                case 18:
                    parcel.enforceInterface("moe.shizuku.server.IShizukuService");
                    IShizukuApplication iShizukuApplicationAsInterface = IShizukuApplication.Stub.asInterface(parcel.readStrongBinder());
                    if (parcel.readInt() != 0) {
                        bundle3 = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                    } else {
                        bundle3 = null;
                    }
                    attachApplication(iShizukuApplicationAsInterface, bundle3);
                    parcel2.writeNoException();
                    return true;
                case 101:
                    parcel.enforceInterface("moe.shizuku.server.IShizukuService");
                    exit();
                    parcel2.writeNoException();
                    return true;
                case 102:
                    parcel.enforceInterface("moe.shizuku.server.IShizukuService");
                    IBinder strongBinder = parcel.readStrongBinder();
                    if (parcel.readInt() != 0) {
                        bundle4 = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                    } else {
                        bundle4 = null;
                    }
                    attachUserService(strongBinder, bundle4);
                    parcel2.writeNoException();
                    return true;
                case TRANSACTION_dispatchPackageChanged /* 103 */:
                    parcel.enforceInterface("moe.shizuku.server.IShizukuService");
                    if (parcel.readInt() != 0) {
                        intent = (Intent) Intent.CREATOR.createFromParcel(parcel);
                    } else {
                        intent = null;
                    }
                    dispatchPackageChanged(intent);
                    return true;
                case 104:
                    parcel.enforceInterface("moe.shizuku.server.IShizukuService");
                    boolean zIsHidden = isHidden(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(zIsHidden ? 1 : 0);
                    return true;
                case TRANSACTION_dispatchPermissionConfirmationResult /* 105 */:
                    parcel.enforceInterface("moe.shizuku.server.IShizukuService");
                    int i3 = parcel.readInt();
                    int i4 = parcel.readInt();
                    int i5 = parcel.readInt();
                    if (parcel.readInt() != 0) {
                        bundle5 = (Bundle) Bundle.CREATOR.createFromParcel(parcel);
                    } else {
                        bundle5 = null;
                    }
                    dispatchPermissionConfirmationResult(i3, i4, i5, bundle5);
                    return true;
                case TRANSACTION_getFlagsForUid /* 106 */:
                    parcel.enforceInterface("moe.shizuku.server.IShizukuService");
                    int flagsForUid = getFlagsForUid(parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(flagsForUid);
                    return true;
                case TRANSACTION_updateFlagsForUid /* 107 */:
                    parcel.enforceInterface("moe.shizuku.server.IShizukuService");
                    updateFlagsForUid(parcel.readInt(), parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                case 1598968902:
                    parcel2.writeString("moe.shizuku.server.IShizukuService");
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        private static class Proxy implements IShizukuService {
            public static IShizukuService sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return "moe.shizuku.server.IShizukuService";
            }

            @Override // moe.shizuku.server.IShizukuService
            public int getVersion() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("moe.shizuku.server.IShizukuService");
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getVersion();
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // moe.shizuku.server.IShizukuService
            public int getUid() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("moe.shizuku.server.IShizukuService");
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getUid();
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // moe.shizuku.server.IShizukuService
            public int checkPermission(String permission) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("moe.shizuku.server.IShizukuService");
                    _data.writeString(permission);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().checkPermission(permission);
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // moe.shizuku.server.IShizukuService
            public IRemoteProcess newProcess(String[] cmd, String[] env, String dir) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("moe.shizuku.server.IShizukuService");
                    _data.writeStringArray(cmd);
                    _data.writeStringArray(env);
                    _data.writeString(dir);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().newProcess(cmd, env, dir);
                    }
                    _reply.readException();
                    IRemoteProcess _result = IRemoteProcess.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // moe.shizuku.server.IShizukuService
            public String getSELinuxContext() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("moe.shizuku.server.IShizukuService");
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getSELinuxContext();
                    }
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // moe.shizuku.server.IShizukuService
            public String getSystemProperty(String name, String defaultValue) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("moe.shizuku.server.IShizukuService");
                    _data.writeString(name);
                    _data.writeString(defaultValue);
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getSystemProperty(name, defaultValue);
                    }
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // moe.shizuku.server.IShizukuService
            public void setSystemProperty(String name, String value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("moe.shizuku.server.IShizukuService");
                    _data.writeString(name);
                    _data.writeString(value);
                    boolean _status = this.mRemote.transact(11, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().setSystemProperty(name, value);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // moe.shizuku.server.IShizukuService
            public int addUserService(IShizukuServiceConnection conn, Bundle args) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("moe.shizuku.server.IShizukuService");
                    _data.writeStrongBinder(conn != null ? conn.asBinder() : null);
                    if (args != null) {
                        _data.writeInt(1);
                        args.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(12, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().addUserService(conn, args);
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // moe.shizuku.server.IShizukuService
            public int removeUserService(IShizukuServiceConnection conn, Bundle args) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("moe.shizuku.server.IShizukuService");
                    _data.writeStrongBinder(conn != null ? conn.asBinder() : null);
                    if (args != null) {
                        _data.writeInt(1);
                        args.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(13, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().removeUserService(conn, args);
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // moe.shizuku.server.IShizukuService
            public void requestPermission(int requestCode) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("moe.shizuku.server.IShizukuService");
                    _data.writeInt(requestCode);
                    boolean _status = this.mRemote.transact(15, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().requestPermission(requestCode);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // moe.shizuku.server.IShizukuService
            public boolean checkSelfPermission() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("moe.shizuku.server.IShizukuService");
                    boolean _status = this.mRemote.transact(16, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().checkSelfPermission();
                    }
                    _reply.readException();
                    boolean _status2 = _reply.readInt() != 0;
                    return _status2;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // moe.shizuku.server.IShizukuService
            public boolean shouldShowRequestPermissionRationale() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("moe.shizuku.server.IShizukuService");
                    boolean _status = this.mRemote.transact(17, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().shouldShowRequestPermissionRationale();
                    }
                    _reply.readException();
                    boolean _status2 = _reply.readInt() != 0;
                    return _status2;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // moe.shizuku.server.IShizukuService
            public void attachApplication(IShizukuApplication application, Bundle args) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("moe.shizuku.server.IShizukuService");
                    _data.writeStrongBinder(application != null ? application.asBinder() : null);
                    if (args != null) {
                        _data.writeInt(1);
                        args.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(18, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().attachApplication(application, args);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // moe.shizuku.server.IShizukuService
            public void exit() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("moe.shizuku.server.IShizukuService");
                    boolean _status = this.mRemote.transact(101, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().exit();
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // moe.shizuku.server.IShizukuService
            public void attachUserService(IBinder binder, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("moe.shizuku.server.IShizukuService");
                    _data.writeStrongBinder(binder);
                    if (options != null) {
                        _data.writeInt(1);
                        options.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(102, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().attachUserService(binder, options);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // moe.shizuku.server.IShizukuService
            public void dispatchPackageChanged(Intent intent) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("moe.shizuku.server.IShizukuService");
                    if (intent != null) {
                        _data.writeInt(1);
                        intent.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_dispatchPackageChanged, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().dispatchPackageChanged(intent);
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // moe.shizuku.server.IShizukuService
            public boolean isHidden(int uid) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("moe.shizuku.server.IShizukuService");
                    _data.writeInt(uid);
                    boolean _status = this.mRemote.transact(104, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().isHidden(uid);
                    }
                    _reply.readException();
                    boolean _status2 = _reply.readInt() != 0;
                    return _status2;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // moe.shizuku.server.IShizukuService
            public void dispatchPermissionConfirmationResult(int requestUid, int requestPid, int requestCode, Bundle data) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("moe.shizuku.server.IShizukuService");
                    _data.writeInt(requestUid);
                    _data.writeInt(requestPid);
                    _data.writeInt(requestCode);
                    if (data != null) {
                        _data.writeInt(1);
                        data.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_dispatchPermissionConfirmationResult, _data, null, 1);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().dispatchPermissionConfirmationResult(requestUid, requestPid, requestCode, data);
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // moe.shizuku.server.IShizukuService
            public int getFlagsForUid(int uid, int mask) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("moe.shizuku.server.IShizukuService");
                    _data.writeInt(uid);
                    _data.writeInt(mask);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_getFlagsForUid, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        return Stub.getDefaultImpl().getFlagsForUid(uid, mask);
                    }
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // moe.shizuku.server.IShizukuService
            public void updateFlagsForUid(int uid, int mask, int value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("moe.shizuku.server.IShizukuService");
                    _data.writeInt(uid);
                    _data.writeInt(mask);
                    _data.writeInt(value);
                    boolean _status = this.mRemote.transact(Stub.TRANSACTION_updateFlagsForUid, _data, _reply, 0);
                    if (!_status && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().updateFlagsForUid(uid, mask, value);
                    } else {
                        _reply.readException();
                    }
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IShizukuService impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static IShizukuService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
