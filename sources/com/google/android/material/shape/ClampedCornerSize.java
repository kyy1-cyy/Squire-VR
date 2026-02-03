package com.google.android.material.shape;

import android.graphics.RectF;
import java.util.Arrays;

/* loaded from: classes.dex */
public final class ClampedCornerSize implements CornerSize {
    private final float target;

    public static ClampedCornerSize createFromCornerSize(AbsoluteCornerSize cornerSize) {
        return new ClampedCornerSize(cornerSize.getCornerSize());
    }

    private static float getMaxCornerSize(RectF bounds) {
        return Math.min(bounds.width() / 2.0f, bounds.height() / 2.0f);
    }

    public ClampedCornerSize(float target) {
        this.target = target;
    }

    @Override // com.google.android.material.shape.CornerSize
    public float getCornerSize(RectF bounds) {
        return Math.min(this.target, getMaxCornerSize(bounds));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClampedCornerSize)) {
            return false;
        }
        ClampedCornerSize that = (ClampedCornerSize) o;
        return this.target == that.target;
    }

    public int hashCode() {
        Object[] hashedFields = {Float.valueOf(this.target)};
        return Arrays.hashCode(hashedFields);
    }
}
