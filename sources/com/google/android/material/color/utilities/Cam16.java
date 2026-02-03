package com.google.android.material.color.utilities;

/* loaded from: classes.dex */
public final class Cam16 {
    private final double astar;
    private final double bstar;
    private final double chroma;
    private final double hue;
    private final double j;
    private final double jstar;
    private final double m;
    private final double q;
    private final double s;
    private final double[] tempArray = {0.0d, 0.0d, 0.0d};
    static final double[][] XYZ_TO_CAM16RGB = {new double[]{0.401288d, 0.650173d, -0.051461d}, new double[]{-0.250268d, 1.204414d, 0.045854d}, new double[]{-0.002079d, 0.048952d, 0.953127d}};
    static final double[][] CAM16RGB_TO_XYZ = {new double[]{1.8620678d, -1.0112547d, 0.14918678d}, new double[]{0.38752654d, 0.62144744d, -0.00897398d}, new double[]{-0.0158415d, -0.03412294d, 1.0499644d}};

    double distance(Cam16 other) {
        double dJ = getJstar() - other.getJstar();
        double dA = getAstar() - other.getAstar();
        double dB = getBstar() - other.getBstar();
        double dEPrime = Math.sqrt((dJ * dJ) + (dA * dA) + (dB * dB));
        double dE = Math.pow(dEPrime, 0.63d) * 1.41d;
        return dE;
    }

    public double getHue() {
        return this.hue;
    }

    public double getChroma() {
        return this.chroma;
    }

    public double getJ() {
        return this.j;
    }

    public double getQ() {
        return this.q;
    }

    public double getM() {
        return this.m;
    }

    public double getS() {
        return this.s;
    }

    public double getJstar() {
        return this.jstar;
    }

    public double getAstar() {
        return this.astar;
    }

    public double getBstar() {
        return this.bstar;
    }

    private Cam16(double hue, double chroma, double j, double q, double m, double s, double jstar, double astar, double bstar) {
        this.hue = hue;
        this.chroma = chroma;
        this.j = j;
        this.q = q;
        this.m = m;
        this.s = s;
        this.jstar = jstar;
        this.astar = astar;
        this.bstar = bstar;
    }

    public static Cam16 fromInt(int argb) {
        return fromIntInViewingConditions(argb, ViewingConditions.DEFAULT);
    }

    static Cam16 fromIntInViewingConditions(int argb, ViewingConditions viewingConditions) {
        int red = (16711680 & argb) >> 16;
        int green = (65280 & argb) >> 8;
        int blue = argb & 255;
        double redL = ColorUtils.linearized(red);
        double greenL = ColorUtils.linearized(green);
        double blueL = ColorUtils.linearized(blue);
        double x = (0.41233895d * redL) + (0.35762064d * greenL) + (0.18051042d * blueL);
        double y = (0.2126d * redL) + (0.7152d * greenL) + (0.0722d * blueL);
        double z = (0.01932141d * redL) + (0.11916382d * greenL) + (0.95034478d * blueL);
        return fromXyzInViewingConditions(x, y, z, viewingConditions);
    }

