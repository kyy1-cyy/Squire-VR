package com.google.android.material.internal;

import android.animation.ValueAnimator;
import android.view.View;

/* loaded from: classes.dex */
public class FadeThroughUpdateListener implements ValueAnimator.AnimatorUpdateListener {
    private final float[] alphas = new float[2];
    private final View fadeInView;
    private final View fadeOutView;

    public FadeThroughUpdateListener(View fadeOutView, View fadeInView) {
        this.fadeOutView = fadeOutView;
        this.fadeInView = fadeInView;
    }

    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
    public void onAnimationUpdate(ValueAnimator animation) {
        float progress = ((Float) animation.getAnimatedValue()).floatValue();
        FadeThroughUtils.calculateFadeOutAndInAlphas(progress, this.alphas);
        if (this.fadeOutView != null) {
            this.fadeOutView.setAlpha(this.alphas[0]);
        }
        if (this.fadeInView != null) {
            this.fadeInView.setAlpha(this.alphas[1]);
        }
    }
}
