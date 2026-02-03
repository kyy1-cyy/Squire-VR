package com.bumptech.glide.load.engine.bitmap_recycle;

import android.util.Log;
import com.bumptech.glide.util.Preconditions;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/* loaded from: classes.dex */
public final class LruArrayPool implements ArrayPool {
    private static final int DEFAULT_SIZE = 4194304;
    static final int MAX_OVER_SIZE_MULTIPLE = 8;
    private static final int SINGLE_ARRAY_MAX_SIZE_DIVISOR = 2;
    private final Map<Class<?>, ArrayAdapterInterface<?>> adapters;
    private int currentSize;
    private final GroupedLinkedMap<Key, Object> groupedMap;
    private final KeyPool keyPool;
    private final int maxSize;
    private final Map<Class<?>, NavigableMap<Integer, Integer>> sortedSizes;

    public LruArrayPool() {
        this.groupedMap = new GroupedLinkedMap<>();
        this.keyPool = new KeyPool();
        this.sortedSizes = new HashMap();
        this.adapters = new HashMap();
        this.maxSize = 4194304;
    }

    public LruArrayPool(int maxSize) {
        this.groupedMap = new GroupedLinkedMap<>();
        this.keyPool = new KeyPool();
        this.sortedSizes = new HashMap();
        this.adapters = new HashMap();
        this.maxSize = maxSize;
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool
    @Deprecated
    public <T> void put(T array, Class<T> arrayClass) {
        put(array);
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool
    public synchronized <T> void put(T array) {
        Class<?> cls = array.getClass();
        ArrayAdapterInterface<T> arrayAdapter = getAdapterFromType(cls);
        int size = arrayAdapter.getArrayLength(array);
        int arrayBytes = arrayAdapter.getElementSizeInBytes() * size;
        if (isSmallEnoughForReuse(arrayBytes)) {
            Key key = this.keyPool.get(size, cls);
            this.groupedMap.put(key, array);
            NavigableMap<Integer, Integer> sizes = getSizesForAdapter(cls);
            Integer current = (Integer) sizes.get(Integer.valueOf(key.size));
            Integer numValueOf = Integer.valueOf(key.size);
            int iIntValue = 1;
            if (current != null) {
                iIntValue = 1 + current.intValue();
            }
            sizes.put(numValueOf, Integer.valueOf(iIntValue));
            this.currentSize += arrayBytes;
            evict();
        }
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool
    public synchronized <T> T getExact(int i, Class<T> cls) {
        return (T) getForKey(this.keyPool.get(i, cls), cls);
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool
    public synchronized <T> T get(int i, Class<T> cls) {
        Key key;
        Integer numCeilingKey = getSizesForAdapter(cls).ceilingKey(Integer.valueOf(i));
        if (mayFillRequest(i, numCeilingKey)) {
            key = this.keyPool.get(numCeilingKey.intValue(), cls);
        } else {
            key = this.keyPool.get(i, cls);
        }
        return (T) getForKey(key, cls);
    }

    private <T> T getForKey(Key key, Class<T> cls) {
        ArrayAdapterInterface<T> adapterFromType = getAdapterFromType(cls);
        T t = (T) getArrayForKey(key);
        if (t != null) {
            this.currentSize -= adapterFromType.getArrayLength(t) * adapterFromType.getElementSizeInBytes();
            decrementArrayOfSize(adapterFromType.getArrayLength(t), cls);
        }
        if (t == null) {
            if (Log.isLoggable(adapterFromType.getTag(), 2)) {
                Log.v(adapterFromType.getTag(), "Allocated " + key.size + " bytes");
            }
            return adapterFromType.newArray(key.size);
        }
        return t;
    }

    private <T> T getArrayForKey(Key key) {
        return (T) this.groupedMap.get(key);
    }

    private boolean isSmallEnoughForReuse(int byteSize) {
        return byteSize <= this.maxSize / 2;
    }

    private boolean mayFillRequest(int requestedSize, Integer actualSize) {
        return actualSize != null && (isNoMoreThanHalfFull() || actualSize.intValue() <= requestedSize * 8);
    }

    private boolean isNoMoreThanHalfFull() {
        return this.currentSize == 0 || this.maxSize / this.currentSize >= 2;
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool
    public synchronized void clearMemory() {
        evictToSize(0);
    }

    @Override // com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool
    public synchronized void trimMemory(int level) {
        try {
            if (level >= 40) {
                clearMemory();
            } else if (level >= 20 || level == 15) {
                evictToSize(this.maxSize / 2);
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    private void evict() {
        evictToSize(this.maxSize);
    }

    private void evictToSize(int size) {
        while (this.currentSize > size) {
            Object evicted = this.groupedMap.removeLast();
            Preconditions.checkNotNull(evicted);
            ArrayAdapterInterface<Object> arrayAdapter = getAdapterFromObject(evicted);
            this.currentSize -= arrayAdapter.getArrayLength(evicted) * arrayAdapter.getElementSizeInBytes();
            decrementArrayOfSize(arrayAdapter.getArrayLength(evicted), evicted.getClass());
            if (Log.isLoggable(arrayAdapter.getTag(), 2)) {
                Log.v(arrayAdapter.getTag(), "evicted: " + arrayAdapter.getArrayLength(evicted));
            }
        }
    }

    private void decrementArrayOfSize(int size, Class<?> arrayClass) {
        NavigableMap<Integer, Integer> sizes = getSizesForAdapter(arrayClass);
        Integer current = (Integer) sizes.get(Integer.valueOf(size));
        if (current == null) {
            throw new NullPointerException("Tried to decrement empty size, size: " + size + ", this: " + this);
        }
        if (current.intValue() == 1) {
            sizes.remove(Integer.valueOf(size));
        } else {
            sizes.put(Integer.valueOf(size), Integer.valueOf(current.intValue() - 1));
        }
    }

    private NavigableMap<Integer, Integer> getSizesForAdapter(Class<?> arrayClass) {
        NavigableMap<Integer, Integer> sizes = this.sortedSizes.get(arrayClass);
        if (sizes == null) {
            NavigableMap<Integer, Integer> sizes2 = new TreeMap<>();
            this.sortedSizes.put(arrayClass, sizes2);
            return sizes2;
        }
        return sizes;
    }

    private <T> ArrayAdapterInterface<T> getAdapterFromObject(T object) {
        return getAdapterFromType(object.getClass());
    }

    private <T> ArrayAdapterInterface<T> getAdapterFromType(Class<T> arrayPoolClass) {
        ArrayAdapterInterface<T> byteArrayAdapter = (ArrayAdapterInterface) this.adapters.get(arrayPoolClass);
        if (byteArrayAdapter == null) {
            if (arrayPoolClass.equals(int[].class)) {
                byteArrayAdapter = new IntegerArrayAdapter();
            } else if (arrayPoolClass.equals(byte[].class)) {
                byteArrayAdapter = new ByteArrayAdapter();
            } else {
                throw new IllegalArgumentException("No array pool found for: " + arrayPoolClass.getSimpleName());
            }
            this.adapters.put(arrayPoolClass, byteArrayAdapter);
        }
        return byteArrayAdapter;
    }

    int getCurrentSize() {
        int currentSize = 0;
        for (Class<?> type : this.sortedSizes.keySet()) {
            for (Integer size : this.sortedSizes.get(type).keySet()) {
                ArrayAdapterInterface<?> adapter = getAdapterFromType(type);
                currentSize += size.intValue() * ((Integer) this.sortedSizes.get(type).get(size)).intValue() * adapter.getElementSizeInBytes();
            }
        }
        return currentSize;
    }

    private static final class KeyPool extends BaseKeyPool<Key> {
        KeyPool() {
        }

        Key get(int size, Class<?> arrayClass) {
            Key result = get();
            result.init(size, arrayClass);
            return result;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.bumptech.glide.load.engine.bitmap_recycle.BaseKeyPool
        public Key create() {
            return new Key(this);
        }
    }

    private static final class Key implements Poolable {
        private Class<?> arrayClass;
        private final KeyPool pool;
        int size;

        Key(KeyPool pool) {
            this.pool = pool;
        }

        void init(int length, Class<?> arrayClass) {
            this.size = length;
            this.arrayClass = arrayClass;
        }

        public boolean equals(Object o) {
            if (!(o instanceof Key)) {
                return false;
            }
            Key other = (Key) o;
            return this.size == other.size && this.arrayClass == other.arrayClass;
        }

        public String toString() {
            return "Key{size=" + this.size + "array=" + this.arrayClass + '}';
        }

        @Override // com.bumptech.glide.load.engine.bitmap_recycle.Poolable
        public void offer() {
            this.pool.offer(this);
        }

        public int hashCode() {
            int result = this.size;
            return (result * 31) + (this.arrayClass != null ? this.arrayClass.hashCode() : 0);
        }
    }
}
