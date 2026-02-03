package com.bumptech.glide.request.target;

import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;

/* loaded from: classes.dex */
public abstract class ThumbnailImageViewTarget<T> extends ImageViewTarget<T> {
    protected abstract Drawable getDrawable(T t);

    public ThumbnailImageViewTarget(ImageView view) {
        super(view);
    }

    @Deprecated
    public ThumbnailImageViewTarget(ImageView view, boolean waitForLayout) {
        super(view, waitForLayout);
    }

    @Override // com.bumptech.glide.request.target.ImageViewTarget
    protected void setResource(T resource) {
        ViewGroup.LayoutParams layoutParams = ((ImageView) this.view).getLayoutParams();
        Drawable result = getDrawable(resource);
        if (layoutParams != null && layoutParams.width > 0 && layoutParams.height > 0) {
            result = new FixedSizeDrawable(result, layoutParams.width, layoutParams.height);
        }
        ((ImageView) this.view).setImageDrawable(result);
    }
}
