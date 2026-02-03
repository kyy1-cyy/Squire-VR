package com.google.android.material.color.utilities;

/* loaded from: classes.dex */
public class SchemeContent extends DynamicScheme {
    public SchemeContent(Hct sourceColorHct, boolean isDark, double contrastLevel) {
        super(sourceColorHct, Variant.CONTENT, isDark, contrastLevel, TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), sourceColorHct.getChroma()), TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), Math.max(sourceColorHct.getChroma() - 32.0d, sourceColorHct.getChroma() * 0.5d)), TonalPalette.fromHct(DislikeAnalyzer.fixIfDisliked(new TemperatureCache(sourceColorHct).getAnalogousColors(3, 6).get(2))), TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), sourceColorHct.getChroma() / 8.0d), TonalPalette.fromHueAndChroma(sourceColorHct.getHue(), (sourceColorHct.getChroma() / 8.0d) + 4.0d));
    }
}
