package com.google.android.material.color.utilities;

/* loaded from: classes.dex */
public final class ToneDeltaConstraint {
    public final double delta;
    public final DynamicColor keepAway;
    public final TonePolarity keepAwayPolarity;

    public ToneDeltaConstraint(double delta, DynamicColor keepAway, TonePolarity keepAwayPolarity) {
        this.delta = delta;
        this.keepAway = keepAway;
        this.keepAwayPolarity = keepAwayPolarity;
    }
}
