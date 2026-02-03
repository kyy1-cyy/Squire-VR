package com.bumptech.glide.signature;

import com.bumptech.glide.load.Key;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import okhttp3.HttpUrl;

/* loaded from: classes.dex */
public class MediaStoreSignature implements Key {
    private final long dateModified;
    private final String mimeType;
    private final int orientation;

    public MediaStoreSignature(String mimeType, long dateModified, int orientation) {
        this.mimeType = mimeType == null ? HttpUrl.FRAGMENT_ENCODE_SET : mimeType;
        this.dateModified = dateModified;
        this.orientation = orientation;
    }

    @Override // com.bumptech.glide.load.Key
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MediaStoreSignature that = (MediaStoreSignature) o;
        if (this.dateModified == that.dateModified && this.orientation == that.orientation && this.mimeType.equals(that.mimeType)) {
            return true;
        }
        return false;
    }

    @Override // com.bumptech.glide.load.Key
    public int hashCode() {
        int result = this.mimeType.hashCode();
        return (((result * 31) + ((int) (this.dateModified ^ (this.dateModified >>> 32)))) * 31) + this.orientation;
    }

    @Override // com.bumptech.glide.load.Key
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        byte[] data = ByteBuffer.allocate(12).putLong(this.dateModified).putInt(this.orientation).array();
        messageDigest.update(data);
        messageDigest.update(this.mimeType.getBytes(CHARSET));
    }
}
