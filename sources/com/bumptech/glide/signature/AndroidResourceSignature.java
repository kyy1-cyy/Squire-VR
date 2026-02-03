package com.bumptech.glide.signature;

import android.content.Context;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.util.Util;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

/* loaded from: classes.dex */
public final class AndroidResourceSignature implements Key {
    private final Key applicationVersion;
    private final int nightMode;

    public static Key obtain(Context context) {
        Key signature = ApplicationVersionSignature.obtain(context);
        int nightMode = context.getResources().getConfiguration().uiMode & 48;
        return new AndroidResourceSignature(nightMode, signature);
    }

    private AndroidResourceSignature(int nightMode, Key applicationVersion) {
        this.nightMode = nightMode;
        this.applicationVersion = applicationVersion;
    }

    @Override // com.bumptech.glide.load.Key
    public boolean equals(Object o) {
        if (!(o instanceof AndroidResourceSignature)) {
            return false;
        }
        AndroidResourceSignature that = (AndroidResourceSignature) o;
        return this.nightMode == that.nightMode && this.applicationVersion.equals(that.applicationVersion);
    }

    @Override // com.bumptech.glide.load.Key
    public int hashCode() {
        return Util.hashCode(this.applicationVersion, this.nightMode);
    }

    @Override // com.bumptech.glide.load.Key
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        this.applicationVersion.updateDiskCacheKey(messageDigest);
        byte[] nightModeData = ByteBuffer.allocate(4).putInt(this.nightMode).array();
        messageDigest.update(nightModeData);
    }
}
