package com.bumptech.glide;

import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.request.transition.NoTransition;
import com.bumptech.glide.request.transition.TransitionFactory;
import com.bumptech.glide.request.transition.ViewAnimationFactory;
import com.bumptech.glide.request.transition.ViewPropertyAnimationFactory;
import com.bumptech.glide.request.transition.ViewPropertyTransition;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;

/* loaded from: classes.dex */
public abstract class TransitionOptions<CHILD extends TransitionOptions<CHILD, TranscodeType>, TranscodeType> implements Cloneable {
    private TransitionFactory<? super TranscodeType> transitionFactory = NoTransition.getFactory();

    public final CHILD dontTransition() {
        return (CHILD) transition(NoTransition.getFactory());
    }

    public final CHILD transition(int i) {
        return (CHILD) transition(new ViewAnimationFactory(i));
    }

    public final CHILD transition(ViewPropertyTransition.Animator animator) {
        return (CHILD) transition(new ViewPropertyAnimationFactory(animator));
    }

    public final CHILD transition(TransitionFactory<? super TranscodeType> transitionFactory) {
        this.transitionFactory = (TransitionFactory) Preconditions.checkNotNull(transitionFactory);
        return (CHILD) self();
    }

    /* renamed from: clone, reason: merged with bridge method [inline-methods] */
    public final CHILD m45clone() {
        try {
            return (CHILD) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    final TransitionFactory<? super TranscodeType> getTransitionFactory() {
        return this.transitionFactory;
    }

    private CHILD self() {
        return this;
    }

    public boolean equals(Object o) {
        if (o instanceof TransitionOptions) {
            TransitionOptions<?, ?> other = (TransitionOptions) o;
            return Util.bothNullOrEqual(this.transitionFactory, other.transitionFactory);
        }
        return false;
    }

    public int hashCode() {
        if (this.transitionFactory != null) {
            return this.transitionFactory.hashCode();
        }
        return 0;
    }
}
