package com.google.android.material.shape;

import android.graphics.RectF;
import java.util.Arrays;

/* loaded from: classes.dex */
public final class RelativeCornerSize implements CornerSize {
    private final float percent;

    public static RelativeCornerSize createFromCornerSize(RectF bounds, CornerSize cornerSize) {
        if (cornerSize instanceof RelativeCornerSize) {
            return (RelativeCornerSize) cornerSize;
        }
        return new RelativeCornerSize(cornerSize.getCornerSize(bounds) / getMaxCornerSize(bounds));
    }

    private static float getMaxCornerSize(RectF bounds) {
        return Math.min(bounds.width(), bounds.height());
    }

    public RelativeCornerSize(float percent) {
        this.percent = percent;
    }

    public float getRelativePercent() {
        return this.percent;
    }

    @Override // com.google.android.material.shape.CornerSize
    public float getCornerSize(RectF bounds) {
        return this.percent * getMaxCornerSize(bounds);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RelativeCornerSize)) {
            return false;
        }
        RelativeCornerSize that = (RelativeCornerSize) o;
        return this.percent == that.percent;
    }

    public int hashCode() {
        Object[] hashedFields = {Float.valueOf(this.percent)};
        return Arrays.hashCode(hashedFields);
    }
}
