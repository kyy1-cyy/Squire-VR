package com.google.android.material.color.utilities;

/* loaded from: classes.dex */
public class SchemeTonalSpot extends DynamicScheme {
    public SchemeTonalSpot(Hct sourceColorHct, boolean isDark, double contrastLevel) {
        super(sourceColorHct, Variant.TONAL_SPOT, isDark, contrastLevel, TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), 40.0d), TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), 16.0d), TonalPalette.fromHueAndChroma(MathUtils.sanitizeDegreesDouble(sourceColorHct.getHue() + 60.0d), 24.0d), TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), 6.0d), TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), 8.0d));
    }
}
