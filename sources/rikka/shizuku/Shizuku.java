package rikka.shizuku;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import moe.shizuku.server.IShizukuApplication;
import moe.shizuku.server.IShizukuService;
import rikka.shizuku.Shizuku;

/* loaded from: classes4.dex */
public class Shizuku {
    private static IBinder binder;
    private static IShizukuService service;
    private static int serverUid = -1;
    private static int serverApiVersion = -1;
    private static int serverPatchVersion = -1;
    private static String serverContext = null;
    private static boolean permissionGranted = false;
    private static boolean shouldShowRequestPermissionRationale = false;
    private static boolean preV11 = false;
    private static boolean binderReady = false;
    private static final IShizukuApplication SHIZUKU_APPLICATION = new IShizukuApplication.Stub() { // from class: rikka.shizuku.Shizuku.1
        @Override // moe.shizuku.server.IShizukuApplication
        public void bindApplication(Bundle data) {
            int unused = Shizuku.serverUid = data.getInt(ShizukuApiConstants.BIND_APPLICATION_SERVER_UID, -1);
            int unused2 = Shizuku.serverApiVersion = data.getInt(ShizukuApiConstants.BIND_APPLICATION_SERVER_VERSION, -1);
            int unused3 = Shizuku.serverPatchVersion = data.getInt(ShizukuApiConstants.BIND_APPLICATION_SERVER_PATCH_VERSION, -1);
            String unused4 = Shizuku.serverContext = data.getString(ShizukuApiConstants.BIND_APPLICATION_SERVER_SECONTEXT);
            boolean unused5 = Shizuku.permissionGranted = data.getBoolean(ShizukuApiConstants.BIND_APPLICATION_PERMISSION_GRANTED, false);
            boolean unused6 = Shizuku.shouldShowRequestPermissionRationale = data.getBoolean(ShizukuApiConstants.BIND_APPLICATION_SHOULD_SHOW_REQUEST_PERMISSION_RATIONALE, false);
            Shizuku.scheduleBinderReceivedListeners();
        }

        @Override // moe.shizuku.server.IShizukuApplication
        public void dispatchRequestPermissionResult(int requestCode, Bundle data) {
            boolean allowed = data.getBoolean(ShizukuApiConstants.REQUEST_PERMISSION_REPLY_ALLOWED, false);
            Shizuku.scheduleRequestPermissionResultListener(requestCode, allowed ? 0 : -1);
        }

        @Override // moe.shizuku.server.IShizukuApplication
        public void showPermissionConfirmation(int requestUid, int requestPid, String requestPackageName, int requestCode) {
        }
    };
    private static final IBinder.DeathRecipient DEATH_RECIPIENT = new IBinder.DeathRecipient() { // from class: rikka.shizuku.Shizuku$$ExternalSyntheticLambda1
        @Override // android.os.IBinder.DeathRecipient
        public final void binderDied() {
            Shizuku.lambda$static$0();
        }
    };
    private static final List<ListenerHolder<OnBinderReceivedListener>> RECEIVED_LISTENERS = new ArrayList();
    private static final List<ListenerHolder<OnBinderDeadListener>> DEAD_LISTENERS = new ArrayList();
    private static final List<ListenerHolder<OnRequestPermissionResultListener>> PERMISSION_LISTENERS = new ArrayList();
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    public interface OnBinderDeadListener {
        void onBinderDead();
    }

    public interface OnBinderReceivedListener {
        void onBinderReceived();
    }

    public interface OnRequestPermissionResultListener {
        void onRequestPermissionResult(int i, int i2);
    }

    static /* synthetic */ void lambda$static$0() {
        binderReady = false;
        onBinderReceived(null, null);
    }

