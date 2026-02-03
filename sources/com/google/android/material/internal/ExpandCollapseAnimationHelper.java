package com.google.android.material.internal;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.view.View;
import com.google.android.material.animation.AnimationUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class ExpandCollapseAnimationHelper {
    private ValueAnimator.AnimatorUpdateListener additionalUpdateListener;
    private final View collapsedView;
    private int collapsedViewOffsetY;
    private long duration;
    private final View expandedView;
    private int expandedViewOffsetY;
    private final List<AnimatorListenerAdapter> listeners = new ArrayList();
    private final List<View> endAnchoredViews = new ArrayList();

    public ExpandCollapseAnimationHelper(View collapsedView, View expandedView) {
        this.collapsedView = collapsedView;
        this.expandedView = expandedView;
    }

    public Animator getExpandAnimator() {
        Animator animator = getAnimatorSet(true);
        animator.addListener(new AnimatorListenerAdapter() { // from class: com.google.android.material.internal.ExpandCollapseAnimationHelper.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                ExpandCollapseAnimationHelper.this.expandedView.setVisibility(0);
            }
        });
        addListeners(animator, this.listeners);
        return animator;
    }

    public Animator getCollapseAnimator() {
        Animator animator = getAnimatorSet(false);
        animator.addListener(new AnimatorListenerAdapter() { // from class: com.google.android.material.internal.ExpandCollapseAnimationHelper.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                ExpandCollapseAnimationHelper.this.expandedView.setVisibility(8);
            }
        });
        addListeners(animator, this.listeners);
        return animator;
    }

    public ExpandCollapseAnimationHelper setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public ExpandCollapseAnimationHelper addListener(AnimatorListenerAdapter listener) {
        this.listeners.add(listener);
        return this;
    }

    public ExpandCollapseAnimationHelper addEndAnchoredViews(View... views) {
        Collections.addAll(this.endAnchoredViews, views);
        return this;
    }

    public ExpandCollapseAnimationHelper addEndAnchoredViews(Collection<View> views) {
        this.endAnchoredViews.addAll(views);
        return this;
    }

    public ExpandCollapseAnimationHelper setAdditionalUpdateListener(ValueAnimator.AnimatorUpdateListener additionalUpdateListener) {
        this.additionalUpdateListener = additionalUpdateListener;
        return this;
    }

    public ExpandCollapseAnimationHelper setCollapsedViewOffsetY(int collapsedViewOffsetY) {
        this.collapsedViewOffsetY = collapsedViewOffsetY;
        return this;
    }

    public ExpandCollapseAnimationHelper setExpandedViewOffsetY(int expandedViewOffsetY) {
        this.expandedViewOffsetY = expandedViewOffsetY;
        return this;
    }

    private AnimatorSet getAnimatorSet(boolean expand) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(getExpandCollapseAnimator(expand), getExpandedViewChildrenAlphaAnimator(expand), getEndAnchoredViewsTranslateAnimator(expand));
        return animatorSet;
    }

    private Animator getExpandCollapseAnimator(boolean expand) {
        Rect fromBounds = ViewUtils.calculateRectFromBounds(this.collapsedView, this.collapsedViewOffsetY);
        Rect toBounds = ViewUtils.calculateRectFromBounds(this.expandedView, this.expandedViewOffsetY);
        final Rect bounds = new Rect(fromBounds);
        ValueAnimator animator = ValueAnimator.ofObject(new RectEvaluator(bounds), fromBounds, toBounds);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.google.android.material.internal.ExpandCollapseAnimationHelper$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.m58xeb41e2ac(bounds, valueAnimator);
            }
        });
        if (this.additionalUpdateListener != null) {
            animator.addUpdateListener(this.additionalUpdateListener);
        }
        animator.setDuration(this.duration);
        animator.setInterpolator(ReversableAnimatedValueInterpolator.of(expand, AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR));
        return animator;
    }

    /* renamed from: lambda$getExpandCollapseAnimator$0$com-google-android-material-internal-ExpandCollapseAnimationHelper, reason: not valid java name */
    /* synthetic */ void m58xeb41e2ac(Rect bounds, ValueAnimator valueAnimator) {
        ViewUtils.setBoundsFromRect(this.expandedView, bounds);
    }

    private Animator getExpandedViewChildrenAlphaAnimator(boolean expand) {
        List<View> expandedViewChildren = ViewUtils.getChildren(this.expandedView);
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.addUpdateListener(MultiViewUpdateListener.alphaListener(expandedViewChildren));
        animator.setDuration(this.duration);
        animator.setInterpolator(ReversableAnimatedValueInterpolator.of(expand, AnimationUtils.LINEAR_INTERPOLATOR));
        return animator;
    }

    private Animator getEndAnchoredViewsTranslateAnimator(boolean expand) {
        int leftDelta = this.expandedView.getLeft() - this.collapsedView.getLeft();
        int rightDelta = this.collapsedView.getRight() - this.expandedView.getRight();
        int fromTranslationX = leftDelta + rightDelta;
        ValueAnimator animator = ValueAnimator.ofFloat(fromTranslationX, 0.0f);
        animator.addUpdateListener(MultiViewUpdateListener.translationXListener(this.endAnchoredViews));
        animator.setDuration(this.duration);
        animator.setInterpolator(ReversableAnimatedValueInterpolator.of(expand, AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR));
        return animator;
    }

    private void addListeners(Animator animator, List<AnimatorListenerAdapter> listeners) {
        for (AnimatorListenerAdapter listener : listeners) {
            animator.addListener(listener);
        }
    }
}
