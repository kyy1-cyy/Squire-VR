package com.google.android.material.color.utilities;

/* loaded from: classes.dex */
public final class Hct {
    private int argb;
    private double chroma;
    private double hue;
    private double tone;

    public static Hct from(double hue, double chroma, double tone) {
        int argb = HctSolver.solveToInt(hue, chroma, tone);
        return new Hct(argb);
    }

    public static Hct fromInt(int argb) {
        return new Hct(argb);
    }

    private Hct(int argb) {
        setInternalState(argb);
    }

    public double getHue() {
        return this.hue;
    }

    public double getChroma() {
        return this.chroma;
    }

    public double getTone() {
        return this.tone;
    }

    public int toInt() {
        return this.argb;
    }

    public void setHue(double newHue) {
        setInternalState(HctSolver.solveToInt(newHue, this.chroma, this.tone));
    }

    public void setChroma(double newChroma) {
        setInternalState(HctSolver.solveToInt(this.hue, newChroma, this.tone));
    }

    public void setTone(double newTone) {
        setInternalState(HctSolver.solveToInt(this.hue, this.chroma, newTone));
    }

    public Hct inViewingConditions(ViewingConditions vc) {
        Cam16 cam16 = Cam16.fromInt(toInt());
        double[] viewedInVc = cam16.xyzInViewingConditions(vc, null);
        Cam16 recastInVc = Cam16.fromXyzInViewingConditions(viewedInVc[0], viewedInVc[1], viewedInVc[2], ViewingConditions.DEFAULT);
        return from(recastInVc.getHue(), recastInVc.getChroma(), ColorUtils.lstarFromY(viewedInVc[1]));
    }

    private void setInternalState(int argb) {
        this.argb = argb;
        Cam16 cam = Cam16.fromInt(argb);
        this.hue = cam.getHue();
        this.chroma = cam.getChroma();
        this.tone = ColorUtils.lstarFromArgb(argb);
    }
}
