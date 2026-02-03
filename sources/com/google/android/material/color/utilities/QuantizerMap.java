package com.google.android.material.color.utilities;

import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: classes.dex */
public final class QuantizerMap implements Quantizer {
    Map<Integer, Integer> colorToCount;

    @Override // com.google.android.material.color.utilities.Quantizer
    public QuantizerResult quantize(int[] pixels, int colorCount) {
        Map<Integer, Integer> pixelByCount = new LinkedHashMap<>();
        for (int pixel : pixels) {
            Integer currentPixelCount = pixelByCount.get(Integer.valueOf(pixel));
            int newPixelCount = 1;
            if (currentPixelCount != null) {
                newPixelCount = 1 + currentPixelCount.intValue();
            }
            pixelByCount.put(Integer.valueOf(pixel), Integer.valueOf(newPixelCount));
        }
        this.colorToCount = pixelByCount;
        return new QuantizerResult(pixelByCount);
    }

    public Map<Integer, Integer> getColorToCount() {
        return this.colorToCount;
    }
}
