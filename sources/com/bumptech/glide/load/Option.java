package com.bumptech.glide.load;

import com.bumptech.glide.util.Preconditions;
import java.security.MessageDigest;

/* loaded from: classes.dex */
public final class Option<T> {
    private static final CacheKeyUpdater<Object> EMPTY_UPDATER = new CacheKeyUpdater<Object>() { // from class: com.bumptech.glide.load.Option.1
        @Override // com.bumptech.glide.load.Option.CacheKeyUpdater
        public void update(byte[] keyBytes, Object value, MessageDigest messageDigest) {
        }
    };
    private final CacheKeyUpdater<T> cacheKeyUpdater;
    private final T defaultValue;
    private final String key;
    private volatile byte[] keyBytes;

    public interface CacheKeyUpdater<T> {
        void update(byte[] bArr, T t, MessageDigest messageDigest);
    }

    public static <T> Option<T> memory(String key) {
        return new Option<>(key, null, emptyUpdater());
    }

    public static <T> Option<T> memory(String key, T defaultValue) {
        return new Option<>(key, defaultValue, emptyUpdater());
    }

    public static <T> Option<T> disk(String key, CacheKeyUpdater<T> cacheKeyUpdater) {
        return new Option<>(key, null, cacheKeyUpdater);
    }

    public static <T> Option<T> disk(String key, T defaultValue, CacheKeyUpdater<T> cacheKeyUpdater) {
        return new Option<>(key, defaultValue, cacheKeyUpdater);
    }

    private Option(String key, T defaultValue, CacheKeyUpdater<T> cacheKeyUpdater) {
        this.key = Preconditions.checkNotEmpty(key);
        this.defaultValue = defaultValue;
        this.cacheKeyUpdater = (CacheKeyUpdater) Preconditions.checkNotNull(cacheKeyUpdater);
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public void update(T value, MessageDigest messageDigest) {
        this.cacheKeyUpdater.update(getKeyBytes(), value, messageDigest);
    }

    private byte[] getKeyBytes() {
        if (this.keyBytes == null) {
            this.keyBytes = this.key.getBytes(Key.CHARSET);
        }
        return this.keyBytes;
    }

    public boolean equals(Object o) {
        if (o instanceof Option) {
            Option<?> other = (Option) o;
            return this.key.equals(other.key);
        }
        return false;
    }

    public int hashCode() {
        return this.key.hashCode();
    }

    private static <T> CacheKeyUpdater<T> emptyUpdater() {
        return (CacheKeyUpdater<T>) EMPTY_UPDATER;
    }

    public String toString() {
        return "Option{key='" + this.key + "'}";
    }
}
