package com.google.android.material.color.utilities;

/* loaded from: classes.dex */
public class DynamicScheme {
    public final double contrastLevel;
    public final TonalPalette errorPalette = TonalPalette.fromHueAndChroma(25.0d, 84.0d);
    public final boolean isDark;
    public final TonalPalette neutralPalette;
    public final TonalPalette neutralVariantPalette;
    public final TonalPalette primaryPalette;
    public final TonalPalette secondaryPalette;
    public final int sourceColorArgb;
    public final Hct sourceColorHct;
    public final TonalPalette tertiaryPalette;
    public final Variant variant;

    public DynamicScheme(Hct sourceColorHct, Variant variant, boolean isDark, double contrastLevel, TonalPalette primaryPalette, TonalPalette secondaryPalette, TonalPalette tertiaryPalette, TonalPalette neutralPalette, TonalPalette neutralVariantPalette) {
        this.sourceColorArgb = sourceColorHct.toInt();
        this.sourceColorHct = sourceColorHct;
        this.variant = variant;
        this.isDark = isDark;
        this.contrastLevel = contrastLevel;
        this.primaryPalette = primaryPalette;
        this.secondaryPalette = secondaryPalette;
        this.tertiaryPalette = tertiaryPalette;
        this.neutralPalette = neutralPalette;
        this.neutralVariantPalette = neutralVariantPalette;
    }

    public static double getRotatedHue(Hct sourceColorHct, double[] hues, double[] rotations) {
        double sourceHue = sourceColorHct.getHue();
        if (rotations.length == 1) {
            return MathUtils.sanitizeDegreesDouble(rotations[0] + sourceHue);
        }
        int size = hues.length;
        for (int i = 0; i <= size - 2; i++) {
            double thisHue = hues[i];
            double nextHue = hues[i + 1];
            if (thisHue < sourceHue && sourceHue < nextHue) {
                return MathUtils.sanitizeDegreesDouble(rotations[i] + sourceHue);
            }
        }
        return sourceHue;
    }
}
