package com.google.android.material.internal;

/* loaded from: classes.dex */
final class FadeThroughUtils {
    static final float THRESHOLD_ALPHA = 0.5f;

    static void calculateFadeOutAndInAlphas(float progress, float[] out) {
        if (progress <= 0.5f) {
            out[0] = 1.0f - (2.0f * progress);
            out[1] = 0.0f;
        } else {
            out[0] = 0.0f;
            out[1] = (2.0f * progress) - 1.0f;
        }
    }

    private FadeThroughUtils() {
    }
}
