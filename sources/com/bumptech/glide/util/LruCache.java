package com.bumptech.glide.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class LruCache<T, Y> {
    private final Map<T, Entry<Y>> cache = new LinkedHashMap(100, 0.75f, true);
    private long currentSize;
    private final long initialMaxSize;
    private long maxSize;

    public LruCache(long size) {
        this.initialMaxSize = size;
        this.maxSize = size;
    }

    public synchronized void setSizeMultiplier(float multiplier) {
        if (multiplier < 0.0f) {
            throw new IllegalArgumentException("Multiplier must be >= 0");
        }
        this.maxSize = Math.round(this.initialMaxSize * multiplier);
        evict();
    }

    protected int getSize(Y item) {
        return 1;
    }

    protected synchronized int getCount() {
        return this.cache.size();
    }

    protected void onItemEvicted(T key, Y item) {
    }

    public synchronized long getMaxSize() {
        return this.maxSize;
    }

    public synchronized long getCurrentSize() {
        return this.currentSize;
    }

    public synchronized boolean contains(T key) {
        return this.cache.containsKey(key);
    }

    public synchronized Y get(T key) {
        Entry<Y> entry;
        entry = this.cache.get(key);
        return entry != null ? entry.value : null;
    }

    public synchronized Y put(T key, Y item) {
        int itemSize = getSize(item);
        if (itemSize >= this.maxSize) {
            onItemEvicted(key, item);
            return null;
        }
        if (item != null) {
            this.currentSize += itemSize;
        }
        Entry<Y> old = this.cache.put(key, item == null ? null : new Entry<>(item, itemSize));
        if (old != null) {
            this.currentSize -= old.size;
            if (!old.value.equals(item)) {
                onItemEvicted(key, old.value);
            }
        }
        evict();
        return old != null ? old.value : null;
    }

    public synchronized Y remove(T key) {
        Entry<Y> entry = this.cache.remove(key);
        if (entry == null) {
            return null;
        }
        this.currentSize -= entry.size;
        return entry.value;
    }

    public void clearMemory() {
        trimToSize(0L);
    }

    protected synchronized void trimToSize(long size) {
        while (this.currentSize > size) {
            Iterator<Map.Entry<T, Entry<Y>>> cacheIterator = this.cache.entrySet().iterator();
            Map.Entry<T, Entry<Y>> last = cacheIterator.next();
            Entry<Y> toRemove = last.getValue();
            this.currentSize -= toRemove.size;
            T key = last.getKey();
            cacheIterator.remove();
            onItemEvicted(key, toRemove.value);
        }
    }

    private void evict() {
        trimToSize(this.maxSize);
    }

    static final class Entry<Y> {
        final int size;
        final Y value;

        Entry(Y value, int size) {
            this.value = value;
            this.size = size;
        }
    }
}
