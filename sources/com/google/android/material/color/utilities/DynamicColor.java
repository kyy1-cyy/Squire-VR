package com.google.android.material.color.utilities;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/* loaded from: classes.dex */
public final class DynamicColor {
    public final Function<DynamicScheme, DynamicColor> background;
    public final Function<DynamicScheme, Double> chroma;
    private final HashMap<DynamicScheme, Hct> hctCache = new HashMap<>();
    public final Function<DynamicScheme, Double> hue;
    public final Function<DynamicScheme, Double> opacity;
    public final Function<DynamicScheme, Double> tone;
    public final Function<DynamicScheme, ToneDeltaConstraint> toneDeltaConstraint;
    public final Function<DynamicScheme, Double> toneMaxContrast;
    public final Function<DynamicScheme, Double> toneMinContrast;

    public DynamicColor(Function<DynamicScheme, Double> hue, Function<DynamicScheme, Double> chroma, Function<DynamicScheme, Double> tone, Function<DynamicScheme, Double> opacity, Function<DynamicScheme, DynamicColor> background, Function<DynamicScheme, Double> toneMinContrast, Function<DynamicScheme, Double> toneMaxContrast, Function<DynamicScheme, ToneDeltaConstraint> toneDeltaConstraint) {
        this.hue = hue;
        this.chroma = chroma;
        this.tone = tone;
        this.opacity = opacity;
        this.background = background;
        this.toneMinContrast = toneMinContrast;
        this.toneMaxContrast = toneMaxContrast;
        this.toneDeltaConstraint = toneDeltaConstraint;
    }

