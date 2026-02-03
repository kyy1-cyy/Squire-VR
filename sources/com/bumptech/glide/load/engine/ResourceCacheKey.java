package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.util.LruCache;
import com.bumptech.glide.util.Util;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

/* loaded from: classes.dex */
final class ResourceCacheKey implements Key {
    private static final LruCache<Class<?>, byte[]> RESOURCE_CLASS_BYTES = new LruCache<>(50);
    private final ArrayPool arrayPool;
    private final Class<?> decodedResourceClass;
    private final int height;
    private final Options options;
    private final Key signature;
    private final Key sourceKey;
    private final Transformation<?> transformation;
    private final int width;

    ResourceCacheKey(ArrayPool arrayPool, Key sourceKey, Key signature, int width, int height, Transformation<?> appliedTransformation, Class<?> decodedResourceClass, Options options) {
        this.arrayPool = arrayPool;
        this.sourceKey = sourceKey;
        this.signature = signature;
        this.width = width;
        this.height = height;
        this.transformation = appliedTransformation;
        this.decodedResourceClass = decodedResourceClass;
        this.options = options;
    }

    @Override // com.bumptech.glide.load.Key
    public boolean equals(Object o) {
        if (!(o instanceof ResourceCacheKey)) {
            return false;
        }
        ResourceCacheKey other = (ResourceCacheKey) o;
        return this.height == other.height && this.width == other.width && Util.bothNullOrEqual(this.transformation, other.transformation) && this.decodedResourceClass.equals(other.decodedResourceClass) && this.sourceKey.equals(other.sourceKey) && this.signature.equals(other.signature) && this.options.equals(other.options);
    }

    @Override // com.bumptech.glide.load.Key
    public int hashCode() {
        int result = this.sourceKey.hashCode();
        int result2 = (((((result * 31) + this.signature.hashCode()) * 31) + this.width) * 31) + this.height;
        if (this.transformation != null) {
            result2 = (result2 * 31) + this.transformation.hashCode();
        }
        return (((result2 * 31) + this.decodedResourceClass.hashCode()) * 31) + this.options.hashCode();
    }

    @Override // com.bumptech.glide.load.Key
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        byte[] dimensions = (byte[]) this.arrayPool.getExact(8, byte[].class);
        ByteBuffer.wrap(dimensions).putInt(this.width).putInt(this.height).array();
        this.signature.updateDiskCacheKey(messageDigest);
        this.sourceKey.updateDiskCacheKey(messageDigest);
        messageDigest.update(dimensions);
        if (this.transformation != null) {
            this.transformation.updateDiskCacheKey(messageDigest);
        }
        this.options.updateDiskCacheKey(messageDigest);
        messageDigest.update(getResourceClassBytes());
        this.arrayPool.put(dimensions);
    }

    private byte[] getResourceClassBytes() {
        byte[] result = RESOURCE_CLASS_BYTES.get(this.decodedResourceClass);
        if (result == null) {
            byte[] result2 = this.decodedResourceClass.getName().getBytes(CHARSET);
            RESOURCE_CLASS_BYTES.put(this.decodedResourceClass, result2);
            return result2;
        }
        return result;
    }

    public String toString() {
        return "ResourceCacheKey{sourceKey=" + this.sourceKey + ", signature=" + this.signature + ", width=" + this.width + ", height=" + this.height + ", decodedResourceClass=" + this.decodedResourceClass + ", transformation='" + this.transformation + "', options=" + this.options + '}';
    }
}
