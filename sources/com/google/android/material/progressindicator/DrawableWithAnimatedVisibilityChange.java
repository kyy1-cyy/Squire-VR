package com.google.android.material.progressindicator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Property;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import com.google.android.material.animation.AnimationUtils;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
abstract class DrawableWithAnimatedVisibilityChange extends Drawable implements Animatable2Compat {
    private static final boolean DEFAULT_DRAWABLE_RESTART = false;
    private static final int GROW_DURATION = 500;
    private static final Property<DrawableWithAnimatedVisibilityChange, Float> GROW_FRACTION = new Property<DrawableWithAnimatedVisibilityChange, Float>(Float.class, "growFraction") { // from class: com.google.android.material.progressindicator.DrawableWithAnimatedVisibilityChange.3
        @Override // android.util.Property
        public Float get(DrawableWithAnimatedVisibilityChange drawable) {
            return Float.valueOf(drawable.getGrowFraction());
        }

        @Override // android.util.Property
        public void set(DrawableWithAnimatedVisibilityChange drawable, Float value) {
            drawable.setGrowFraction(value.floatValue());
        }
    };
    private List<Animatable2Compat.AnimationCallback> animationCallbacks;
    final BaseProgressIndicatorSpec baseSpec;
    final Context context;
    private float growFraction;
    private ValueAnimator hideAnimator;
    private boolean ignoreCallbacks;
    private Animatable2Compat.AnimationCallback internalAnimationCallback;
    private float mockGrowFraction;
    private boolean mockHideAnimationRunning;
    private boolean mockShowAnimationRunning;
    private ValueAnimator showAnimator;
    private int totalAlpha;
    final Paint paint = new Paint();
    AnimatorDurationScaleProvider animatorDurationScaleProvider = new AnimatorDurationScaleProvider();

    DrawableWithAnimatedVisibilityChange(Context context, BaseProgressIndicatorSpec baseSpec) {
        this.context = context;
        this.baseSpec = baseSpec;
        setAlpha(255);
    }

    private void maybeInitializeAnimators() {
        if (this.showAnimator == null) {
            this.showAnimator = ObjectAnimator.ofFloat(this, GROW_FRACTION, 0.0f, 1.0f);
            this.showAnimator.setDuration(500L);
            this.showAnimator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
            setShowAnimator(this.showAnimator);
        }
        if (this.hideAnimator == null) {
            this.hideAnimator = ObjectAnimator.ofFloat(this, GROW_FRACTION, 1.0f, 0.0f);
            this.hideAnimator.setDuration(500L);
            this.hideAnimator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
            setHideAnimator(this.hideAnimator);
        }
    }

    public void registerAnimationCallback(Animatable2Compat.AnimationCallback callback) {
        if (this.animationCallbacks == null) {
            this.animationCallbacks = new ArrayList();
        }
        if (!this.animationCallbacks.contains(callback)) {
            this.animationCallbacks.add(callback);
        }
    }

    public boolean unregisterAnimationCallback(Animatable2Compat.AnimationCallback callback) {
        if (this.animationCallbacks != null && this.animationCallbacks.contains(callback)) {
            this.animationCallbacks.remove(callback);
            if (this.animationCallbacks.isEmpty()) {
                this.animationCallbacks = null;
                return true;
            }
            return true;
        }
        return false;
    }

    public void clearAnimationCallbacks() {
        this.animationCallbacks.clear();
        this.animationCallbacks = null;
    }

