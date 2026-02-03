package com.google.gson.internal;

import java.lang.reflect.Type;

/* loaded from: classes.dex */
public final class Primitives {
    private Primitives() {
    }

    public static boolean isPrimitive(Type type) {
        return (type instanceof Class) && ((Class) type).isPrimitive();
    }

    public static boolean isWrapperType(Type type) {
        return type == Integer.class || type == Float.class || type == Byte.class || type == Double.class || type == Long.class || type == Character.class || type == Boolean.class || type == Short.class || type == Void.class;
    }

    public static <T> Class<T> wrap(Class<T> type) {
        return type == Integer.TYPE ? Integer.class : type == Float.TYPE ? Float.class : type == Byte.TYPE ? Byte.class : type == Double.TYPE ? Double.class : type == Long.TYPE ? Long.class : type == Character.TYPE ? Character.class : type == Boolean.TYPE ? Boolean.class : type == Short.TYPE ? Short.class : type == Void.TYPE ? Void.class : type;
    }

    public static <T> Class<T> unwrap(Class<T> type) {
        return type == Integer.class ? Integer.TYPE : type == Float.class ? Float.TYPE : type == Byte.class ? Byte.TYPE : type == Double.class ? Double.TYPE : type == Long.class ? Long.TYPE : type == Character.class ? Character.TYPE : type == Boolean.class ? Boolean.TYPE : type == Short.class ? Short.TYPE : type == Void.class ? Void.TYPE : type;
    }
}