    private static boolean attachApplicationV13(IBinder binder2, String packageName) throws RemoteException {
        Bundle args = new Bundle();
        args.putInt(ShizukuApiConstants.ATTACH_APPLICATION_API_VERSION, 13);
        args.putString(ShizukuApiConstants.ATTACH_APPLICATION_PACKAGE_NAME, packageName);
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(ShizukuApiConstants.BINDER_DESCRIPTOR);
            data.writeStrongBinder(SHIZUKU_APPLICATION.asBinder());
            data.writeInt(1);
            args.writeToParcel(data, 0);
            boolean result = binder2.transact(18, data, reply, 0);
            reply.readException();
            return result;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    private static boolean attachApplicationV11(IBinder binder2, String packageName) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            data.writeInterfaceToken(ShizukuApiConstants.BINDER_DESCRIPTOR);
            data.writeStrongBinder(SHIZUKU_APPLICATION.asBinder());
            data.writeString(packageName);
            boolean result = binder2.transact(14, data, reply, 0);
            reply.readException();
            return result;
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    public static void onBinderReceived(IBinder newBinder, String packageName) {
        if (binder == newBinder) {
            return;
        }
        if (newBinder == null) {
            binder = null;
            service = null;
            serverUid = -1;
            serverApiVersion = -1;
            serverContext = null;
            scheduleBinderDeadListeners();
            return;
        }
        if (binder != null) {
            binder.unlinkToDeath(DEATH_RECIPIENT, 0);
        }
        binder = newBinder;
        service = IShizukuService.Stub.asInterface(newBinder);
        try {
            binder.linkToDeath(DEATH_RECIPIENT, 0);
        } catch (Throwable th) {
            Log.i("ShizukuApplication", "attachApplication");
        }
        try {
            if (!attachApplicationV13(binder, packageName) && !attachApplicationV11(binder, packageName)) {
                preV11 = true;
            }
            Log.i("ShizukuApplication", "attachApplication");
        } catch (Throwable e) {
            Log.w("ShizukuApplication", Log.getStackTraceString(e));
        }
        if (preV11) {
            binderReady = true;
            scheduleBinderReceivedListeners();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class ListenerHolder<T> {
        private final Handler handler;
        private final T listener;

        private ListenerHolder(T listener, Handler handler) {
            this.listener = listener;
            this.handler = handler;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ListenerHolder<?> that = (ListenerHolder) o;
            if (Objects.equals(this.listener, that.listener) && Objects.equals(this.handler, that.handler)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.listener, this.handler);
        }
    }

    public static void addBinderReceivedListener(OnBinderReceivedListener listener) {
        addBinderReceivedListener(listener, null);
    }

    public static void addBinderReceivedListener(OnBinderReceivedListener listener, Handler handler) {
        addBinderReceivedListener((OnBinderReceivedListener) Objects.requireNonNull(listener), false, handler);
    }

    public static void addBinderReceivedListenerSticky(OnBinderReceivedListener listener) {
        addBinderReceivedListenerSticky((OnBinderReceivedListener) Objects.requireNonNull(listener), null);
    }

    public static void addBinderReceivedListenerSticky(OnBinderReceivedListener listener, Handler handler) {
        addBinderReceivedListener((OnBinderReceivedListener) Objects.requireNonNull(listener), true, handler);
    }

    private static void addBinderReceivedListener(OnBinderReceivedListener listener, boolean sticky, Handler handler) {
        if (sticky && binderReady) {
            if (handler != null) {
                Objects.requireNonNull(listener);
                handler.post(new Shizuku$$ExternalSyntheticLambda0(listener));
            } else if (Looper.myLooper() == Looper.getMainLooper()) {
                listener.onBinderReceived();
            } else {
                Handler handler2 = MAIN_HANDLER;
                Objects.requireNonNull(listener);
                handler2.post(new Shizuku$$ExternalSyntheticLambda0(listener));
            }
        }
        synchronized (RECEIVED_LISTENERS) {
            RECEIVED_LISTENERS.add(new ListenerHolder<>(listener, handler));
        }
    }

    public static boolean removeBinderReceivedListener(final OnBinderReceivedListener listener) {
        boolean zRemoveIf;
        synchronized (RECEIVED_LISTENERS) {
            zRemoveIf = RECEIVED_LISTENERS.removeIf(new Predicate() { // from class: rikka.shizuku.Shizuku$$ExternalSyntheticLambda2
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return Shizuku.lambda$removeBinderReceivedListener$1(listener, (Shizuku.ListenerHolder) obj);
                }
            });
        }
        return zRemoveIf;
    }

    static /* synthetic */ boolean lambda$removeBinderReceivedListener$1(OnBinderReceivedListener listener, ListenerHolder holder) {
        return holder.listener == listener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void scheduleBinderReceivedListeners() {
        synchronized (RECEIVED_LISTENERS) {
            for (ListenerHolder<OnBinderReceivedListener> holder : RECEIVED_LISTENERS) {
                if (((ListenerHolder) holder).handler != null) {
                    Handler handler = ((ListenerHolder) holder).handler;
                    OnBinderReceivedListener onBinderReceivedListener = (OnBinderReceivedListener) ((ListenerHolder) holder).listener;
                    Objects.requireNonNull(onBinderReceivedListener);
                    handler.post(new Shizuku$$ExternalSyntheticLambda0(onBinderReceivedListener));
                } else if (Looper.myLooper() == Looper.getMainLooper()) {
                    ((OnBinderReceivedListener) ((ListenerHolder) holder).listener).onBinderReceived();
                } else {
                    Handler handler2 = MAIN_HANDLER;
                    OnBinderReceivedListener onBinderReceivedListener2 = (OnBinderReceivedListener) ((ListenerHolder) holder).listener;
                    Objects.requireNonNull(onBinderReceivedListener2);
                    handler2.post(new Shizuku$$ExternalSyntheticLambda0(onBinderReceivedListener2));
                }
            }
        }
        binderReady = true;
    }

    public static void addBinderDeadListener(OnBinderDeadListener listener) {
        addBinderDeadListener(listener, null);
    }

    public static void addBinderDeadListener(OnBinderDeadListener listener, Handler handler) {
        synchronized (RECEIVED_LISTENERS) {
            DEAD_LISTENERS.add(new ListenerHolder<>(listener, handler));
        }
    }

    public static boolean removeBinderDeadListener(final OnBinderDeadListener listener) {
        boolean zRemoveIf;
        synchronized (RECEIVED_LISTENERS) {
            zRemoveIf = DEAD_LISTENERS.removeIf(new Predicate() { // from class: rikka.shizuku.Shizuku$$ExternalSyntheticLambda6
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return Shizuku.lambda$removeBinderDeadListener$2(listener, (Shizuku.ListenerHolder) obj);
                }
            });
        }
        return zRemoveIf;
    }

    static /* synthetic */ boolean lambda$removeBinderDeadListener$2(OnBinderDeadListener listener, ListenerHolder holder) {
        return holder.listener == listener;
    }

    private static void scheduleBinderDeadListeners() {
        synchronized (RECEIVED_LISTENERS) {
            for (ListenerHolder<OnBinderDeadListener> holder : DEAD_LISTENERS) {
                if (((ListenerHolder) holder).handler != null) {
                    Handler handler = ((ListenerHolder) holder).handler;
                    final OnBinderDeadListener onBinderDeadListener = (OnBinderDeadListener) ((ListenerHolder) holder).listener;
                    Objects.requireNonNull(onBinderDeadListener);
                    handler.post(new Runnable() { // from class: rikka.shizuku.Shizuku$$ExternalSyntheticLambda5
                        @Override // java.lang.Runnable
                        public final void run() {
                            onBinderDeadListener.onBinderDead();
                        }
                    });
                } else if (Looper.myLooper() == Looper.getMainLooper()) {
                    ((OnBinderDeadListener) ((ListenerHolder) holder).listener).onBinderDead();
                } else {
                    Handler handler2 = MAIN_HANDLER;
                    final OnBinderDeadListener onBinderDeadListener2 = (OnBinderDeadListener) ((ListenerHolder) holder).listener;
                    Objects.requireNonNull(onBinderDeadListener2);
                    handler2.post(new Runnable() { // from class: rikka.shizuku.Shizuku$$ExternalSyntheticLambda5
                        @Override // java.lang.Runnable
                        public final void run() {
                            onBinderDeadListener2.onBinderDead();
                        }
                    });
                }
            }
        }
    }

    public static void addRequestPermissionResultListener(OnRequestPermissionResultListener listener) {
        addRequestPermissionResultListener(listener, null);
    }

    public static void addRequestPermissionResultListener(OnRequestPermissionResultListener listener, Handler handler) {
        synchronized (RECEIVED_LISTENERS) {
            PERMISSION_LISTENERS.add(new ListenerHolder<>(listener, handler));
        }
    }

    public static boolean removeRequestPermissionResultListener(final OnRequestPermissionResultListener listener) {
        boolean zRemoveIf;
        synchronized (RECEIVED_LISTENERS) {
            zRemoveIf = PERMISSION_LISTENERS.removeIf(new Predicate() { // from class: rikka.shizuku.Shizuku$$ExternalSyntheticLambda7
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return Shizuku.lambda$removeRequestPermissionResultListener$3(listener, (Shizuku.ListenerHolder) obj);
                }
            });
        }
        return zRemoveIf;
    }

    static /* synthetic */ boolean lambda$removeRequestPermissionResultListener$3(OnRequestPermissionResultListener listener, ListenerHolder holder) {
        return holder.listener == listener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void scheduleRequestPermissionResultListener(final int requestCode, final int result) {
        synchronized (RECEIVED_LISTENERS) {
            for (final ListenerHolder<OnRequestPermissionResultListener> holder : PERMISSION_LISTENERS) {
                if (((ListenerHolder) holder).handler != null) {
                    ((ListenerHolder) holder).handler.post(new Runnable() { // from class: rikka.shizuku.Shizuku$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            ((Shizuku.OnRequestPermissionResultListener) holder.listener).onRequestPermissionResult(requestCode, result);
                        }
                    });
                } else if (Looper.myLooper() == Looper.getMainLooper()) {
                    ((OnRequestPermissionResultListener) ((ListenerHolder) holder).listener).onRequestPermissionResult(requestCode, result);
                } else {
                    MAIN_HANDLER.post(new Runnable() { // from class: rikka.shizuku.Shizuku$$ExternalSyntheticLambda4
                        @Override // java.lang.Runnable
                        public final void run() {
                            ((Shizuku.OnRequestPermissionResultListener) holder.listener).onRequestPermissionResult(requestCode, result);
                        }
                    });
                }
            }
        }
    }

    protected static IShizukuService requireService() {
        if (service == null) {
            throw new IllegalStateException("binder haven't been received");
        }
        return service;
    }

    public static IBinder getBinder() {
        return binder;
    }

    public static boolean pingBinder() {
        return binder != null && binder.pingBinder();
    }

    private static RuntimeException rethrowAsRuntimeException(RemoteException e) {
        return new RuntimeException(e);
    }

    public static void transactRemote(Parcel data, Parcel reply, int flags) throws RemoteException {
        try {
            requireService().asBinder().transact(1, data, reply, flags);
        } catch (RemoteException e) {
            throw rethrowAsRuntimeException(e);
        }
    }

    private static ShizukuRemoteProcess newProcess(String[] cmd, String[] env, String dir) {
        try {
            return new ShizukuRemoteProcess(requireService().newProcess(cmd, env, dir));
        } catch (RemoteException e) {
            throw rethrowAsRuntimeException(e);
        }
    }

    public static int getUid() {
        if (serverUid != -1) {
            return serverUid;
        }
        try {
            serverUid = requireService().getUid();
            return serverUid;
        } catch (RemoteException e) {
            throw rethrowAsRuntimeException(e);
        } catch (SecurityException e2) {
            return -1;
        }
    }

    public static int getVersion() {
        if (serverApiVersion != -1) {
            return serverApiVersion;
        }
        try {
            serverApiVersion = requireService().getVersion();
            return serverApiVersion;
        } catch (RemoteException e) {
            throw rethrowAsRuntimeException(e);
        } catch (SecurityException e2) {
            return -1;
        }
    }

    public static boolean isPreV11() {
        return preV11;
    }

    public static int getLatestServiceVersion() {
        return 13;
    }

    public static String getSELinuxContext() {
        if (serverContext != null) {
            return serverContext;
        }
        try {
            serverContext = requireService().getSELinuxContext();
            return serverContext;
        } catch (RemoteException e) {
            throw rethrowAsRuntimeException(e);
        } catch (SecurityException e2) {
            return null;
        }
    }

    public static class UserServiceArgs {
        final ComponentName componentName;
        String processName;
        String tag;
        int versionCode = 1;
        boolean debuggable = false;
        boolean daemon = true;
        boolean use32BitAppProcess = false;

        public UserServiceArgs(ComponentName componentName) {
            this.componentName = componentName;
        }

        public UserServiceArgs daemon(boolean daemon) {
            this.daemon = daemon;
            return this;
        }

        public UserServiceArgs tag(String tag) {
            this.tag = tag;
            return this;
        }

        public UserServiceArgs version(int versionCode) {
            this.versionCode = versionCode;
            return this;
        }

        public UserServiceArgs debuggable(boolean debuggable) {
            this.debuggable = debuggable;
            return this;
        }

        public UserServiceArgs processNameSuffix(String processNameSuffix) {
            this.processName = processNameSuffix;
            return this;
        }

        private UserServiceArgs use32BitAppProcess(boolean use32BitAppProcess) {
            this.use32BitAppProcess = use32BitAppProcess;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Bundle forAdd() {
            Bundle options = new Bundle();
            options.putParcelable(ShizukuApiConstants.USER_SERVICE_ARG_COMPONENT, this.componentName);
            options.putBoolean(ShizukuApiConstants.USER_SERVICE_ARG_DEBUGGABLE, this.debuggable);
            options.putInt(ShizukuApiConstants.USER_SERVICE_ARG_VERSION_CODE, this.versionCode);
            options.putBoolean(ShizukuApiConstants.USER_SERVICE_ARG_DAEMON, this.daemon);
            options.putBoolean(ShizukuApiConstants.USER_SERVICE_ARG_USE_32_BIT_APP_PROCESS, this.use32BitAppProcess);
            options.putString(ShizukuApiConstants.USER_SERVICE_ARG_PROCESS_NAME, (String) Objects.requireNonNull(this.processName, "process name suffix must not be null"));
            if (this.tag != null) {
                options.putString(ShizukuApiConstants.USER_SERVICE_ARG_TAG, this.tag);
            }
            return options;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Bundle forRemove(boolean remove) {
            Bundle options = new Bundle();
            options.putParcelable(ShizukuApiConstants.USER_SERVICE_ARG_COMPONENT, this.componentName);
            if (this.tag != null) {
                options.putString(ShizukuApiConstants.USER_SERVICE_ARG_TAG, this.tag);
            }
            options.putBoolean(ShizukuApiConstants.USER_SERVICE_ARG_REMOVE, remove);
            return options;
        }
    }

    public static void bindUserService(UserServiceArgs args, ServiceConnection conn) {
        ShizukuServiceConnection connection = ShizukuServiceConnections.get(args);
        connection.addConnection(conn);
        try {
            requireService().addUserService(connection, args.forAdd());
        } catch (RemoteException e) {
            throw rethrowAsRuntimeException(e);
        }
    }

    public static int peekUserService(UserServiceArgs args, ServiceConnection conn) {
        ShizukuServiceConnection connection = ShizukuServiceConnections.get(args);
        connection.addConnection(conn);
        try {
            Bundle bundle = args.forAdd();
            bundle.putBoolean(ShizukuApiConstants.USER_SERVICE_ARG_NO_CREATE, true);
            int result = requireService().addUserService(connection, bundle);
            boolean atLeast13 = !isPreV11() && getVersion() >= 13;
            return atLeast13 ? result : result == 0 ? 0 : -1;
        } catch (RemoteException e) {
            throw rethrowAsRuntimeException(e);
        }
    }

    public static void unbindUserService(UserServiceArgs args, ServiceConnection conn, boolean remove) {
        if (remove) {
            try {
                requireService().removeUserService(null, args.forRemove(true));
                return;
            } catch (RemoteException e) {
                throw rethrowAsRuntimeException(e);
            }
        }
        ShizukuServiceConnection connection = ShizukuServiceConnections.get(args);
        if (getVersion() >= 14 || (getVersion() == 13 && getServerPatchVersion() >= 4)) {
            try {
                requireService().removeUserService(connection, args.forRemove(false));
            } catch (RemoteException e2) {
                throw rethrowAsRuntimeException(e2);
            }
        }
        connection.clearConnections();
        ShizukuServiceConnections.remove(connection);
    }

    public static int checkRemotePermission(String permission) {
        if (serverUid == 0) {
            return 0;
        }
        try {
            return requireService().checkPermission(permission);
        } catch (RemoteException e) {
            throw rethrowAsRuntimeException(e);
        }
    }

    public static void requestPermission(int requestCode) {
        try {
            requireService().requestPermission(requestCode);
        } catch (RemoteException e) {
            throw rethrowAsRuntimeException(e);
        }
    }

    public static int checkSelfPermission() {
        if (permissionGranted) {
            return 0;
        }
        try {
            permissionGranted = requireService().checkSelfPermission();
            return permissionGranted ? 0 : -1;
        } catch (RemoteException e) {
            throw rethrowAsRuntimeException(e);
        }
    }

    public static boolean shouldShowRequestPermissionRationale() {
        if (permissionGranted) {
            return false;
        }
        if (shouldShowRequestPermissionRationale) {
            return true;
        }
        try {
            shouldShowRequestPermissionRationale = requireService().shouldShowRequestPermissionRationale();
            return shouldShowRequestPermissionRationale;
        } catch (RemoteException e) {
            throw rethrowAsRuntimeException(e);
        }
    }

    public static void exit() {
        try {
            requireService().exit();
        } catch (RemoteException e) {
            throw rethrowAsRuntimeException(e);
        }
    }

    public static void attachUserService(IBinder binder2, Bundle options) {
        try {
            requireService().attachUserService(binder2, options);
        } catch (RemoteException e) {
            throw rethrowAsRuntimeException(e);
        }
    }

    public static void dispatchPermissionConfirmationResult(int requestUid, int requestPid, int requestCode, Bundle data) {
        try {
            requireService().dispatchPermissionConfirmationResult(requestUid, requestPid, requestCode, data);
        } catch (RemoteException e) {
            throw rethrowAsRuntimeException(e);
        }
    }

    public static int getFlagsForUid(int uid, int mask) {
        try {
            return requireService().getFlagsForUid(uid, mask);
        } catch (RemoteException e) {
            throw rethrowAsRuntimeException(e);
        }
    }

    public static void updateFlagsForUid(int uid, int mask, int value) {
        try {
            requireService().updateFlagsForUid(uid, mask, value);
        } catch (RemoteException e) {
            throw rethrowAsRuntimeException(e);
        }
    }

    public static int getServerPatchVersion() {
        return serverPatchVersion;
    }
}
