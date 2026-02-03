package com.google.android.material.color.utilities;

/* loaded from: classes.dex */
public final class DislikeAnalyzer {
    private DislikeAnalyzer() {
        throw new UnsupportedOperationException();
    }

    public static boolean isDisliked(Hct hct) {
        boolean huePasses = ((double) Math.round(hct.getHue())) >= 90.0d && ((double) Math.round(hct.getHue())) <= 111.0d;
        boolean chromaPasses = ((double) Math.round(hct.getChroma())) > 16.0d;
        boolean tonePasses = ((double) Math.round(hct.getTone())) < 70.0d;
        return huePasses && chromaPasses && tonePasses;
    }

    public static Hct fixIfDisliked(Hct hct) {
        if (isDisliked(hct)) {
            return Hct.from(hct.getHue(), hct.getChroma(), 70.0d);
        }
        return hct;
    }
}