    static Cam16 fromXyzInViewingConditions(double x, double y, double z, ViewingConditions viewingConditions) {
        double hue;
        double[][] matrix = XYZ_TO_CAM16RGB;
        double rT = (x * matrix[0][0]) + (matrix[0][1] * y) + (matrix[0][2] * z);
        double gT = (matrix[1][0] * x) + (matrix[1][1] * y) + (matrix[1][2] * z);
        double bT = (matrix[2][0] * x) + (matrix[2][1] * y) + (matrix[2][2] * z);
        double rD = viewingConditions.getRgbD()[0] * rT;
        double gD = viewingConditions.getRgbD()[1] * gT;
        double bD = viewingConditions.getRgbD()[2] * bT;
        double rAF = Math.pow((viewingConditions.getFl() * Math.abs(rD)) / 100.0d, 0.42d);
        double gAF = Math.pow((viewingConditions.getFl() * Math.abs(gD)) / 100.0d, 0.42d);
        double bAF = Math.pow((viewingConditions.getFl() * Math.abs(bD)) / 100.0d, 0.42d);
        double rA = ((Math.signum(rD) * 400.0d) * rAF) / (rAF + 27.13d);
        double gA = ((Math.signum(gD) * 400.0d) * gAF) / (gAF + 27.13d);
        double bA = ((Math.signum(bD) * 400.0d) * bAF) / (bAF + 27.13d);
        double rAF2 = (((rA * 11.0d) + ((-12.0d) * gA)) + bA) / 11.0d;
        double bAF2 = ((rA + gA) - (bA * 2.0d)) / 9.0d;
        double u = (((rA * 20.0d) + (gA * 20.0d)) + (21.0d * bA)) / 20.0d;
        double p2 = (((40.0d * rA) + (gA * 20.0d)) + bA) / 20.0d;
        double atan2 = Math.atan2(bAF2, rAF2);
        double atanDegrees = Math.toDegrees(atan2);
        if (atanDegrees < 0.0d) {
            hue = atanDegrees + 360.0d;
        } else {
            hue = atanDegrees >= 360.0d ? atanDegrees - 360.0d : atanDegrees;
        }
        double hueRadians = Math.toRadians(hue);
        double ac = p2 * viewingConditions.getNbb();
        double j = Math.pow(ac / viewingConditions.getAw(), viewingConditions.getC() * viewingConditions.getZ()) * 100.0d;
        double q = (4.0d / viewingConditions.getC()) * Math.sqrt(j / 100.0d) * (viewingConditions.getAw() + 4.0d) * viewingConditions.getFlRoot();
        double huePrime = hue < 20.14d ? hue + 360.0d : hue;
        double eHue = (Math.cos(Math.toRadians(huePrime) + 2.0d) + 3.8d) * 0.25d;
        double p1 = 3846.153846153846d * eHue * viewingConditions.getNc() * viewingConditions.getNcb();
        double a = (Math.hypot(rAF2, bAF2) * p1) / (u + 0.305d);
        double b = viewingConditions.getN();
        double alpha = Math.pow(1.64d - Math.pow(0.29d, b), 0.73d) * Math.pow(a, 0.9d);
        double c = Math.sqrt(j / 100.0d) * alpha;
        double m = viewingConditions.getFlRoot() * c;
        double s = Math.sqrt((viewingConditions.getC() * alpha) / (viewingConditions.getAw() + 4.0d)) * 50.0d;
        double jstar = (1.7000000000000002d * j) / ((0.007d * j) + 1.0d);
        double mstar = Math.log1p(0.0228d * m) * 43.859649122807014d;
        double astar = mstar * Math.cos(hueRadians);
        double bstar = mstar * Math.sin(hueRadians);
        return new Cam16(hue, c, j, q, m, s, jstar, astar, bstar);
    }

    static Cam16 fromJch(double j, double c, double h) {
        return fromJchInViewingConditions(j, c, h, ViewingConditions.DEFAULT);
    }

    private static Cam16 fromJchInViewingConditions(double j, double c, double h, ViewingConditions viewingConditions) {
        double q = (4.0d / viewingConditions.getC()) * Math.sqrt(j / 100.0d) * (viewingConditions.getAw() + 4.0d) * viewingConditions.getFlRoot();
        double m = c * viewingConditions.getFlRoot();
        double alpha = c / Math.sqrt(j / 100.0d);
        double s = Math.sqrt((viewingConditions.getC() * alpha) / (viewingConditions.getAw() + 4.0d)) * 50.0d;
        double hueRadians = Math.toRadians(h);
        double jstar = (1.7000000000000002d * j) / ((0.007d * j) + 1.0d);
        double mstar = Math.log1p(0.0228d * m) * 43.859649122807014d;
        double astar = mstar * Math.cos(hueRadians);
        double bstar = mstar * Math.sin(hueRadians);
        return new Cam16(h, c, j, q, m, s, jstar, astar, bstar);
    }

    public static Cam16 fromUcs(double jstar, double astar, double bstar) {
        return fromUcsInViewingConditions(jstar, astar, bstar, ViewingConditions.DEFAULT);
    }

    public static Cam16 fromUcsInViewingConditions(double jstar, double astar, double bstar, ViewingConditions viewingConditions) {
        double h;
        double m = Math.hypot(astar, bstar);
        double m2 = Math.expm1(m * 0.0228d) / 0.0228d;
        double c = m2 / viewingConditions.getFlRoot();
        double h2 = Math.atan2(bstar, astar) * 57.29577951308232d;
        if (h2 >= 0.0d) {
            h = h2;
        } else {
            h = h2 + 360.0d;
        }
        double j = jstar / (1.0d - ((jstar - 100.0d) * 0.007d));
        return fromJchInViewingConditions(j, c, h, viewingConditions);
    }