    void setInternalAnimationCallback(Animatable2Compat.AnimationCallback callback) {
        this.internalAnimationCallback = callback;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchAnimationStart() {
        if (this.internalAnimationCallback != null) {
            this.internalAnimationCallback.onAnimationStart(this);
        }
        if (this.animationCallbacks != null && !this.ignoreCallbacks) {
            for (Animatable2Compat.AnimationCallback callback : this.animationCallbacks) {
                callback.onAnimationStart(this);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchAnimationEnd() {
        if (this.internalAnimationCallback != null) {
            this.internalAnimationCallback.onAnimationEnd(this);
        }
        if (this.animationCallbacks != null && !this.ignoreCallbacks) {
            for (Animatable2Compat.AnimationCallback callback : this.animationCallbacks) {
                callback.onAnimationEnd(this);
            }
        }
    }

    public void start() {
        setVisibleInternal(true, true, false);
    }

    public void stop() {
        setVisibleInternal(false, true, false);
    }

    public boolean isRunning() {
        return isShowing() || isHiding();
    }

    public boolean isShowing() {
        return (this.showAnimator != null && this.showAnimator.isRunning()) || this.mockShowAnimationRunning;
    }

    public boolean isHiding() {
        return (this.hideAnimator != null && this.hideAnimator.isRunning()) || this.mockHideAnimationRunning;
    }

    public boolean hideNow() {
        return setVisible(false, false, false);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean visible, boolean restart) {
        return setVisible(visible, restart, true);
    }

    public boolean setVisible(boolean visible, boolean restart, boolean animate) {
        float systemAnimatorDurationScale = this.animatorDurationScaleProvider.getSystemAnimatorDurationScale(this.context.getContentResolver());
        return setVisibleInternal(visible, restart, animate && systemAnimatorDurationScale > 0.0f);
    }

    boolean setVisibleInternal(boolean visible, boolean restart, boolean animate) {
        maybeInitializeAnimators();
        if (!isVisible() && !visible) {
            return false;
        }
        ValueAnimator animatorInAction = visible ? this.showAnimator : this.hideAnimator;
        ValueAnimator animatorNotInAction = visible ? this.hideAnimator : this.showAnimator;
        if (!animate) {
            if (animatorNotInAction.isRunning()) {
                cancelAnimatorsWithoutCallbacks(animatorNotInAction);
            }
            if (animatorInAction.isRunning()) {
                animatorInAction.end();
            } else {
                endAnimatorsWithoutCallbacks(animatorInAction);
            }
            return super.setVisible(visible, false);
        }
        if (animate && animatorInAction.isRunning()) {
            return false;
        }
        boolean changed = !visible || super.setVisible(visible, false);
        BaseProgressIndicatorSpec baseProgressIndicatorSpec = this.baseSpec;
        boolean specAnimationEnabled = visible ? baseProgressIndicatorSpec.isShowAnimationEnabled() : baseProgressIndicatorSpec.isHideAnimationEnabled();
        if (!specAnimationEnabled) {
            endAnimatorsWithoutCallbacks(animatorInAction);
            return changed;
        }
        if (restart || !animatorInAction.isPaused()) {
            animatorInAction.start();
        } else {
            animatorInAction.resume();
        }
        return changed;
    }

    private void cancelAnimatorsWithoutCallbacks(ValueAnimator... animators) {
        boolean ignoreCallbacksOrig = this.ignoreCallbacks;
        this.ignoreCallbacks = true;
        for (ValueAnimator animator : animators) {
            animator.cancel();
        }
        this.ignoreCallbacks = ignoreCallbacksOrig;
    }

    private void endAnimatorsWithoutCallbacks(ValueAnimator... animators) {
        boolean ignoreCallbacksOrig = this.ignoreCallbacks;
        this.ignoreCallbacks = true;
        for (ValueAnimator animator : animators) {
            animator.end();
        }
        this.ignoreCallbacks = ignoreCallbacksOrig;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.totalAlpha = alpha;
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.totalAlpha;
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    private void setShowAnimator(ValueAnimator showAnimator) {
        if (this.showAnimator != null && this.showAnimator.isRunning()) {
            throw new IllegalArgumentException("Cannot set showAnimator while the current showAnimator is running.");
        }
        this.showAnimator = showAnimator;
        showAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.google.android.material.progressindicator.DrawableWithAnimatedVisibilityChange.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                DrawableWithAnimatedVisibilityChange.this.dispatchAnimationStart();
            }
        });
    }

    ValueAnimator getHideAnimator() {
        return this.hideAnimator;
    }

    private void setHideAnimator(ValueAnimator hideAnimator) {
        if (this.hideAnimator != null && this.hideAnimator.isRunning()) {
            throw new IllegalArgumentException("Cannot set hideAnimator while the current hideAnimator is running.");
        }
        this.hideAnimator = hideAnimator;
        hideAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.google.android.material.progressindicator.DrawableWithAnimatedVisibilityChange.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                DrawableWithAnimatedVisibilityChange.super.setVisible(false, false);
                DrawableWithAnimatedVisibilityChange.this.dispatchAnimationEnd();
            }
        });
    }

    float getGrowFraction() {
        if (!this.baseSpec.isShowAnimationEnabled() && !this.baseSpec.isHideAnimationEnabled()) {
            return 1.0f;
        }
        if (this.mockHideAnimationRunning || this.mockShowAnimationRunning) {
            return this.mockGrowFraction;
        }
        return this.growFraction;
    }

    void setGrowFraction(float growFraction) {
        if (this.growFraction != growFraction) {
            this.growFraction = growFraction;
            invalidateSelf();
        }
    }

    void setMockShowAnimationRunning(boolean running, float fraction) {
        this.mockShowAnimationRunning = running;
        this.mockGrowFraction = fraction;
    }

    void setMockHideAnimationRunning(boolean running, float fraction) {
        this.mockHideAnimationRunning = running;
        this.mockGrowFraction = fraction;
    }
}
