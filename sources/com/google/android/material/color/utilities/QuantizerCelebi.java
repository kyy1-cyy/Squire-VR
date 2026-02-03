package com.google.android.material.color.utilities;

import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public final class QuantizerCelebi {
    private QuantizerCelebi() {
    }

    public static Map<Integer, Integer> quantize(int[] pixels, int maxColors) {
        QuantizerWu wu = new QuantizerWu();
        QuantizerResult wuResult = wu.quantize(pixels, maxColors);
        Set<Integer> wuClustersAsObjects = wuResult.colorToCount.keySet();
        int index = 0;
        int[] wuClusters = new int[wuClustersAsObjects.size()];
        for (Integer argb : wuClustersAsObjects) {
            wuClusters[index] = argb.intValue();
            index++;
        }
        return QuantizerWsmeans.quantize(pixels, wuClusters, maxColors);
    }
}
