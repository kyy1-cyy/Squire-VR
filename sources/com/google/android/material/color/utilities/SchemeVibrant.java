package com.google.android.material.color.utilities;

/* loaded from: classes.dex */
public class SchemeVibrant extends DynamicScheme {
    private static final double[] HUES = {0.0d, 41.0d, 61.0d, 101.0d, 131.0d, 181.0d, 251.0d, 301.0d, 360.0d};
    private static final double[] SECONDARY_ROTATIONS = {18.0d, 15.0d, 10.0d, 12.0d, 15.0d, 18.0d, 15.0d, 12.0d, 12.0d};
    private static final double[] TERTIARY_ROTATIONS = {35.0d, 30.0d, 20.0d, 25.0d, 30.0d, 35.0d, 30.0d, 25.0d, 25.0d};

    public SchemeVibrant(Hct sourceColorHct, boolean isDark, double contrastLevel) {
        super(sourceColorHct, Variant.VIBRANT, isDark, contrastLevel, TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), 200.0d), TonalPalette.fromHueAndChroma(DynamicScheme.getRotatedHue(sourceColorHct, HUES, SECONDARY_ROTATIONS), 24.0d), TonalPalette.fromHueAndChroma(DynamicScheme.getRotatedHue(sourceColorHct, HUES, TERTIARY_ROTATIONS), 32.0d), TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), 8.0d), TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), 12.0d));
    }
}
