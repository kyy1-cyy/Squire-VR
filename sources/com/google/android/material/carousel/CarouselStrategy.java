package com.google.android.material.carousel;

import android.view.View;

/* loaded from: classes.dex */
public abstract class CarouselStrategy {
    abstract KeylineState onFirstChildMeasuredWithMargins(Carousel carousel, View view);

    static float getChildMaskPercentage(float maskedSize, float unmaskedSize, float childMargins) {
        return 1.0f - ((maskedSize - childMargins) / (unmaskedSize - childMargins));
    }
}
