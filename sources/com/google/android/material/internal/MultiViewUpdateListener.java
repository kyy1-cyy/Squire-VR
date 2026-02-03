package com.google.android.material.internal;

import android.animation.ValueAnimator;
import android.view.View;
import java.util.Collection;

/* loaded from: classes.dex */
public class MultiViewUpdateListener implements ValueAnimator.AnimatorUpdateListener {
    private final Listener listener;
    private final View[] views;

    interface Listener {
        void onAnimationUpdate(ValueAnimator valueAnimator, View view);
    }

    public MultiViewUpdateListener(Listener listener, View... views) {
        this.listener = listener;
        this.views = views;
    }

    public MultiViewUpdateListener(Listener listener, Collection<View> views) {
        this.listener = listener;
        this.views = (View[]) views.toArray(new View[0]);
    }

    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        for (View view : this.views) {
            this.listener.onAnimationUpdate(valueAnimator, view);
        }
    }

    public static MultiViewUpdateListener alphaListener(View... views) {
        return new MultiViewUpdateListener(new MultiViewUpdateListener$$ExternalSyntheticLambda3(), views);
    }

    public static MultiViewUpdateListener alphaListener(Collection<View> views) {
        return new MultiViewUpdateListener(new MultiViewUpdateListener$$ExternalSyntheticLambda3(), views);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setAlpha(ValueAnimator animator, View view) {
        view.setAlpha(((Float) animator.getAnimatedValue()).floatValue());
    }

    public static MultiViewUpdateListener scaleListener(View... views) {
        return new MultiViewUpdateListener(new MultiViewUpdateListener$$ExternalSyntheticLambda1(), views);
    }

    public static MultiViewUpdateListener scaleListener(Collection<View> views) {
        return new MultiViewUpdateListener(new MultiViewUpdateListener$$ExternalSyntheticLambda1(), views);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setScale(ValueAnimator animator, View view) {
        Float scale = (Float) animator.getAnimatedValue();
        view.setScaleX(scale.floatValue());
        view.setScaleY(scale.floatValue());
    }

    public static MultiViewUpdateListener translationXListener(View... views) {
        return new MultiViewUpdateListener(new MultiViewUpdateListener$$ExternalSyntheticLambda0(), views);
    }

    public static MultiViewUpdateListener translationXListener(Collection<View> views) {
        return new MultiViewUpdateListener(new MultiViewUpdateListener$$ExternalSyntheticLambda0(), views);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setTranslationX(ValueAnimator animator, View view) {
        view.setTranslationX(((Float) animator.getAnimatedValue()).floatValue());
    }

    public static MultiViewUpdateListener translationYListener(View... views) {
        return new MultiViewUpdateListener(new MultiViewUpdateListener$$ExternalSyntheticLambda2(), views);
    }

    public static MultiViewUpdateListener translationYListener(Collection<View> views) {
        return new MultiViewUpdateListener(new MultiViewUpdateListener$$ExternalSyntheticLambda2(), views);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setTranslationY(ValueAnimator animator, View view) {
        view.setTranslationY(((Float) animator.getAnimatedValue()).floatValue());
    }
}
