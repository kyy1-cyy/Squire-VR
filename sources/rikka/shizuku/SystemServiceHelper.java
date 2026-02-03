package rikka.shizuku;

import android.os.IBinder;
import android.os.Parcel;
import android.text.TextUtils;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes4.dex */
public class SystemServiceHelper {
    private static final Map<String, IBinder> SYSTEM_SERVICE_CACHE = new HashMap();
    private static final Map<String, Integer> TRANSACT_CODE_CACHE = new HashMap();
    private static Method getService;

    static {
        try {
            Class<?> sm = Class.forName("android.os.ServiceManager");
            getService = sm.getMethod("getService", String.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            Log.w("SystemServiceHelper", Log.getStackTraceString(e));
        }
    }

    public static IBinder getSystemService(String name) {
        IBinder binder = SYSTEM_SERVICE_CACHE.get(name);
        if (binder == null) {
            try {
                binder = (IBinder) getService.invoke(null, name);
            } catch (IllegalAccessException | InvocationTargetException e) {
                Log.w("SystemServiceHelper", Log.getStackTraceString(e));
            }
            SYSTEM_SERVICE_CACHE.put(name, binder);
        }
        return binder;
    }

    @Deprecated
    public static Integer getTransactionCode(String className, String methodName) throws NoSuchFieldException, ClassNotFoundException {
        String fieldName = "TRANSACTION_" + methodName;
        String key = className + "." + fieldName;
        Integer value = TRANSACT_CODE_CACHE.get(key);
        if (value != null) {
            return value;
        }
        try {
            Class<?> cls = Class.forName(className);
            Field declaredField = null;
            try {
                declaredField = cls.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                Field[] declaredFields = cls.getDeclaredFields();
                int length = declaredFields.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    Field f = declaredFields[i];
                    if (f.getType() == Integer.TYPE) {
                        String name = f.getName();
                        if (name.startsWith(fieldName + "_") && TextUtils.isDigitsOnly(name.substring(fieldName.length() + 1))) {
                            declaredField = f;
                            break;
                        }
                    }
                    i++;
                }
            }
            if (declaredField == null) {
                return null;
            }
            declaredField.setAccessible(true);
            Integer value2 = Integer.valueOf(declaredField.getInt(cls));
            TRANSACT_CODE_CACHE.put(key, value2);
            return value2;
        } catch (ClassNotFoundException | IllegalAccessException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    @Deprecated
    public static Parcel obtainParcel(String serviceName, String interfaceName, String methodName) {
        return obtainParcel(serviceName, interfaceName, interfaceName + "$Stub", methodName);
    }

    @Deprecated
    public static Parcel obtainParcel(String serviceName, String interfaceName, String className, String methodName) {
        throw new UnsupportedOperationException("Direct use of Shizuku#transactRemote is no longer supported, please use ShizukuBinderWrapper");
    }
}
