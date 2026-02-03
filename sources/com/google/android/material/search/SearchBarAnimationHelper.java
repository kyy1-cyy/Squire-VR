package com.google.android.material.search;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.widget.ActionMenuView;
import androidx.core.view.ViewCompat;
import com.google.android.material.animation.AnimatableView;
import com.google.android.material.animation.AnimationUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.internal.ExpandCollapseAnimationHelper;
import com.google.android.material.internal.MultiViewUpdateListener;
import com.google.android.material.internal.ToolbarUtils;
import com.google.android.material.internal.ViewUtils;
import com.google.android.material.search.SearchBar;
import com.google.android.material.shape.MaterialShapeDrawable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/* loaded from: classes.dex */
class SearchBarAnimationHelper {
    private static final long COLLAPSE_DURATION_MS = 250;
    private static final long COLLAPSE_FADE_IN_CHILDREN_DURATION_MS = 100;
    private static final long EXPAND_DURATION_MS = 300;
    private static final long EXPAND_FADE_OUT_CHILDREN_DURATION_MS = 75;
    private static final long ON_LOAD_ANIM_CENTER_VIEW_DEFAULT_FADE_DURATION_MS = 250;
    private static final long ON_LOAD_ANIM_CENTER_VIEW_DEFAULT_FADE_IN_START_DELAY_MS = 500;
    private static final long ON_LOAD_ANIM_CENTER_VIEW_DEFAULT_FADE_OUT_START_DELAY_MS = 750;
    private static final long ON_LOAD_ANIM_SECONDARY_DURATION_MS = 250;
    private static final long ON_LOAD_ANIM_SECONDARY_START_DELAY_MS = 250;
    private boolean collapsing;
    private Animator defaultCenterViewAnimator;
    private boolean expanding;
    private Animator secondaryViewAnimator;
    private final Set<SearchBar.OnLoadAnimationCallback> onLoadAnimationCallbacks = new LinkedHashSet();
    private final Set<AnimatorListenerAdapter> expandAnimationListeners = new LinkedHashSet();
    private final Set<AnimatorListenerAdapter> collapseAnimationListeners = new LinkedHashSet();
    private boolean onLoadAnimationFadeInEnabled = true;
    private Animator runningExpandOrCollapseAnimator = null;

    /* JADX INFO: Access modifiers changed from: private */
    interface OnLoadAnimationInvocation {
        void invoke(SearchBar.OnLoadAnimationCallback onLoadAnimationCallback);
    }

    SearchBarAnimationHelper() {
    }

