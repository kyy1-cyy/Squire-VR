package com.google.android.material.color.utilities;

/* loaded from: classes.dex */
public class SchemeNeutral extends DynamicScheme {
    public SchemeNeutral(Hct sourceColorHct, boolean isDark, double contrastLevel) {
        super(sourceColorHct, Variant.NEUTRAL, isDark, contrastLevel, TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), 12.0d), TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), 8.0d), TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), 16.0d), TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), 2.0d), TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), 2.0d));
    }
}
