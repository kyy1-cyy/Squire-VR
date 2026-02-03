package com.google.android.material.color.utilities;

/* loaded from: classes.dex */
public class SchemeMonochrome extends DynamicScheme {
    public SchemeMonochrome(Hct sourceColorHct, boolean isDark, double contrastLevel) {
        super(sourceColorHct, Variant.MONOCHROME, isDark, contrastLevel, TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), 0.0d), TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), 0.0d), TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), 0.0d), TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), 0.0d), TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), 0.0d));
    }
}