    /* JADX WARN: Multi-variable type inference failed */
    void startOnLoadAnimation(SearchBar searchBar) {
        dispatchOnLoadAnimation(new OnLoadAnimationInvocation() { // from class: com.google.android.material.search.SearchBarAnimationHelper$$ExternalSyntheticLambda3
            @Override // com.google.android.material.search.SearchBarAnimationHelper.OnLoadAnimationInvocation
            public final void invoke(SearchBar.OnLoadAnimationCallback onLoadAnimationCallback) {
                onLoadAnimationCallback.onAnimationStart();
            }
        });
        TextView textView = searchBar.getTextView();
        final View centerView = searchBar.getCenterView();
        View secondaryActionMenuItemView = ToolbarUtils.getSecondaryActionMenuItemView(searchBar);
        final Animator secondaryViewAnimator = getSecondaryViewAnimator(textView, secondaryActionMenuItemView);
        secondaryViewAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.google.android.material.search.SearchBarAnimationHelper.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                SearchBarAnimationHelper.this.dispatchOnLoadAnimation(new OnLoadAnimationInvocation() { // from class: com.google.android.material.search.SearchBarAnimationHelper$1$$ExternalSyntheticLambda0
                    @Override // com.google.android.material.search.SearchBarAnimationHelper.OnLoadAnimationInvocation
                    public final void invoke(SearchBar.OnLoadAnimationCallback onLoadAnimationCallback) {
                        onLoadAnimationCallback.onAnimationEnd();
                    }
                });
            }
        });
        this.secondaryViewAnimator = secondaryViewAnimator;
        textView.setAlpha(0.0f);
        if (secondaryActionMenuItemView != null) {
            secondaryActionMenuItemView.setAlpha(0.0f);
        }
        if (centerView instanceof AnimatableView) {
            Objects.requireNonNull(secondaryViewAnimator);
            ((AnimatableView) centerView).startAnimation(new AnimatableView.Listener() { // from class: com.google.android.material.search.SearchBarAnimationHelper$$ExternalSyntheticLambda4
                @Override // com.google.android.material.animation.AnimatableView.Listener
                public final void onAnimationEnd() {
                    secondaryViewAnimator.start();
                }
            });
        } else {
            if (centerView != 0) {
                centerView.setAlpha(0.0f);
                centerView.setVisibility(0);
                Animator defaultCenterViewAnimator = getDefaultCenterViewAnimator(centerView);
                this.defaultCenterViewAnimator = defaultCenterViewAnimator;
                defaultCenterViewAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.google.android.material.search.SearchBarAnimationHelper.2
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        centerView.setVisibility(8);
                        secondaryViewAnimator.start();
                    }
                });
                defaultCenterViewAnimator.start();
                return;
            }
            secondaryViewAnimator.start();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    void stopOnLoadAnimation(SearchBar searchBar) {
        if (this.secondaryViewAnimator != null) {
            this.secondaryViewAnimator.end();
        }
        if (this.defaultCenterViewAnimator != null) {
            this.defaultCenterViewAnimator.end();
        }
        View centerView = searchBar.getCenterView();
        if (centerView instanceof AnimatableView) {
            ((AnimatableView) centerView).stopAnimation();
        }
        if (centerView != 0) {
            centerView.setAlpha(0.0f);
        }
    }

    boolean isOnLoadAnimationFadeInEnabled() {
        return this.onLoadAnimationFadeInEnabled;
    }

    void setOnLoadAnimationFadeInEnabled(boolean onLoadAnimationFadeInEnabled) {
        this.onLoadAnimationFadeInEnabled = onLoadAnimationFadeInEnabled;
    }

    void addOnLoadAnimationCallback(SearchBar.OnLoadAnimationCallback onLoadAnimationCallback) {
        this.onLoadAnimationCallbacks.add(onLoadAnimationCallback);
    }

    boolean removeOnLoadAnimationCallback(SearchBar.OnLoadAnimationCallback onLoadAnimationCallback) {
        return this.onLoadAnimationCallbacks.remove(onLoadAnimationCallback);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchOnLoadAnimation(OnLoadAnimationInvocation invocation) {
        for (SearchBar.OnLoadAnimationCallback onLoadAnimationCallback : this.onLoadAnimationCallbacks) {
            invocation.invoke(onLoadAnimationCallback);
        }
    }

    private Animator getDefaultCenterViewAnimator(View centerView) {
        ValueAnimator fadeInAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        fadeInAnimator.addUpdateListener(MultiViewUpdateListener.alphaListener(centerView));
        fadeInAnimator.setInterpolator(AnimationUtils.LINEAR_INTERPOLATOR);
        fadeInAnimator.setDuration(this.onLoadAnimationFadeInEnabled ? 250L : 0L);
        fadeInAnimator.setStartDelay(this.onLoadAnimationFadeInEnabled ? ON_LOAD_ANIM_CENTER_VIEW_DEFAULT_FADE_IN_START_DELAY_MS : 0L);
        ValueAnimator fadeOutAnimator = ValueAnimator.ofFloat(1.0f, 0.0f);
        fadeOutAnimator.addUpdateListener(MultiViewUpdateListener.alphaListener(centerView));
        fadeOutAnimator.setInterpolator(AnimationUtils.LINEAR_INTERPOLATOR);
        fadeOutAnimator.setDuration(250L);
        fadeOutAnimator.setStartDelay(ON_LOAD_ANIM_CENTER_VIEW_DEFAULT_FADE_OUT_START_DELAY_MS);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(fadeInAnimator, fadeOutAnimator);
        return animatorSet;
    }

    private Animator getSecondaryViewAnimator(TextView textView, View secondaryActionMenuItemView) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setStartDelay(250L);
        animatorSet.play(getTextViewAnimator(textView));
        if (secondaryActionMenuItemView != null) {
            animatorSet.play(getSecondaryActionMenuItemAnimator(secondaryActionMenuItemView));
        }
        return animatorSet;
    }

    private Animator getTextViewAnimator(TextView textView) {
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.addUpdateListener(MultiViewUpdateListener.alphaListener(textView));
        animator.setInterpolator(AnimationUtils.LINEAR_INTERPOLATOR);
        animator.setDuration(250L);
        return animator;
    }

    private Animator getSecondaryActionMenuItemAnimator(View secondaryActionMenuItemView) {
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.addUpdateListener(MultiViewUpdateListener.alphaListener(secondaryActionMenuItemView));
        animator.setInterpolator(AnimationUtils.LINEAR_INTERPOLATOR);
        animator.setDuration(250L);
        return animator;
    }

    void startExpandAnimation(final SearchBar searchBar, final View expandedView, final AppBarLayout appBarLayout, final boolean skipAnimation) {
        if (isCollapsing() && this.runningExpandOrCollapseAnimator != null) {
            this.runningExpandOrCollapseAnimator.cancel();
        }
        this.expanding = true;
        expandedView.setVisibility(4);
        expandedView.post(new Runnable() { // from class: com.google.android.material.search.SearchBarAnimationHelper$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m62x1b96b119(searchBar, expandedView, appBarLayout, skipAnimation);
            }
        });
    }

    /* renamed from: lambda$startExpandAnimation$0$com-google-android-material-search-SearchBarAnimationHelper, reason: not valid java name */
    /* synthetic */ void m62x1b96b119(SearchBar searchBar, View expandedView, AppBarLayout appBarLayout, boolean skipAnimation) {
        AnimatorSet fadeAndExpandAnimatorSet = new AnimatorSet();
        Animator fadeOutChildrenAnimator = getFadeOutChildrenAnimator(searchBar, expandedView);
        Animator expandAnimator = getExpandAnimator(searchBar, expandedView, appBarLayout);
        fadeAndExpandAnimatorSet.playSequentially(fadeOutChildrenAnimator, expandAnimator);
        fadeAndExpandAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.google.android.material.search.SearchBarAnimationHelper.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                SearchBarAnimationHelper.this.runningExpandOrCollapseAnimator = null;
            }
        });
        for (AnimatorListenerAdapter listener : this.expandAnimationListeners) {
            fadeAndExpandAnimatorSet.addListener(listener);
        }
        if (skipAnimation) {
            fadeAndExpandAnimatorSet.setDuration(0L);
        }
        fadeAndExpandAnimatorSet.start();
        this.runningExpandOrCollapseAnimator = fadeAndExpandAnimatorSet;
    }

    private Animator getExpandAnimator(final SearchBar searchBar, View expandedView, AppBarLayout appBarLayout) {
        return getExpandCollapseAnimationHelper(searchBar, expandedView, appBarLayout).setDuration(EXPAND_DURATION_MS).addListener(new AnimatorListenerAdapter() { // from class: com.google.android.material.search.SearchBarAnimationHelper.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                searchBar.setVisibility(4);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                SearchBarAnimationHelper.this.expanding = false;
            }
        }).getExpandAnimator();
    }

    boolean isExpanding() {
        return this.expanding;
    }

    void addExpandAnimationListener(AnimatorListenerAdapter listener) {
        this.expandAnimationListeners.add(listener);
    }

    boolean removeExpandAnimationListener(AnimatorListenerAdapter listener) {
        return this.expandAnimationListeners.remove(listener);
    }

    void startCollapseAnimation(SearchBar searchBar, View expandedView, AppBarLayout appBarLayout, boolean skipAnimation) {
        if (isExpanding() && this.runningExpandOrCollapseAnimator != null) {
            this.runningExpandOrCollapseAnimator.cancel();
        }
        this.collapsing = true;
        AnimatorSet collapseAndFadeAnimatorSet = new AnimatorSet();
        Animator collapseAnimator = getCollapseAnimator(searchBar, expandedView, appBarLayout);
        Animator fadeInChildrenAnimator = getFadeInChildrenAnimator(searchBar);
        collapseAndFadeAnimatorSet.playSequentially(collapseAnimator, fadeInChildrenAnimator);
        collapseAndFadeAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.google.android.material.search.SearchBarAnimationHelper.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                SearchBarAnimationHelper.this.runningExpandOrCollapseAnimator = null;
            }
        });
        for (AnimatorListenerAdapter listener : this.collapseAnimationListeners) {
            collapseAndFadeAnimatorSet.addListener(listener);
        }
        if (skipAnimation) {
            collapseAndFadeAnimatorSet.setDuration(0L);
        }
        collapseAndFadeAnimatorSet.start();
        this.runningExpandOrCollapseAnimator = collapseAndFadeAnimatorSet;
    }

    private Animator getCollapseAnimator(final SearchBar searchBar, View expandedView, AppBarLayout appBarLayout) {
        return getExpandCollapseAnimationHelper(searchBar, expandedView, appBarLayout).setDuration(250L).addListener(new AnimatorListenerAdapter() { // from class: com.google.android.material.search.SearchBarAnimationHelper.6
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                searchBar.stopOnLoadAnimation();
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                searchBar.setVisibility(0);
                SearchBarAnimationHelper.this.collapsing = false;
            }
        }).getCollapseAnimator();
    }

    boolean isCollapsing() {
        return this.collapsing;
    }

    void addCollapseAnimationListener(AnimatorListenerAdapter listener) {
        this.collapseAnimationListeners.add(listener);
    }

    boolean removeCollapseAnimationListener(AnimatorListenerAdapter listener) {
        return this.collapseAnimationListeners.remove(listener);
    }

    private ExpandCollapseAnimationHelper getExpandCollapseAnimationHelper(SearchBar searchBar, View expandedView, AppBarLayout appBarLayout) {
        return new ExpandCollapseAnimationHelper(searchBar, expandedView).setAdditionalUpdateListener(getExpandedViewBackgroundUpdateListener(searchBar, expandedView)).setCollapsedViewOffsetY(appBarLayout != null ? appBarLayout.getTop() : 0).addEndAnchoredViews(getEndAnchoredViews(expandedView));
    }

    private ValueAnimator.AnimatorUpdateListener getExpandedViewBackgroundUpdateListener(SearchBar searchBar, final View expandedView) {
        final MaterialShapeDrawable expandedViewBackground = MaterialShapeDrawable.createWithElevationOverlay(expandedView.getContext());
        expandedViewBackground.setCornerSize(searchBar.getCornerSize());
        expandedViewBackground.setElevation(ViewCompat.getElevation(searchBar));
        return new ValueAnimator.AnimatorUpdateListener() { // from class: com.google.android.material.search.SearchBarAnimationHelper$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                SearchBarAnimationHelper.lambda$getExpandedViewBackgroundUpdateListener$1(expandedViewBackground, expandedView, valueAnimator);
            }
        };
    }

    static /* synthetic */ void lambda$getExpandedViewBackgroundUpdateListener$1(MaterialShapeDrawable expandedViewBackground, View expandedView, ValueAnimator valueAnimator) {
        expandedViewBackground.setInterpolation(1.0f - valueAnimator.getAnimatedFraction());
        ViewCompat.setBackground(expandedView, expandedViewBackground);
        expandedView.setAlpha(1.0f);
    }

    private Animator getFadeOutChildrenAnimator(SearchBar searchBar, final View expandedView) {
        List<View> children = getFadeChildren(searchBar);
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, 0.0f);
        animator.addUpdateListener(MultiViewUpdateListener.alphaListener(children));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.google.android.material.search.SearchBarAnimationHelper$$ExternalSyntheticLambda2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                expandedView.setAlpha(0.0f);
            }
        });
        animator.setDuration(75L);
        animator.setInterpolator(AnimationUtils.LINEAR_INTERPOLATOR);
        return animator;
    }

    private Animator getFadeInChildrenAnimator(SearchBar searchBar) {
        List<View> children = getFadeChildren(searchBar);
        ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.addUpdateListener(MultiViewUpdateListener.alphaListener(children));
        animator.setDuration(COLLAPSE_FADE_IN_CHILDREN_DURATION_MS);
        animator.setInterpolator(AnimationUtils.LINEAR_INTERPOLATOR);
        return animator;
    }

    private List<View> getFadeChildren(SearchBar searchBar) {
        List<View> children = ViewUtils.getChildren(searchBar);
        if (searchBar.getCenterView() != null) {
            children.remove(searchBar.getCenterView());
        }
        return children;
    }

    private List<View> getEndAnchoredViews(View expandedView) {
        boolean isRtl = ViewUtils.isLayoutRtl(expandedView);
        List<View> endAnchoredViews = new ArrayList<>();
        if (expandedView instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) expandedView;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                if ((!isRtl && (child instanceof ActionMenuView)) || (isRtl && !(child instanceof ActionMenuView))) {
                    endAnchoredViews.add(child);
                }
            }
        }
        return endAnchoredViews;
    }
}
