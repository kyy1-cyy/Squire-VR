package com.google.android.material.color.utilities;

/* loaded from: classes.dex */
public final class Contrast {
    private static final double CONTRAST_RATIO_EPSILON = 0.04d;
    private static final double LUMINANCE_GAMUT_MAP_TOLERANCE = 0.4d;
    public static final double RATIO_30 = 3.0d;
    public static final double RATIO_45 = 4.5d;
    public static final double RATIO_70 = 7.0d;
    public static final double RATIO_MAX = 21.0d;
    public static final double RATIO_MIN = 1.0d;

    private Contrast() {
    }

    public static double ratioOfYs(double y1, double y2) {
        double lighter = Math.max(y1, y2);
        double darker = lighter == y2 ? y1 : y2;
        return (lighter + 5.0d) / (5.0d + darker);
    }

    public static double ratioOfTones(double t1, double t2) {
        return ratioOfYs(ColorUtils.yFromLstar(t1), ColorUtils.yFromLstar(t2));
    }

    public static double lighter(double tone, double ratio) {
        if (tone < 0.0d || tone > 100.0d) {
            return -1.0d;
        }
        double darkY = ColorUtils.yFromLstar(tone);
        double lightY = ((darkY + 5.0d) * ratio) - 5.0d;
        if (lightY < 0.0d || lightY > 100.0d) {
            return -1.0d;
        }
        double realContrast = ratioOfYs(lightY, darkY);
        double delta = Math.abs(realContrast - ratio);
        if (realContrast < ratio && delta > CONTRAST_RATIO_EPSILON) {
            return -1.0d;
        }
        double returnValue = ColorUtils.lstarFromY(lightY) + LUMINANCE_GAMUT_MAP_TOLERANCE;
        if (returnValue < 0.0d || returnValue > 100.0d) {
            return -1.0d;
        }
        return returnValue;
    }

    public static double lighterUnsafe(double tone, double ratio) {
        double lighterSafe = lighter(tone, ratio);
        if (lighterSafe < 0.0d) {
            return 100.0d;
        }
        return lighterSafe;
    }

    public static double darker(double tone, double ratio) {
        if (tone < 0.0d || tone > 100.0d) {
            return -1.0d;
        }
        double lightY = ColorUtils.yFromLstar(tone);
        double darkY = ((lightY + 5.0d) / ratio) - 5.0d;
        if (darkY < 0.0d || darkY > 100.0d) {
            return -1.0d;
        }
        double realContrast = ratioOfYs(lightY, darkY);
        double delta = Math.abs(realContrast - ratio);
        if (realContrast < ratio && delta > CONTRAST_RATIO_EPSILON) {
            return -1.0d;
        }
        double returnValue = ColorUtils.lstarFromY(darkY) - LUMINANCE_GAMUT_MAP_TOLERANCE;
        if (returnValue < 0.0d || returnValue > 100.0d) {
            return -1.0d;
        }
        return returnValue;
    }

    public static double darkerUnsafe(double tone, double ratio) {
        double darkerSafe = darker(tone, ratio);
        return Math.max(0.0d, darkerSafe);
    }
}
