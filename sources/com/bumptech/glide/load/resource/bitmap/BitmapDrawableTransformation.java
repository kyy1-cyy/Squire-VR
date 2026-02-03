package com.bumptech.glide.load.resource.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.Preconditions;
import java.security.MessageDigest;

@Deprecated
/* loaded from: classes.dex */
public class BitmapDrawableTransformation implements Transformation<BitmapDrawable> {
    private final Transformation<Drawable> wrapped;

    public BitmapDrawableTransformation(Transformation<Bitmap> wrapped) {
        this.wrapped = (Transformation) Preconditions.checkNotNull(new DrawableTransformation(wrapped, false));
    }

    @Override // com.bumptech.glide.load.Transformation
    public Resource<BitmapDrawable> transform(Context context, Resource<BitmapDrawable> drawableResourceToTransform, int outWidth, int outHeight) {
        Resource<Drawable> toTransform = convertToDrawableResource(drawableResourceToTransform);
        Resource<Drawable> transformed = this.wrapped.transform(context, toTransform, outWidth, outHeight);
        return convertToBitmapDrawableResource(transformed);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static Resource<BitmapDrawable> convertToBitmapDrawableResource(Resource<Drawable> resource) {
        if (!(resource.get() instanceof BitmapDrawable)) {
            throw new IllegalArgumentException("Wrapped transformation unexpectedly returned a non BitmapDrawable resource: " + resource.get());
        }
        return resource;
    }

    private static Resource<Drawable> convertToDrawableResource(Resource<BitmapDrawable> toConvert) {
        return toConvert;
    }

    @Override // com.bumptech.glide.load.Key
    public boolean equals(Object o) {
        if (o instanceof BitmapDrawableTransformation) {
            BitmapDrawableTransformation other = (BitmapDrawableTransformation) o;
            return this.wrapped.equals(other.wrapped);
        }
        return false;
    }

    @Override // com.bumptech.glide.load.Key
    public int hashCode() {
        return this.wrapped.hashCode();
    }

    @Override // com.bumptech.glide.load.Key
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        this.wrapped.updateDiskCacheKey(messageDigest);
    }
}
