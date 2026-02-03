package com.google.android.material.internal;

import android.animation.TimeInterpolator;

/* loaded from: classes.dex */
public class ReversableAnimatedValueInterpolator implements TimeInterpolator {
    private final TimeInterpolator sourceInterpolator;

    public ReversableAnimatedValueInterpolator(TimeInterpolator sourceInterpolator) {
        this.sourceInterpolator = sourceInterpolator;
    }

    public static TimeInterpolator of(boolean useSourceInterpolator, TimeInterpolator sourceInterpolator) {
        if (useSourceInterpolator) {
            return sourceInterpolator;
        }
        return new ReversableAnimatedValueInterpolator(sourceInterpolator);
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float input) {
        return 1.0f - this.sourceInterpolator.getInterpolation(input);
    }
}
