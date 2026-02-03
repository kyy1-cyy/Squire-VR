package com.google.android.material.progressindicator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import com.google.android.material.progressindicator.BaseProgressIndicatorSpec;

/* loaded from: classes.dex */
abstract class DrawingDelegate<S extends BaseProgressIndicatorSpec> {
    protected DrawableWithAnimatedVisibilityChange drawable;
    S spec;

    abstract void adjustCanvas(Canvas canvas, Rect rect, float f);

    abstract void fillIndicator(Canvas canvas, Paint paint, float f, float f2, int i);

    abstract void fillTrack(Canvas canvas, Paint paint);

    abstract int getPreferredHeight();

    abstract int getPreferredWidth();

    public DrawingDelegate(S spec) {
        this.spec = spec;
    }

    protected void registerDrawable(DrawableWithAnimatedVisibilityChange drawable) {
        this.drawable = drawable;
    }

    void validateSpecAndAdjustCanvas(Canvas canvas, Rect bounds, float trackThicknessFraction) {
        this.spec.validateSpec();
        adjustCanvas(canvas, bounds, trackThicknessFraction);
    }
}