    public int toInt() {
        return viewed(ViewingConditions.DEFAULT);
    }

    int viewed(ViewingConditions viewingConditions) {
        double[] xyz = xyzInViewingConditions(viewingConditions, this.tempArray);
        return ColorUtils.argbFromXyz(xyz[0], xyz[1], xyz[2]);
    }

    /* JADX WARN: Failed to analyze thrown exceptions
    java.util.ConcurrentModificationException
    	at java.base/java.util.ArrayList$Itr.checkForComodification(ArrayList.java:1096)
    	at java.base/java.util.ArrayList$Itr.next(ArrayList.java:1050)
    	at jadx.core.dex.visitors.MethodThrowsVisitor.processInstructions(MethodThrowsVisitor.java:131)
    	at jadx.core.dex.visitors.MethodThrowsVisitor.visit(MethodThrowsVisitor.java:69)
    	at jadx.core.dex.visitors.MethodThrowsVisitor.checkInsn(MethodThrowsVisitor.java:179)
    	at jadx.core.dex.visitors.MethodThrowsVisitor.processInstructions(MethodThrowsVisitor.java:132)
    	at jadx.core.dex.visitors.MethodThrowsVisitor.visit(MethodThrowsVisitor.java:69)
     */
    double[] xyzInViewingConditions(ViewingConditions viewingConditions, double[] returnArray) {
        double alpha = (getChroma() == 0.0d || getJ() == 0.0d) ? 0.0d : getChroma() / Math.sqrt(getJ() / 100.0d);
        double t = Math.pow(alpha / Math.pow(1.64d - Math.pow(0.29d, viewingConditions.getN()), 0.73d), 1.1111111111111112d);
        double hRad = Math.toRadians(getHue());
        double eHue = (Math.cos(2.0d + hRad) + 3.8d) * 0.25d;
        double ac = viewingConditions.getAw() * Math.pow(getJ() / 100.0d, (1.0d / viewingConditions.getC()) / viewingConditions.getZ());
        double p1 = 3846.153846153846d * eHue * viewingConditions.getNc() * viewingConditions.getNcb();
        double p2 = ac / viewingConditions.getNbb();
        double hSin = Math.sin(hRad);
        double hCos = Math.cos(hRad);
        double gamma = (((p2 + 0.305d) * 23.0d) * t) / (((23.0d * p1) + ((11.0d * t) * hCos)) + ((108.0d * t) * hSin));
        double a = gamma * hCos;
        double b = gamma * hSin;
        double rA = (((p2 * 460.0d) + (451.0d * a)) + (288.0d * b)) / 1403.0d;
        double gA = (((p2 * 460.0d) - (891.0d * a)) - (261.0d * b)) / 1403.0d;
        double bA = (((460.0d * p2) - (220.0d * a)) - (6300.0d * b)) / 1403.0d;
        double alpha2 = (Math.abs(rA) * 27.13d) / (400.0d - Math.abs(rA));
        double rCBase = Math.max(0.0d, alpha2);
        double rC = Math.signum(rA) * (100.0d / viewingConditions.getFl()) * Math.pow(rCBase, 2.380952380952381d);
        double gCBase = Math.max(0.0d, (Math.abs(gA) * 27.13d) / (400.0d - Math.abs(gA)));
        double gC = Math.signum(gA) * (100.0d / viewingConditions.getFl()) * Math.pow(gCBase, 2.380952380952381d);
        double bCBase = Math.max(0.0d, (Math.abs(bA) * 27.13d) / (400.0d - Math.abs(bA)));
        double bC = Math.signum(bA) * (100.0d / viewingConditions.getFl()) * Math.pow(bCBase, 2.380952380952381d);
        double rF = rC / viewingConditions.getRgbD()[0];
        double gF = gC / viewingConditions.getRgbD()[1];
        double bF = bC / viewingConditions.getRgbD()[2];
        double[][] matrix = CAM16RGB_TO_XYZ;
        double x = (matrix[0][0] * rF) + (matrix[0][1] * gF) + (matrix[0][2] * bF);
        double y = (matrix[1][0] * rF) + (matrix[1][1] * gF) + (matrix[1][2] * bF);
        double z = (matrix[2][0] * rF) + (matrix[2][1] * gF) + (matrix[2][2] * bF);
        if (returnArray == null) {
            return new double[]{x, y, z};
        }
        returnArray[0] = x;
        returnArray[1] = y;
        returnArray[2] = z;
        return returnArray;
    }
}