    public static DynamicColor fromArgb(int argb) {
        final Hct hct = Hct.fromInt(argb);
        final TonalPalette palette = TonalPalette.fromInt(argb);
        return fromPalette(new Function() { // from class: com.google.android.material.color.utilities.DynamicColor$$ExternalSyntheticLambda6
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return DynamicColor.lambda$fromArgb$0(palette, (DynamicScheme) obj);
            }
        }, new Function() { // from class: com.google.android.material.color.utilities.DynamicColor$$ExternalSyntheticLambda7
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Double.valueOf(hct.getTone());
            }
        });
    }

    static /* synthetic */ TonalPalette lambda$fromArgb$0(TonalPalette palette, DynamicScheme s) {
        return palette;
    }

    public static DynamicColor fromArgb(final int argb, Function<DynamicScheme, Double> tone) {
        return fromPalette(new Function() { // from class: com.google.android.material.color.utilities.DynamicColor$$ExternalSyntheticLambda17
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return TonalPalette.fromInt(argb);
            }
        }, tone);
    }

    public static DynamicColor fromArgb(final int argb, Function<DynamicScheme, Double> tone, Function<DynamicScheme, DynamicColor> background) {
        return fromPalette(new Function() { // from class: com.google.android.material.color.utilities.DynamicColor$$ExternalSyntheticLambda16
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return TonalPalette.fromInt(argb);
            }
        }, tone, background);
    }

    public static DynamicColor fromArgb(final int argb, Function<DynamicScheme, Double> tone, Function<DynamicScheme, DynamicColor> background, Function<DynamicScheme, ToneDeltaConstraint> toneDeltaConstraint) {
        return fromPalette(new Function() { // from class: com.google.android.material.color.utilities.DynamicColor$$ExternalSyntheticLambda5
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return TonalPalette.fromInt(argb);
            }
        }, tone, background, toneDeltaConstraint);
    }

    public static DynamicColor fromPalette(Function<DynamicScheme, TonalPalette> palette, Function<DynamicScheme, Double> tone) {
        return fromPalette(palette, tone, null, null);
    }

    public static DynamicColor fromPalette(Function<DynamicScheme, TonalPalette> palette, Function<DynamicScheme, Double> tone, Function<DynamicScheme, DynamicColor> background) {
        return fromPalette(palette, tone, background, null);
    }

    public static DynamicColor fromPalette(final Function<DynamicScheme, TonalPalette> palette, final Function<DynamicScheme, Double> tone, final Function<DynamicScheme, DynamicColor> background, final Function<DynamicScheme, ToneDeltaConstraint> toneDeltaConstraint) {
        return new DynamicColor(new Function() { // from class: com.google.android.material.color.utilities.DynamicColor$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Double.valueOf(((TonalPalette) palette.apply((DynamicScheme) obj)).getHue());
            }
        }, new Function() { // from class: com.google.android.material.color.utilities.DynamicColor$$ExternalSyntheticLambda10
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Double.valueOf(((TonalPalette) palette.apply((DynamicScheme) obj)).getChroma());
            }
        }, tone, null, background, new Function() { // from class: com.google.android.material.color.utilities.DynamicColor$$ExternalSyntheticLambda11
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Double.valueOf(DynamicColor.toneMinContrastDefault(tone, background, (DynamicScheme) obj, toneDeltaConstraint));
            }
        }, new Function() { // from class: com.google.android.material.color.utilities.DynamicColor$$ExternalSyntheticLambda12
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Double.valueOf(DynamicColor.toneMaxContrastDefault(tone, background, (DynamicScheme) obj, toneDeltaConstraint));
            }
        }, toneDeltaConstraint);
    }

    public int getArgb(DynamicScheme scheme) {
        int argb = getHct(scheme).toInt();
        if (this.opacity == null) {
            return argb;
        }
        double percentage = this.opacity.apply(scheme).doubleValue();
        int alpha = MathUtils.clampInt(0, 255, (int) Math.round(255.0d * percentage));
        return (16777215 & argb) | (alpha << 24);
    }

    public Hct getHct(DynamicScheme scheme) {
        Hct cachedAnswer = this.hctCache.get(scheme);
        if (cachedAnswer != null) {
            return cachedAnswer;
        }
        Hct answer = Hct.from(this.hue.apply(scheme).doubleValue(), this.chroma.apply(scheme).doubleValue(), getTone(scheme));
        if (this.hctCache.size() > 4) {
            this.hctCache.clear();
        }
        this.hctCache.put(scheme, answer);
        return answer;
    }

    public double getTone(final DynamicScheme scheme) {
        double endTone;
        double maxRatio;
        double minRatio;
        double answer = this.tone.apply(scheme).doubleValue();
        boolean decreasingContrast = scheme.contrastLevel < 0.0d;
        if (scheme.contrastLevel == 0.0d) {
            endTone = answer;
        } else {
            double startTone = this.tone.apply(scheme).doubleValue();
            double endTone2 = (decreasingContrast ? this.toneMinContrast : this.toneMaxContrast).apply(scheme).doubleValue();
            double delta = (endTone2 - startTone) * Math.abs(scheme.contrastLevel);
            double answer2 = delta + startTone;
            endTone = answer2;
        }
        final DynamicColor bgDynamicColor = this.background == null ? null : this.background.apply(scheme);
        if (bgDynamicColor == null) {
            maxRatio = 21.0d;
            minRatio = 1.0d;
        } else {
            boolean bgHasBg = (bgDynamicColor.background == null || bgDynamicColor.background.apply(scheme) == null) ? false : true;
            double standardRatio = Contrast.ratioOfTones(this.tone.apply(scheme).doubleValue(), bgDynamicColor.tone.apply(scheme).doubleValue());
            if (decreasingContrast) {
                double dDoubleValue = this.toneMinContrast.apply(scheme).doubleValue();
                double minRatio2 = bgDynamicColor.toneMinContrast.apply(scheme).doubleValue();
                double minContrastRatio = Contrast.ratioOfTones(dDoubleValue, minRatio2);
                minRatio = bgHasBg ? minContrastRatio : 1.0d;
                maxRatio = standardRatio;
            } else {
                double maxContrastRatio = Contrast.ratioOfTones(this.toneMaxContrast.apply(scheme).doubleValue(), bgDynamicColor.toneMaxContrast.apply(scheme).doubleValue());
                minRatio = bgHasBg ? Math.min(maxContrastRatio, standardRatio) : 1.0d;
                maxRatio = bgHasBg ? Math.max(maxContrastRatio, standardRatio) : 21.0d;
            }
        }
        final double finalMinRatio = minRatio;
        final double finalMaxRatio = maxRatio;
        final double finalAnswer = endTone;
        double answer3 = calculateDynamicTone(scheme, this.tone, new Function() { // from class: com.google.android.material.color.utilities.DynamicColor$$ExternalSyntheticLambda18
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Double.valueOf(((DynamicColor) obj).getTone(scheme));
            }
        }, new BiFunction() { // from class: com.google.android.material.color.utilities.DynamicColor$$ExternalSyntheticLambda1
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return Double.valueOf(finalAnswer);
            }
        }, new Function() { // from class: com.google.android.material.color.utilities.DynamicColor$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return DynamicColor.lambda$getTone$11(this.f$0, (DynamicScheme) obj);
            }
        }, this.toneDeltaConstraint, new Function() { // from class: com.google.android.material.color.utilities.DynamicColor$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Double.valueOf(finalMinRatio);
            }
        }, new Function() { // from class: com.google.android.material.color.utilities.DynamicColor$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Double.valueOf(finalMaxRatio);
            }
        });
        return answer3;
    }

    static /* synthetic */ DynamicColor lambda$getTone$11(DynamicColor bgDynamicColor, DynamicScheme s) {
        return bgDynamicColor;
    }

    public static double toneMinContrastDefault(final Function<DynamicScheme, Double> tone, final Function<DynamicScheme, DynamicColor> background, final DynamicScheme scheme, Function<DynamicScheme, ToneDeltaConstraint> toneDeltaConstraint) {
        return calculateDynamicTone(scheme, tone, new Function() { // from class: com.google.android.material.color.utilities.DynamicColor$$ExternalSyntheticLambda13
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((DynamicColor) obj).toneMinContrast.apply(scheme);
            }
        }, new BiFunction() { // from class: com.google.android.material.color.utilities.DynamicColor$$ExternalSyntheticLambda14
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return DynamicColor.lambda$toneMinContrastDefault$15(tone, scheme, background, (Double) obj, (Double) obj2);
            }
        }, background, toneDeltaConstraint, null, new Function() { // from class: com.google.android.material.color.utilities.DynamicColor$$ExternalSyntheticLambda15
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return DynamicColor.lambda$toneMinContrastDefault$16((Double) obj);
            }
        });
    }

    static /* synthetic */ Double lambda$toneMinContrastDefault$15(Function tone, DynamicScheme scheme, Function background, Double stdRatio, Double bgTone) {
        double answer = ((Double) tone.apply(scheme)).doubleValue();
        if (stdRatio.doubleValue() >= 7.0d) {
            answer = contrastingTone(bgTone.doubleValue(), 4.5d);
        } else if (stdRatio.doubleValue() >= 3.0d) {
            answer = contrastingTone(bgTone.doubleValue(), 3.0d);
        } else {
            boolean backgroundHasBackground = (background == null || background.apply(scheme) == null || ((DynamicColor) background.apply(scheme)).background == null || ((DynamicColor) background.apply(scheme)).background.apply(scheme) == null) ? false : true;
            if (backgroundHasBackground) {
                answer = contrastingTone(bgTone.doubleValue(), stdRatio.doubleValue());
            }
        }
        return Double.valueOf(answer);
    }

    static /* synthetic */ Double lambda$toneMinContrastDefault$16(Double standardRatio) {
        return standardRatio;
    }

    public static double toneMaxContrastDefault(Function<DynamicScheme, Double> tone, final Function<DynamicScheme, DynamicColor> background, final DynamicScheme scheme, Function<DynamicScheme, ToneDeltaConstraint> toneDeltaConstraint) {
        return calculateDynamicTone(scheme, tone, new Function() { // from class: com.google.android.material.color.utilities.DynamicColor$$ExternalSyntheticLambda8
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((DynamicColor) obj).toneMaxContrast.apply(scheme);
            }
        }, new BiFunction() { // from class: com.google.android.material.color.utilities.DynamicColor$$ExternalSyntheticLambda9
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return DynamicColor.lambda$toneMaxContrastDefault$18(background, scheme, (Double) obj, (Double) obj2);
            }
        }, background, toneDeltaConstraint, null, null);
    }

    static /* synthetic */ Double lambda$toneMaxContrastDefault$18(Function background, DynamicScheme scheme, Double stdRatio, Double bgTone) {
        boolean backgroundHasBackground = (background == null || background.apply(scheme) == null || ((DynamicColor) background.apply(scheme)).background == null || ((DynamicColor) background.apply(scheme)).background.apply(scheme) == null) ? false : true;
        return backgroundHasBackground ? Double.valueOf(contrastingTone(bgTone.doubleValue(), 7.0d)) : Double.valueOf(contrastingTone(bgTone.doubleValue(), Math.max(7.0d, stdRatio.doubleValue())));
    }

    public static double calculateDynamicTone(DynamicScheme scheme, Function<DynamicScheme, Double> toneStandard, Function<DynamicColor, Double> toneToJudge, BiFunction<Double, Double, Double> desiredTone, Function<DynamicScheme, DynamicColor> background, Function<DynamicScheme, ToneDeltaConstraint> toneDeltaConstraint, Function<Double, Double> minRatio, Function<Double, Double> maxRatio) {
        double answer;
        double toneStd = toneStandard.apply(scheme).doubleValue();
        DynamicColor bgDynamic = background == null ? null : background.apply(scheme);
        if (bgDynamic != null) {
            double bgToneStd = bgDynamic.tone.apply(scheme).doubleValue();
            double stdRatio = Contrast.ratioOfTones(toneStd, bgToneStd);
            double bgTone = toneToJudge.apply(bgDynamic).doubleValue();
            double myDesiredTone = desiredTone.apply(Double.valueOf(stdRatio), Double.valueOf(bgTone)).doubleValue();
            double currentRatio = Contrast.ratioOfTones(bgTone, myDesiredTone);
            double minRatioRealized = 1.0d;
            if (minRatio != null && minRatio.apply(Double.valueOf(stdRatio)) != null) {
                minRatioRealized = minRatio.apply(Double.valueOf(stdRatio)).doubleValue();
            }
            double maxRatioRealized = 21.0d;
            if (maxRatio != null && maxRatio.apply(Double.valueOf(stdRatio)) != null) {
                maxRatioRealized = maxRatio.apply(Double.valueOf(stdRatio)).doubleValue();
            }
            double desiredRatio = MathUtils.clampDouble(minRatioRealized, maxRatioRealized, currentRatio);
            if (desiredRatio == currentRatio) {
                answer = myDesiredTone;
            } else {
                answer = contrastingTone(bgTone, desiredRatio);
            }
            if (bgDynamic.background == null || bgDynamic.background.apply(scheme) == null) {
                answer = enableLightForeground(answer);
            }
            return ensureToneDelta(answer, toneStd, scheme, toneDeltaConstraint, toneToJudge);
        }
        return toneStd;
    }

    static double ensureToneDelta(double tone, double toneStandard, DynamicScheme scheme, Function<DynamicScheme, ToneDeltaConstraint> toneDeltaConstraint, Function<DynamicColor, Double> toneToDistanceFrom) {
        ToneDeltaConstraint constraint = toneDeltaConstraint == null ? null : toneDeltaConstraint.apply(scheme);
        if (constraint == null) {
            return tone;
        }
        double requiredDelta = constraint.delta;
        double keepAwayTone = toneToDistanceFrom.apply(constraint.keepAway).doubleValue();
        double delta = Math.abs(tone - keepAwayTone);
        if (delta < requiredDelta) {
            switch (constraint.keepAwayPolarity) {
                case NO_PREFERENCE:
                    double keepAwayToneStandard = constraint.keepAway.tone.apply(scheme).doubleValue();
                    boolean lighten = true;
                    boolean preferLighten = toneStandard > keepAwayToneStandard;
                    double alterAmount = Math.abs(delta - requiredDelta);
                    if (!preferLighten ? tone >= alterAmount : tone + alterAmount > 100.0d) {
                        lighten = false;
                    }
                    if (!lighten) {
                        break;
                    } else {
                        break;
                    }
                    break;
            }
            return tone;
        }
        return tone;
    }

    public static double contrastingTone(double bgTone, double ratio) {
        double lighterTone = Contrast.lighterUnsafe(bgTone, ratio);
        double darkerTone = Contrast.darkerUnsafe(bgTone, ratio);
        double lighterRatio = Contrast.ratioOfTones(lighterTone, bgTone);
        double darkerRatio = Contrast.ratioOfTones(darkerTone, bgTone);
        boolean preferLighter = tonePrefersLightForeground(bgTone);
        if (!preferLighter) {
            return (darkerRatio >= ratio || darkerRatio >= lighterRatio) ? darkerTone : lighterTone;
        }
        boolean negligibleDifference = Math.abs(lighterRatio - darkerRatio) < 0.1d && lighterRatio < ratio && darkerRatio < ratio;
        if (lighterRatio >= ratio || lighterRatio >= darkerRatio || negligibleDifference) {
            return lighterTone;
        }
        return darkerTone;
    }

    public static double enableLightForeground(double tone) {
        if (tonePrefersLightForeground(tone) && !toneAllowsLightForeground(tone)) {
            return 49.0d;
        }
        return tone;
    }

    public static boolean tonePrefersLightForeground(double tone) {
        return Math.round(tone) <= 60;
    }

    public static boolean toneAllowsLightForeground(double tone) {
        return Math.round(tone) <= 49;
    }
}
