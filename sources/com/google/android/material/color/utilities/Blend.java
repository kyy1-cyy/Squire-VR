package com.google.android.material.color.utilities;

/* loaded from: classes.dex */
public class Blend {
    private Blend() {
    }

    public static int harmonize(int designColor, int sourceColor) {
        Hct fromHct = Hct.fromInt(designColor);
        Hct toHct = Hct.fromInt(sourceColor);
        double differenceDegrees = MathUtils.differenceDegrees(fromHct.getHue(), toHct.getHue());
        double rotationDegrees = Math.min(0.5d * differenceDegrees, 15.0d);
        double outputHue = MathUtils.sanitizeDegreesDouble(fromHct.getHue() + (MathUtils.rotationDirection(fromHct.getHue(), toHct.getHue()) * rotationDegrees));
        return Hct.from(outputHue, fromHct.getChroma(), fromHct.getTone()).toInt();
    }

    public static int hctHue(int from, int to, double amount) {
        int ucs = cam16Ucs(from, to, amount);
        Cam16 ucsCam = Cam16.fromInt(ucs);
        Cam16 fromCam = Cam16.fromInt(from);
        Hct blended = Hct.from(ucsCam.getHue(), fromCam.getChroma(), ColorUtils.lstarFromArgb(from));
        return blended.toInt();
    }

    public static int cam16Ucs(int from, int to, double amount) {
        Cam16 fromCam = Cam16.fromInt(from);
        Cam16 toCam = Cam16.fromInt(to);
        double fromJ = fromCam.getJstar();
        double fromA = fromCam.getAstar();
        double fromB = fromCam.getBstar();
        double toJ = toCam.getJstar();
        double toA = toCam.getAstar();
        double toB = toCam.getBstar();
        double jstar = ((toJ - fromJ) * amount) + fromJ;
        double astar = fromA + ((toA - fromA) * amount);
        double bstar = fromB + ((toB - fromB) * amount);
        return Cam16.fromUcs(jstar, astar, bstar).toInt();
    }
}
